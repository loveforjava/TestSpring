package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.ClientType;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
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

import static java.lang.String.valueOf;
import static java.time.LocalDate.now;

import static com.opinta.util.AlphabetUtil.characterOf;
import static com.opinta.util.AlphabetUtil.generateRandomChars;
import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByFieldLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByFieldOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final CounterpartyService counterpartyService;
    private final PhoneService phoneService;
    private final AddressService addressService;
    private final UserService userService;
    private final ClientMapper clientMapper;
    private final PostIdInnerNumberGenerator postIdInnerNumberGenerator;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper, PhoneService phoneService,
                             AddressService addressService, CounterpartyService counterpartyService,
                             UserService userService, PostIdInnerNumberGenerator postIdInnerNumberGenerator) {
        this.clientDao = clientDao;
        this.counterpartyService = counterpartyService;
        this.phoneService = phoneService;
        this.addressService = addressService;
        this.clientMapper = clientMapper;
        this.userService = userService;
        this.postIdInnerNumberGenerator = postIdInnerNumberGenerator;
    }

    @Override
    @Transactional
    public List<Client> getAllEntities(User user) {
        log.info(getAllLogEndpoint(Client.class));
        return clientDao.getAll(user);
    }

    @Override
    @Transactional
    public List<Client> getAllEntitiesByCounterpartyUuid(UUID counterpartyUuid, User user)
            throws IncorrectInputDataException, AuthException {
        log.info(getAllByFieldLogEndpoint(Client.class, Counterparty.class, counterpartyUuid));
        return clientDao.getAllByCounterparty(counterpartyService.getEntityByUuid(counterpartyUuid, user));
    }
    
    @Override
    @Transactional
    public Client saveOrGetEntity(Client client, User user) throws IncorrectInputDataException, AuthException {
        if (client.getUuid() != null) {
            return getEntityByUuid(client.getUuid(), user);
        }
        if (client.getPostId() != null) {
            return getEntityByPostId(client.getPostId(), user);
        }
        return saveEntity(client, user);
    }

    @Override
    public Client saveOrGetEntityAnonymous(Client client, User user) throws IncorrectInputDataException, AuthException {
        if (client.getUuid() == null) {
            return saveEntity(client, user);
        } else {
            return getEntityByUuidAnonymous(client.getUuid());
        }
    }

    @Override
    @Transactional
    public Client getEntityByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(getByIdLogEndpoint(Client.class, uuid));
        Client client = clientDao.getByUuid(uuid);
        if (client == null) {
            log.error(getByIdOnErrorLogEndpoint(Client.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Client.class, uuid));
        }

        userService.authorizeForAction(client, user);

        return client;
    }
    
    @Override
    @Transactional
    public Client getEntityByPostId(String postId, User user) throws IncorrectInputDataException, AuthException {
        log.info(getByFieldLogEndpoint(Client.class, String.class, postId));
        Client client = clientDao.getByPostId(postId);
        if (client == null) {
            log.error(getByFieldOnErrorLogEndpoint(Client.class, String.class, postId));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Client.class, postId));
        }
        
        userService.authorizeForAction(client, user);
    
        return client;
    }
    
    @Override
    @Transactional
    public Client getEntityByUuidAnonymous(UUID uuid) throws IncorrectInputDataException {
        log.info("Getting client by uuid without token check. Client ", uuid);
        Client client = clientDao.getByUuid(uuid);
        if (client == null) {
            log.error(getByIdOnErrorLogEndpoint(Client.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Client.class, uuid));
        }
        return client;
    }
    
    @Override
    @Transactional
    public Client saveEntity(Client client, User user) throws IncorrectInputDataException, AuthException {
        client.setPostId(null);
        validateInnerReferencesAndFillObjectFromDB(client, user);
        client.setPhone(phoneService.getOrCreateEntityByPhoneNumber(client.getPhone().getPhoneNumber())
                .removeNonNumericalCharacters());
        userService.authorizeForAction(client, user);
        log.info(saveLogEndpoint(Client.class, client));
        return clientDao.save(client);
    }
    
    @Override
    @Transactional
    public ClientDto updatePostId(UUID uuid, ClientType clientType, User user)
            throws IncorrectInputDataException, AuthException {
        Client client = getEntityByUuid(uuid, user);
        if (client.getPostId() != null) {
            return clientMapper.toDto(client);
        }
        client.setPostId(generatePostId(clientType));
        clientDao.update(client);
        return clientMapper.toDto(client);
    }
    
    private String generatePostId(ClientType clientType) throws IncorrectInputDataException {
        String yearString = valueOf(now().getYear());
        yearString = yearString.substring(yearString.length() - 2);
        return characterOf(clientType) + yearString +
                postIdInnerNumberGenerator.generateNextNumber() + generateRandomChars(3, true);
    }
    
    @Override
    @Transactional
    public Client updateEntity(Client client, User user) throws IncorrectInputDataException, AuthException {
        log.info(updateLogEndpoint(Client.class, client));
        userService.authorizeForAction(client, user);
        clientDao.update(client);
        return client;
    }

    @Override
    @Transactional
    public List<ClientDto> getAll(User user) {
        return clientMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid, User user)
            throws IncorrectInputDataException, AuthException {
        return clientMapper.toDto(getAllEntitiesByCounterpartyUuid(counterpartyUuid, user));
    }
    
    @Override
    @Transactional
    public ClientDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        return clientMapper.toDto(getEntityByUuid(uuid, user));
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto, User user) throws AuthException, IncorrectInputDataException {
        Client client = clientMapper.toEntity(clientDto);
        client.setCounterparty(user.getCounterparty());
        return clientMapper.toDto(saveEntity(client, user));
    }

    @Override
    @Transactional
    public ClientDto update(UUID uuid, ClientDto clientDto, User user) throws AuthException,
            IncorrectInputDataException, PerformProcessFailedException {
        Client source = clientMapper.toEntity(clientDto);
        source.setCounterparty(null);
        source.setPostId(null);
        Client target = getEntityByUuid(uuid, user);

        validateInnerReferencesAndFillObjectFromDB(source, user);

        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Client.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(Client.class, source, target, e));
        }
        
        target.setUuid(uuid);
        target.getPhone().removeNonNumericalCharacters();
        target.setPhone(phoneService.getOrCreateEntityByPhoneNumber(target.getPhone().getPhoneNumber()));
        target.setAddress(source.getAddress());
        updateEntity(target, user);
        return clientMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(Client.class, uuid));
        Client client = getEntityByUuid(uuid, user);
        clientDao.delete(client);
    }

    private void validateInnerReferencesAndFillObjectFromDB(Client source, User user)
            throws IncorrectInputDataException, AuthException {
        Counterparty counterparty = user.getCounterparty();
        Address address = addressService.getEntityById(source.getAddress().getId());
        source.setCounterparty(counterparty);
        source.setAddress(address);
    }
}
