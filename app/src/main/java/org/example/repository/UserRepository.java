package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.entity.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
