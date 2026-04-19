package com.project.computers_store.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductDTO {

    private long id;
    @NotEmpty(message = "name required ! ")
    @NotNull
    private String name;
    @NotEmpty(message = "category required ! ")
    private String category;
    @NotEmpty(message = "brand required ! ")
    private String brand;
     @Size(min = 10, max = 1000 ,message = "description min is 10 and max is 10000")
    private String description;
    @Min(value = 10,message = "price can not be less than 10  ")
    private double price;
  //  @NotBlank(message = "image is requered ")
    private String imageFileName;


}
