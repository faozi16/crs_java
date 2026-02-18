package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.FeedbackDto;
import com.af.carrsvt.entity.Feedback;

@Mapper(
    componentModel = "spring"
)
public interface FeedbackMapper {
    FeedbackDto feedbackToFeedbackDto(Feedback feedback);
    Feedback feedbackDtoToFeedback(FeedbackDto feedbackDto);
}
