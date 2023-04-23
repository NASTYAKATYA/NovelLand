package ru.mirea.novelland.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.novelland.models.Novel;
import ru.mirea.novelland.models.NovelNode;

import java.util.List;

public interface INovelNodeRepository extends JpaRepository<NovelNode, Integer> {
    @Transactional
    List<NovelNode> findAllByNovel(Novel novel);
    @Transactional
    NovelNode findById(int id);
    Long deleteById(int id);
}
