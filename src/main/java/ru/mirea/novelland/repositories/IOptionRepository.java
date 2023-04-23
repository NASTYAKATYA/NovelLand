package ru.mirea.novelland.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.Option;

public interface IOptionRepository extends JpaRepository<Option, Integer> {
    Option findById(int id);
}
