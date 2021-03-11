package socialnetwork;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CreatePdf {

    /**
     * Creeaza un fisier pdf
     * @param lista ->textul  de pus pe pdf
     * @param dest ->path, locul in care se pune fisierul
     */
    public void creare(List<String> lista, String dest) {

        try {
            PDDocument pdDoc = new PDDocument();

            PDPage page = new PDPage();
            // add page to the document
            pdDoc.addPage(page);
            // write to a page content stream
            try(PDPageContentStream cs = new PDPageContentStream(pdDoc, page)){

                // setting font family and font size
                cs.setFont(PDType1Font.TIMES_ITALIC, 12);
                // Text color in PDF
                cs.setNonStrokingColor(Color.BLUE);
                // set offset from where content starts in PDF
                //cs.newLineAtOffset(20, 750);

               // cs.newLine();
                cs.beginText();
                cs.newLineAtOffset(10, 400);

                for(String s: lista) {

                    cs.showText(s);
                    //cs.newLine();
                    cs.newLineAtOffset(0, -15);


                }
                cs.endText();


            } catch (IOException e) {
                e.printStackTrace();
            }

            pdDoc.save(dest);
            pdDoc.close();
        } catch(IOException e) {

           e.printStackTrace();
        }
    }
}