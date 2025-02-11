package com.example.eclat.mapper;


import com.example.eclat.entities.SkinType;
import com.example.eclat.model.request.quiz.SkinTypeRequest;
import com.example.eclat.model.response.quiz.SkinTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkinTypeMapper {

//    @Mapping(target = "id", source = "id")
    @Mapping(target = "skinName", source = "skinName")
    @Mapping(target = "description", source = "description")
    SkinType toSkinType(SkinTypeRequest request);

//    @Mapping(target = "id", source = "id")
    @Mapping(target = "skinName", source = "skinName")
    @Mapping(target = "description", source = "description")
    SkinTypeResponse toSkinTypeResponse(SkinType skinType);

    void updateSkinType(@MappingTarget SkinType skinType , SkinTypeRequest request);

}
