package com.ram.testforgithubandcyclic.mapper;

import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.dto.UserWithRoleNameDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleNameConverter extends AbstractConverter<User, UserWithRoleNameDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    protected UserWithRoleNameDto convert(User user) {

        //getting rolename from user
        String roleName = user.getRole().getRoleName();

        //creating new userDto
        UserWithRoleNameDto userWithRoleNameDto = new UserWithRoleNameDto();

        //setting rolename inside userDto
        userWithRoleNameDto.setRoleName(roleName);

        //mapping remaining fields of user using general modelmapper
        modelMapper.map(user,userWithRoleNameDto);

        //finally returning userDto
        return userWithRoleNameDto;
    }
}
