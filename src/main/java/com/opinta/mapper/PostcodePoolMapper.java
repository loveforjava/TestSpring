package com.opinta.mapper;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.model.PostcodePool;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostcodePoolMapper extends BaseMapper<PostcodePoolDto, PostcodePool> {
}
