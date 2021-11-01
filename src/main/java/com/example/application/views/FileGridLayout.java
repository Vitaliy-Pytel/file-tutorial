package com.example.application.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileGridLayout extends HorizontalLayout {
    private final Path ROOT;
    Grid<Path> packageGrid = new Grid<>();
    Grid<Path> fileGrid = new Grid<>();
    PackagesForm packagesForm = new PackagesForm();
    UploadComponent uploadComponent = new UploadComponent();

    FileGridLayout(Path ROOT) {
        this.ROOT = ROOT;
        setWidth("100%");
        customizePackageGrid(ROOT);
        add(packageGrid);
        customizeFileGrid();
        add(fileGrid);

        //    adding and configuration of package form
        configurePackagesForm();
        add(combineTwoButtonComponents());
//        add(packagesForm);
    }

    private Component combineTwoButtonComponents() {
        VerticalLayout layout = new VerticalLayout(packagesForm, uploadComponent);
        layout.setMaxWidth("500px");
        return layout;
    }

    private void configurePackagesForm() {
        packagesForm.addListener(PackagesForm.AddEvent.class, this::create);
        packagesForm.addListener(PackagesForm.DeleteEvent.class, this::delete);
    }

    private void delete(PackagesForm.DeleteEvent event) {
        try {
            Files.delete(event.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customizePackageGrid(ROOT);
        customizeFileGrid();
    }

    private void create(PackagesForm.AddEvent event) {
        String fileName = packagesForm.textField.getValue();
        try {
            Files.createDirectory(Path.of(ROOT + "/" + fileName));
            System.out.println(packagesForm.path + " from creating package method");
        } catch (IOException e) {
            e.printStackTrace();
        }
        customizePackageGrid(ROOT);

    }

    //  initial settings of tables
    private void customizePackageGrid(Path root) {
        packageGrid.removeAllColumns();
        upgradePackagesGrid();
        packageGrid.addColumn(Path::getFileName);
        packageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        packageGrid.asSingleSelect().addValueChangeListener(e -> {
            Path selectedFile = e.getValue();
            upgradeFileGrid(selectedFile);
        });
        packageGrid.asSingleSelect().addValueChangeListener(event -> editPackage(event.getValue()));
        packageGrid.asSingleSelect().addValueChangeListener(event -> uploadComponent.setROOT(event.getValue()));
    }

    private void customizeFileGrid() {
        fileGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        fileGrid.asSingleSelect().addValueChangeListener(event -> editPackage(event.getValue()));
    }
    //*****

    private void editPackage(Path value) {
        packagesForm.setPath(value);
    }

    //    upgrading grids
    private void upgradePackagesGrid() {
        try {
            packageGrid.setItems(Files.walk(ROOT, 1).filter(Files::isDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upgradeFileGrid(Path path) {
        if (path == null) {
            return;
        }
            try {
                fileGrid.removeAllColumns();
                fileGrid.setItems(Files.walk(path, 1).filter(Files::isRegularFile));
                fileGrid.addColumn(Path::getFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
