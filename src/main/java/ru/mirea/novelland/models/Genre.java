package ru.mirea.novelland.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name", nullable=false)
    private String name;
    @ManyToMany(mappedBy = "genres", cascade = CascadeType.REMOVE)
    private Set<Novel> novels;
}
