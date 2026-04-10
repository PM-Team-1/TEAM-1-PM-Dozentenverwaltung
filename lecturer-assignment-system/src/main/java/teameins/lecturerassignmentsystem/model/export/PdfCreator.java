package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class PdfCreator extends FileCreator{

    public PdfCreator(List<LecturerReportEntity> allValidLecturers) {
        super(allValidLecturers);
    }

    @Override
    public byte[] createFile() {
        try {
            PdfDocumentBuilder builder = new PdfDocumentBuilder();

            builder.writeHeading("Modus");
            builder.writeItemSeparator();

            for (LecturerReportEntity lecturerReportEntity : allValidLecturers) {
                writeEntity(lecturerReportEntity, builder, PdfDocumentBuilder.INDENT_ITEM);
                builder.writeItemSeparator();
            }

            return builder.toByteArray();

        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeEntity(Object obj, PdfDocumentBuilder builder, float indent) throws IOException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            String label = jsonProperty != null ? jsonProperty.value() : field.getName();
            if(!field.getType().equals(List.class)) {
                Object value = field.get(obj);

                builder.writeLabelWithValue(label, value != null ? value.toString() : "null", PdfDocumentBuilder.MARGIN_LEFT + indent);
            } else {
                List<?> list = (List<?>) field.get(obj);
                builder.writeLabel(label, PdfDocumentBuilder.MARGIN_LEFT + indent);
                for(Object item : list) {
                    builder.writeListSeparator();
                    writeEntity(item, builder, PdfDocumentBuilder.INDENT_SUB);
                }
            }
        }
    }

    private class PdfDocumentBuilder {
        // Layout-Konstanten
        private static final float MARGIN_LEFT       = 50f;
        private static final float MARGIN_TOP        = 750f;
        private static final float INDENT_ITEM       = 20f;   // Einrückung Hauptlisten-Item
        private static final float INDENT_SUB        = 60f;   // Einrückung Sub-Liste
        private static final float LINE_HEIGHT_TITLE = 24f;
        private static final float LINE_HEIGHT_BODY  = 16f;
        private static final float ITEM_SECTION_GAP  = 10f;
        private static final float LIST_SECTION_GAP  = 5f;

        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream cs;
        private float y;

        public PdfDocumentBuilder() throws IOException {
            this.document = new PDDocument();
            newPage();
        }

        // --- Öffentliche Schreib-Methoden ---

        public void writeHeading(String text) throws IOException {
            var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            beginLine(MARGIN_LEFT, LINE_HEIGHT_TITLE + ITEM_SECTION_GAP);
            cs.setFont(font, 18f);
            cs.showText(text);
            cs.endText();
        }

        public void writeLabelWithValue(String label, String value, float x) throws IOException {
            var bold  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            var plain = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            beginLine(x, LINE_HEIGHT_BODY);
            cs.setFont(bold, 11f);
            cs.showText(label + ": ");
            cs.setFont(plain, 11f);
            cs.showText(value);
            cs.endText();
        }

        public void writeLabel(String label, float x) throws IOException {
            var bold  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            beginLine(x, LINE_HEIGHT_BODY);
            cs.setFont(bold, 11f);
            cs.showText(label + ": ");
            cs.endText();
        }

        public void writeItemSeparator() throws IOException {
            y -= ITEM_SECTION_GAP;
        }

        public void writeListSeparator() throws IOException {
            y -= LIST_SECTION_GAP;
        }

        public byte[] toByteArray() throws IOException {
            cs.close();
            var baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            return baos.toByteArray();
        }

        // --- Private Hilfsmethoden ---

        private void beginLine(float x, float lineHeight) throws IOException {
            y -= lineHeight;
            if (y < 50) {
                newPage();
                y = MARGIN_TOP - lineHeight;
            }
            cs.beginText();
            cs.newLineAtOffset(x, y);
        }

        private void newPage() throws IOException {
            if (cs != null) cs.close();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            cs = new PDPageContentStream(document, page);
            y = MARGIN_TOP;
        }
    }
}
