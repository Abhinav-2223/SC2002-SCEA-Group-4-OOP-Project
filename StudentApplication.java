import enums.ApplicationStatus;
import enums.WithdrawalDecision;

public class StudentApplication {
    private static int counter = 1;

    private String applicationID;
    private Student student;
    private Internships internship;
    private ApplicationStatus appStatus;
    private WithdrawalDecision withdrawDecision;

    public StudentApplication(Student student, Internships internship) {
        this.applicationID = "APP" + String.format("%03d", counter++);
        this.student = student;
        this.internship = internship;
        this.appStatus = ApplicationStatus.PENDING;
        this.withdrawDecision = WithdrawalDecision.NONE;
    }

    //getters
    public String getApplicationID() { return applicationID; }
    public Student getStudent() { return student; }
    public Internships getInternship() { return internship; }
    public ApplicationStatus getAppStatus() { return appStatus; }
    public WithdrawalDecision getWithdrawDecision() { return withdrawDecision; }

    //setters
    public void setAppStatus(ApplicationStatus appStatus) { this.appStatus = appStatus; }
    public void setWithdrawDecision(WithdrawalDecision withdrawDecision) { this.withdrawDecision = withdrawDecision; }

    //display info
    public void displayApplicationDetails() {
        System.out.println("Application ID: " + applicationID);
        System.out.println("Internship: " + internship.getTitle());
        System.out.println("Status: " + appStatus);
        System.out.println("Withdrawal Decision: " + withdrawDecision);
    }
}
