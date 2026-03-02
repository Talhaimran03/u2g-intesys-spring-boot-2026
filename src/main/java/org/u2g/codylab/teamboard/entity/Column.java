package org.u2g.codylab.teamboard.entity;

import jakarta.persistence.*;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}
