package com.opinta.service;

import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
import com.opinta.dto.ClientDto;
import com.opinta.mapper.ClientMapper;
import com.opinta.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    
    private final ClientDao clientDao;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
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
    public ClientDto getById(long id) {
        log.info("Getting client by id " + id);
        Client client = this.clientDao.getById(id);
        return this.clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public boolean save(ClientDto clientDto) {
        log.info("Saving client " + clientDto);
        Client client = this.clientMapper.toEntity(clientDto);
        this.clientDao.save(client);
        return true;
    }

    @Override
    @Transactional
    public ClientDto update(long id, ClientDto dtoClient) {
        Client storedClient = this.clientDao.getById(id);
        if (storedClient == null) {
            log.info("Can't update client. Client doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(storedClient, dtoClient);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
        }
        storedClient.setId(id);
        log.info("Updating client " + storedClient);
        clientDao.update(storedClient);
        return this.clientMapper.toDto(storedClient);
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
