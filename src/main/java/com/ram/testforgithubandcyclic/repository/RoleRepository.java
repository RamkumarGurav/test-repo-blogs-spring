package com.ram.testforgithubandcyclic.repository;

import com.ram.testforgithubandcyclic.collection.Role;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role,String> {

    Optional<Role>  findByRoleName(String roleName);

    Optional<Role> findByIdAndActive(String id, boolean active);

}
