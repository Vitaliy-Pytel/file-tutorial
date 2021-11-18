package com.example.application.views.treeGrid;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

public class PdfCreator {
    private final int LINES_COUNT = 20;

    public void createPdf() {
        File ROOT = new File("/home");
//        File ROOT = new File("src/main/resources/file-storage/");
        List<File> fileNamesList = getFileList();
        //creating empty pdf document
        PDDocument document = new PDDocument();
        PDPage pdPage = new PDPage();
        document.addPage(pdPage);

        PDDocumentInformation documentInformation = document.getDocumentInformation();
        documentInformation.setAuthor("Vitaliy Pytel");
        documentInformation.setTitle("Test document");

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);
            //  -->>    start text
            contentStream.beginText();
            contentStream.newLineAtOffset(25f, 725f);
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.HELVETICA, 12f);
            contentStream.setCharacterSpacing(3f);
            int recorded = 0;
            for (File file : fileNamesList) {
                contentStream.showText(getStringToDoc(file));
                contentStream.newLine();
                recorded++;
                if (recorded == LINES_COUNT) {
                    document.addPage(new PDPage());
                }
            }
            contentStream.endText();
            //  -->>    end text
            contentStream.close();

            fileNamesList.stream()
                    .forEach(System.out::println);
            document.save(new File("src/main/resources/file-storage/PDF/formattedFileListText.pdf"));
            // closing
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<File> getFileList() {
        List<File> fileNamesList = new ArrayList<>();
        try {
            fileNamesList = Files.walk(Path.of("/home/vitaliy/Util_Progs"))
                    .map(Path::toFile)
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNamesList;
    }

    private String getStringToDoc(File file) {
        return " ".repeat(file.getParent().split("/").length) + file.getName();
    }
}
