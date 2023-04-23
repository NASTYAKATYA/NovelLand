package ru.mirea.novelland.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.Genre;

public interface IGenreRepository extends JpaRepository<Genre, Integer> {
    Genre findById(int id);
}
