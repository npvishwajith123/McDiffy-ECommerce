package com.np.mcdiffy.repositories;

import com.np.mcdiffy.entities.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends CrudRepository<Product, Integer> {

    List<Product> findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(String keyword1, String keyword2);
    Optional<List<Product>> findBySellerUsername(String username);
}
