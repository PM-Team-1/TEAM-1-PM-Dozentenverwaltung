package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.export.FileCreator;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private MappingService mappingService;

    @InjectMocks
    private ExportService exportService;

    private List<LecturerReportEntity> reportEntities;
    private FileCreator mockFileCreator;

    @BeforeEach
    void setUp() {
        reportEntities = List.of(new LecturerReportEntity("Dr.", "John", "Doe", "", "john.doe@example.com", "123456", false, "Alles", List.of()));
        mockFileCreator = mock(FileCreator.class);
    }

    @Test
    void testExportFile_Success() {
        byte[] expectedBytes = "test data".getBytes();
        when(mockFileCreator.createFile()).thenReturn(expectedBytes);

        try (MockedStatic<FileCreator> mockedStatic = mockStatic(FileCreator.class)) {
            mockedStatic.when(() -> FileCreator.createCreator(any(FileCreationMode.class), eq(reportEntities), any(ReportMode.class)))
                    .thenReturn(mockFileCreator);

            byte[] result = exportService.exportFile(FileCreationMode.CSV, ReportMode.ALL_COURSES_IN_PROVADIS, reportEntities);

            assertNotNull(result);
            assertArrayEquals(expectedBytes, result);
            mockedStatic.verify(() -> FileCreator.createCreator(FileCreationMode.CSV, reportEntities, ReportMode.ALL_COURSES_IN_PROVADIS), times(1));
            verify(mockFileCreator, times(1)).createFile();
        }
    }

    @Test
    void testExportFile_EmptyReportEntities() {
        List<LecturerReportEntity> emptyList = List.of();

        byte[] result = exportService.exportFile(FileCreationMode.PDF, ReportMode.ALL_COURSES_WITH_NO_LECTURERS, emptyList);

        assertNotNull(result);
        assertArrayEquals(new byte[]{}, result);
    }

    // Weitere Tests für verschiedene FileCreationMode (CSV, JSON, PDF) können ähnlich hinzugefügt werden
}
