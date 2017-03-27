package com.opinta.service;

import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dao.VirtualPostOfficeDao;
import com.opinta.dto.ClientDto;
import com.opinta.mapper.ClientMapper;
import com.opinta.entity.Client;
import com.opinta.entity.VirtualPostOffice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final VirtualPostOfficeDao virtualPostOfficeDao;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper,
                             VirtualPostOfficeDao virtualPostOfficeDao) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
        this.virtualPostOfficeDao = virtualPostOfficeDao;
    }

    @Override
    @Transactional
    public List<Client> getAllEntities() {
        log.info("Getting all clients");
        return clientDao.getAll();
    }

    @Override
    @Transactional
    public Client getEntityById(long id) {
        log.info("Getting address by id {}", id);
        return clientDao.getById(id);
    }

    @Override
    @Transactional
    public Client saveEntity(Client client) {
        log.info("Saving address {}", client);
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public List<ClientDto> getAll() {
        log.info("Getting all clients");
        List<Client> allClients = clientDao.getAll();
        return this.clientMapper.toDto(allClients);
    }

    @Override
    @Transactional
    public List<ClientDto> getAllByVirtualPostOfficeId(long virtualPostOfficeId) {
        VirtualPostOffice virtualPostOffice = virtualPostOfficeDao.getById(virtualPostOfficeId);
        if (virtualPostOffice == null) {
            log.debug("Can't get client list by virtualPostOffice. VirtualPostOffice {} doesn't exist",
                    virtualPostOfficeId);
            return null;
        }
        log.info("Getting all clients by virtualPostOffice {}", virtualPostOffice);
        return clientMapper.toDto(clientDao.getAllByVirtualPostOffice(virtualPostOffice));
    }

    @Override
    @Transactional
    public ClientDto getById(long id) {
        log.info("Getting client by id " + id);
        Client client = this.clientDao.getById(id);
        return this.clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        log.info("Saving client " + clientDto);
        Client client = this.clientMapper.toEntity(clientDto);
        client = this.clientDao.save(client);
        return this.clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto update(long id, ClientDto clientDto) {
        Client source = clientMapper.toEntity(clientDto);
        Client target = clientDao.getById(id);
        if (target == null) {
            log.debug("Can't update client. Client doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
        }
        target.setId(id);
        log.info("Updating client {}", target);
        clientDao.update(target);
        return clientMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Client storedClient = this.clientDao.getById(id);
        if (storedClient == null) {
            log.debug("Can't delete client. Client doesn't exist " + id);
            return false;
        }
        log.info("Deleting client " + storedClient);
        clientDao.delete(storedClient);
        return true;
    }
}
