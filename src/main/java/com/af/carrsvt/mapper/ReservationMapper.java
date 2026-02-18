package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.ReservationDto;
import com.af.carrsvt.entity.Reservation;

@Mapper(
    componentModel = "spring"
)
public interface ReservationMapper {
    ReservationDto reservationToReservationDto(Reservation reservation);
    Reservation reservationDtoToReservation(ReservationDto reservationDto);
}
