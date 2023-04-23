package ru.mirea.novelland.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.mirea.novelland.models.Genre;
import ru.mirea.novelland.models.Novel;
import ru.mirea.novelland.repositories.IGenreRepository;
import ru.mirea.novelland.repositories.INovelRepository;
import ru.mirea.novelland.repositories.IUserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NovelService {
    private final INovelRepository iNovelRepository;
    private final IGenreRepository iGenreRepository;
    private final IUserRepository iUserRepository;

    public NovelService(INovelRepository iNovelRepository, IGenreRepository iGenreRepository, IUserRepository iUserRepository) {
        this.iNovelRepository = iNovelRepository;
        this.iGenreRepository = iGenreRepository;
        this.iUserRepository = iUserRepository;
    }

    public Novel save(String name, String description, List<Integer> genreIds, int userId) {
        Novel novel = new Novel();
        novel.setName(name);
        novel.setDescription(description);
        novel.setGenres(convertListOfGenreIdsToSetOfGenres(genreIds));
        novel.setUser(iUserRepository.findById(userId));
        return iNovelRepository.save(novel);
    }

    public void deleteById(int novelId) {
        iNovelRepository.deleteById(novelId);
    }

    public Novel save(Novel novel, List<Integer> genreIds) {
        novel.setGenres(convertListOfGenreIdsToSetOfGenres(genreIds));
        return iNovelRepository.save(novel);
    }
    @Transactional
    public Novel get(int novelId) {
        return iNovelRepository.findById(novelId);
    }

    private Set<Genre> convertListOfGenreIdsToSetOfGenres(List<Integer> genreIds) {
        Set<Genre> genres = new HashSet<>();
        for (int id : genreIds) {
            Genre genre = iGenreRepository.findById(id);
            if (genre != null) {
                genres.add(genre);
            }
        }
        return genres;
    }
}
