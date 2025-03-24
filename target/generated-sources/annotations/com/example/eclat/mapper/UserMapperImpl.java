package com.example.eclat.mapper;

import com.example.eclat.entities.User;
import com.example.eclat.model.request.user.UserCreationRequest;
import com.example.eclat.model.request.user.UserUpdateEmailRequest;
import com.example.eclat.model.response.user.UserResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-24T21:55:05+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( request.getUsername() );
        user.password( request.getPassword() );
        user.email( request.getEmail() );
        user.phone( request.getPhone() );
        user.create_at( request.getCreate_at() );
        user.update_at( request.getUpdate_at() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.username( user.getUsername() );
        userResponse.password( user.getPassword() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.create_at( user.getCreate_at() );
        userResponse.update_at( user.getUpdate_at() );
        userResponse.status( user.isStatus() );
        Set<String> set = user.getRole();
        if ( set != null ) {
            userResponse.role( new LinkedHashSet<String>( set ) );
        }

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserUpdateEmailRequest request) {
        if ( request == null ) {
            return;
        }

        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );
        user.setUpdate_at( request.getUpdate_at() );
    }
}
