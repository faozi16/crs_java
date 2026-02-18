package com.af.carrsvt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.af.carrsvt.entity.Feedback;
import com.af.carrsvt.repository.FeedbackRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId).orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
    }

    public Feedback updateFeedback(Long feedbackId, Feedback feedbackDetails) {
        Feedback feedback = getFeedbackById(feedbackId);
        feedback.setCustomerId(feedbackDetails.getCustomerId());
        feedback.setReservationId(feedbackDetails.getReservationId());
        feedback.setRating(feedbackDetails.getRating());
        feedback.setComments(feedbackDetails.getComments());
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = getFeedbackById(feedbackId);
        feedbackRepository.delete(feedback);
    }
}
