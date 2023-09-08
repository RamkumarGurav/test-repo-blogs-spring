package com.ram.testforgithubandcyclic.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public abstract class BaseEntity {

    //it maps the with the _id of the parent object (so don't give explicit id with differentName like userId or postId in the parent entity)
    @Id
    private String id;

    private boolean deleted = false;

    private boolean updated = false;

    private  boolean active = true;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date createdAt = new Date();

    @LastModifiedDate
    private Date lastModifiedAt;

    @LastModifiedBy
    private String lastModifiedBy;



}
