package ru.mirea.novelland.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.Novel;

public interface INovelRepository extends JpaRepository<Novel, Integer> {
    @Transactional
    Novel findById(int id);
    Long deleteById(int id);
}
