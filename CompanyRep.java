//import java.util.List;
//import java.util.ArrayList;
//
//public class CompanyRep extends User{
//
//    // instance variables
//    private String companyName;
//    private boolean isApproved;
//    private List<Internships> internships;
//
//    public CompanyRep() {
//        this.internships = new ArrayList<>();
//    }
//
//    // methods
//    public boolean getIsApproved() {
//        return isApproved;
//    }
//    public void setIsApproved(boolean isApproved) {
//        this.isApproved = isApproved;
//    }
//
//    public String getCompanyName() {
//        return companyName;
//    }
//
//    public void registerCompany(String companyName) {
//        if (isApproved) {
//            System.out.println("Company is already registered and approved.");
//        } else {
//            this.companyName = companyName;
//            this.isApproved = false; // registration pending approval
//            System.out.println("Company registration submitted for approval.");
//        }
//    }
//
//    public void createInternships(){
//
//    }
//
//    public void approveRegistration(){
//
//    }
//
//    public void toggleInternshipVisibility(Internships internship){
//        internship.toggleVisibility();
//    }
//
//    public List<Internships> getInternships(){
//        return internships;
//    }
//
//    public void viewInternship(Internships internship){
//        //need do StudentApplication first then get information from there
//    }
//
//    //need implement another method to approve or reject student's application
//
//}
