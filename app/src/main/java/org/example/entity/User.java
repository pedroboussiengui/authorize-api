package org.example.entity;
import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String email;
    private String name;
    private List<Unidade> unidades;
    private List<String> roles;

    public User(Long id, String email, String name, List<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.unidades = new ArrayList<>();
    }

    public User(String email, String name, List<String> roles) {
        this.id = null;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.unidades = new ArrayList<>();
    }

    public boolean validate() {
        return validateName() && validateEmail();
    }

    private boolean validateName() {
        return this.name.length() >= 3;
    }

    private boolean validateEmail() {
        return this.email.contains("@");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void addUnidade(Unidade unidade) {
        this.unidades.add(unidade);
    }

    public List<Unidade> getUnidades() {
        return unidades;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", unidades=" + unidades + ", roles=" + roles + "]";
    }
}