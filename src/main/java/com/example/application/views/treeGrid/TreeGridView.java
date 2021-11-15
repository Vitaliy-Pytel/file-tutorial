package com.example.application.views.treeGrid;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.File;

@Route("")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@CssImport(value = "styles/shared-styles.css", id = "my-style")
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
        treeGrid.addComponentHierarchyColumn(file -> concatFileNameWithItem(file))
                .setHeader("File name")
                .setSortable(true)
                .setComparator((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        treeGrid.addColumn(file -> file.isDirectory() ? "--" : file.length() + " bytes")
                .setHeader("Length");
        treeGrid.asSingleSelect().setEnabled(true);

        treeGrid.getElement().getStyle().set("my-style", null);

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

    private HorizontalLayout concatFileNameWithItem(File file) {
        Icon htmlIcon;
        if (file.isDirectory()) {
            htmlIcon = VaadinIcon.FOLDER_O.create();
            htmlIcon.setColor("orange");
        } else {
            htmlIcon = VaadinIcon.FILE_O.create();
            htmlIcon.setColor("red");
        }
        String fileName = file.getName();
        HorizontalLayout hl = new HorizontalLayout();
        hl.add(htmlIcon);
        hl.add(new Label(fileName));
        return hl;
    }

}
