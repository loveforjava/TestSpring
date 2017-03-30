package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.Counterparty;
import java.util.List;
import java.util.UUID;

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
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper, PhoneService phoneService,
                             AddressService addressService, CounterpartyService counterpartyService) {
        this.clientDao = clientDao;
        this.counterpartyService = counterpartyService;
        this.phoneService = phoneService;
        this.addressService = addressService;
        this.clientMapper = clientMapper;
    }

    @Override
    @Transactional
    public List<Client> getAllEntities() {
        log.info("Getting all clients");
        return clientDao.getAll();
    }

    @Override
    @Transactional
    public Client getEntityByUuid(UUID uuid) {
        log.info("Getting Client by uuid {}", uuid);
        return clientDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public Client saveEntity(Client client) {
        log.info("Saving Client {}", client);
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public List<ClientDto> getAll() {
        log.info("Getting all clients");
        List<Client> allClients = clientDao.getAll();
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
    public ClientDto getByUuid(UUID uuid) {
        log.info("Getting client by uuid {}", uuid);
        Client client = clientDao.getByUuid(uuid);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) throws Exception {
        Client client = clientMapper.toEntity(clientDto);
        // validate reference fields
        try {
            validateInnerReferenceAndFillObjectFromDB(client);
        } catch (Exception e) {
            throw new Exception(e);
        }

        client.setPhone(phoneService.getOrCreateEntityByPhoneNumber(clientDto.getPhoneNumber()));

        log.info("Saving client {}", clientDto);
        client = clientDao.save(client);
        log.info("saved Client uuid: " + client.getUuid());
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto update(UUID uuid, ClientDto clientDto) throws Exception {
        Client source = clientMapper.toEntity(clientDto);
        Client target = clientDao.getByUuid(uuid);
        // validate reference fields
        try {
            validateInnerReferenceAndFillObjectFromDB(source);
        } catch (Exception e) {
            throw new Exception(e);
        }

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
    public boolean delete(UUID uuid) {
        Client client = clientDao.getByUuid(uuid);
        if (client == null) {
            log.debug("Can't delete client. Client doesn't exist " + uuid);
            return false;
        }
        log.info("Deleting client {}", client);
        clientDao.delete(client);
        return true;
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
