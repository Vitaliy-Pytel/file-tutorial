package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UploadComponent extends FormLayout {

    private Path rootDirectory = Path.of("src/main/resources/file-storage");

    MemoryBuffer memoryBuffer = new MemoryBuffer();
    Upload upload = new Upload(memoryBuffer);
    Button uploadButton = new Button("Upload file");
    SuccessEvent successEvent;

    public UploadComponent(SuccessEvent successEvent) {
        this.successEvent = successEvent;
        customizeUpload();
        add(upload);
    }

    private void customizeUpload(){
        upload.setSizeUndefined();
        upload.setVisible(true);
        upload.setUploadButton(uploadButton);
        upload.addFinishedListener(e -> {
            writeFile(memoryBuffer.getInputStream(), e.getFileName());
            successEvent.uploadFinished();
        });
//        upload.addSucceededListener(event -> successEvent.uploadFinished());

    }

    private void writeFile(InputStream inputStream, String fileName) {
        Path newFile = null;
        System.out.println(rootDirectory.toString());
        try {
            newFile = Files.createFile(Path.of(rootDirectory + "/" + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try (FileOutputStream outputStream = new FileOutputStream(String.valueOf(newFile))){
            byte[] bytes = inputStream.readAllBytes();
            outputStream.write(bytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setRootDirectory(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }



}
