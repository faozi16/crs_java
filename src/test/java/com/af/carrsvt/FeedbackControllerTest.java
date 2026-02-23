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

import com.af.carrsvt.controller.FeedbackController;
import com.af.carrsvt.dto.FeedbackDto;
import com.af.carrsvt.entity.Feedback;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.FeedbackMapper;
import com.af.carrsvt.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private FeedbackMapper feedbackMapper;

    @InjectMocks
    private FeedbackController feedbackController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createFeedback_shouldReturnSavedFeedback() throws Exception {
        FeedbackDto requestDto = new FeedbackDto();
        requestDto.setCustomerId(1L);
        requestDto.setReservationId(1L);
        requestDto.setRating(5);
        requestDto.setComments("Great service");

        Feedback entity = new Feedback();
        Feedback savedEntity = new Feedback();
        savedEntity.setFeedbackId(1L);

        FeedbackDto responseDto = new FeedbackDto();
        responseDto.setFeedbackId(1L);
        responseDto.setRating(5);

        when(feedbackMapper.feedbackDtoToFeedback(any(FeedbackDto.class))).thenReturn(entity);
        when(feedbackService.saveFeedback(entity)).thenReturn(savedEntity);
        when(feedbackMapper.feedbackToFeedbackDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/feedbacks")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbackId").value(1L));
    }

    @Test
    void updateFeedback_shouldReturnUpdatedFeedback() throws Exception {
        FeedbackDto requestDto = new FeedbackDto();
        requestDto.setCustomerId(1L);
        requestDto.setReservationId(1L);
        requestDto.setRating(4);
        requestDto.setComments("Good service");

        Feedback entity = new Feedback();
        Feedback updatedEntity = new Feedback();
        updatedEntity.setFeedbackId(1L);

        FeedbackDto responseDto = new FeedbackDto();
        responseDto.setFeedbackId(1L);
        responseDto.setRating(4);

        when(feedbackMapper.feedbackDtoToFeedback(any(FeedbackDto.class))).thenReturn(entity);
        when(feedbackService.updateFeedback(1L, entity)).thenReturn(updatedEntity);
        when(feedbackMapper.feedbackToFeedbackDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/feedbacks/1")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbackId").value(1L));
    }

    @Test
    void deleteFeedback_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/feedbacks/1"))
            .andExpect(status().isNoContent());

        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void createFeedback_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        FeedbackDto requestDto = new FeedbackDto();
        requestDto.setCustomerId(1L);
        requestDto.setReservationId(1L);
        requestDto.setRating(6);

        mockMvc.perform(post("/api/feedbacks")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateFeedback_shouldReturnNotFound_whenFeedbackMissing() throws Exception {
        FeedbackDto requestDto = new FeedbackDto();
        requestDto.setCustomerId(1L);
        requestDto.setReservationId(1L);
        requestDto.setRating(4);
        requestDto.setComments("Good service");

        Feedback entity = new Feedback();

        when(feedbackMapper.feedbackDtoToFeedback(any(FeedbackDto.class))).thenReturn(entity);
        when(feedbackService.updateFeedback(999L, entity)).thenThrow(new EntityNotFoundException("Feedback not found"));

        mockMvc.perform(put("/api/feedbacks/999")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteFeedback_shouldReturnNotFound_whenFeedbackMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Feedback not found"))
                .when(feedbackService).deleteFeedback(999L);

        mockMvc.perform(delete("/api/feedbacks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
