package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.af.carrsvt.controller.DriverController;
import com.af.carrsvt.dto.DriverDto;
import com.af.carrsvt.entity.Driver;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.DriverMapper;
import com.af.carrsvt.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverService driverService;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverController driverController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(driverController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createDriver_shouldReturnSavedDriver() throws Exception {
        DriverDto requestDto = new DriverDto();
        requestDto.setUsername("driver-1");
        requestDto.setPassword("secret1");
        requestDto.setEmail("driver1@example.com");
        requestDto.setLicenseDriver("DRV-123");

        Driver entity = new Driver();
        Driver savedEntity = new Driver();
        savedEntity.setDriverId(1L);

        DriverDto responseDto = new DriverDto();
        responseDto.setDriverId(1L);
        responseDto.setUsername("driver-1");
        responseDto.setEmail("driver1@example.com");

        when(driverMapper.driverDtoToDriver(any(DriverDto.class))).thenReturn(entity);
        when(driverService.saveDriver(entity)).thenReturn(savedEntity);
        when(driverMapper.driverToDriverDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/drivers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.driverId").value(1L));
    }

    @Test
    void updateDriver_shouldReturnUpdatedDriver() throws Exception {
        DriverDto requestDto = new DriverDto();
        requestDto.setUsername("driver-updated");
        requestDto.setPassword("secret1");
        requestDto.setEmail("driver.updated@example.com");
        requestDto.setLicenseDriver("DRV-999");

        Driver entity = new Driver();
        Driver updatedEntity = new Driver();
        updatedEntity.setDriverId(1L);

        DriverDto responseDto = new DriverDto();
        responseDto.setDriverId(1L);
        responseDto.setUsername("driver-updated");

        when(driverMapper.driverDtoToDriver(any(DriverDto.class))).thenReturn(entity);
        when(driverService.updateDriver(1L, entity)).thenReturn(updatedEntity);
        when(driverMapper.driverToDriverDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.driverId").value(1L))
            .andExpect(jsonPath("$.username").value("driver-updated"));
    }

    @Test
    void deleteDriver_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/drivers/1"))
            .andExpect(status().isNoContent());

        verify(driverService).deleteDriver(1L);
    }

    @Test
    void createDriver_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        DriverDto requestDto = new DriverDto();
        requestDto.setUsername("");
        requestDto.setPassword("123");
        requestDto.setEmail("invalid-email");
        requestDto.setLicenseDriver("");

        mockMvc.perform(post("/api/drivers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateDriver_shouldReturnNotFound_whenDriverMissing() throws Exception {
        DriverDto requestDto = new DriverDto();
        requestDto.setUsername("driver-updated");
        requestDto.setPassword("secret1");
        requestDto.setEmail("driver.updated@example.com");
        requestDto.setLicenseDriver("DRV-999");

        Driver entity = new Driver();

        when(driverMapper.driverDtoToDriver(any(DriverDto.class))).thenReturn(entity);
        when(driverService.updateDriver(999L, entity)).thenThrow(new EntityNotFoundException("Driver not found"));

        mockMvc.perform(put("/api/drivers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteDriver_shouldReturnNotFound_whenDriverMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Driver not found"))
                .when(driverService).deleteDriver(999L);

        mockMvc.perform(delete("/api/drivers/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
