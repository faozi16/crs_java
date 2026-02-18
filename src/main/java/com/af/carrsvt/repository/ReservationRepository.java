package com.af.carrsvt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
