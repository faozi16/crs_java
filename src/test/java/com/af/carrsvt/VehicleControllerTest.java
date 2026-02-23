package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

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

import com.af.carrsvt.controller.VehicleController;
import com.af.carrsvt.dto.VehicleDto;
import com.af.carrsvt.entity.Vehicle;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.VehicleMapper;
import com.af.carrsvt.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleController vehicleController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createVehicle_shouldReturnSavedVehicle() throws Exception {
        VehicleDto requestDto = new VehicleDto();
        requestDto.setDriverId(1L);
        requestDto.setVehicleType("SUV");
        requestDto.setLicensePlate("B1234CD");

        Vehicle entity = new Vehicle();
        Vehicle savedEntity = new Vehicle();
        savedEntity.setVehicleId(1L);

        VehicleDto responseDto = new VehicleDto();
        responseDto.setVehicleId(1L);
        responseDto.setVehicleType("SUV");

        when(vehicleMapper.vehicleDtoToVehicle(any(VehicleDto.class))).thenReturn(entity);
        when(vehicleService.saveVehicle(entity)).thenReturn(savedEntity);
        when(vehicleMapper.vehicleToVehicleDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/vehicles/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.vehicleId").value(1L));
    }

    @Test
    void updateVehicle_shouldReturnUpdatedVehicle() throws Exception {
        VehicleDto requestDto = new VehicleDto();
        requestDto.setDriverId(1L);
        requestDto.setVehicleType("SEDAN");
        requestDto.setLicensePlate("B9999ZZ");

        Vehicle entity = new Vehicle();
        Vehicle updatedEntity = new Vehicle();
        updatedEntity.setVehicleId(1L);

        VehicleDto responseDto = new VehicleDto();
        responseDto.setVehicleId(1L);
        responseDto.setVehicleType("SEDAN");

        when(vehicleMapper.vehicleDtoToVehicle(any(VehicleDto.class))).thenReturn(entity);
        when(vehicleService.updateVehicle(1L, entity)).thenReturn(updatedEntity);
        when(vehicleMapper.vehicleToVehicleDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/vehicles/1")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.vehicleId").value(1L));
    }

    @Test
    void deleteVehicle_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1"))
            .andExpect(status().isNoContent());

        verify(vehicleService).deleteVehicle(1L);
    }

    @Test
    void createVehicle_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        VehicleDto requestDto = new VehicleDto();
        requestDto.setDriverId(1L);
        requestDto.setVehicleType("");
        requestDto.setLicensePlate("");

        mockMvc.perform(post("/api/vehicles/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateVehicle_shouldReturnNotFound_whenVehicleMissing() throws Exception {
        VehicleDto requestDto = new VehicleDto();
        requestDto.setDriverId(1L);
        requestDto.setVehicleType("SEDAN");
        requestDto.setLicensePlate("B9999ZZ");

        Vehicle entity = new Vehicle();

        when(vehicleMapper.vehicleDtoToVehicle(any(VehicleDto.class))).thenReturn(entity);
        when(vehicleService.updateVehicle(999L, entity)).thenThrow(new EntityNotFoundException("Vehicle not found"));

        mockMvc.perform(put("/api/vehicles/999")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteVehicle_shouldReturnNotFound_whenVehicleMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Vehicle not found"))
                .when(vehicleService).deleteVehicle(999L);

        mockMvc.perform(delete("/api/vehicles/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
