package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final List<ProductDto> PRODUCTS = Arrays.asList(
        new ProductDto(1, "Sepatu Running", 150000, "Sepatu olahraga ringan dan nyaman untuk lari sehari-hari", "Sepatu", "👟", 4.5, 50),
        new ProductDto(2, "Tas Ransel", 200000, "Tas ransel anti air dengan banyak kompartemen", "Tas", "🎒", 4.2, 30),
        new ProductDto(3, "Jam Tangan", 350000, "Jam tangan elegan dengan desain klasik modern", "Aksesoris", "⌚", 4.8, 20),
        new ProductDto(4, "Headphone", 250000, "Headphone wireless dengan kualitas suara jernih", "Elektronik", "🎧", 4.6, 40),
        new ProductDto(5, "Dompet Kulit", 120000, "Dompet kulit asli premium dengan banyak slot kartu", "Aksesoris", "👛", 4.3, 25)
    );

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return PRODUCTS;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable int id) {
        return PRODUCTS.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
