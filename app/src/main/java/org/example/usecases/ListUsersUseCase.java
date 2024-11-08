package org.example.usecases;

import java.util.List;

import org.example.config.DatabaseConfig;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.repository.UserRepositoryJdbc;

public class ListUsersUseCase {
    DatabaseConfig db = new DatabaseConfig();
    UserRepository userRepositoryJdbc = new UserRepositoryJdbc(db);
    
    public List<User> execute() {
        return userRepositoryJdbc.findAll();
    }
}
