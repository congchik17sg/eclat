package com.example.eclat.mapper;

import com.example.eclat.entities.User;
import com.example.eclat.model.request.UserCreationRequest;
import com.example.eclat.model.request.UserUpdateRequest;
import com.example.eclat.model.response.UserResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-14T12:35:50+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( request.getUsername() );
        user.setPassword( request.getPassword() );
        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );
        user.setAddress( request.getAddress() );
        user.setCreate_at( request.getCreate_at() );
        user.setUpdate_at( request.getUpdate_at() );
        user.setStatus( request.isStatus() );

        return user;
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
        userResponse.address( user.getAddress() );
        userResponse.create_at( user.getCreate_at() );
        userResponse.update_at( user.getUpdate_at() );
        userResponse.status( user.isStatus() );

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        user.setPassword( request.getPassword() );
        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );
        user.setAddress( request.getAddress() );
        user.setCreate_at( request.getCreate_at() );
        user.setUpdate_at( request.getUpdate_at() );
    }
}
