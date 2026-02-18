package com.af.carrsvt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
