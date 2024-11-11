package com.np.mcdiffy.repositories;

import com.np.mcdiffy.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Integer> {
}
