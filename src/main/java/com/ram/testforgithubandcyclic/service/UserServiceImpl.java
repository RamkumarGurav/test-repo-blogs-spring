package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Role;
import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.dto.UserDto;
import com.ram.testforgithubandcyclic.dto.UserWithRoleNameDto;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.RoleRepository;
import com.ram.testforgithubandcyclic.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService{


    @Autowired
    public UserRepository userRepository;



    @Autowired
    public RoleRepository roleRepository;


    @Autowired
    public PasswordEncoder passwordEncoder;


    @Autowired
    public MongoTemplate mongoTemplate;

    @Autowired
    private ModelMapper userMapper;

    @Override
    public User createUser(User user) {

        Optional<User> userOptional =userRepository.findByEmail(user.getEmail());

        if(userOptional.isPresent()){
            throw new CustomException("This email already has an account,use different Email",HttpStatus.BAD_REQUEST);
        }

        Optional<Role> role = roleRepository.findByRoleName("USER");

        if(!role.isPresent()){
           throw new CustomException("Role not Found", HttpStatus.NOT_FOUND);
        }
        user.setRole(role.get());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(null);

        //making  LastModifiedAt and LastModifiedBy null when user is created for the the firstTime
        user.setLastModifiedAt(null);
        user.setLastModifiedBy(null);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersStartsWith(String name) {
        return userRepository.findByNameStartsWith(name);
    }

    @Override
    public Optional<User> getSingleUser(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getSingleActiveUserById(String userId, boolean active) {
        return userRepository.findByIdAndActive(userId,active);
    }

    @Override
    public Optional<User> getSingleActiveUserByEmail(String email, boolean active) {
        return userRepository.findByEmailAndActive(email,active);
    }

    @Override
    public Optional<User> getSingleUserByEmailId(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User register(User user) {

        Optional<User> userOptional =userRepository.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            throw new CustomException("This email already has an account,use different Email",HttpStatus.BAD_REQUEST);
        }
        Optional<Role> role = roleRepository.findByRoleName("ROLE_USER");

        if(!role.isPresent()){
            throw new CustomException("Role not Found", HttpStatus.NOT_FOUND);
        }

        user.setRole(role.get());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(null);

        //making  LastModifiedAt and LastModifiedBy null when user is created for the the firstTime
        user.setLastModifiedAt(null);
        user.setLastModifiedBy(null);

        User newUser= userRepository.save(user);

        return newUser;
    }

    @Override
    public User registerUserByAdmin(User user){
        Optional<User> userOptional =userRepository.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            throw new CustomException("This email already has an account,use different Email",HttpStatus.BAD_REQUEST);
        }

        Optional<Role> role = roleRepository.findByRoleName("ROLE_USER");
        if(!role.isPresent()){
            throw new CustomException("Role not Found", HttpStatus.NOT_FOUND);
        }

        user.setRole(role.get());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(null);

        //making  LastModifiedAt and LastModifiedBy null when user is created for the the firstTime
        user.setLastModifiedAt(null);
        user.setLastModifiedBy(null);

        User newUser= userRepository.save(user);
        return newUser;
    }



    @Override
    public User registerAdmin(User user) {

        Optional<User> userOptional =userRepository.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            throw new CustomException("This email already has an account,use different Email",HttpStatus.BAD_REQUEST);
        }

        Optional<Role> role = roleRepository.findByRoleName("ROLE_ADMIN");
        if(!role.isPresent()){
            throw new CustomException("Role not Found", HttpStatus.NOT_FOUND);
        }

        user.setRole(role.get());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(null);


        //making  LastModifiedAt and LastModifiedBy null when user is created for the the firstTime
        user.setLastModifiedAt(null);
        user.setLastModifiedBy(null);

        User newUser= userRepository.save(user);
        return newUser;
    }

    @Override
    public Page<User> search(String name, String email, String gender, String mobile, List<String> fields, String role, Pageable pageable) {

        Query query = new Query().with(pageable);

        //excluding and including some fields

        if (fields !=null && fields.size()>0 && !fields.isEmpty()){
            List<String> excludeFields=new ArrayList<>();
            List<String> includeFields=new ArrayList<>();

            for(String field : fields){
                if(field.startsWith("-")){
                    excludeFields.add(field.substring(1));
                }else{
                    includeFields.add(field);
                }
            }
            if (excludeFields.size()>0){
                //if they are any excludeFields then , add the logic to query
                log.info("********: excludeFields: "+excludeFields.toString());
                query.fields().exclude(excludeFields.toArray(new String[excludeFields.size()]));
            }
            if(includeFields.size()>0){
                //if they are any includeFields then , add the logic to query
                log.info("********: includeFields: "+includeFields.toString());
                query.fields().include(includeFields.toArray(new String[includeFields.size()]));
            }
        }




        //here we used ArrayList bcz ArrayList is flexible with the length but Array has fixed length,
        //bcz based on conditions we are adding the criteria to the list,if array we can't do this incremental addition of criteria
        List<Criteria> criteria = new ArrayList<>();


        // Check if a search term "name" is provided and add a name-based regex criteria
        if (name != null && !name.isEmpty()) {
            criteria.add(Criteria.where("name").regex(name, "i"));
        }

        if (email != null && !email.isEmpty()) {
            criteria.add(Criteria.where("email").regex(email, "i"));
        }

        if (gender != null && !gender.isEmpty()) {
            criteria.add(Criteria.where("gender").regex(gender, "i"));
        }

        if (mobile != null && !mobile.isEmpty()) {
            criteria.add(Criteria.where("mobile").is(mobile));
        }



        // If any criteria are provided, combine them using an AND operator and add to the query
        if (!criteria.isEmpty()) {

            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));

        }






        Page<User> usersPage = PageableExecutionUtils.getPage(
                mongoTemplate.find(query,User.class),//getPage method needs first parameter as list of resultants docs
                pageable,//2nd parameter is pageable object for paging
                ()->mongoTemplate.count(query.skip(0).limit(0),User.class)
                //3rd paramter is counting all resultant products
                // the combination of skip(0) and limit(0) with a value of zero is a technique used to optimize the
                // performance of the count operation when you're only interested in the total count of matching
                // documents and not in retrieving the actual data.
        );



        return usersPage;


    }


    @Override
    public User updateUser(User user) {

        user.setUpdated(true);
        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(User user) {
        user.setActive(false);
        user.setDeleted(false);
        user.setUpdated(true);
        return userRepository.save(user);
    }

    @Override
    public User deleteUser(User user) {
        user.setActive(false);
        user.setDeleted(true);
        user.setUpdated(true);
        return userRepository.save(user);
    }



    @Override
    public void  permanentlyDeleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    @Override
    public User undeleteUserButNotActive(User user) {
        user.setActive(false);
        user.setDeleted(false);
        user.setUpdated(true);

        return userRepository.save(user);
    }


    @Override
    public User activateUser(User user) {
        user.setActive(true);
        user.setDeleted(false);
        user.setUpdated(true);
        return userRepository.save(user);
    }


//--------------DTO conversion----------------------------------------------------
    @Override
    public UserDto userToUserDto(User user){
      return  userMapper.map(user, UserDto.class);
    }


    @Override
    public UserWithRoleNameDto userToUserDtoWithRoleName(User user){
        return  userMapper.map(user, UserWithRoleNameDto.class);
    }

    @Override
    public List<UserWithRoleNameDto> usersToUserDtosWithRoleName(List<User> users) {
        return users.stream().map(user->userMapper.map(user,UserWithRoleNameDto.class)).collect(Collectors.toList());
    }



//    @Override
//    public User userDtoToUser(UserDto userDto){
//        return  userMapper.map(userDto,User.class);
//    }


}
