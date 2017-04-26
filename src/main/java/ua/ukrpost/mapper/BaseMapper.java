package ua.ukrpost.mapper;

import java.util.List;
import org.mapstruct.InheritInverseConfiguration;

/**
 * Base dto mapper
 *
 * @param <Dto>    type of Dto
 * @param <Entity> type of Entity
 */
public interface BaseMapper<Dto, Entity> {

    Dto toDto(Entity entity);

    List<Dto> toDto(List<Entity> entities);

    @InheritInverseConfiguration
    Entity toEntity(Dto dto);

    List<Entity> toEntity(List<Dto> dtos);
}
