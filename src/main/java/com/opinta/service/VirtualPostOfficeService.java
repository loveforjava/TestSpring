package com.opinta.service;

import com.opinta.model.VirtualPostOffice;
import java.util.List;

public interface VirtualPostOfficeService {
    List<VirtualPostOffice> getAll();
    VirtualPostOffice getById(Long id);
    void save(VirtualPostOffice virtualPostOffice);
    VirtualPostOffice update(Long id, VirtualPostOffice source);
    boolean delete(Long id);
}
