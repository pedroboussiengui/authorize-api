package org.example.usecases;

import java.util.Optional;

import org.example.config.DatabaseConfig;
import org.example.dto.GenericResDTO;
import org.example.dto.UserRegisterReqDTO;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.repository.UserRepositoryJdbc;

public class UserRegisterUseCase {
    DatabaseConfig db = new DatabaseConfig();
    UserRepository userRepositoryJdbc = new UserRepositoryJdbc(db);
    
    public GenericResDTO execute(UserRegisterReqDTO input) {
        GenericResDTO output = new GenericResDTO();

        // check if user already exists
        Optional<User> existinguser = userRepositoryJdbc.findByEmail(input.getEmail());
        if (existinguser.isPresent()) {
            output.setHttpStatus(409);
            output.setMessage("User with this email already registered!");
            output.setSuccess(false);
            return output;
        }

        var newuser = new User(input.getEmail(), input.getName(), input.getRoles());
        
        if (newuser.validate()) {
            userRepositoryJdbc.save(newuser);
            output.setHttpStatus(200);
            output.setMessage("User succesfully registered!");
            output.setSuccess(true);
            return output;
        }
        output.setHttpStatus(400);
        output.setMessage("Some erros was found in request!");
        output.setSuccess(false);
        return output;
}
}
