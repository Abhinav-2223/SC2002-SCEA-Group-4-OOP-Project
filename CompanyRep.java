import enums.InternshipLevel;
import enums.OpportunityStatus;
import java.util.List;
import java.util.ArrayList;

public class CompanyRep extends User {

    // instance variables
    private String companyName;
    private boolean isApproved;
    private List<Internships> internships;

    public CompanyRep() {
        this.internships = new ArrayList<>();
    }

    // methods
    public boolean getIsApproved() {
        return isApproved;
    }
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void registerCompany(String companyName) {
        if (isApproved) {
            System.out.println("Company is already registered and approved.");
        } else {
            this.companyName = companyName;
            this.isApproved = false; // registration pending approval
            System.out.println("Company registration submitted for approval.");
        }
    }

    public void createInternships(String title, String description, InternshipLevel internshipLevel,
                                    String preferredMajor, int openingDate, int closingDate, int slots){
        if (!isApproved) {
            System.out.println("Company must be approved first in order to create internships.");
            return;
        }
        if (internships.size()>=5) {
            System.out.println("Max of 5 internships can be created per company.");
            return;
        }
        if (slots>10) {
            System.out.println("Max of 10 slots can be set for each internship.");
            return;
        }

        int formattedOpeningDate = convertDateFormat(openingDate);
        int formattedClosingDate = convertDateFormat(closingDate);

        if (formattedOpeningDate>=formattedClosingDate) {
            System.out.println("Opening date must be before the closing date.");
            return;
        }

        Internships newInternship = new Internships(title, description, internshipLevel, preferredMajor,
                                                    openingDate, closingDate, OpportunityStatus.PENDING,
                                                    companyName, new String[]{getUserid()}, slots);
        internships.add(newInternship);
        System.out.println("Internship created successfully.");

    }

    private int convertDateFormat(int date) {
        int day = date/10000;
        int month = (date%10000)/100;
        int year = date%100;
        return year*10000 + month*100 + day;
    }

    public void approveStudentApplication(){

    }

    public void toggleInternshipVisibility(Internships internship){
        internship.toggleVisibility();
    }

    public List<Internships> getInternships(){
        return internships;
    }

    public void viewInternship(Internships internship){
        //need do StudentApplication first then get information from there
    }

}
