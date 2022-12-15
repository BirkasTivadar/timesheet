package timesheet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectTest {
    Project project = new Project("Java");

    @Test
    void createProjectTest() {
        assertEquals("Java", project.name());
    }
}