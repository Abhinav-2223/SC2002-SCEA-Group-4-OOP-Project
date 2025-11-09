import enums.InternshipLevel;
import enums.OpportunityStatus;

public class Internships {
    // TODO: fix all these
    private String title;
    private String description;
    private InternshipLevel internshipLevel;
    private String preferredMajor;
    private int preferredYear;
    private int openingDate;
    private int closingDate;
    private OpportunityStatus oppStatus;
    private String companyName;
    private String[] companyReps;
    private int slots;
    private boolean isVisible;
    private boolean isApprovedByStaff;

    // note: each internship can have >= 1 companyrep representing it
    // TODO: check and edit this
    public Internships(String title, String description, InternshipLevel internshipLevel,
                       String preferredMajor, int preferredYear, int openingDate, int closingDate,
                       OpportunityStatus oppStatus, String companyName,
                       String[] companyReps, int slots, boolean visibility) {
        this.title = title;
        this.description = description;
        this.internshipLevel = internshipLevel;
        this.preferredMajor = preferredMajor;
        this.preferredYear = preferredYear;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.oppStatus = oppStatus;
        this.companyName = companyName;
        this.companyReps = companyReps;
        this.slots = slots;
        this.isVisible = false;
    }
}