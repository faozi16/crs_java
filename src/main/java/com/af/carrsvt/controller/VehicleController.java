package com.af.carrsvt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.carrsvt.dto.VehicleDto;
import com.af.carrsvt.entity.Vehicle;
import com.af.carrsvt.mapper.VehicleMapper;
import com.af.carrsvt.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleMapper vehicleMapper;

    @PostMapping("/create")
    public ResponseEntity<VehicleDto> createVehicle(@jakarta.validation.Valid @RequestBody VehicleDto vehicleDto) {
        Vehicle entity = vehicleMapper.vehicleDtoToVehicle(vehicleDto);
        Vehicle saved = vehicleService.saveVehicle(entity);
        return ResponseEntity.ok(vehicleMapper.vehicleToVehicleDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        List<Vehicle> list = vehicleService.getAllVehicles();
        List<VehicleDto> dtos = list.stream().map(vehicleMapper::vehicleToVehicleDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        Vehicle v = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicleMapper.vehicleToVehicleDto(v));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable Long id, @jakarta.validation.Valid @RequestBody VehicleDto vehicleDetails) {
        Vehicle entity = vehicleMapper.vehicleDtoToVehicle(vehicleDetails);
        Vehicle updated = vehicleService.updateVehicle(id, entity);
        return ResponseEntity.ok(vehicleMapper.vehicleToVehicleDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
