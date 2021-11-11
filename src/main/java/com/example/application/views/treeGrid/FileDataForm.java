package com.example.application.views.treeGrid;

import com.example.application.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataForm extends FormLayout {
    TextField textField = new TextField();
    Button createButton = new Button("Create");
    Button deleteButton = new Button("Delete");
    Button renameButton =new Button("Rename");
    String selectedFile;
    public FileDataForm(AddEvent addEvent) {
        add(new HorizontalLayout(createButton,
                deleteButton,
                renameButton));
        config(addEvent);
        add(textField);
    }

    private void config(AddEvent addEvent) {
        textField.setPlaceholder("Enter package name");

        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(click -> {
            try {
                Files.createDirectory(Path.of(MainView.ROOT + "/" + textField.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            addEvent.updateGrid();
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(click -> {
            try {
                Files.delete(Path.of(selectedFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            addEvent.updateGrid();
        });

        renameButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        renameButton.addClickListener(click -> {
            File file = new File(selectedFile);
            String path = selectedFile.substring(0, selectedFile.lastIndexOf("/"));
            File newFile = new File(path + "/" + textField.getValue());
            file.renameTo(newFile);
            addEvent.updateGrid();
        });
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }
}
