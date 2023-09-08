package com.ram.testforgithubandcyclic.repository;

import com.ram.testforgithubandcyclic.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {


    Optional<User> findByEmail(String email);


    Optional<User> findByEmailAndActive(String email,boolean active);

    Optional<User> findByIdAndActive(String id,boolean active);

    Optional<User> findByName(String Name);

    List<User> findByNameStartsWith(String name);

//    List<User> findByAgeBetween(Integer min,Integer max);

    Optional<User> findByPasswordResetToken(String passwordResetToken);


      Optional<User> findByPasswordResetTokenAndPasswordResetTokenExpiresAfter(String passwordResetToken,Date passwordResetTokenExpires);



}
