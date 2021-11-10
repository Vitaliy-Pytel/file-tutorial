package com.example.application.views.treeGrid;

import com.example.application.views.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataForm extends FormLayout {
    TextField fileNameField = new TextField();
    Button createButton = new Button("Create");
    Button deleteButton = new Button("Delete");
    String selectedFile;
    public FileDataForm(AddPackageEvent addPackageEvent) {
        add(createButton);
        add(deleteButton);
        config(addPackageEvent);
        add(fileNameField);
    }

    private void config(AddPackageEvent addPackageEvent) {
        fileNameField.setPlaceholder("Enter package name");

        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(click -> {
            try {
                Files.createDirectory(Path.of(MainView.ROOT + "/" + fileNameField.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            addPackageEvent.updateGrid();
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(click -> {
            try {
                Files.delete(Path.of(selectedFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            addPackageEvent.updateGrid();
        });
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }
}
