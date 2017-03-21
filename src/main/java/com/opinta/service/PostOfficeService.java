package com.opinta.service;

import com.opinta.dto.PostOfficeDto;
import java.util.List;

public interface PostOfficeService {
    List<PostOfficeDto> getAll();
    PostOfficeDto getById(Long id);
    PostOfficeDto save(PostOfficeDto postOfficeDto);
    PostOfficeDto update(Long id, PostOfficeDto postOfficeDto);
    boolean delete(Long id);
}
