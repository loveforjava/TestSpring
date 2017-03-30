package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

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
    public Client getEntityByUuid(UUID uuid, User user) throws AuthenticationException {
        log.info("Getting client by uuid: ", uuid);
        Client client = clientDao.getByUuid(uuid);

        userService.authorizeForAction(client, user);

        return client;
    }

    @Override
    @Transactional
    public Client getEntityByUuidAnonymous(UUID uuid) {
        log.info("Getting client by uuid without token check ", uuid);
        return clientDao.getByUuid(uuid);
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
    public List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid) {
        Counterparty counterparty = counterpartyService.getEntityByUuid(counterpartyUuid);
        if (counterparty == null) {
            log.debug("Can't get client list by counterparty. Counterparty {} doesn't exist", counterpartyUuid);
            return null;
        }
        log.info("Getting all clients by counterparty {}", counterparty);
        return clientMapper.toDto(clientDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ClientDto getByUuid(UUID uuid, User user) throws AuthenticationException {
        Client client = getEntityByUuid(uuid, user);
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
    public ClientDto update(UUID uuid, ClientDto clientDto, User user) throws Exception {
        Client source = clientMapper.toEntity(clientDto);
        Client target = clientDao.getByUuid(uuid);

        userService.authorizeForAction(target, user);
        // validate reference fields
        validateInnerReferenceAndFillObjectFromDB(source);

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
            throw new Exception("Can't get properties from object to updatable object for client", e);
        }

        target.setUuid(uuid);
        target.setCounterparty(source.getCounterparty());
        target.setPhone(phoneService.getOrCreateEntityByPhoneNumber(clientDto.getPhoneNumber()));
        target.setAddress(source.getAddress());
        log.info("Updating client {}", target);
        clientDao.update(target);
        return clientMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthenticationException {
        Client client = clientDao.getByUuid(uuid);

        userService.authorizeForAction(client, user);

        if (client == null) {
            log.error("Can't delete client. Client {} doesn't exist ", uuid);
            throw new AuthenticationException(format("Can't delete client. Client %s doesn't exist ", uuid));
        }
        log.info("Deleting client {}", client);
        clientDao.delete(client);
    }

    private void validateInnerReferenceAndFillObjectFromDB(Client source) throws Exception {
        Counterparty counterparty = counterpartyService.getEntityByUuid(source.getCounterparty().getUuid());
        if (counterparty == null) {
            log.error("Counterparty %s doesn't exist ", source.getCounterparty().getUuid());
            throw new Exception(format("Counterparty %s doesn't exist ", source.getCounterparty().getUuid()));
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
