package Utilities;

import Beans.Spesa;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePDF {

    public static ByteArrayOutputStream getPdfFile(List<Spesa> spese) {

        Document document = new Document();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(90);
            table.setWidths(new int[]{7, 3, 3});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("Descrizione", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Data", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Importo", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);
            float totale = 0;
            for (Spesa spesa : spese) {
                totale += spesa.getValue();
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(spesa.getDescription()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(spesa.getDate().toString()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.format("%.2f", spesa.getValue())+"€"));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell.setPaddingRight(5);
                table.addCell(cell);
            }

            PdfPTable tableEnd = new PdfPTable(2);
            tableEnd.setWidthPercentage(90);
            tableEnd.setWidths(new int[]{10, 3});

            PdfPCell finalCell;
            finalCell = new PdfPCell(new Phrase("TOTALE: ", headFont));
            finalCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            finalCell.setVerticalAlignment(Element.ALIGN_CENTER);
            tableEnd.addCell(finalCell);


            finalCell = new PdfPCell(new Phrase(String.format("%.2f", totale)+"€", headFont));
            finalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            finalCell.setVerticalAlignment(Element.ALIGN_CENTER);
            tableEnd.addCell(finalCell);

            PdfWriter.getInstance(document, bout);
            document.open();
            document.add(table);
            document.add(tableEnd);

            document.close();

        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bout;
    }
}
