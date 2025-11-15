import enums.InternshipLevel;
import enums.OpportunityStatus;

import java.util.List;

public class Internships {
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

    // constructor
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
        this.isVisible = visibility;
        this.isApprovedByStaff = (oppStatus == OpportunityStatus.VACANT);
    }

    // getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public InternshipLevel getInternshipLevel() { return internshipLevel; }
    public String getPreferredMajor() { return preferredMajor; }
    public int getPreferredYear() { return preferredYear; }
    public int getOpeningDate() { return openingDate; }
    public int getClosingDate() { return closingDate; }
    public OpportunityStatus getOpportunityStatus() { return oppStatus; }
    public String getCompanyName() { return companyName; }
    public String[] getCompanyReps() { return companyReps; }
    public int getSlots() { return slots; }
    public boolean isVisible() { return isVisible; }
    public boolean isApprovedByStaff() { return isApprovedByStaff; }

    // setters
    public void setOpportunityStatus(OpportunityStatus status) { 
        this.oppStatus = status;
        this.isApprovedByStaff = (status == OpportunityStatus.VACANT);
    }
    public void setVisible(boolean visible) { this.isVisible = visible; }
    public void setSlots(int slots) { this.slots = slots; }

    // check if internship can be applied to
    public boolean canApply() {
        return isVisible && isApprovedByStaff && oppStatus != OpportunityStatus.FILLED;
    }

    // static method to get all visible internships from CSV
    public static List<Internships> getAllVisibleInternships() {
        return CSVUtils.readInternshipsFromCSV(Internships::isVisible);
    }
}