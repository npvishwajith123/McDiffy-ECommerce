package com.np.mcdiffy.configuration;

import com.np.mcdiffy.entities.Role;
import com.np.mcdiffy.repositories.UserDetailsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.np.mcdiffy.entities.UserDetails> user = userRepo.findByUsername(username);
        if(user.isPresent()) {
            return new User(user.get().getUsername(), user.get().getPassword(),
                    getUserRoles(user.get().getRoles()));
        }
        else {
            throw new BadCredentialsException("Invalid user details found for: "+username);
        }
    }

    private Collection<? extends GrantedAuthority> getUserRoles(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role: roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }
}
