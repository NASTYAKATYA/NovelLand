package ru.mirea.novelland.services;


import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.mirea.novelland.core.Tuple2;
import ru.mirea.novelland.models.NovelNode;
import ru.mirea.novelland.models.Option;
import ru.mirea.novelland.models.diagram.Choice;
import ru.mirea.novelland.models.diagram.Link;
import ru.mirea.novelland.models.diagram.Node;

import java.util.*;

@Service
public class DbToDiagramService {
    private final NovelService novelService;
    private final NovelNodeService novelNodeService;

    public DbToDiagramService(NovelService novelService, NovelNodeService novelNodeService) {
        this.novelService = novelService;
        this.novelNodeService = novelNodeService;
    }

    public Tuple2<List<Node>, List<Link>> convert(int novelId) {
        var novel = novelService.get(novelId);
        var novelNodes = novelNodeService.getAllByNovel(novel);
        var startNovelNode = novelNodes.stream().filter(NovelNode::isStart).findFirst().get();
        List<Node> diagNodes = new ArrayList<>();
        List<Link> diagLinks = new ArrayList<>();
        Key currKey = new Key();
        Key maxKey = new Key();
        convertRecursion(startNovelNode, currKey, maxKey, diagNodes, diagLinks);
        addNotLinkedNovelNodes(novelNodes, diagNodes);
        filterNodes(diagNodes);
        return new Tuple2<>(diagNodes, diagLinks);
    }
    private void filterNodes(List<Node> diagNodes) {
        List<Node> temp = new ArrayList<>();
        for (int i = 0; i < diagNodes.size(); i++) {
            if (isDuplicate(temp, diagNodes.get(i))) {
                diagNodes.remove(diagNodes.get(i));
            }
            else {
                temp.add(diagNodes.get(i));
            }
        }
    }
    private void addNotLinkedNovelNodes(List<NovelNode> novelNodes, List<Node> diagNodes) {
        var maxKey = diagNodes.stream().max(Comparator.comparing(n -> n.key)).get().key;
        List<NovelNode> notLinkedNodes = novelNodes.stream().filter(nn -> {
            var diagNode = novelNodeToDiagNode(nn, -1);
            return !isDuplicate(diagNodes, diagNode);
        }).toList();
        List<Node> notLinkedDiagNodes = new ArrayList<>();
        for (var novelNode: notLinkedNodes) {
            notLinkedDiagNodes.add(novelNodeToDiagNode(novelNode, ++maxKey));
        }
        diagNodes.addAll(notLinkedDiagNodes);
    }
    private class Key {
        public int value;
    }
    private void convertRecursion(NovelNode currNovelNode, Key currKey, Key maxKey, List<Node> diagNodes, List<Link> diagLinks) {
        var diagNode = novelNodeToDiagNode(currNovelNode, currKey.value);

        // Fill nodes
        diagNodes.add(diagNode);

        // Fill links and call Recursion
        List<NovelNode> childrenNovelNodes = currNovelNode.getOptions().stream().map(Option::getChildrenNovelNode).toList();
        for (int i = 0; i < childrenNovelNodes.size(); i++) {
            if (childrenNovelNodes.get(i) == null) {
                continue;
            }
            Key childKey = new Key();

            if (isDuplicate(diagNodes, novelNodeToDiagNode(childrenNovelNodes.get(i), -1))) {
                childKey.value = findKeyByNovelNode(diagNodes, childrenNovelNodes.get(i));
            }
            else {
                childKey.value = maxKey.value + 1;
                maxKey.value = childKey.value;
            }
            diagLinks.add(new Link(currKey.value, childKey.value, i + 1));
            if (!isDuplicate(diagNodes, novelNodeToDiagNode(childrenNovelNodes.get(i), -1))) {
                convertRecursion(childrenNovelNodes.get(i), childKey, maxKey, diagNodes, diagLinks);
            }
        }
    }
//    private void convertRecursion(NovelNode currNovelNode, int currKey, List<Node> diagNodes, List<Link> diagLinks) {
//        var diagNode = novelNodeToDiagNode(currNovelNode, currKey);
//
//        // Fill nodes
//        diagNodes.add(diagNode);
//
//        // Fill links and call Recursion
//        List<NovelNode> childrenNovelNodes = currNovelNode.getOptions().stream().map(Option::getChildrenNovelNode).toList();
//        for (int i = 0, duplicatesCount = 0; i < childrenNovelNodes.size(); i++) {
//            if (childrenNovelNodes.get(i) == null) {
//                continue;
//            }
//            int nextKey;
//            if (isDuplicate(diagNodes, novelNodeToDiagNode(childrenNovelNodes.get(i), -1))) {
//                nextKey = findKeyByNovelNode(diagNodes, childrenNovelNodes.get(i));
//                duplicatesCount++;
//            }
//            else {
//                nextKey = currKey + i + 1 - duplicatesCount;
//            }
//            diagLinks.add(new Link(currKey, nextKey, i + 1));
//            convertRecursion(childrenNovelNodes.get(i), nextKey, diagNodes, diagLinks);
//        }
//    }

    private Node novelNodeToDiagNode(NovelNode novelNode, int currentKey) {
        List<Choice> diagChoices = new ArrayList<>();
        List<Option> novelNodeOptions = new ArrayList<>(novelNode.getOptions());

        for (int i = 0; i < novelNodeOptions.size(); i++) {
            diagChoices.add(new Choice(i + 1, novelNodeOptions.get(i).getValue()));
        }
        return new Node(currentKey, novelNode.getName(),"/" + novelNode.getId(), diagChoices);
    }

    private boolean isDuplicate(List<Node> diagNodes, Node diagNode) {
        return diagNodes.stream().anyMatch(dn -> Objects.equals(dn.url, diagNode.url));
    }

    private int findKeyByNovelNode(List<Node> diagNodes, NovelNode novelNode) {
        return diagNodes.stream().filter(dn -> Objects.equals(dn.url,"/" + novelNode.getId())).findFirst().get().key;
    }
}
