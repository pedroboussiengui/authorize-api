package org.example.auth;

import java.util.HashMap;
import java.util.Map;

import org.example.entity.User;

public class AuthorizationManager {
    private final Map<String, AuthorizationHandler> handlers = new HashMap<>();
    private final RoleChecker roleChecker;

    public AuthorizationManager(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public <T> void registerHandler(String useCase, AuthorizationHandler handler) {
        handlers.put(useCase, handler);
    }

    public <T> boolean authorize(String useCase, User user, T entity) {
        AuthorizationHandler<T> handler = (AuthorizationHandler<T>) handlers.get(useCase);
        if (handler == null) {
            throw new IllegalArgumentException("Caso de uso não registrado: " + useCase);
        }
        return handler.authorize(user, entity, roleChecker);
    }
}
