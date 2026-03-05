package org.u2g.codylab.teamboard.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String email;

    @jakarta.persistence.Column(unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private java.util.List<Card> assignedCards = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<Card> getAssignedCards() {
        return assignedCards;
    }

    public User setAssignedCards(List<Card> assignedCards) {
        this.assignedCards = assignedCards;
        return this;
    }
}
