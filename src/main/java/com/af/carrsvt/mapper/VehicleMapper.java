package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.VehicleDto;
import com.af.carrsvt.entity.Vehicle;

@Mapper(
    componentModel = "spring"
)
public interface VehicleMapper {
    VehicleDto vehicleToVehicleDto(Vehicle vehicle);
    Vehicle vehicleDtoToVehicle(VehicleDto vehicleDto);
}
