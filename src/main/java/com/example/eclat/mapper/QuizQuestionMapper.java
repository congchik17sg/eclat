package com.example.eclat.mapper;


import com.example.eclat.entities.QuizQuestion;
import com.example.eclat.model.request.quiz.QuizQuestionRequest;
import com.example.eclat.model.request.quiz.QuizQuestionUpdateRequest;
import com.example.eclat.model.response.quiz.QuizQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface QuizQuestionMapper {

//    @Mapping(target = "questionText", source = "question_text")
//    //@Mapping(target = "createAt", source = "create_at")
//    //@Mapping(target = "updateAt", source = "update_at")
//    QuizQuestion toQuizQuestion(QuizQuestionRequest request);

    @Mapping(target = "question_text", source = "questionText") // Đảm bảo ánh xạ đúng tên
    @Mapping(target = "create_at", source = "createAt")
    @Mapping(target = "update_at", source = "updateAt")
    QuizQuestionResponse toQuizQuestionResponse(QuizQuestion quizQuestion);

//    @Mapping(target = "questionText", source = "question_text")
//    @Mapping(target = "updateAt", source = "update_at")
//    void updateQuizQuestion(@MappingTarget QuizQuestion quizQuestion, QuizQuestionUpdateRequest request);

}
