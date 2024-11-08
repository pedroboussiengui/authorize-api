package org.example.entity;

public class Unidade {
    private Long id;
    private String title;
    private User owner;

    public Unidade(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unidade{");
        sb.append("id=").append(id);
        sb.append(", title=").append(title);
        sb.append('}');
        return sb.toString();
    }
}
