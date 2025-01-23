package com.example.eclat.service;

import com.example.eclat.entities.SkinType;
import com.example.eclat.mapper.SkinTypeMapper;
import com.example.eclat.model.request.quiz.SkinTypeRequest;
import com.example.eclat.model.response.SkinTypeResponse;
import com.example.eclat.repository.SkinTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SkinTypeService {

    @Autowired
    SkinTypeRepository skinTypeRepository;

    @Autowired
    SkinTypeMapper skinTypeMapper;

    public SkinTypeResponse createSkinType(SkinTypeRequest request) {
//        if (quizQuestionRepository.existsByQuestionText(request.getQuestion_text()))
//            throw new AppException(ErrorCode.USER_EXISTED);

        SkinType skinType = skinTypeMapper.toSkinType(request);
        skinType = skinTypeRepository.save(skinType);

        return skinTypeMapper.toSkinTypeResponse(skinType);
    }

}
