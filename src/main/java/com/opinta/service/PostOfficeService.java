package com.opinta.service;

import com.opinta.dto.PostOfficeDto;
import java.util.List;

public interface PostOfficeService {
    List<PostOfficeDto> getAll();
    PostOfficeDto getById(long id);
    PostOfficeDto save(PostOfficeDto postOfficeDto);
    PostOfficeDto update(long id, PostOfficeDto postOfficeDto);
    boolean delete(long id);
}
