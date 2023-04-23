package ru.mirea.novelland.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.novelland.models.Novel;
import ru.mirea.novelland.models.NovelNode;
import ru.mirea.novelland.models.Option;
import ru.mirea.novelland.repositories.IImageRepository;
import ru.mirea.novelland.repositories.INovelNodeRepository;
import ru.mirea.novelland.repositories.INovelRepository;
import ru.mirea.novelland.repositories.IOptionRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NovelNodeService {
    private final IImageRepository iImageRepository;
    private final INovelRepository iNovelRepository;
    private final INovelNodeRepository iNovelNodeRepository;
    private final IOptionRepository iOptionRepository;
    private final ImageService imageService;
    public NovelNodeService(IImageRepository iImageRepository, INovelRepository iNovelRepository, INovelNodeRepository iNovelNodeRepository, IOptionRepository iOptionRepository, ImageService imageService) {
        this.iImageRepository = iImageRepository;
        this.iNovelRepository = iNovelRepository;
        this.iNovelNodeRepository = iNovelNodeRepository;
        this.iOptionRepository = iOptionRepository;
        this.imageService = imageService;
    }
    public NovelNode createStart(int novelId) {
        var novelNode = createDefault(novelId);
        novelNode.setStart(true);
        return iNovelNodeRepository.save(novelNode);
    }

    public NovelNode createDefault(int novelId) {
        var novelNode = new NovelNode();
        novelNode.setName("Название");
        novelNode.setStart(false);
        novelNode.setContent("Контент");
        novelNode.setNovel(iNovelRepository.findById(novelId));
        var defaultNovelNode = iNovelNodeRepository.save(novelNode);
        var defaultOption = new Option();
        defaultOption.setValue("Далее");
        defaultOption.setNovelNode(defaultNovelNode);
        defaultNovelNode.setOptions(new HashSet<>(List.of(defaultOption)));
        var createdNovel = iNovelNodeRepository.save(defaultNovelNode);
        iOptionRepository.save(defaultOption);
        return createdNovel;
    }
    public NovelNode save(NovelNode novelNode,
                          List<Integer> childrenIds,
                          String name,
                          String content,
                          List<Integer> optionIds,
                          List<String> optionValues,
                          MultipartFile backgroundImage,
                          MultipartFile characterImage) throws IOException {
        novelNode.setName(name);
        novelNode.setContent(content);
        removeDeletedOptionsFromNovelNode(novelNode, optionIds);
        saveOptions(novelNode, optionIds, optionValues, childrenIds);
        if (!backgroundImage.isEmpty()) {
            var bImg = imageService.convertToImage(backgroundImage);
            if (novelNode.getBackgroundImage() != null) {
                novelNode.getBackgroundImage().setImageData(bImg.getImageData());
                novelNode.getBackgroundImage().setType(bImg.getType());
            }
            else {
                novelNode.setBackgroundImage(bImg);
            }
        }
        if (!characterImage.isEmpty()) {
            var cImg = imageService.convertToImage(characterImage);
            if (novelNode.getCharacterImage() != null) {
                novelNode.getCharacterImage().setImageData(cImg.getImageData());
                novelNode.getCharacterImage().setType(cImg.getType());
            }
            else {
                novelNode.setCharacterImage(cImg);
            }
        }
        if (novelNode.getBackgroundImage() != null) {
            iImageRepository.save(novelNode.getBackgroundImage());
        }
        if (novelNode.getCharacterImage() != null) {
            iImageRepository.save(novelNode.getCharacterImage());
        }
        return iNovelNodeRepository.save(novelNode);
    }
    private void setChildrenToOption(NovelNode current, int optionId, int childrenId) {

        var novel = current.getNovel();
        var option = iOptionRepository.findById(optionId);
        var novelNode = iNovelNodeRepository.findById(childrenId);
        if (childrenId == -1) {
            option.setChildrenNovelNode(null);
            iOptionRepository.save(option);
            return;
        }
        if (novel.getNovelNodes().contains(novelNode) &&
                novel.getNovelNodes().stream().anyMatch(nn -> nn.getOptions().contains(option))) {
            option.setChildrenNovelNode(novelNode);
            iOptionRepository.save(option);
            return;
        }
    }
    private void removeDeletedOptionsFromNovelNode(NovelNode novelNode, List<Integer> optionIds) {
        var options = novelNode.getOptions();
        List<Integer> idsToRemove = new ArrayList<>();
        for (var option: options) {
            if (optionIds == null) {
                idsToRemove.add(option.getId());
            }
            else if (!optionIds.contains(option.getId())) {
                idsToRemove.add(option.getId());
            }
        }
        for (var id: idsToRemove) {
            iOptionRepository.deleteById(id);
        }
        options = options.stream().filter(op -> !idsToRemove.contains(op.getId())).collect(Collectors.toSet());
        novelNode.setOptions(options);
    }
    private void saveOptions(NovelNode novelNode, List<Integer> optionIds, List<String> optionValues, List<Integer> childrenIds) {
        for (int i = 0; i < optionValues.size(); i++) {
            //if old
            Option option;
            if (optionIds != null && i < optionIds.size()) {
                option = iOptionRepository.findById(optionIds.get(i)).get();
            }
            else {
                option = new Option();
            }
            option.setNovelNode(novelNode);
            option.setValue(optionValues.get(i));
            option = iOptionRepository.save(option);
            setChildrenToOption(novelNode, option.getId(), childrenIds.get(i));
        }
    }

    public void deleteById(int novelNodeId) {
        var novelNode = iNovelNodeRepository.findById(novelNodeId);
        if (novelNode.getBackgroundImage() != null) {
            iImageRepository.deleteById(novelNode.getBackgroundImage().getId());
        }
        if (novelNode.getCharacterImage() != null) {
            iImageRepository.deleteById(novelNode.getCharacterImage().getId());
        }
        iNovelNodeRepository.deleteById(novelNodeId);
    }

    @Transactional
    public NovelNode get(int id) {
        return iNovelNodeRepository.findById(id);
    }

    @Transactional
    public List<NovelNode> getAllByNovel(Novel novel) {
        return iNovelNodeRepository.findAllByNovel(novel);
    }
}
