package com.example.application.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UploadComponent extends FormLayout {

    private Path ROOT = Path.of("src/main/resources/file-storage");

    MemoryBuffer memoryBuffer = new MemoryBuffer();
    Upload upload = new Upload(memoryBuffer);
    Button uploadButton = new Button("Upload file");

    public UploadComponent() {
        customizeUpload();
        add(upload);
    }

    private void customizeUpload(){
        upload.setSizeUndefined();
        upload.setVisible(true);
        upload.setUploadButton(uploadButton);
        upload.addFinishedListener(e ->
        {
            InputStream inputStream = memoryBuffer.getInputStream();
            StringBuffer fileName = new StringBuffer();
            fileName.append(ROOT.toString())
                    .append(memoryBuffer.getFileName());
            Path newFile = null;
            try {
                newFile = Files.createFile(Path.of(ROOT + "/" + memoryBuffer.getFileName()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try (FileOutputStream outputStream = new FileOutputStream(String.valueOf(newFile))){
                byte[] bytes = inputStream.readAllBytes();
                outputStream.write(bytes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void setROOT(Path ROOT) {
        this.ROOT = ROOT;
    }

}
