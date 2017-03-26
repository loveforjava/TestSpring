package com.opinta.service;

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
    public VirtualPostOfficeServiceImpl(
            VirtualPostOfficeDao virtualPostOfficeDao,
            VirtualPostOfficeMapper virtualPostOfficeMapper) {
        this.virtualPostOfficeDao = virtualPostOfficeDao;
        this.virtualPostOfficeMapper = virtualPostOfficeMapper;
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
        return this.virtualPostOfficeMapper.toDto(this.virtualPostOfficeDao.save(postOffice));
    }

    @Override
    @Transactional
    public VirtualPostOfficeDto update(long id, VirtualPostOfficeDto updated) {
        VirtualPostOffice persisted = this.virtualPostOfficeDao.getById(id);
        if (persisted == null) {
            log.info("Can't update virtualPostOffices. VirtualPostOffices doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(persisted, updated);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for virtualPostOffices", e);
        }
        persisted.setId(id);
        log.info("Updating virtualPostOffices " + persisted);
        this.virtualPostOfficeDao.update(persisted);
        return this.virtualPostOfficeMapper.toDto(persisted);
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
