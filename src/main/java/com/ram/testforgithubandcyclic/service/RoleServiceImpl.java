package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Role;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {

        Optional<Role> roleOptional = roleRepository.findByRoleName(role.getRoleName());

        if(roleOptional.isPresent()){
            throw new CustomException("This Role has already Created", HttpStatus.BAD_REQUEST);
        }

        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getActiveSingleRole(String id, boolean active) {
        return roleRepository.findByIdAndActive(id,active);
    }

    @Override
    public Optional<Role> getSingleRole(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public void permanentlyDeleteRole(String id) {
         roleRepository.deleteById(id);
    }
}
