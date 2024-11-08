package org.example.auth;

import java.util.List;

public interface RoleChecker {
    boolean hasRequiredRoles(List<String> roles, String... requiredRoles);
}
