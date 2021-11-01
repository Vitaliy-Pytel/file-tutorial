package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.nio.file.Path;

@Route("")
public class MainView extends VerticalLayout {
//    private final Path ROOT= Path.of("/home/vitaliy/Загрузки/");
    private final Path ROOT = Path.of("src/main/resources/file-storage/");
    FileGridLayout fileGrid = new FileGridLayout(ROOT);
    public MainView() {
        add(fileGrid);
    }
}
