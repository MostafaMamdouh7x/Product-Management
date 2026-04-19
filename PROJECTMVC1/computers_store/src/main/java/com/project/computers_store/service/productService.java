package com.project.computers_store.service;

import com.project.computers_store.dto.ProductDTO;
import com.project.computers_store.dto.ProductDTO;
import com.project.computers_store.exception.ResourceNotFoundException;
import com.project.computers_store.mapper.ProductMapper;
import com.project.computers_store.model.Product;
import com.project.computers_store.repository.productRebository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "products")
@Slf4j
public class productService {
    private final productRebository repository;
    private final ProductMapper productMapper;
//    Logger log= LoggerFactory.getLogger(productService.class);
    @Value("${images.dir}")
    private String uploadDir;

    @CacheEvict(key = "#root.methodName", allEntries = true)
    @CachePut(key = "#root.methodName" )
    public ProductDTO addORUpdateProduct(ProductDTO productDto)
    {

        Product product=productMapper.toEntity(productDto);
        Date date = new Date(System.currentTimeMillis());
        product.setCreate_at(String.valueOf(date));
        repository.save(product);
        log.info("use added  ...  at  :{}", product.getCreate_at());
        log.warn("Return value of the method is never used ---- ");
        return productDto;

    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'selectAllProduct'")
    })
    public void deleteProduct(long id)
    {

        String imgPath =uploadDir+this.selectProduct(id).getImageFileName();
        try {
            Files.delete(Paths.get(imgPath));
        } catch (IOException e) {
            throw new RuntimeException("delete is faild .. . "+e);
        }
        repository.deleteById(id);
    }

    @CacheEvict(key = "#root.methodName", allEntries = true )
    public void deleteAllProduct()
    {


        File imgsDir=new File(uploadDir);
        if(imgsDir.exists() && imgsDir.isDirectory()) {
            File[] files = imgsDir.listFiles();
            if(files != null){
                for(File file : files){
                    file.delete();
                }
            }
        repository.deleteAll();
    }
    }

 @Cacheable( key = "#id" ,sync = true)
    public ProductDTO selectProduct(long id)
    {
        Optional<Product> product=repository.findById(id);
        if(product.isPresent()){
            return productMapper.toDTO(product.get());
        }
        else {throw  new ResourceNotFoundException("product not found with id   :-->  "+id);}

    }

    @Cacheable(key = "#root.methodName" ,sync = true)
    public List<ProductDTO> selectAllProduct()
    {
        return  repository.findAll().stream().map(productMapper::toDTO).toList();
    }


//  @Scheduled(fixedRate = 1000)
//  //@Async
//    public void runTask() throws InterruptedException {
//      System.out.println("start -- ... "+Thread.currentThread().getName());
//        Thread.sleep(10000);
//        log.info("Running Scheduling taskkk ...  -->>  {},{} ", System.currentTimeMillis(),Thread.currentThread().getName());
//    }


//    @Async
//    public void runTaskAsync() {
//        log.info("Running Async __ tasks ...  -->> {}", System.currentTimeMillis());
//    }





}
