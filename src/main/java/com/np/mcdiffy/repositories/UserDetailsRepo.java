package com.np.mcdiffy.repositories;

import com.np.mcdiffy.entities.UserDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDetailsRepo extends CrudRepository<UserDetails, Integer> {

    Optional<UserDetails> findByUsername(String username);
}
