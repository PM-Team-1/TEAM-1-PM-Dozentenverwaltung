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
import java.util.ArrayList;
import java.util.List;

public class PdfCreator extends FileCreator{

    private static final float MARGIN = 40;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LINE_HEIGHT = 12;
    private static final float FONT_SIZE = 10;
    private static final float TABLE_FONT_SIZE = 9;
    private static final float CHAR_WIDTH = 4.3f;
    private static final float BOLD_CHAR_WIDTH = 5.2f;
    private static final int MAX_CHARS_PER_LINE = 100;

    public PdfCreator(List<LecturerReportEntity> allValidLecturers, ReportMode reportMode) {
        super(allValidLecturers, reportMode);
    }

    @Override
    public byte[] createFile() {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            if (allValidLecturers.isEmpty()) {
                // Create at least one empty page
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
            } else {
                for (LecturerReportEntity lecturer : allValidLecturers) {
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), FONT_SIZE);
                        
                        float currentY = PAGE_HEIGHT - MARGIN; // Reset Y position for each page
                        currentY = drawLecturerData(contentStream, lecturer, currentY);
                        
                        // Draw courses table
                        if (lecturer.getCanHoldCourses() != null && !lecturer.getCanHoldCourses().isEmpty()) {
                            currentY = drawCourseLabel(contentStream, currentY);
                            currentY = drawCoursesTable(contentStream, lecturer.getCanHoldCourses(), currentY, page);
                        }
                    }
                }
            }
            
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Erstellen des PDF-Dokuments", e);
        }
    }

    private float drawLecturerData(PDPageContentStream contentStream, LecturerReportEntity lecturer, float startY) throws IOException {
        float currentY = startY;
        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        
        Field[] fields = LecturerReportEntity.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getName().equals("canHoldCourses")) {
                continue; // Handle separately
            }
            
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(lecturer);
                    String label = jsonProperty.value();
                    String displayValue = value != null ? value.toString() : "";
                    
                    // Check if we need a new page
                    if (currentY - LINE_HEIGHT < MARGIN) {
                        currentY = PAGE_HEIGHT - MARGIN;
                    }
                    
                    // Draw label in bold
                    contentStream.beginText();
                    contentStream.setFont(boldFont, FONT_SIZE);
                    contentStream.newLineAtOffset(MARGIN, currentY);
                    contentStream.showText(label + ":");
                    contentStream.endText();
                    
                    // Draw value in regular font
                    contentStream.beginText();
                    contentStream.setFont(regularFont, FONT_SIZE);
                    contentStream.newLineAtOffset(MARGIN + 150, currentY); // Offset for value
                    contentStream.showText(displayValue);
                    contentStream.endText();
                    
                    currentY -= LINE_HEIGHT;
                } catch (IllegalAccessException e) {
                    // Skip field if access fails
                }
            }
        }
        // Smaller space before table
        return currentY;
    }

    private float drawCourseLabel(PDPageContentStream contentStream, float startY) throws IOException {
        float currentY = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        
        // Check if we need a new page
        if (currentY - LINE_HEIGHT < MARGIN) {
            currentY = PAGE_HEIGHT - MARGIN;
        }
        
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText("Kann Kurse halten:");
        contentStream.endText();
        
        return currentY - LINE_HEIGHT * 0.8f;
    }

    private float drawCoursesTable(PDPageContentStream contentStream, List<CourseReportEntity> courses, float startY, PDPage page) throws IOException {
        if (courses.isEmpty()) {
            return startY;
        }
        
        float currentY = startY;
        
        // Get table headers from CourseReportEntity
        List<String> headers = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = CourseReportEntity.class.getDeclaredFields();
        
        for (Field field : fields) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                headers.add(jsonProperty.value());
                fieldNames.add(field.getName());
            }
        }
        
        // Calculate column widths
        float tableWidth = PAGE_WIDTH - (2 * MARGIN);
        float columnWidth = tableWidth / headers.size();
        
        // Draw header row
        currentY = drawTableHeaderRow(contentStream, headers, columnWidth, currentY);
        
        // Draw data rows
        for (CourseReportEntity course : courses) {
            List<String> rowData = new ArrayList<>();
            for (String fieldName : fieldNames) {
                try {
                    Field field = CourseReportEntity.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(course);
                    rowData.add(value != null ? value.toString() : "");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    rowData.add("");
                }
            }
            
            currentY = drawTableRow(contentStream, rowData, columnWidth, currentY, page);
        }
        
        return currentY - LINE_HEIGHT;
    }

    private float drawTableHeaderRow(PDPageContentStream contentStream, List<String> headers, float columnWidth, float startY) throws IOException {
        float currentY = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        
        contentStream.setFont(font, TABLE_FONT_SIZE);
        
        // First, calculate the row height
        float rowHeight = LINE_HEIGHT;
        for (String header : headers) {
            int lineCount = getLineCount(header, (int)(columnWidth / BOLD_CHAR_WIDTH));
            rowHeight = Math.max(rowHeight, lineCount * LINE_HEIGHT);
        }
        
        // Draw header content
        float xPosition = MARGIN;
        for (String header : headers) {
            List<String> lines = wrapTextToLines(header, (int)(columnWidth / BOLD_CHAR_WIDTH));
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
        
        // Draw table grid (all borders including top)
        float cellX = MARGIN;
        float tableEndX = PAGE_WIDTH - MARGIN;
        float headerTopY = startY;
        float headerBottomY = currentY;
        
        contentStream.setStrokingColor(0, 0, 0);
        contentStream.setLineWidth(0.5f);
        
        // Draw top line
        contentStream.moveTo(MARGIN, headerTopY);
        contentStream.lineTo(tableEndX, headerTopY);
        contentStream.stroke();
        
        // Draw bottom line
        contentStream.moveTo(MARGIN, headerBottomY);
        contentStream.lineTo(tableEndX, headerBottomY);
        contentStream.stroke();
        
        // Draw left border
        contentStream.moveTo(MARGIN, headerTopY);
        contentStream.lineTo(MARGIN, headerBottomY);
        contentStream.stroke();
        
        // Draw right border
        contentStream.moveTo(tableEndX, headerTopY);
        contentStream.lineTo(tableEndX, headerBottomY);
        contentStream.stroke();
        
        // Draw vertical lines for columns
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
        
        // First pass: determine row height
        float rowHeight = LINE_HEIGHT * 1.2f;
        for (String data : rowData) {
            int lineCount = getLineCount(data, (int)(columnWidth / CHAR_WIDTH));
            rowHeight = Math.max(rowHeight, lineCount * LINE_HEIGHT);
        }
        
        // Check if we need a new page
        if (currentY - rowHeight < MARGIN) {
            currentY = PAGE_HEIGHT - MARGIN;
        }
        
        float rowTopY = currentY;
        float rowBottomY = currentY - rowHeight;
        
        // Draw cell content
        float xPosition = MARGIN;
        for (String data : rowData) {
            List<String> lines = wrapTextToLines(data, (int)(columnWidth / CHAR_WIDTH));
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
        
        // Draw cell borders
        float tableEndX = PAGE_WIDTH - MARGIN;
        
        contentStream.setStrokingColor(0, 0, 0);
        contentStream.setLineWidth(0.5f);
        
        // Draw bottom line
        contentStream.moveTo(MARGIN, rowBottomY-3.5f);
        contentStream.lineTo(tableEndX, rowBottomY-3.5f);
        contentStream.stroke();
        
        // Draw left border
        contentStream.moveTo(MARGIN, rowTopY+10);
        contentStream.lineTo(MARGIN, rowBottomY-3.5f);
        contentStream.stroke();
        
        // Draw right border
        contentStream.moveTo(tableEndX, rowTopY+10);
        contentStream.lineTo(tableEndX, rowBottomY-3.5f);
        contentStream.stroke();
        
        // Draw vertical lines for columns
        float cellX = MARGIN + columnWidth;
        for (int i = 1; i < rowData.size(); i++) {
            contentStream.moveTo(cellX, rowTopY+10);
            contentStream.lineTo(cellX, rowBottomY-3.5f);
            contentStream.stroke();
            cellX += columnWidth;
        }
        
        return rowBottomY - 3.5f - LINE_HEIGHT * 0.3f;
    }

    private String wrapText(String text, int maxCharsPerLine) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        List<String> lines = wrapTextToLines(text, maxCharsPerLine);
        return String.join("\n", lines);
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
            // If a single word is longer than maxCharsPerLine, split it with hyphen
            if (word.length() > maxCharsPerLine) {
                // Add current line if it has content
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                
                // Split long word with hyphens
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
                // Check if adding the word would exceed the line length
                String potentialLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                
                if (potentialLine.length() > maxCharsPerLine && !currentLine.isEmpty()) {
                    // Current line is full, add it and start a new line with this word
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Add word to current line
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }
        }
        
        // Add remaining line
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // Ensure at least one line
        if (lines.isEmpty()) {
            lines.add("");
        }
        
        return lines;
    }

    private int getLineCount(String text, int maxCharsPerLine) {
        return wrapTextToLines(text, maxCharsPerLine).size();
    }
}
