package timesheet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Company {

    private final List<Employee> employees = new ArrayList<>();

    private final List<Project> projects = new ArrayList<>();

    List<TimeSheetItem> timeSheetItems = new ArrayList<>();

    public Company(InputStream employeeIS, InputStream projectIS) {
        loadEmployeesFromFile(employeeIS);
        loadProjectsFromFile(projectIS);
    }

    private void loadProjectsFromFile(InputStream projectIS) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(projectIS))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                projects.add(new Project(line));
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Can not read file", ioException);
        }
    }

    private void loadEmployeesFromFile(InputStream employeeIS) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(employeeIS))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strArr = line.split(" ");
                employees.add(new Employee(strArr[0], strArr[1]));
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Can not read file", ioException);
        }
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public void addTimeSheetItem(Employee employee, Project project, LocalDateTime beginDate, LocalDateTime endDate) {
        timeSheetItems.add(new TimeSheetItem(employee, project, beginDate, endDate));
    }

    public List<ReportLine> calculateProjectByNameYearMonth(String employeeName, int year, int month) {
        Employee employee = employees.stream()
                .filter(e -> e.getName().equals(employeeName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Wrong name"));

        List<ReportLine> result = new ArrayList<>();
        projects.forEach(project -> result.add(new ReportLine(project, 0)));

        List<TimeSheetItem> timeSheetItemsOfEmployee = getTimeSheetItemsOfEmployee(employee, year, month);

        loadResultFromTimeSheetItems(result, timeSheetItemsOfEmployee);

        return result;
    }

    private List<TimeSheetItem> getTimeSheetItemsOfEmployee(Employee employee, int year, int month) {
        return timeSheetItems.stream()
                .filter(timeSheetItem ->
                        timeSheetItem.employee().equals(employee)
                                && timeSheetItem.beginDate().getYear() == year
                                && timeSheetItem.beginDate().getMonth().getValue() == month)
                .toList();
    }

    private void loadResultFromTimeSheetItems(List<ReportLine> result, List<TimeSheetItem> timeSheetItemsOfEmployee) {
        result.forEach(
                reportLine -> reportLine.addTime(
                        timeSheetItemsOfEmployee.stream()
                                .filter(timeSheetItem -> timeSheetItem.project().equals(reportLine.getProject()))
                                .mapToLong(TimeSheetItem::hoursBetweenDates)
                                .sum()
                )
        );
    }

    public void printToFile(String employeeName, int year, int month, Path file) {
        List<ReportLine> reportLines = calculateProjectByNameYearMonth(employeeName, year, month);
        long sumHours = getSumHours(reportLines);

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(employeeName)
                    .append("\t")
                    .append(year)
                    .append("-")
                    .append(String.format("%02d", month))
                    .append("\t")
                    .append(sumHours)
                    .append(System.lineSeparator());
            writer.write(stringBuilder.toString());

            writeReports(reportLines, writer);

        } catch (IOException ioException) {
            throw new IllegalStateException("Can not write file", ioException);
        }
    }

    private long getSumHours(List<ReportLine> reportLines) {
        return reportLines.stream()
                .mapToLong(ReportLine::getTime)
                .sum();
    }

    private void writeReports(List<ReportLine> reportLines, BufferedWriter writer) {
        reportLines.stream()
                .filter(reportLine -> reportLine.getTime() != 0)
                .forEach(reportLine -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        stringBuilder
                                .append(reportLine.getProject().name())
                                .append("\t")
                                .append(reportLine.getTime())
                                .append(System.lineSeparator());
                        writer.write(stringBuilder.toString());
                    } catch (IOException ioException) {
                        throw new IllegalStateException("Can not write file", ioException);
                    }
                });
    }
}

