package teameins.lecturerassignmentsystem.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import teameins.lecturerassignmentsystem.model.export.CsvCreator;
import teameins.lecturerassignmentsystem.model.export.FileCreator;
import teameins.lecturerassignmentsystem.model.export.JsonCreator;
import teameins.lecturerassignmentsystem.model.export.PdfCreator;

@AllArgsConstructor
@Getter
public enum FileCreationMode {
    CSV(".csv", "text/csv; charset=UTF-8", CsvCreator.class),
    PDF(".pdf", "application/pdf", PdfCreator.class),
    JSON(".json", "application/json", JsonCreator.class),;

    private final String fileEnd;
    private final String contentType;
    private final Class<?> creatorClass;
}
