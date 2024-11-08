package org.example.auth;

import java.util.List;

import org.example.entity.User;

public class AuthorizationHandler<T> {
    private final List<String> requiredRoles;
    private final List<PermissionCondition> permissionConditions;

    public AuthorizationHandler(List<String> requiredRoles, List<PermissionCondition> permissionConditions) {
        this.requiredRoles = requiredRoles;
        this.permissionConditions = permissionConditions;
    }

    public AuthorizationHandler(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
        this.permissionConditions = List.of();
    }

    public boolean authorize(User user, T entity, RoleChecker roleChecker) {
        boolean hasRole = roleChecker.hasRequiredRoles(user.getRoles(), requiredRoles.toArray(new String[0]));
        if (entity != null) {
            boolean hasPermission =  permissionConditions.stream().allMatch(condition -> condition.isPermitted(user, entity));
            return hasRole && hasPermission;
        }
        return hasRole;
    }
}
