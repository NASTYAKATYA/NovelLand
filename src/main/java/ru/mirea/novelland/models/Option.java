package ru.mirea.novelland.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "options")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="value", nullable=false)
    private String value;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="novel_node_id", nullable=false)
    private NovelNode novelNode;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="children_novel_node_id")
    private NovelNode childrenNovelNode;
}
