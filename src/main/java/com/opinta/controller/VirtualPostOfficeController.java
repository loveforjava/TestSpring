package com.opinta.controller;

import com.opinta.mapper.VirtualPostOfficeMapper;
import com.opinta.service.VirtualPostOfficeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Diarsid on 20.03.2017.
 */

@RestController
@RequestMapping("/virtual-post-offices")
public class VirtualPostOfficeController {
    
    private final VirtualPostOfficeService postOfficeService;
    private final VirtualPostOfficeMapper postOfficeMapper;
    
    public VirtualPostOfficeController(VirtualPostOfficeService postOfficeService,
            VirtualPostOfficeMapper postOfficeMapper) {
        this.postOfficeService = postOfficeService;
        this.postOfficeMapper = postOfficeMapper;
    }
    
    
}
