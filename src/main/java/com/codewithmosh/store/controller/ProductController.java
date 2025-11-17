package com.codewithmosh.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.dto.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("products")
public class ProductController {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;

    @GetMapping()
    public List<ProductDto> getAllProducts(){

        return productRepository.findAll()
                                .stream()
                                .map(productMapper::toDto)
                                .toList();
        
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
        @PathVariable Long id
    ){

        var product = productRepository.findById(id).orElse(null);

        if (product == null){
            return ResponseEntity.notFound().build();
        }

        var ProductDto = productMapper.toDto(product);

        return ResponseEntity.ok().body(ProductDto);

    }

    @PostMapping()
    public ResponseEntity<Product> newProduct(
        @RequestBody ProductDto productDto,
        UriComponentsBuilder uriBuilder
    ){
    
        var product = productMapper.toProduct(productDto);

        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

        if (category == null){
            return ResponseEntity.notFound().build();
        }

        product.setCategory(category);

        productRepository.save(product);

        var uri = uriBuilder.path("products/{id}").buildAndExpand(product.getId()).toUri();

       return  ResponseEntity.created(uri).body(product);

    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
        @RequestBody ProductDto productDto,
        @PathVariable Long id
    ){
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

        if (category == null){
            return ResponseEntity.notFound().build();
        }

        

        var product = productRepository.findById(id).orElse(null);

        if (product == null){
            return ResponseEntity.notFound().build();
        }

        product.setCategory(category);

        productMapper.updateProduct(productDto, product);

        productRepository.save(product);

        return ResponseEntity.ok().body(product);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable  Long id

    ){

        var product = productRepository.findById(id).orElse(null);

        if (product == null){
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);

        return ResponseEntity.ok().build();




    }




    

    


    
    
    
}
