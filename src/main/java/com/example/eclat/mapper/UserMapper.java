package com.example.eclat.mapper;


import com.example.eclat.entities.User;
import com.example.eclat.model.request.user.UserCreationRequest;
import com.example.eclat.model.request.user.UserUpdateEmailRequest;
import com.example.eclat.model.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);


    UserResponse toUserResponse(User user);

//    void updateUser(@MappingTarget  User user , UserUpdateRequest request);
//    không hoạt động được với kiểu list

//    @Mapping(target = "role", source = "roles", qualifiedByName = "listToSet")
    void updateUser(@MappingTarget User user, UserUpdateEmailRequest request);

//    @Named("listToSet")
//    default Set<String> mapListToSet(List<String> roles) {
//        return roles != null ? new HashSet<>(roles) : new HashSet<>();
//    }
}
