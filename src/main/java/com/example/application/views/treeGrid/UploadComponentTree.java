package com.example.application.views.treeGrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UploadComponentTree extends FormLayout {
    MemoryBuffer memoryBuffer = new MemoryBuffer();
    Upload upload = new Upload(memoryBuffer);
    Button uploadButton = new Button("Upload file");
    String path;
    AddEvent successEvent;

    public UploadComponentTree(AddEvent successEvent) {
        this.successEvent = successEvent;
        customizeUpload();
        add(upload);
    }

    private void customizeUpload(){
        upload.setSizeUndefined();
        upload.setVisible(true);
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(e -> {
            writeFile(memoryBuffer.getInputStream(), e.getFileName());
            successEvent.updateGrid();
        });
    }

    private void writeFile(InputStream inputStream, String fileName) {
        Path newFile = null;
        System.out.println(path);
        if (path == null) {
            new ErrorNotification();
        } else {
            try {
                newFile = Files.createFile(Path.of(path + "/" + fileName));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try (FileOutputStream outputStream = new FileOutputStream(String.valueOf(newFile))) {
                byte[] bytes = inputStream.readAllBytes();
                outputStream.write(bytes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            path = null;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }
}