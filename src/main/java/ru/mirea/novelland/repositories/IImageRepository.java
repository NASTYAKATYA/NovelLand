package ru.mirea.novelland.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.Image;

public interface IImageRepository extends JpaRepository<Image, Long> {
    @Transactional
    Image findById(int id);
    @Transactional
    Image findByName(String name);
    Long deleteById(int id);
}