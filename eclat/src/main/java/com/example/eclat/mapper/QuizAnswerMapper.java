package com.example.eclat.mapper;

import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.model.response.QuizAnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface QuizAnswerMapper {

    @Mappings({
            @Mapping(source = "quizAnswer.quizQuestion.id", target = "questionId"),
            @Mapping(source = "quizAnswer.quizQuestion.questionText", target = "questionText"),
            @Mapping(source = "quizAnswer.skinType.id", target = "skinTypeId"),
            @Mapping(source = "quizAnswer.skinType.skinName", target = "skinName"),
            @Mapping(source = "quizAnswer.skinType.description", target = "skinDescription")
    })
    QuizAnswerResponse toQuizAnswerResponse(QuizAnswer quizAnswer);
}
