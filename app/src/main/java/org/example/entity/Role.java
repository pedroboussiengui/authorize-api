package org.example.entity;

public enum Role {
    ADMIN(1L, "admin"), GERENTE(2L, "gerente"), OPERADOR(3L, "operador");

    public final Long id;
    public final String nome;

    public static Role fromString(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("roleName cannot be null");
        }
        for (Role role : Role.values()) {
            if (role.nome.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + roleName);
    }

    Role(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
