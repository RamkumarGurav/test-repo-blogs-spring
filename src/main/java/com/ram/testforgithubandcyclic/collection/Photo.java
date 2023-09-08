package com.ram.testforgithubandcyclic.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "photos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Photo {


    private String title;
    //type is Binary bcz we store the photo's data in binary form in mongodb
    private Binary photo;
}
