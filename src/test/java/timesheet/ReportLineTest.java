package timesheet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportLineTest {
    ReportLine reportLine = new ReportLine(new Project("Java"), 10);

    @Test
    void createReportLine() {
        assertEquals("Java", reportLine.getProject().name());
        assertEquals(10L, reportLine.getTime());
    }

    @Test
    void setTimeTest() {
        reportLine.addTime(2);
        assertEquals(12L, reportLine.getTime());
    }
}