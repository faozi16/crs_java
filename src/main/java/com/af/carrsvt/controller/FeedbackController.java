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

import com.af.carrsvt.dto.FeedbackDto;
import com.af.carrsvt.entity.Feedback;
import com.af.carrsvt.mapper.FeedbackMapper;
import com.af.carrsvt.service.FeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @PostMapping
    public ResponseEntity<FeedbackDto> createFeedback(@jakarta.validation.Valid @RequestBody FeedbackDto feedbackDto) {
        Feedback entity = feedbackMapper.feedbackDtoToFeedback(feedbackDto);
        Feedback saved = feedbackService.saveFeedback(entity);
        return ResponseEntity.ok(feedbackMapper.feedbackToFeedbackDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<FeedbackDto>> getAllFeedbacks() {
        List<Feedback> list = feedbackService.getAllFeedbacks();
        List<FeedbackDto> dtos = list.stream().map(feedbackMapper::feedbackToFeedbackDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDto> getFeedbackById(@PathVariable Long id) {
        Feedback f = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedbackMapper.feedbackToFeedbackDto(f));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDto> updateFeedback(@PathVariable Long id, @jakarta.validation.Valid @RequestBody FeedbackDto feedbackDetails) {
        Feedback entity = feedbackMapper.feedbackDtoToFeedback(feedbackDetails);
        Feedback updated = feedbackService.updateFeedback(id, entity);
        return ResponseEntity.ok(feedbackMapper.feedbackToFeedbackDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
