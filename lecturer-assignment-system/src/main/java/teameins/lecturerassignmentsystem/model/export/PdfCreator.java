package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class PdfCreator extends FileCreator{

    public PdfCreator(List<LecturerReportEntity> allValidLecturers, ReportMode reportMode) {
        super(allValidLecturers, reportMode);
    }

    @Override
    public byte[] createFile() {
        try {
            PdfDocumentBuilder builder = new PdfDocumentBuilder();

            builder.writeHeading("Report");
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
                if (reportMode != ReportMode.ALL_COURSES_WITH_NO_LECTURERS || obj instanceof CourseReportEntity) {
                    builder.writeLabelWithValue(label, value != null ? value.toString() : "null", PdfDocumentBuilder.MARGIN_LEFT + indent);
                }
            } else {
                List<?> list = (List<?>) field.get(obj);

                if (reportMode != ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
                    builder.writeLabel(label, PdfDocumentBuilder.MARGIN_LEFT + indent);
                } else {
                    builder.writeLabel("Kurse ohne Dozenten", PdfDocumentBuilder.MARGIN_LEFT + indent);
                }
                if (!list.isEmpty()) {
                    builder.writeTable(list);
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
        private static final float ROW_HEIGHT        = LINE_HEIGHT_BODY + 20f;

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

        public void writeTable(List<?> items) throws IOException, IllegalAccessException {
            if (items.isEmpty()) return;

            Class<?> itemClass = items.get(0).getClass();
            Field[] fields = itemClass.getDeclaredFields();
            for (Field f : fields) f.setAccessible(true);

            float tableWidth = PDRectangle.A4.getWidth() - MARGIN_LEFT * 2;
            float colWidth   = tableWidth / fields.length;
            float tableX     = MARGIN_LEFT;

            var bold  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            var plain = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // Header schreiben
            writeTableHeader(fields, tableX, tableWidth, colWidth, bold);

            // Datenzeilen
            for (Object item : items) {
                y -= ROW_HEIGHT;
                if (y < 50) {
                    newPage();
                    y = MARGIN_TOP - ROW_HEIGHT;
                    writeTableHeader(fields, tableX, tableWidth, colWidth, bold);
                    y -= ROW_HEIGHT;
                }

                for (int i = 0; i < fields.length; i++) {
                    Object val = fields[i].get(item);
                    String text = val != null ? val.toString() : "-";
                    cs.beginText();
                    cs.setFont(plain, 9f);
                    cs.newLineAtOffset(tableX + i * colWidth + 3f, y);
                    cs.showText(truncate(text, colWidth - 6f, plain, 9f));
                    cs.endText();
                }

                for (int i = 0; i <= fields.length; i++) {
                    drawVerticalLine(tableX + i * colWidth, y + ROW_HEIGHT, y - 4f);
                }
                drawHorizontalLine(tableX, y - 4f, tableWidth);
            }

            y -= LIST_SECTION_GAP;
        }

        // --- Private Hilfsmethoden ---

        private void writeTableHeader(Field[] fields, float tableX, float tableWidth, float colWidth, PDType1Font bold) throws IOException {
            // Obere Kante
            drawHorizontalLine(tableX, y, tableWidth);

            y -= ROW_HEIGHT;
            if (y < 50) { newPage(); y = MARGIN_TOP - ROW_HEIGHT; }

            drawRowBackground(tableX, y - 4f, tableWidth, ROW_HEIGHT);

            for (int i = 0; i < fields.length; i++) {
                JsonProperty jp = fields[i].getAnnotation(JsonProperty.class);
                String header = jp != null ? jp.value() : fields[i].getName();
                writeWrappedTableCell(header, tableX + i * colWidth, y, colWidth - 6f, bold, 9f);
            }

            for (int i = 0; i <= fields.length; i++) {
                drawVerticalLine(tableX + i * colWidth, y + ROW_HEIGHT, y - 4f);
            }
            drawHorizontalLine(tableX, y - 4f, tableWidth);
        }

        private void writeWrappedTableCell(String text, float x, float startY, float maxWidth, PDType1Font font, float fontSize) throws IOException {
            String[] words = text.split(" ");
            StringBuilder currentLine = new StringBuilder();
            float cellY = startY;

            for (String word : words) {
                String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                float lineWidth = font.getStringWidth(testLine) / 1000 * fontSize;
                if (lineWidth > maxWidth && !currentLine.isEmpty()) {
                    cs.beginText();
                    cs.setFont(font, fontSize);
                    cs.newLineAtOffset(x + 3f, cellY);
                    cs.showText(currentLine.toString());
                    cs.endText();
                    currentLine = new StringBuilder(word);
                    cellY -= (fontSize + 2f);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
            if (!currentLine.isEmpty()) {
                cs.beginText();
                cs.setFont(font, fontSize);
                cs.newLineAtOffset(x + 3f, cellY);
                cs.showText(currentLine.toString());
                cs.endText();
            }
        }

        private void writeWrappedText(String text, float x, float maxWidth, PDType1Font font, float fontSize) throws IOException {
            String[] words = text.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                float lineWidth = font.getStringWidth(testLine) / 1000 * fontSize;
                if (lineWidth > maxWidth && !currentLine.isEmpty()) {
                    beginLine(x, LINE_HEIGHT_BODY);
                    cs.setFont(font, fontSize);
                    cs.showText(currentLine.toString());
                    cs.endText();
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
            if (!currentLine.isEmpty()) {
                beginLine(x, LINE_HEIGHT_BODY);
                cs.setFont(font, fontSize);
                cs.showText(currentLine.toString());
                cs.endText();
            }
        }

        private void drawHorizontalLine(float x, float lineY, float width) throws IOException {
            cs.setLineWidth(0.5f);
            cs.moveTo(x, lineY);
            cs.lineTo(x + width, lineY);
            cs.stroke();
        }

        private void drawVerticalLine(float x, float fromY, float toY) throws IOException {
            cs.setLineWidth(0.5f);
            cs.moveTo(x, fromY);
            cs.lineTo(x, toY);
            cs.stroke();
        }

        private void drawRowBackground(float x, float lineY, float width, float height) throws IOException {
            cs.setNonStrokingColor(0.9f, 0.9f, 0.9f);
            cs.addRect(x, lineY, width, height);
            cs.fill();
            cs.setNonStrokingColor(0f, 0f, 0f);
        }

        private String truncate(String text, float maxWidth, PDType1Font font, float fontSize) throws IOException {
            float width = font.getStringWidth(text) / 1000 * fontSize;
            if (width <= maxWidth) return text;
            while (text.length() > 1) {
                text = text.substring(0, text.length() - 1);
                if (font.getStringWidth(text + "…") / 1000 * fontSize <= maxWidth) return text + "…";
            }
            return text;
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
