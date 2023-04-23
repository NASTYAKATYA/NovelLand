package ru.mirea.novelland.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "novel_nodes")
public class NovelNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="is_start", nullable=false)
    private boolean isStart;
    @Column(name="name", nullable=false)
    private String name;
    @Column(name="content", nullable=false)
    private String content;
    @OneToMany(mappedBy="novelNode", cascade = CascadeType.REMOVE)
    private Set<Option> options;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "background_image_id", referencedColumnName = "id")
    private Image backgroundImage;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "character_image_id", referencedColumnName = "id")
    private Image characterImage;
    @JsonIgnore
    @OneToMany(mappedBy = "childrenNovelNode")
    private Set<Option> parentOptions;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="novel_id", nullable=false)
    private Novel novel;
}