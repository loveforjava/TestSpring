package com.opinta.service;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import java.util.List;

public interface PostcodePoolService {
    List<PostcodePoolDto> getAll();
    PostcodePoolDto getById(Long id);
    PostcodePoolDto save(PostcodePoolDto postcodePoolDto);
    PostcodePoolDto update(Long id, PostcodePoolDto postcodePoolDto);
    boolean delete(Long id);
    boolean addBarcodeInnerNumbers(long postcodeId, List<BarcodeInnerNumberDto> barcodeInnerNumberDtos);
}
