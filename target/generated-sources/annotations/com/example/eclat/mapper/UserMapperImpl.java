package com.example.eclat.mapper;

import com.example.eclat.entities.User;
import com.example.eclat.model.request.UserCreationRequest;
import com.example.eclat.model.request.UserUpdateRequest;
import com.example.eclat.model.response.UserResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-14T17:43:20+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
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
        user.address( request.getAddress() );
        user.create_at( request.getCreate_at() );
        user.update_at( request.getUpdate_at() );
        user.status( request.isStatus() );

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
        userResponse.address( user.getAddress() );
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
