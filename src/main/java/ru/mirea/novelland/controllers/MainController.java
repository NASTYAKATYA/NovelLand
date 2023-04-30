package ru.mirea.novelland.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.novelland.core.Tuple2;
import ru.mirea.novelland.models.Image;
import ru.mirea.novelland.models.NovelNode;
import ru.mirea.novelland.models.Option;
import ru.mirea.novelland.models.User;
import ru.mirea.novelland.repositories.IGenreRepository;
import ru.mirea.novelland.repositories.IOptionRepository;
import ru.mirea.novelland.services.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {
    private final ImageService imageService;
    private final IGenreRepository iGenreRepository;
    private final NovelService novelService;
    private final UserService userService;
    private final NovelNodeService novelNodeService;
    private final DbToDiagramService dbToDiagramService;
    private final IOptionRepository iOptionRepository;

    public MainController(ImageService imageService, IGenreRepository iGenreRepository, NovelService novelService, UserService userService, NovelNodeService novelNodeService, DbToDiagramService dbToDiagramService, IOptionRepository iOptionRepository) {
        this.imageService = imageService;
        this.iGenreRepository = iGenreRepository;
        this.novelService = novelService;
        this.userService = userService;
        this.novelNodeService = novelNodeService;
        this.dbToDiagramService = dbToDiagramService;
        this.iOptionRepository = iOptionRepository;
    }

    private int getUserId(Authentication authentication) {
        if (authentication == null)
            return 0;
        else
            return ((User)userService.loadUserByUsername(authentication.getName())).getId();
    }

    @GetMapping("/")
    public String index() {
        return "MainController/index";
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<?> image(@PathVariable int imageId) {
        Image image = imageService.get(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(image.getType()))
                .body(image.getImageData());
    }

    @GetMapping("/image")
    public ResponseEntity<?> image(@RequestParam(value="name") String name) {
        Image image = imageService.get(name);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(image.getType()))
                .body(image.getImageData());
    }

    @GetMapping("/load-image")
    public String image() {
        return "MainController/load-image";
    }

    @PostMapping("/load-image")
    public String image(@RequestParam MultipartFile file, @RequestParam String name) throws IOException {
        Image image = imageService.save(file, name);
        return "redirect:/image/" + image.getId();
    }
    @GetMapping("/create-novel")
    public String createNovel(Model model) {
        model.addAttribute("allGenres", iGenreRepository.findAll());
        return "MainController/create-novel";
    }
    @PostMapping("/create-novel")
    public String createNovel(@RequestParam String name, @RequestParam List<Integer> genres, @RequestParam String description, Authentication authentication) {
        var userId = getUserId(authentication);
        var novel = novelService.save(name, description, genres, userId);
        novelNodeService.createStart(novel.getId());
        return "redirect:/edit-novel/" + novel.getId();
    }
    @GetMapping("/edit-novel/{novelId}")
    public String editNovel(@PathVariable int novelId, Authentication authentication, Model model) {
        if (!novelAuthorizationCheck(authentication, novelId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        var novel = novelService.get(novelId);
        model.addAttribute("novel", novel);
        model.addAttribute("allGenres", iGenreRepository.findAll());
        var diagramData = dbToDiagramService.convert(novelId);
        Gson gson = new GsonBuilder().create();
        model.addAttribute("nodeDataArrayJson", gson.toJson(diagramData.first));
        model.addAttribute("linkDataArrayJson", gson.toJson(diagramData.second));
        return "MainController/edit-novel";
    }
    @PostMapping(value = "/edit-novel/{novelId}", params = "save")
    public String editNovel(@PathVariable int novelId, @RequestParam String name, @RequestParam List<Integer> genres, @RequestParam String description, Authentication authentication) {
        if (!novelAuthorizationCheck(authentication, novelId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        var novel = novelService.get(novelId);
        novel.setName(name);
        novel.setDescription(description);
        novelService.save(novel, genres);
        return  "redirect:/edit-novel/" + novelId;
    }
    @PostMapping(value = "/edit-novel/{novelId}", params = "delete")
    public String editNovel(@PathVariable int novelId, Authentication authentication) {
        if (!novelAuthorizationCheck(authentication, novelId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        novelService.deleteById(novelId);
        return "redirect:/";
    }
    @PostMapping(value = "/edit-novel/{novelId}", params = "add")
    public String createNovelNode(@PathVariable int novelId, Authentication authentication) {
        if (!novelAuthorizationCheck(authentication, novelId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        var novel = novelService.get(novelId);
        var novelNode = novelNodeService.createDefault(novel.getId());
        return "redirect:/edit-novel/" + novel.getId() + "/" + novelNode.getId();
    }

    @Transactional
    @GetMapping("/edit-novel/{novelId}/{novelNodeId}")
    public String editNovelNode(@PathVariable int novelId, @PathVariable int novelNodeId, Authentication authentication, Model model) throws JsonProcessingException {
        if (!novelNodeAuthorizationCheck(authentication, novelId, novelNodeId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        var novel = novelService.get(novelId);
        var childrenNovelNodes = novelNodeService.getAllByNovel(novel);
        var novelNode = novelNodeService.get(novelNodeId);
        var filtered = childrenNovelNodes.stream().filter(nn -> nn.getId() != novelNode.getId()).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        String childrenNovelNodesJson = objectMapper.writeValueAsString(filtered);
        model.addAttribute("childrenNovelNodesJson", childrenNovelNodesJson);
        model.addAttribute("novelNodeJson", objectMapper.writeValueAsString(novelNode));
        model.addAttribute("novelNode", novelNode);
        var selectedChildrenIds = novelNode.getOptions().stream().filter(o -> o.getChildrenNovelNode() != null).map(o -> new Tuple2(o.getId(), o.getChildrenNovelNode().getId())).toList();
        model.addAttribute("selectedChildrenIdsJson", objectMapper.writeValueAsString(selectedChildrenIds));
        return "MainController/edit-novel-node";
    }
    @Transactional
    @PostMapping(value = "/edit-novel/{novelId}/{novelNodeId}", params = "save")
    public String editNovel(@PathVariable int novelId,
                            @PathVariable int novelNodeId,
                            @RequestParam(name = "childrenIds[]", required = false) List<Integer> childrenIds,
                            @RequestParam String name,
                            @RequestParam String content,
                            @RequestParam(name = "optionIds[]", required = false) List<Integer> optionIds,
                            @RequestParam(name = "optionValues[]") List<String> optionValues,
                            @RequestParam MultipartFile backgroundImage,
                            @RequestParam MultipartFile characterImage, Authentication authentication) throws IOException {
        if (!novelNodeAuthorizationCheck(authentication, novelId, novelNodeId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        if (optionValues == null || optionValues.size() == 0) {
            // TODO
            return "redirect:/edit-novel/" + novelId + "/" + novelNodeId + "?error=must_be_at_least_1_option";
        }
        var novel = novelService.get(novelId);
        var novelNode = novelNodeService.get(novelNodeId);
        novelNodeService.save(novelNode, childrenIds, name, content, optionIds,optionValues, backgroundImage, characterImage);
        return "redirect:/edit-novel/" + novelId;
    }
    @Transactional
    @PostMapping(value = "/edit-novel/{novelId}/{novelNodeId}", params = "delete")
    public String editNovel(@PathVariable int novelId, @PathVariable int novelNodeId, Authentication authentication) {
        if (!novelNodeAuthorizationCheck(authentication, novelId, novelNodeId)) {
            // TODO redirect to 401
            return "redirect:/error?code=401";
        }
        novelNodeService.deleteById(novelNodeId);
        return "redirect:/edit-novel/" + novelId;
    }

    @GetMapping("/novel/{novelId}")
    public String novel(@PathVariable int novelId) {
        return "MainController/novel";
    }

    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable int userId) {
        return "MainController/profile";
    }

    @GetMapping("/play-novel/{novelNodeId}")
    public String playNovel(@PathVariable int novelNodeId) {
        return "MainController/play-novel";
    }

    @GetMapping("/sign-in")
    public String signIn() {
        return "MainController/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return "MainController/sign-up";
    }

    @PostMapping("/sign-up")
    public String signCreate(HttpServletRequest request, @RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String repeatPassword, Model model) {
        if (userService.loadUserByEmail(email) != null) {
            model.addAttribute("error", "email_exists");
            return "MainController/sign-up";
        }
        else if (userService.loadUserByUsername(username) != null) {
            model.addAttribute("error", "username_exists");
            return "MainController/sign-up";
        }
        else if (!password.equals(repeatPassword)) {
            model.addAttribute("error", "password_mismatch");
            return "MainController/sign-up";
        }
        else {
            userService.save(email, username, password);
            model.addAttribute("error", "not_error");
            authWithHttpServletRequest(request, username, password);
            return "redirect:/";
        }
    }

    // true if ok
    @Transactional
    private boolean novelAuthorizationCheck(Authentication authentication, int novelId) {
        var userId = getUserId(authentication);
        var novel = novelService.get(novelId);
        return (novel.getUser().getId() == userId);
    }
    @Transactional
    private boolean novelNodeAuthorizationCheck(Authentication authentication, int novelId, int novelNodeId) {
        var novel = novelService.get(novelId);
        var novelNode = novelNodeService.get(novelNodeId);
        return novelAuthorizationCheck(authentication, novelId) && (novel.getNovelNodes().contains(novelNode));
    }
    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) { }
    }
}
