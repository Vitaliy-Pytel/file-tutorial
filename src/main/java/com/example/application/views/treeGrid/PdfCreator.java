package com.example.application.views.treeGrid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

public class PdfCreator {
    private final int LINES_COUNT = 40;

    public void createPdf() {
        File ROOT = new File("src/main/resources/file-storage/");
        List<File> fileNamesList = getFileList();
        PDDocument document = new PDDocument();

        PDDocumentInformation documentInformation = document.getDocumentInformation();
        documentInformation.setAuthor("Vitaliy Pytel");
        documentInformation.setTitle("Test document");

        try {
            var listP = listOfPages(fileNamesList, LINES_COUNT);
            for (List<File> fileList : listP) {
                fileList.stream()
                                .forEach(System.out::println);
                System.out.println("-------------------------");
                writePage(fileList, document);
            }
            document.save(new File("src/main/resources/file-storage/PDF/formattedFileListText.pdf"));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<File> getFileList() {
        List<File> fileNamesList = new ArrayList<>();
        try {
            fileNamesList = Files.walk(Path.of("/home/vitaliy/Util_Progs/Discord"))
                    .map(Path::toFile)
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNamesList;
    }

    private String getStringToDoc(File file) {
        return file.isFile() ? " ".repeat(file.getParent().split("/").length) + file.getName()
                : "--" + file.getName();
    }

    private void writePage(List<File> list, PDDocument document) throws IOException {
        PDPage page = new PDPage();

        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        //  -->>    start text
        contentStream.beginText();
        contentStream.newLineAtOffset(25f, 725f);
        contentStream.setLeading(14.5f);
        contentStream.setFont(PDType1Font.HELVETICA, 12f);
        contentStream.setCharacterSpacing(3f);

        for (File file : list) {
            contentStream.showText(getStringToDoc(file));
            contentStream.newLine();
        }
        contentStream.endText();
        contentStream.close();
    }

    private List<List<File>> listOfPages(List<File> fileList, int lines) {
        List<List<File>> pagedList = new ArrayList<>();
        int count = 0;
        List<File> internal = new ArrayList<>();
        for (File file : fileList) {
            internal.add(file);
            count++;
            if (count % 20 == 0) {
                pagedList.add(internal);
                internal = new ArrayList<>();
            }
        }
        return pagedList;
    }
}
