package ua.ukrpost.mapper;

import ua.ukrpost.dto.PostcodePoolDto;
import ua.ukrpost.entity.PostcodePool;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostcodePoolMapper extends BaseMapper<PostcodePoolDto, PostcodePool> {
}
