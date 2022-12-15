package timesheet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeTest {
    Employee employee = new Employee("John", "Connor");

    @Test
    void createEmployee() {
        assertEquals("John Connor", employee.getName());
    }
}