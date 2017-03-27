package com.opinta.service;

import com.opinta.entity.PostcodePool;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.VirtualPostOfficeDao;
import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.mapper.VirtualPostOfficeMapper;
import com.opinta.entity.VirtualPostOffice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class VirtualPostOfficeServiceImpl implements VirtualPostOfficeService {
    private final VirtualPostOfficeDao virtualPostOfficeDao;
    private final VirtualPostOfficeMapper virtualPostOfficeMapper;

    @Autowired
    public VirtualPostOfficeServiceImpl(VirtualPostOfficeDao virtualPostOfficeDao,
                                        VirtualPostOfficeMapper virtualPostOfficeMapper) {
        this.virtualPostOfficeDao = virtualPostOfficeDao;
        this.virtualPostOfficeMapper = virtualPostOfficeMapper;
    }

    @Override
    @Transactional
    public List<VirtualPostOffice> getAllEntities() {
        log.info("Getting all virtualPostOffices");
        return virtualPostOfficeDao.getAll();
    }

    @Override
    @Transactional
    public VirtualPostOffice getEntityById(long id) {
        log.info("Getting virtualPostOffice {}", id);
        return virtualPostOfficeDao.getById(id);
    }

    @Override
    @Transactional
    public List<VirtualPostOffice> getEntityByPostcodePool(PostcodePool postcodePool) {
        log.info("Getting virtualPostOffice by postcodePool {}", postcodePool);
        return virtualPostOfficeDao.getByPostcodePool(postcodePool);
    }

    @Override
    @Transactional
    public VirtualPostOffice saveEntity(VirtualPostOffice virtualPostOffice) {
        List<VirtualPostOffice> virtualPostOffices = getEntityByPostcodePool(virtualPostOffice.getActivePostcodePool());
        if (virtualPostOffices.size() != 0) {
            log.error("PostcodePool {} is already used in the VPO {}", virtualPostOffice.getActivePostcodePool(),
                    virtualPostOffices);
            return null;
        }
        log.info("Saving virtualPostOffice {}", virtualPostOffice);
        return virtualPostOfficeDao.save(virtualPostOffice);
    }

    @Override
    @Transactional
    public List<VirtualPostOfficeDto> getAll() {
        log.info("Getting all virtualPostOffices");
        List<VirtualPostOffice> all =  virtualPostOfficeDao.getAll();
        return this.virtualPostOfficeMapper.toDto(all);
    }

    @Override
    @Transactional
    public VirtualPostOfficeDto getById(long id) {
        log.info("Getting virtualPostOffice by id " + id);
        VirtualPostOffice office = this.virtualPostOfficeDao.getById(id);
        return this.virtualPostOfficeMapper.toDto(office);
    }

    @Override
    @Transactional
    public VirtualPostOfficeDto save(VirtualPostOfficeDto virtualPostOfficeDto) {
        log.info("Saving virtualPostOffices " + virtualPostOfficeDto);
        VirtualPostOffice postOffice = this.virtualPostOfficeMapper.toEntity(virtualPostOfficeDto);
        return this.virtualPostOfficeMapper.toDto(saveEntity(postOffice));
    }

    @Override
    @Transactional
    public VirtualPostOfficeDto update(long id, VirtualPostOfficeDto virtualPostOfficeDto) {
        VirtualPostOffice source = virtualPostOfficeMapper.toEntity(virtualPostOfficeDto);
        VirtualPostOffice target = virtualPostOfficeDao.getById(id);
        if (target == null) {
            log.debug("Can't update virtualPostOffice. VirtualPostOffice doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for virtualPostOffice", e);
        }
        target.setId(id);
        log.info("Updating virtualPostOffice {}", target);
        virtualPostOfficeDao.update(target);
        return virtualPostOfficeMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        VirtualPostOffice virtualPostOffice = this.virtualPostOfficeDao.getById(id);
        if (virtualPostOffice == null) {
            log.debug("Can't delete virtualPostOffices. VirtualPostOffices doesn't exist " + id);
            return false;
        } else {
            log.info("Deleting virtualPostOffices " + virtualPostOffice);
            virtualPostOfficeDao.delete(virtualPostOffice);
            return true;
        }
    }
}
