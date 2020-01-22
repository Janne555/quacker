package com.group5.quacker.services;

import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.OldUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private OldUserRepository oldUserRepository;

    public UserDetails loadUserByUsername(String name)
            throws UsernameNotFoundException {

        User user = oldUserRepository.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException(
                    "No user found with username: " + name);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPasswordHash(), true, true,
                true, true,
                getAuthorities(user.getRoles()));
    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}