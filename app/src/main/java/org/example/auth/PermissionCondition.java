package org.example.auth;

import org.example.entity.User;

public interface PermissionCondition<T> {
    boolean isPermitted(User user, T entity);
}
