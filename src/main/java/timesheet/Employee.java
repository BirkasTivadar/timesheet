package timesheet;

public record Employee(String firstname, String lastname) {

    public String getName() {
        return String.format("%s %s", firstname, lastname);
    }
}
