package org.example.config;

import java.util.List;

import org.example.auth.AuthorizationHandler;
import org.example.auth.AuthorizationManager;
import org.example.auth.SimpleRoleChecker;

public class AuthorizationConfig {

    private final AuthorizationManager authManager;

    public AuthorizationConfig() {
        this.authManager = new AuthorizationManager(new SimpleRoleChecker());
        registerHandlers();
    }

    private void registerHandlers() {
        authManager.registerHandler("register user", new AuthorizationHandler<>(
            List.of("admin")
        ));

        authManager.registerHandler("grant role", new AuthorizationHandler<>(
            List.of("admin")
        ));

        authManager.registerHandler("revoke role", new AuthorizationHandler<>(
            List.of("admin")
        ));

        authManager.registerHandler("register unidade", new AuthorizationHandler<>(
            List.of("gerente")
        ));
        
        authManager.registerHandler("registrar produto", new AuthorizationHandler<>(
            List.of("gerente", "operador"),
            List.of((user, unidade) -> user.getUnidades().contains(unidade))
        ));
    }

    public AuthorizationManager getAuthManager() {
        return authManager;
    }
}