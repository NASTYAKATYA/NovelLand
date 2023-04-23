package ru.mirea.novelland.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @Column(name="type", nullable=false)
    private String type;
    @Lob
    @Column(name = "image_data", nullable=false)
    private byte[] imageData;
}