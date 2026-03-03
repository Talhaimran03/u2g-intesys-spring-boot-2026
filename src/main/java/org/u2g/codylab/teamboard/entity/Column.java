package org.u2g.codylab.teamboard.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "columns")
public class Column {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false)
    private String title;

    @jakarta.persistence.Column(nullable = false)
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private java.util.List<Card> cards = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public Column setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Column setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public Column setPosition(Integer position) {
        this.position = position;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public Column setProject(Project project) {
        this.project = project;
        return this;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Column setCards(List<Card> cards) {
        this.cards = cards;
        return this;
    }
}
