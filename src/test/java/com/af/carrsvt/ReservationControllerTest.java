package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import com.af.carrsvt.controller.ReservationController;
import com.af.carrsvt.dto.ReservationDto;
import com.af.carrsvt.entity.Reservation;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.ReservationMapper;
import com.af.carrsvt.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReservation_shouldReturnSavedReservation() throws Exception {
        ReservationDto requestDto = new ReservationDto();
        requestDto.setCustomerId(1L);
        requestDto.setVehicleId(1L);
        requestDto.setPickupTime(OffsetDateTime.parse("2026-02-20T10:00:00Z"));
        requestDto.setPickupLocation("Point A");
        requestDto.setDropoffLocation("Point B");

        Reservation entity = new Reservation();
        Reservation savedEntity = new Reservation();
        savedEntity.setReservationId(1L);

        ReservationDto responseDto = new ReservationDto();
        responseDto.setReservationId(1L);
        responseDto.setPickupLocation("Point A");

        when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(entity);
        when(reservationService.saveReservation(entity)).thenReturn(savedEntity);
        when(reservationMapper.reservationToReservationDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/reservations/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1L));
    }

    @Test
    void updateReservation_shouldReturnUpdatedReservation() throws Exception {
        ReservationDto requestDto = new ReservationDto();
        requestDto.setCustomerId(1L);
        requestDto.setVehicleId(1L);
        requestDto.setPickupTime(OffsetDateTime.parse("2026-02-20T12:00:00Z"));
        requestDto.setPickupLocation("Point C");
        requestDto.setDropoffLocation("Point D");

        Reservation entity = new Reservation();
        Reservation updatedEntity = new Reservation();
        updatedEntity.setReservationId(1L);

        ReservationDto responseDto = new ReservationDto();
        responseDto.setReservationId(1L);
        responseDto.setPickupLocation("Point C");

        when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(entity);
        when(reservationService.updateReservation(1L, entity)).thenReturn(updatedEntity);
        when(reservationMapper.reservationToReservationDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/reservations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1L));
    }

    @Test
    void deleteReservation_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/reservations/1"))
            .andExpect(status().isNoContent());

        verify(reservationService).deleteReservation(1L);
    }

    @Test
    void createReservation_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        ReservationDto requestDto = new ReservationDto();
        requestDto.setPickupLocation("");
        requestDto.setDropoffLocation("");

        mockMvc.perform(post("/api/reservations/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateReservation_shouldReturnNotFound_whenReservationMissing() throws Exception {
        ReservationDto requestDto = new ReservationDto();
        requestDto.setCustomerId(1L);
        requestDto.setVehicleId(1L);
        requestDto.setPickupTime(OffsetDateTime.parse("2026-02-20T12:00:00Z"));
        requestDto.setPickupLocation("Point C");
        requestDto.setDropoffLocation("Point D");

        Reservation entity = new Reservation();

        when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(entity);
        when(reservationService.updateReservation(999L, entity)).thenThrow(new EntityNotFoundException("Reservation not found"));

        mockMvc.perform(put("/api/reservations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteReservation_shouldReturnNotFound_whenReservationMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Reservation not found"))
                .when(reservationService).deleteReservation(999L);

        mockMvc.perform(delete("/api/reservations/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
