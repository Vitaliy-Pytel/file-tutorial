package com.example.application.views;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SingleSelect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileGridLayout extends HorizontalLayout {
    private final Path ROOT;
    Grid<Path> packageGrid = new Grid<>();
    Grid<Path> fileGrid = new Grid<>();
    PackagesForm packagesForm = new PackagesForm();

    FileGridLayout(Path ROOT) {
        this.ROOT = ROOT;
        setWidth("100%");
        customizePackageGrid(ROOT);
        add(packageGrid);
        add(fileGrid);

        //    adding and configuration of package form
        configurePackagesForm();
        add(packagesForm);
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
    }

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
