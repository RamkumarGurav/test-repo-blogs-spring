package com.ram.testforgithubandcyclic.config;

import com.ram.testforgithubandcyclic.mapper.RoleNameConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {



    //general modelMapper - it can be used where there is no extra logic

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Bean
    public ModelMapper postMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelMapper userMapper(RoleNameConverter roleNameConverter) {
        ModelMapper modelMapper = new ModelMapper();

//---------without using converter-------------
//        TypeMap<User, UserDto> typeMap = modelMapper.createTypeMap(User.class, UserDto.class);
//        typeMap.addMappings(mapper -> {
//            mapper.map(src -> src.getRole().getRoleName(),UserDto::setRoleName );
//        });
//


//--------------with using converter----------------------
        modelMapper.addConverter(roleNameConverter); // Add the custom converter


        return modelMapper;
    }


//    @Bean
//    public ModelMapper productMapper(ProductWithNoOfReviewsConverter productWithNoOfReviewsConverter) {
//
//        ModelMapper modelMapper = new ModelMapper();
//
//
////--------------with using converter----------------------
//        // Add the custom converter
//        modelMapper.addConverter(productWithNoOfReviewsConverter);
//
//
//        return modelMapper;
//    }



//    @Bean
//    public ModelMapper postMapper(LikesCounterConverter likesCounterConverter){
//
//        ModelMapper modelMapper=new ModelMapper();
//
//        modelMapper.addConverter(likesCounterConverter);
//
//        return modelMapper;
//    }


}
