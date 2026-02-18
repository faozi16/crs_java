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

import com.af.carrsvt.dto.ReservationDto;
import com.af.carrsvt.entity.Reservation;
import com.af.carrsvt.mapper.ReservationMapper;
import com.af.carrsvt.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapper reservationMapper;

    @PostMapping("/create")
    public ResponseEntity<ReservationDto> createReservation(@jakarta.validation.Valid @RequestBody ReservationDto reservationDto) {
        Reservation entity = reservationMapper.reservationDtoToReservation(reservationDto);
        Reservation saved = reservationService.saveReservation(entity);
        return ResponseEntity.ok(reservationMapper.reservationToReservationDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<Reservation> list = reservationService.getAllReservations();
        List<ReservationDto> dtos = list.stream().map(reservationMapper::reservationToReservationDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        Reservation r = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.reservationToReservationDto(r));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ReservationDto reservationDetails) {
        Reservation entity = reservationMapper.reservationDtoToReservation(reservationDetails);
        Reservation updated = reservationService.updateReservation(id, entity);
        return ResponseEntity.ok(reservationMapper.reservationToReservationDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
