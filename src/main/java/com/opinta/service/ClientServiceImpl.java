package com.opinta.service;

import com.opinta.dao.ClientDao;
import com.opinta.model.Client;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private ClientDao clientDao;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    @Transactional
    public List<Client> getAll() {
        log.info("Getting all clients");
        return clientDao.getAll();
    }

    @Override
    @Transactional
    public Client getById(Long id) {
        log.info("Getting client by id " + id);
        return clientDao.getById(id);
    }

    @Override
    @Transactional
    public void save(Client client) {
        log.info("Saving client " + client);
        clientDao.save(client);
    }

    @Override
    @Transactional
    public Client update(Long id, Client source) {
        Client target = getById(id);
        if (target == null) {
            log.info("Can't update client. Client doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
        }
        target.setId(id);
        log.info("Updating client " + target);
        clientDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Client client = getById(id);
        if (client == null) {
            log.debug("Can't delete client. Client doesn't exist " + id);
            return false;
        }
        log.info("Deleting client " + client);
        clientDao.delete(client);
        return true;
    }
}
