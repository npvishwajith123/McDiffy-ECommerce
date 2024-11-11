package com.np.mcdiffy.repositories;

import com.np.mcdiffy.entities.CartProduct;
import org.springframework.data.repository.CrudRepository;

public interface CartProductRepo extends CrudRepository<CartProduct, Integer> {
}
