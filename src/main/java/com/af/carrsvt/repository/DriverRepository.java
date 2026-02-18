package com.af.carrsvt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
