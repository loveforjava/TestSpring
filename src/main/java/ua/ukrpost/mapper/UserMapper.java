package ua.ukrpost.mapper;

import ua.ukrpost.dto.UserDto;
import ua.ukrpost.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserDto, User> {
    
    @Override
    @Mappings({
            @Mapping(source = "counterparty.uuid", target = "counterpartyUuid")})
    UserDto toDto(User user);

    @Override
    @Mappings({
            @Mapping(source = "counterpartyUuid", target = "counterparty.uuid")})
    User toEntity(UserDto userDto);
}
