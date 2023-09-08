package com.ram.testforgithubandcyclic.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity{



    @NotBlank(message = "Please Provide Tag Name")
    @Size(min = 2,max = 50,message = "Tag Name must be between 2 and 50 characters in length")
    private String categoryName;

}

