package com.example.eclat.service;

import com.example.eclat.entities.SkinType;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.mapper.SkinTypeMapper;
import com.example.eclat.model.request.quiz.SkinTypeRequest;
import com.example.eclat.model.response.quiz.SkinTypeResponse;
import com.example.eclat.repository.SkinTypeRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<SkinTypeResponse> getSkinType() {
        return skinTypeRepository.findAll().stream()
                .map(skinTypeMapper::toSkinTypeResponse).toList();
    }

    public SkinTypeResponse updateSkinType(Long skintypeId, SkinTypeRequest request) {
        SkinType skinType = skinTypeRepository.findById(skintypeId).
                orElseThrow(() -> new RuntimeException("Không tìm thấy skin"));
        skinTypeMapper.updateSkinType(skinType, request);
        skinTypeRepository.save(skinType);
        return skinTypeMapper.toSkinTypeResponse(skinType);
    }
    public void deleteSkintype(Long skintypeId){
        SkinType skinType = skinTypeRepository.findById(skintypeId).
                orElseThrow(() -> new RuntimeException("Skin not found"));
        skinType.setStatus(false);
        skinTypeRepository.save(skinType);
    }

}
