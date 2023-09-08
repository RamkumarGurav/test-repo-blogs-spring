package com.ram.testforgithubandcyclic.controller;

import com.ram.testforgithubandcyclic.collection.Role;
import com.ram.testforgithubandcyclic.dto.MsgRBody;
import com.ram.testforgithubandcyclic.dto.RBody;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.RoleRepository;
import com.ram.testforgithubandcyclic.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @PostMapping("/admin-protected/roles")
    public ResponseEntity<Object> createRole(@Valid @RequestBody Role role){

        Optional<Role> foundRoleOp=roleRepository.findByRoleName(role.getRoleName());
        if(foundRoleOp.isPresent()){
            throw new CustomException("This Role is Already Exists",HttpStatus.BAD_REQUEST);
        }

        Role newRole = roleService.createRole(role);


        RBody rbody = new RBody("success", newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(rbody);
    }


    @GetMapping("/admin-protected/roles/{id}")
    public ResponseEntity<Object> getSingleRole(@PathVariable("id") String id){

       Optional<Role> foundRoleOp = roleService.getSingleRole(id);

       if (!foundRoleOp.isPresent()){
           throw new CustomException("Role not found",HttpStatus.NOT_FOUND);
       }


        RBody rbody = new RBody("success", foundRoleOp.get());
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }


    @GetMapping("/admin-protected/roles")
    public ResponseEntity<Object> getAllRoles(){

        List<Role> roles = roleService.getAllRoles();


        RBody rbody = new RBody("success", roles);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    @DeleteMapping("/admin-protected/roles")
    public ResponseEntity<Object> permanentlyDeleteRole(@PathVariable("id") String id){

        Optional<Role>  foundRoleOp=roleService.getSingleRole(id);
        if (!foundRoleOp.isPresent()){
            throw new CustomException("Role not found",HttpStatus.NOT_FOUND);
        }


         roleService.permanentlyDeleteRole(id);


        MsgRBody rbody = new MsgRBody("success", "successfully deleted role with roleName: "+foundRoleOp.get().getRoleName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
    }




}
