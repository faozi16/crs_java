package com.af.carrsvt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.af.carrsvt.entity.Driver;
import com.af.carrsvt.repository.DriverRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
    }

    public Driver updateDriver(Long driverId, Driver driverDetails) {
        Driver driver = getDriverById(driverId);
        driver.setUsername(driverDetails.getUsername());
        driver.setPassword(driverDetails.getPassword());
        driver.setEmail(driverDetails.getEmail());
        driver.setPhoneNumber(driverDetails.getPhoneNumber());
        driver.setLicenseDriver(driverDetails.getLicenseDriver());
        driver.setDateOfBirth(driverDetails.getDateOfBirth());
        driver.setPlaceOfBirth(driverDetails.getPlaceOfBirth());
        driver.setAddress(driverDetails.getAddress());
        driver.setStatus(driverDetails.getStatus());
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        driverRepository.delete(driver);
    }
}
