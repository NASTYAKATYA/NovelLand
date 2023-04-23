package ru.mirea.novelland.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.User;

public interface IUserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(int id);
    Long deleteById(int id);
}
