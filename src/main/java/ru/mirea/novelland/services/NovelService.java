package ru.mirea.novelland.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.novelland.models.Genre;
import ru.mirea.novelland.models.Novel;
import ru.mirea.novelland.repositories.IGenreRepository;
import ru.mirea.novelland.repositories.IImageRepository;
import ru.mirea.novelland.repositories.INovelRepository;
import ru.mirea.novelland.repositories.IUserRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NovelService {
    private final INovelRepository iNovelRepository;
    private final IGenreRepository iGenreRepository;
    private final IUserRepository iUserRepository;
    private final ImageService imageService;
    private final IImageRepository iImageRepository;

    public NovelService(INovelRepository iNovelRepository, IGenreRepository iGenreRepository, IUserRepository iUserRepository, ImageService imageService, IImageRepository iImageRepository) {
        this.iNovelRepository = iNovelRepository;
        this.iGenreRepository = iGenreRepository;
        this.iUserRepository = iUserRepository;
        this.imageService = imageService;
        this.iImageRepository = iImageRepository;
    }

    public Novel save(String name, String description, List<Integer> genreIds, int userId, MultipartFile previewImage) throws IOException {
        Novel novel = new Novel();
        novel.setName(name);
        novel.setDescription(description);
        novel.setGenres(convertListOfGenreIdsToSetOfGenres(genreIds));
        novel.setUser(iUserRepository.findById(userId));
        if (!previewImage.isEmpty()) {
            var cImg = imageService.convertToImage(previewImage);
            if (novel.getPreviewImage() != null) {
                novel.getPreviewImage().setImageData(cImg.getImageData());
                novel.getPreviewImage().setType(cImg.getType());
            }
            else {
                novel.setPreviewImage(cImg);
            }
        }
        if (novel.getPreviewImage() != null) {
            iImageRepository.save(novel.getPreviewImage());
        }
        return iNovelRepository.save(novel);
    }

    public void deleteById(int novelId) {
        iNovelRepository.deleteById(novelId);
    }

    public Novel save(Novel novel, List<Integer> genreIds, MultipartFile previewImage) throws IOException {
        novel.setGenres(convertListOfGenreIdsToSetOfGenres(genreIds));
        if (!previewImage.isEmpty()) {
            var cImg = imageService.convertToImage(previewImage);
            if (novel.getPreviewImage() != null) {
                novel.getPreviewImage().setImageData(cImg.getImageData());
                novel.getPreviewImage().setType(cImg.getType());
            }
            else {
                novel.setPreviewImage(cImg);
            }
        }
        if (novel.getPreviewImage() != null) {
            iImageRepository.save(novel.getPreviewImage());
        }
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
    public List<Novel> getAllNovels() {
        return iNovelRepository.findAll();
    }
    public Novel getNovel(int id){
        return iNovelRepository.findById(id);
    }

}
