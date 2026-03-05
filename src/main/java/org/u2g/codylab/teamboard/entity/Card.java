package org.u2g.codylab.teamboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false)
    private String title;

    @jakarta.persistence.Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;

    public Long getId() {
        return id;
    }

    public Card setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Card setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Card setDescription(String description) {
        this.description = description;
        return this;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public Card setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
        return this;
    }

    public Column getColumn() {
        return column;
    }

    public Card setColumn(Column column) {
        this.column = column;
        return this;
    }
}
