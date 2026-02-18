package com.af.carrsvt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.af.carrsvt.entity.Reservation;
import com.af.carrsvt.repository.ReservationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
    }

    public Reservation updateReservation(Long reservationId, Reservation reservationDetails) {
        Reservation reservation = getReservationById(reservationId);
        reservation.setCustomerId(reservationDetails.getCustomerId());
        reservation.setVehicleId(reservationDetails.getVehicleId());
        reservation.setPickupTime(reservationDetails.getPickupTime());
        reservation.setPickupLocation(reservationDetails.getPickupLocation());
        reservation.setDropoffLocation(reservationDetails.getDropoffLocation());
        reservation.setStatus(reservationDetails.getStatus());
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);
        reservationRepository.delete(reservation);
    }
}
