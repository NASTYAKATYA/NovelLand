package ru.mirea.novelland.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.novelland.models.Image;
import ru.mirea.novelland.repositories.IImageRepository;
import ru.mirea.novelland.utils.ImageUtil;

import java.io.IOException;

@Service
public class ImageService {
    private final IImageRepository iImageRepository;

    public ImageService(IImageRepository iImageRepository) {
        this.iImageRepository = iImageRepository;
    }
    public Image convertToImage(MultipartFile file) throws IOException {
        return Image.builder()
                .type(file.getContentType())
                .imageData(ImageUtil.compressImage(file.getBytes())).build();
    }
    public Image convertToImage(MultipartFile file, String name) throws IOException {
        return Image.builder()
                .name(name)
                .type(file.getContentType())
                .imageData(ImageUtil.compressImage(file.getBytes())).build();
    }

    public Image save(MultipartFile file) throws IOException {
        return iImageRepository.save(convertToImage(file));
    }
    public Image save(MultipartFile file, String name) throws IOException {
        return iImageRepository.save(convertToImage(file, name));
    }

    @Transactional
    public Image get(int id) {
        var image = iImageRepository.findById(id);
        return Image.builder()
                .type(image.getType())
                .imageData(ImageUtil.decompressImage(image.getImageData())).build();
    }
    @Transactional
    public Image get(String name) {
        var image = iImageRepository.findByName(name);
        return Image.builder()
                .type(image.getType())
                .imageData(ImageUtil.decompressImage(image.getImageData())).build();
    }

}
