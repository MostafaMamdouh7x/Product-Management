package com.project.computers_store.mapper;
import com.project.computers_store.dto.ProductDTO;
import com.project.computers_store.model.Product;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductDTO dto);
    ProductDTO toDTO(Product entity);
}
