package ua.ukrpost.service;

import ua.ukrpost.dto.postid.ClientTypeDto;
import ua.ukrpost.entity.Address;
import ua.ukrpost.entity.ClientType;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import ua.ukrpost.dao.ClientDao;
import ua.ukrpost.dto.ClientDto;
import ua.ukrpost.mapper.ClientMapper;
import ua.ukrpost.entity.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ua.ukrpost.util.AuthorizationUtil.authorizeForAction;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import static ua.ukrpost.util.AlphabetCharactersGenerationUtil.characterOf;
import static ua.ukrpost.util.AlphabetCharactersGenerationUtil.generateRandomChars;
import static ua.ukrpost.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByFieldLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByFieldOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final CounterpartyService counterpartyService;
    private final PhoneService phoneService;
    private final AddressService addressService;
    private final ClientMapper clientMapper;
    private final PostIdInnerNumberGenerator postIdInnerNumberGenerator;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper, PhoneService phoneService,
                             AddressService addressService, CounterpartyService counterpartyService,
                             PostIdInnerNumberGenerator postIdInnerNumberGenerator) {
        this.clientDao = clientDao;
        this.counterpartyService = counterpartyService;
        this.phoneService = phoneService;
        this.addressService = addressService;
        this.clientMapper = clientMapper;
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

        authorizeForAction(client, user);

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
        
        authorizeForAction(client, user);
    
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
        authorizeForAction(client, user);
        LocalDateTime now = LocalDateTime.now();
        client.setCreated(now);
        client.setLastModified(now);
        client.setCreator(user);
        client.setLastModifier(user);
        log.info(saveLogEndpoint(Client.class, client));
        return clientDao.save(client);
    }
    
    private String generatePostId(ClientType clientType) throws IncorrectInputDataException {
        String yearString = valueOf(LocalDate.now().getYear());
        yearString = yearString.substring(yearString.length() - 2);
        return characterOf(clientType) + yearString + postIdInnerNumberGenerator.generate() +
                generateRandomChars(3, true);
    }
    
    @Override
    @Transactional
    public Client updateEntity(Client client, User user) throws IncorrectInputDataException, AuthException {
        log.info(updateLogEndpoint(Client.class, client));
        authorizeForAction(client, user);
        client.setLastModified(LocalDateTime.now());
        client.setLastModifier(user);
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
    public ClientDto updatePostId(UUID uuid, ClientTypeDto clientTypeDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException {
        Client client = getEntityByUuid(uuid, user);
        if (client.getPostId() != null) {
            throw new PerformProcessFailedException(
                    format("Client %s already has postId: %s", uuid.toString(), client.getPostId()));
        }
        client.setPostId(generatePostId(clientTypeDto.getType()));
        clientDao.update(client);
        return clientMapper.toDto(client);
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
