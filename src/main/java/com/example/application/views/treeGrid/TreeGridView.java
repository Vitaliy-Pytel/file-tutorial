package com.example.application.views.treeGrid;

import com.example.application.views.UploadComponent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.util.Comparator;

@Route("")
public class TreeGridView extends VerticalLayout {
    private final File ROOT = new File("src/main/resources/file-storage");
    FileSystemDataProvider dataProvider = new FileSystemDataProvider(ROOT);
    TreeGrid<File> treeGrid = new TreeGrid(dataProvider);
    UploadComponentTree uploadComponent = new UploadComponentTree(() -> upgradeGrid());
    FileDataForm fileDataForm = new FileDataForm(() -> upgradeGrid());

    public TreeGridView() {
        add(customizeGrid());
        add(uploadComponent);
        add(fileDataForm);
    }

    private TreeGrid<File> customizeGrid() {
        treeGrid.addComponentColumn(file -> {
                    Icon htmlIcon;
                    if (file.isDirectory()) {
                        htmlIcon = VaadinIcon.FOLDER_O.create();
                        htmlIcon.setColor("orange");
                    } else {
                        htmlIcon = VaadinIcon.FILE_O.create();
                        htmlIcon.setColor("red");
                    }
                    return htmlIcon;
                }).setHeader("Type")
                .setSortable(true)
                .setComparator(Comparator.comparing(File::isDirectory))
                .setWidth("10em")
                .setFlexGrow(0);
        treeGrid.addHierarchyColumn(File::getName)
                .setHeader("File name")
                .setSortable(true)
                .setComparator((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        treeGrid.addColumn(file -> file.isDirectory() ? "--" : file.length() + " bytes")
                .setHeader("Length");
        treeGrid.asSingleSelect().setEnabled(true);

        treeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<File>, File> singleSelect = treeGrid.asSingleSelect();
        var element = singleSelect.addValueChangeListener(e -> {
            File selectedFile = e.getValue();
            String path = selectedFile.getParent() + "/" + selectedFile.getName();
            uploadComponent.setPath(path);
            fileDataForm.setSelectedFile(path);
            System.out.println("Selected file: " + selectedFile.getParent() + " " + selectedFile.getName());
        });
        return treeGrid;
    }

    public void upgradeGrid() {
        treeGrid.getDataProvider().refreshAll();
    }

}
