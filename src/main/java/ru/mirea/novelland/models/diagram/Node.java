package ru.mirea.novelland.models.diagram;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Node {
    public int key;
    public String title;
    public String url;
    public List<Choice> choices;
}
