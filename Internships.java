import enums.InternshipLevel;
import enums.OpportunityStatus;
import java.util.ArrayList;
import java.util.List;

public class Internships {
    // Static storage for all internships
    private static List<Internships> allInternships = new ArrayList<>();
    
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


    // == Static Method for Test Data Initialization ==
    public static void initializeTestInternships() {
        // Clear existing internships
        clearAllInternships();
        
        // Test Internship 1 - Computer Science, Year 2, BASIC
        Internships intern1 = new Internships(
            "Software Developer Intern",
            "Work on web development projects using Java and Spring Boot",
            InternshipLevel.BASIC,
            "Computer Science",
            2,
            20250101,  // Opening date: 01/01/2025
            20251231,  // Closing date: 31/12/2025
            OpportunityStatus.APPROVED,
            "Tech Corp",
            new String[]{"john@techcorp.com"},
            5
        );
        intern1.setVisible(true);
        intern1.setApprovedByStaff(true);
        addInternship(intern1);
        
        // Test Internship 2 - Computer Science, Year 3, INTERMEDIATE
        Internships intern2 = new Internships(
            "Backend Developer",
            "Develop RESTful APIs and microservices",
            InternshipLevel.INTERMEDIATE,
            "Computer Science",
            3,
            20250115,  // Opening date: 15/01/2025
            20251215,  // Closing date: 15/12/2025
            OpportunityStatus.APPROVED,
            "Innovation Labs",
            new String[]{"hr@innovationlabs.com"},
            3
        );
        intern2.canApply();
        intern2.setVisible(true);
        intern2.setApprovedByStaff(true);
        addInternship(intern2);
        
        // Test Internship 3 - Business, Year 2, BASIC
        Internships intern3 = new Internships(
            "Business Analyst Intern",
            "Assist in market research and business strategy",
            InternshipLevel.BASIC,
            "Business",
            2,
            20250201,  // Opening date: 01/02/2025
            20251130,  // Closing date: 30/11/2025
            OpportunityStatus.APPROVED,
            "Global Consulting",
            new String[]{"careers@globalconsulting.com"},
            4
        );
        intern3.setVisible(true);
        intern3.setApprovedByStaff(true);
        addInternship(intern3);
        
        // Test Internship 4 - Computer Science, Year 4, ADVANCED
        Internships intern4 = new Internships(
            "Machine Learning Engineer",
            "Research and implement ML algorithms for production systems",
            InternshipLevel.ADVANCED,
            "Computer Science",
            4,
            20250110,  // Opening date: 10/01/2025
            20251220,  // Closing date: 20/12/2025
            OpportunityStatus.APPROVED,
            "AI Solutions Inc",
            new String[]{"talent@aisolutions.com"},
            2
        );
        intern4.setVisible(true);
        intern4.setApprovedByStaff(true);
        addInternship(intern4);
        
        // Test Internship 5 - Engineering, Year 2, BASIC (Closed - for testing)
        Internships intern5 = new Internships(
            "Mechanical Engineering Intern",
            "CAD design and prototype testing",
            InternshipLevel.BASIC,
            "Engineering",
            2,
            20240101,  // Opening date: 01/01/2024
            20240630,  // Closing date: 30/06/2024 (PAST DATE)
            OpportunityStatus.APPROVED,
            "Manufacturing Co",
            new String[]{"jobs@manufacturing.com"},
            3
        );
        intern5.setVisible(true);
        intern5.setApprovedByStaff(true);
        addInternship(intern5);
        
        System.out.println(" Test internships initialized: " + allInternships.size() + " internships loaded.");
    }

    // Static methods for managing internships
    public static List<Internships> getAllVisibleInternships() {
        List<Internships> visibleInternships = new ArrayList<>();
        for (Internships internship : allInternships) {
            if (internship.isVisible() && internship.isApprovedByStaff()) {
                visibleInternships.add(internship);
            }
        }
        return visibleInternships;
    }

    public static void addInternship(Internships internship) {
        allInternships.add(internship);
    }

    public static List<Internships> getAllInternships() {
        return allInternships;
    }

    public static void clearAllInternships() {
        allInternships.clear();
    }
}