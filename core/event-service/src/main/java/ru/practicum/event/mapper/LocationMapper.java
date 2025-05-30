package ru.practicum.event.mapper;


import org.mapstruct.Mapper;
import ru.practicum.dto.LocationDto;
import ru.practicum.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);

}
