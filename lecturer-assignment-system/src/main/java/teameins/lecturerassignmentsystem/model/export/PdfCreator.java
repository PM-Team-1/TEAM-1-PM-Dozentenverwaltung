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
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PdfCreator extends FileCreator {

    private static final float MARGIN = 40;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LINE_HEIGHT = 12;
    private static final float FONT_SIZE = 10;
    private static final float TABLE_FONT_SIZE = 9;
    private static final float CHAR_WIDTH = 4.3f;
    private static final float BOLD_CHAR_WIDTH = 5.2f;

    public PdfCreator(List<?> reportEntities, ReportMode reportMode) {
        super(reportEntities, reportMode);
    }

    @Override
    public byte[] createFile() {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (reportEntities.isEmpty()) {
                document.addPage(new PDPage(PDRectangle.A4));
            } else if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
                // Report 3: nur Course-Felder, keine Tabelle
                for (CourseReportEntity course : asCourseList()) {
                    renderPage(document, course, CourseReportEntity.class, null, null, null);
                }
            } else if (reportMode.isCourseBased()) {
                for (CourseReportEntity course : asCourseList()) {
                    renderPage(document, course, CourseReportEntity.class,
                            "Kann von Dozenten gehalten werden:",
                            course.getCanBeHeldBy(),
                            LecturerReportEntity.class);
                }
            } else {
                for (LecturerReportEntity lecturer : asLecturerList()) {
                    renderPage(document, lecturer, LecturerReportEntity.class,
                            "Kann Kurse halten:",
                            lecturer.getCanHoldCourses(),
                            CourseReportEntity.class);
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Erstellen des PDF-Dokuments", e);
        }
    }

    private void renderPage(PDDocument document, Object outerEntity, Class<?> outerClass,
                            String tableLabel,
                            List<LecturerCanHoldCourseReportEntity> lchcList,
                            Class<?> innerClass) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), FONT_SIZE);

            float currentY = PAGE_HEIGHT - MARGIN;
            currentY = drawOuterData(contentStream, outerEntity, outerClass, currentY);

            if (tableLabel != null && lchcList != null && !lchcList.isEmpty()) {
                currentY = drawSectionLabel(contentStream, tableLabel, currentY);
                drawInnerTable(contentStream, lchcList, innerClass, currentY, page);
            }
        }
    }

    private float drawOuterData(PDPageContentStream contentStream, Object entity, Class<?> clazz, float startY) throws IOException {
        float currentY = startY;
        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        float labelOffset = 150;
        float valueX = MARGIN + labelOffset;
        float valueWidth = PAGE_WIDTH - MARGIN - valueX;

        for (Field field : getNonTransientFields(clazz)) {
            if (field.getType().equals(List.class)) continue;

            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            String label = jsonProperty != null ? jsonProperty.value() : field.getName();
            try {
                Object value = entity != null ? field.get(entity) : null;
                String displayValue = value != null ? value.toString() : "";

                List<String> valueLines = wrapTextByWidth(displayValue, regularFont, FONT_SIZE, valueWidth);
                float requiredHeight = LINE_HEIGHT * valueLines.size();

                if (currentY - requiredHeight < MARGIN) {
                    currentY = PAGE_HEIGHT - MARGIN;
                }

                // Label nur einmal in der ersten Zeile
                contentStream.beginText();
                contentStream.setFont(boldFont, FONT_SIZE);
                contentStream.newLineAtOffset(MARGIN, currentY);
                contentStream.showText(label + ":");
                contentStream.endText();

                // Wert ggf. mehrzeilig rendern
                float lineY = currentY;
                for (String line : valueLines) {
                    contentStream.beginText();
                    contentStream.setFont(regularFont, FONT_SIZE);
                    contentStream.newLineAtOffset(valueX, lineY);
                    contentStream.showText(line);
                    contentStream.endText();
                    lineY -= LINE_HEIGHT;
                }

                currentY -= requiredHeight;
            } catch (IllegalAccessException e) {
                // skip
            }
        }
        return currentY;
    }

    /**
     * Bricht einen Text anhand der tatsächlichen Glyphenbreite des angegebenen Fonts um,
     * sodass keine Zeile breiter als {@code maxWidth} wird. Lange "Worte" (z. B. Emails ohne
     * Leerzeichen) werden notfalls mit einem Trennstrich hart umgebrochen.
     */
    private List<String> wrapTextByWidth(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (textWidth(word, font, fontSize) > maxWidth) {
                // Aktuelle Zeile ggf. abschließen
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                // Wort zeichenweise mit Trennstrich aufbrechen
                StringBuilder part = new StringBuilder();
                for (int i = 0; i < word.length(); i++) {
                    char c = word.charAt(i);
                    String candidate = part.toString() + c + "-";
                    if (textWidth(candidate, font, fontSize) > maxWidth && part.length() > 0) {
                        lines.add(part.toString() + "-");
                        part.setLength(0);
                    }
                    part.append(c);
                }
                if (part.length() > 0) {
                    current.append(part);
                }
            } else {
                String potential = current.length() == 0 ? word : current + " " + word;
                if (textWidth(potential, font, fontSize) > maxWidth && current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    if (current.length() > 0) current.append(' ');
                    current.append(word);
                }
            }
        }
        if (current.length() > 0) {
            lines.add(current.toString());
        }
        if (lines.isEmpty()) {
            lines.add("");
        }
        return lines;
    }

    private float textWidth(String text, PDType1Font font, float fontSize) throws IOException {
        if (text == null || text.isEmpty()) return 0;
        try {
            return font.getStringWidth(text) / 1000f * fontSize;
        } catch (IllegalArgumentException ex) {
            // Falls einzelne Zeichen im Font nicht vorhanden sind, konservativ schätzen
            return text.length() * (fontSize * 0.6f);
        }
    }

    private float drawSectionLabel(PDPageContentStream contentStream, String label, float startY) throws IOException {
        float currentY = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        if (currentY - LINE_HEIGHT < MARGIN) {
            currentY = PAGE_HEIGHT - MARGIN;
        }

        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText(label);
        contentStream.endText();

        return currentY - LINE_HEIGHT * 0.8f;
    }

    private float drawInnerTable(PDPageContentStream contentStream,
                                 List<LecturerCanHoldCourseReportEntity> lchcList,
                                 Class<?> innerClass,
                                 float startY, PDPage page) throws IOException {
        if (lchcList.isEmpty()) {
            return startY;
        }

        float currentY = startY;

        // Headers/Felder: zuerst innere Entität, dann LCHC
        List<String> headers = new ArrayList<>();
        List<Field> innerFields = new ArrayList<>();
        for (Field field : getNonTransientFields(innerClass)) {
            if (field.getType().equals(List.class)) continue;
            JsonProperty jp = field.getAnnotation(JsonProperty.class);
            headers.add(jp != null ? jp.value() : field.getName());
            innerFields.add(field);
        }
        List<Field> lchcFields = new ArrayList<>();
        for (Field field : getNonTransientFields(LecturerCanHoldCourseReportEntity.class)) {
            if (field.getType().equals(List.class)) continue;
            JsonProperty jp = field.getAnnotation(JsonProperty.class);
            headers.add(jp != null ? jp.value() : field.getName());
            lchcFields.add(field);
        }

        float tableWidth = PAGE_WIDTH - (2 * MARGIN);
        float columnWidth = tableWidth / headers.size();

        currentY = drawTableHeaderRow(contentStream, headers, columnWidth, currentY);

        boolean takeCourse = (innerClass == CourseReportEntity.class);
        for (LecturerCanHoldCourseReportEntity lchc : lchcList) {
            Object innerEntity = takeCourse ? lchc.getCourse() : lchc.getLecturer();
            List<String> rowData = new ArrayList<>();
            appendStringValues(rowData, innerEntity, innerFields);
            appendStringValues(rowData, lchc, lchcFields);
            currentY = drawTableRow(contentStream, rowData, columnWidth, currentY, page);
        }

        return currentY - LINE_HEIGHT;
    }

    private void appendStringValues(List<String> row, Object obj, List<Field> fields) {
        for (Field field : fields) {
            if (obj == null) {
                row.add("");
                continue;
            }
            try {
                Object value = field.get(obj);
                row.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                row.add("");
            }
        }
    }

    private float drawTableHeaderRow(PDPageContentStream contentStream, List<String> headers, float columnWidth, float startY) throws IOException {
        float currentY = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        contentStream.setFont(font, TABLE_FONT_SIZE);

        // Innen-Padding der Zelle berücksichtigen (links 2pt, rechts ~2pt Sicherheit)
        float cellTextWidth = columnWidth - 4;

        // Erst die Zeilen pro Zelle berechnen, um die Zeilenhöhe zu bestimmen
        List<List<String>> wrappedHeaders = new ArrayList<>();
        float rowHeight = LINE_HEIGHT;
        for (String header : headers) {
            List<String> lines = wrapTextByWidth(header, font, TABLE_FONT_SIZE, cellTextWidth);
            wrappedHeaders.add(lines);
            rowHeight = Math.max(rowHeight, lines.size() * LINE_HEIGHT);
        }

        float xPosition = MARGIN;
        for (List<String> lines : wrappedHeaders) {
            float cellY = currentY - (LINE_HEIGHT * 0.8f);

            for (String line : lines) {
                contentStream.beginText();
                contentStream.setFont(font, TABLE_FONT_SIZE);
                contentStream.newLineAtOffset(xPosition + 2, cellY);
                contentStream.showText(line);
                contentStream.endText();
                cellY -= LINE_HEIGHT;
            }

            xPosition += columnWidth;
        }

        currentY -= rowHeight;

        float cellX;
        float tableEndX = PAGE_WIDTH - MARGIN;
        float headerTopY = startY;
        float headerBottomY = currentY;

        contentStream.setStrokingColor(0, 0, 0);
        contentStream.setLineWidth(0.5f);

        contentStream.moveTo(MARGIN, headerTopY);
        contentStream.lineTo(tableEndX, headerTopY);
        contentStream.stroke();

        contentStream.moveTo(MARGIN, headerBottomY);
        contentStream.lineTo(tableEndX, headerBottomY);
        contentStream.stroke();

        contentStream.moveTo(MARGIN, headerTopY);
        contentStream.lineTo(MARGIN, headerBottomY);
        contentStream.stroke();

        contentStream.moveTo(tableEndX, headerTopY);
        contentStream.lineTo(tableEndX, headerBottomY);
        contentStream.stroke();

        cellX = MARGIN + columnWidth;
        for (int i = 1; i < headers.size(); i++) {
            contentStream.moveTo(cellX, headerTopY);
            contentStream.lineTo(cellX, headerBottomY);
            contentStream.stroke();
            cellX += columnWidth;
        }

        return currentY - LINE_HEIGHT * 0.3f;
    }

    private float drawTableRow(PDPageContentStream contentStream, List<String> rowData, float columnWidth, float startY, PDPage page) throws IOException {
        float currentY = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        contentStream.setFont(font, TABLE_FONT_SIZE);

        // Innen-Padding der Zelle berücksichtigen
        float cellTextWidth = columnWidth - 4;

        // Erst Zeilen pro Zelle berechnen
        List<List<String>> wrappedCells = new ArrayList<>();
        float rowHeight = LINE_HEIGHT * 1.2f;
        for (String data : rowData) {
            List<String> lines = wrapTextByWidth(data, font, TABLE_FONT_SIZE, cellTextWidth);
            wrappedCells.add(lines);
            rowHeight = Math.max(rowHeight, lines.size() * LINE_HEIGHT);
        }

        if (currentY - rowHeight < MARGIN) {
            currentY = PAGE_HEIGHT - MARGIN;
        }

        float rowTopY = currentY;
        float rowBottomY = currentY - rowHeight;

        float xPosition = MARGIN;
        for (List<String> lines : wrappedCells) {
            float cellY = currentY - (LINE_HEIGHT * 0.8f);

            for (String line : lines) {
                contentStream.beginText();
                contentStream.setFont(font, TABLE_FONT_SIZE);
                contentStream.newLineAtOffset(xPosition + 2, cellY);
                contentStream.showText(line);
                contentStream.endText();
                cellY -= LINE_HEIGHT;
            }

            xPosition += columnWidth;
        }

        float tableEndX = PAGE_WIDTH - MARGIN;

        contentStream.setStrokingColor(0, 0, 0);
        contentStream.setLineWidth(0.5f);

        contentStream.moveTo(MARGIN, rowBottomY - 3.5f);
        contentStream.lineTo(tableEndX, rowBottomY - 3.5f);
        contentStream.stroke();

        contentStream.moveTo(MARGIN, rowTopY + 10);
        contentStream.lineTo(MARGIN, rowBottomY - 3.5f);
        contentStream.stroke();

        contentStream.moveTo(tableEndX, rowTopY + 10);
        contentStream.lineTo(tableEndX, rowBottomY - 3.5f);
        contentStream.stroke();

        float cellX = MARGIN + columnWidth;
        for (int i = 1; i < rowData.size(); i++) {
            contentStream.moveTo(cellX, rowTopY + 10);
            contentStream.lineTo(cellX, rowBottomY - 3.5f);
            contentStream.stroke();
            cellX += columnWidth;
        }

        return rowBottomY - 3.5f - LINE_HEIGHT * 0.3f;
    }

    private List<String> wrapTextToLines(String text, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word.length() > maxCharsPerLine) {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }

                int index = 0;
                while (index < word.length()) {
                    int endIndex = Math.min(index + maxCharsPerLine - 1, word.length());
                    String part = word.substring(index, endIndex);

                    if (endIndex < word.length()) {
                        lines.add(part + "-");
                    } else {
                        lines.add(part);
                    }
                    index = endIndex;
                }
            } else {
                String potentialLine = currentLine.isEmpty() ? word : currentLine + " " + word;

                if (potentialLine.length() > maxCharsPerLine && !currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        if (lines.isEmpty()) {
            lines.add("");
        }

        return lines;
    }

    private int getLineCount(String text, int maxCharsPerLine) {
        return wrapTextToLines(text, maxCharsPerLine).size();
    }
}
