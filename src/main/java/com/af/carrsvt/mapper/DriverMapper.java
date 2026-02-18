package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.DriverDto;
import com.af.carrsvt.entity.Driver;

@Mapper(
    componentModel = "spring"
)
public interface DriverMapper {
    DriverDto driverToDriverDto(Driver driver);
    Driver driverDtoToDriver(DriverDto driverDto);
}
