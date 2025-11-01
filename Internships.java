import enums.InternshipLevel;
import enums.OpportunityStatus;

public class Internships {
    private String title;
    private String description;
    private InternshipLevel internshipLevel;
    private String preferredMajor;
    private int preferredYear;
    private int openingDate;
    private int closingDate;
    private OpportunityStatus opportunityStatus;
    private String companyName;
    private String[] companyReps;
    private int slots;
    private boolean isVisible;
    private boolean isApprovedByStaff;

    public Internships() {
        this.opportunityStatus = OpportunityStatus.PENDING;
        this.companyReps = new String[0];
        this.slots = 0;
        this.isVisible = false;
        this.isApprovedByStaff = false;
    }

    public Internships(String title, String description, InternshipLevel internshipLevel,
                      String preferredMajor, int preferredYear, int openingDate, int closingDate,
                      OpportunityStatus opportunityStatus, String companyName,
                      String[] companyReps, int slots) {
        this.title = title;
        this.description = description;
        this.internshipLevel = internshipLevel;
        this.preferredMajor = preferredMajor;
        this.preferredYear = preferredYear;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.opportunityStatus = opportunityStatus;
        this.companyName = companyName;
        this.companyReps = companyReps;
        this.slots = slots;
        this.isVisible = false;
        this.isApprovedByStaff = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public InternshipLevel getInternshipLevel() {
        return internshipLevel;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }
    public int getPreferredYear() {
        return preferredYear;
    }

    public int getOpeningDate() {
        return openingDate;
    }

    public int getClosingDate() {
        return closingDate;
    }

    public OpportunityStatus getOpportunityStatus() {
        return opportunityStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String[] getCompanyReps() {
        return companyReps;
    }

    public int getSlots() {
        return slots;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isApprovedByStaff() {
        return isApprovedByStaff;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInternshipLevel(InternshipLevel internshipLevel) {
        this.internshipLevel = internshipLevel;
    }

    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public void setOpeningDate(int openingDate) {
        this.openingDate = openingDate;
    }

    public void setClosingDate(int closingDate) {
        this.closingDate = closingDate;
    }

    public void setOpportunityStatus(OpportunityStatus opportunityStatus) {
        this.opportunityStatus = opportunityStatus;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyReps(String[] companyReps) {
        this.companyReps = companyReps;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setApprovedByStaff(boolean isApprovedByStaff) {
        this.isApprovedByStaff = isApprovedByStaff;
    }

    public void toggleVisibility() {
        this.isVisible = !this.isVisible;
    }

    public boolean canApply() {
        return opportunityStatus==OpportunityStatus.APPROVED && isVisible && slots>0 && !isPastClosingDate();
    }

    public boolean isPastClosingDate() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentDay = now.getDayOfMonth();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear()%100;

        int currentDate = currentYear*10000 + currentMonth*100 + currentDay;
        int formattedCurrentDate = convertDateFormat(currentDate);
        int formattedClosingDate = convertDateFormat(closingDate);

        return formattedCurrentDate>formattedClosingDate;
    }

    private int convertDateFormat(int date) {
        int day = date/10000;
        int month = (date%10000)/100;
        int year = date%100;
        return year*10000 + month*100 + day;
    }

    public void decreaseSlots() {
        if (slots>0) {
            slots--;
        }
    }


}