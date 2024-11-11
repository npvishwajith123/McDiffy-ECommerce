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

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PublicAPIController {

    private final ProductRepo productRepo;
    private final UserDetailsRepo userRepo;
    private final JwtUtility jwtUtility;

    @GetMapping("api/public/product/search")
    public ResponseEntity<?>  searchForProduct(@RequestParam(required = false) String keyword) {
        if(keyword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(null);
        }
        List<Product> products = productRepo.findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(keyword, keyword);
        return ResponseEntity.ok(products);
    }

    @PostMapping("api/public/login")
    public ResponseEntity<?> login(@RequestBody UserDetails user) {
        Optional<UserDetails> userDetails = userRepo.findByUsername(user.getUsername());
        if(userDetails.isPresent()) {
            if(user.getPassword().equalsIgnoreCase(userDetails.get().getPassword())) {
                return ResponseEntity.ok(jwtUtility.generateJwtToken(userDetails.get()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(null);
    }
}
