package com.np.mcdiffy.repositories;

import com.np.mcdiffy.entities.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepo extends CrudRepository<Cart, Integer> {

    Optional<Cart> findByUserUsername(String username);
}
