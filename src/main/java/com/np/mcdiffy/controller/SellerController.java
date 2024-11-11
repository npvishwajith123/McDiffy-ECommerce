package com.np.mcdiffy.controller;

import com.np.mcdiffy.entities.Product;
import com.np.mcdiffy.entities.UserDetails;
import com.np.mcdiffy.repositories.ProductRepo;
import com.np.mcdiffy.repositories.UserDetailsRepo;
import com.np.mcdiffy.utilities.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SellerController {

    private final JwtUtility jwtUtility;
    private final UserDetailsRepo userRepo;
    private final ProductRepo productRepo;

    @GetMapping("/api/auth/seller/product")
    public ResponseEntity<?> getSellerProducts(@RequestHeader("JWT") String jwt) {
        String userName = jwtUtility.extractUserName(jwt);
        Optional<UserDetails> user = userRepo.findByUsername(userName);
        if (user.isPresent()) {


            Optional<List<Product>> product = productRepo.findBySellerUsername(user.get().getUsername());
            if (product.isPresent()) {
                return ResponseEntity.ok(product.get());
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Couldn't found any details for user");
    }

    @GetMapping("/api/auth/seller/product/{productId}")
    public ResponseEntity<?> getSellerProduct(@RequestHeader("JWT") String jwt,
                                              @PathVariable(value = "productId") String productId) {
        String userName = jwtUtility.extractUserName(jwt);
        Optional<UserDetails> user = userRepo.findByUsername(userName);
        if (user.isPresent()) {
            Optional<Product> prod = productRepo.findById(Integer.valueOf(productId));
            if (prod.isPresent() && prod.get().getSeller().getUsername().equalsIgnoreCase(userName)) {
                return ResponseEntity.ok(prod.get());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Couldn't found any details for user");
    }

    @PostMapping("/api/auth/seller/product")
    public ResponseEntity<?> saveProductToDb(@RequestHeader("JWT") String jwt, @RequestBody Product product) {
        String userName = jwtUtility.extractUserName(jwt);
        Optional<UserDetails> user = userRepo.findByUsername(userName);
        if (user.isPresent()) {
            product.setSeller(user.get());
            Product save = productRepo.save(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .location(URI.create("http://localhost/api/auth/seller/product/" + save.getProductId()))
                    .body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Couldn't found any details for user");
    }

    @PutMapping("/api/auth/seller/product")
    public ResponseEntity<?> updateProduct(@RequestHeader("JWT") String jwt, @RequestBody Product product) {
        String userName = jwtUtility.extractUserName(jwt);
        Optional<UserDetails> user = userRepo.findByUsername(userName);
        Optional<Product> byId = productRepo.findById(product.getProductId());
        if (user.isPresent() && byId.isPresent()) {
            product.setSeller(user.get());
            productRepo.save(product);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Updated Product In DB");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Couldn't found any details for user");
    }

    @DeleteMapping("/api/auth/seller/product/{productId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("JWT") String jwt,
                                           @PathVariable(value = "productId") String productId) {
        try {
            String userName = jwtUtility.extractUserName(jwt);
            Optional<UserDetails> user = userRepo.findByUsername(userName);
            if (user.isPresent()) {
                productRepo.deleteById(Integer.valueOf(productId));
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Deleted Product In DB");

            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Couldn't found any details for user");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Couldn't found any details for user");
        }
    }
}
