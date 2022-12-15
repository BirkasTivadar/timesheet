package timesheet;

import java.time.Duration;
import java.time.LocalDateTime;

public record TimeSheetItem(Employee employee, Project project, LocalDateTime beginDate, LocalDateTime endDate) {

    public TimeSheetItem {
        if (!beginDate.toLocalDate().equals(endDate.toLocalDate())) {
            throw new IllegalArgumentException("Begin date and end date must be on the same day");
        }
        if (beginDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Begin date is after the end date");
        }
    }

    public long hoursBetweenDates() {
        return Duration.between(beginDate, endDate).toHours();
    }
}
