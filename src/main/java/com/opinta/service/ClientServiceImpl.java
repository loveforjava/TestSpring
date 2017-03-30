package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dto.ClientDto;
import com.opinta.mapper.ClientMapper;
import com.opinta.entity.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.beanutils.PropertyUtils.copyProperties;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final CounterpartyService counterpartyService;
    private final PhoneService phoneService;
    private final AddressService addressService;
    private final UserService userService;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper, PhoneService phoneService,
                             AddressService addressService, CounterpartyService counterpartyService,
                             UserService userService) {
        this.clientDao = clientDao;
        this.counterpartyService = counterpartyService;
        this.phoneService = phoneService;
        this.addressService = addressService;
        this.clientMapper = clientMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public List<Client> getAllEntities(User user) {
        log.info("Getting all clients");
        return clientDao.getAll(user);
    }

    @Override
    @Transactional
    public Client getEntityById(long id, User user) throws AuthenticationException {
        log.info("Getting client by id {}", id);
        Client client = clientDao.getById(id);

        userService.authorizeForAction(client, user);

        return client;
    }

    @Override
    @Transactional
    public Client getEntityByIdAnonymous(long id) {
        log.info("Getting client by id without token check {}", id);
        return clientDao.getById(id);
    }

    @Override
    @Transactional
    public Client saveEntity(Client client, User user) throws Exception {
        // validate reference fields
        try {
            validateInnerReferenceAndFillObjectFromDB(client);
        } catch (Exception e) {
            throw new Exception(e);
        }

        client.setPhone(phoneService.getOrCreateEntityByPhoneNumber(client.getPhone().getPhoneNumber()));

        userService.authorizeForAction(client, user);

        log.info("Saving client {}", client);
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public List<ClientDto> getAll(User user) {
        log.info("Getting all clients");
        List<Client> allClients = clientDao.getAll(user);
        return clientMapper.toDto(allClients);
    }

    @Override
    @Transactional
    public List<ClientDto> getAllByCounterpartyId(long counterpartyId) {
        Counterparty counterparty = counterpartyService.getEntityById(counterpartyId);
        if (counterparty == null) {
            log.debug("Can't get client list by counterparty. Counterparty {} doesn't exist", counterpartyId);
            return null;
        }
        log.info("Getting all clients by counterparty {}", counterparty);
        return clientMapper.toDto(clientDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ClientDto getById(long id, User user) throws AuthenticationException {
        Client client = getEntityById(id, user);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto, User user) throws Exception {
        Client client = clientMapper.toEntity(clientDto);
        return clientMapper.toDto(saveEntity(client, user));
    }

    @Override
    @Transactional
    public ClientDto update(long id, ClientDto clientDto, User user) throws Exception {
        Client source = clientMapper.toEntity(clientDto);
        Client target = clientDao.getById(id);

        userService.authorizeForAction(target, user);

        // validate reference fields
        validateInnerReferenceAndFillObjectFromDB(source);

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
            throw new Exception("Can't get properties from object to updatable object for client", e);
        }

        target.setId(id);
        target.setCounterparty(source.getCounterparty());
        target.setPhone(phoneService.getOrCreateEntityByPhoneNumber(clientDto.getPhoneNumber()));
        target.setAddress(source.getAddress());

        log.info("Updating client {}", target);
        clientDao.update(target);
        return clientMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(long id, User user) throws AuthenticationException {
        Client client = clientDao.getById(id);

        userService.authorizeForAction(client, user);

        if (client == null) {
            log.error("Can't delete client. Client {} doesn't exist ", id);
            throw new AuthenticationException(format("Can't delete client. Client %d doesn't exist ", id));
        }
        log.info("Deleting client {}", client);
        clientDao.delete(client);
    }

    private void validateInnerReferenceAndFillObjectFromDB(Client source) throws Exception {
        Counterparty counterparty = counterpartyService.getEntityById(source.getCounterparty().getId());
        if (counterparty == null) {
            log.error("Counterparty %s doesn't exist ", source.getCounterparty().getId());
            throw new Exception(format("Counterparty %s doesn't exist ", source.getCounterparty().getId()));
        }
        Address address = addressService.getEntityById(source.getAddress().getId());
        if (address == null) {
            log.error("Given client address doesn't exist {}", source.getAddress().getId());
            throw new Exception(format("Given client address doesn't exist %s", source.getAddress().getId()));
        }
        source.setCounterparty(counterparty);
        source.setAddress(address);
    }
}
