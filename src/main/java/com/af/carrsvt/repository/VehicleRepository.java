package com.af.carrsvt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
