package com.project.computers_store.controller;

import com.project.computers_store.dto.ProductDTO;
import com.project.computers_store.mapper.ProductMapper;
import com.project.computers_store.model.Product;
import com.project.computers_store.service.productService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/computers_store")
@RequiredArgsConstructor
public class productController {

    private final productService productService;
    @Value("${images.dir}")
   private   String uploadDir ;
    @GetMapping("")
    public String allProducts(Model model) {
        model.addAttribute("products", productService.selectAllProduct());
        return "products/list";
    }

    @GetMapping("product/{id}")
    public String getProduct(Model model, @PathVariable("id") long id) {
        ProductDTO product = productService.selectProduct(id);

        if (product == null) {
            System.out.println("product is nullllllllllllllllllllll "+ id );
            return "redirect:/computers_store";
        }

        model.addAttribute("product", product);
        return "products/productInfo";
    }

    @GetMapping("delete-all")
    public String deleteAll() {
        productService.deleteAllProduct();
        return "redirect:/computers_store";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/computers_store";
    }

    @GetMapping("product/new")
    public String showAddForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "products/addForm";
    }

    @GetMapping("product/edit/{id}")
    public String showAEditeForm(Model model, @PathVariable Long id) {
        model.addAttribute("product", productService.selectProduct(id));
        return "products/editeForm";
    }

    @PostMapping("product/save")
    public String saveProduct(@Valid @ModelAttribute("product") ProductDTO product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile) throws Exception {


        if (result.hasErrors()) {
            return "products/addForm";
        }

        if (!imageFile.isEmpty()) {
            String fileName = UUID.randomUUID()+Paths.get(imageFile.getOriginalFilename()).getFileName().toString().replace(" ", "_");
            product.setImageFileName(fileName);
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());
        } else {
            product.setImageFileName(productService.selectProduct(product.getId()).getImageFileName());
        }

        productService.addORUpdateProduct(product);

        return "redirect:/computers_store";
    }

    @GetMapping("image/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("id") long id) {
        Path filePath = Paths.get(uploadDir)
                .resolve(productService.selectProduct(id).getImageFileName());
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + System.currentTimeMillis() + resource.getFilename() + "\"")
                .body(resource);

    }

}
