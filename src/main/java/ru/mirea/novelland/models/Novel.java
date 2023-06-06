package ru.mirea.novelland.models;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "novels")
public class Novel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name", nullable=false)
    private String name;
    @Column(name="description", nullable=false)
    private String description;
    @ManyToMany
    @JoinTable(
            name = "novels_genres",
            joinColumns = @JoinColumn(name = "novel_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;
    @OneToMany(mappedBy="novel", cascade = CascadeType.REMOVE)
    private Set<NovelNode> novelNodes;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "preview_image_id", referencedColumnName = "id")
    private Image previewImage;
}