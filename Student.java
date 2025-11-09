
public class Student extends User {
    // class
    private final String major;
    private final int studyYear;

    public Student(String studentId, String name, String password, String major, int studyYear) {
        super(studentId, name, password, "student"); // initialize shared fields
        this.major = major;
        this.studyYear = studyYear;
    }

    // getters and setters
    public String getMajor() { return this.major; }
    public int getStudyYear() { return this.studyYear; }


    // implemented inherited abstract methods
    @Override
    public boolean logout(){
        // TODO: change this to properly match studentUI()
        return false;
    }

    // class methods
    public static void studentUI(){
        System.out.println("I am Student");
    }
    // instance methods
//     Student can view the list of available internships --> need to figure out how to store internships first
    public void viewInternshipList() {
        if (Internships.isVisible().isEmpty()) { // check internship storage if empty or if no internships are visible
            System.out.println("No internships available at the moment.");
            return;
        }else {

            System.out.println("Available internships for year " + this.studyYear + " " + this.major + ":");
            boolean foundMatchingInternship = false;

            for (Internships internships : Internships.VisibleInternships()) { // iterate through all visible internships
                // Filter by major and study year
                if (internships.getPreferredMajor().equalsIgnoreCase(this.major) &&
                    internships.getPreferredYear() == this.studyYear &&
                    internships.canApply()) {

                    internships.displayDetails(); // need to create this method in Internships class
                    System.out.println("-------------------------");
                    foundMatchingInternship = true;
                }
            }

            if (!foundMatchingInternship) {
                System.out.println("No internships available for year " + this.studyYear + " " + this.major + " at the moment.");
            }
        }
    }


    // Student can apply for an internship --> sent to StudentApplication class --> send to CarreerCenStaff for approval
    public void applyForInternship(Internships internships) {

        // check if student has reached max application limit of 3
        if (applications.size() >= 3) {
            System.out.println("Maximum application limit of 3 reached. Cannot apply for more internships.");
            return;
        }

        // prevent multiple applications for the same internship
        for (StudentApplication app : applications) {
            if (app.getInternship().equals(internships)) {
                System.out.println("You have already applied for this internship: " + internships.getTitle());
                return;
            }
        }

        // check if internship can be applied to
        if (!internships.canApply()) {
            System.out.println("Cannot apply for this internship. It may be closed or you do not meet the criteria.");
            return;
        }


        if (studyYear <= 2) { // Year 1 and 2 students can only apply for BASIC level internships
            if (internships.getInternshipLevel() == enums.InternshipLevel.BASIC) {
                StudentApplication newApplication1n2 = new StudentApplication(this, internships);
                applications.add(newApplication1n2);
                System.out.println("Application submitted for internship: " + internships.getTitle());

            }else { // non-BASIC level internship cannot be applied by year 1 and 2 students
                System.out.println("You do not meet the criteria to apply for this internship. (only year 3 and above can apply)");

            }
        } else { // year 3 and above students can apply for all levels
            System.out.println("Application submitted for internship: " + internships.getTitle());
            StudentApplication newApplication3n4 = new StudentApplication(this, internships);
            applications.add(newApplication3n4);
        }

    }


    // Student can view the status of their applications -- TODO: FIX CAN VIEW EVEN AFTER VISIBIITY IS OFF
    public void viewApplicationStatus() {
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        for (StudentApplication app : applications) {
            app.displayApplicationDetails();
            System.out.println("-------------------------");
        }
    }

    // Student can accept an internship placement offer
    public void acceptInternshipPlacement(Internships internships) {
        // Student can only accept 1 internship offer
        if (!acceptedapplications.equals("NONE")) {
            System.out.println("You have already accepted an internship placement: " + acceptedapplications);
            return;
        }

        // Find the application for the given internship
        for (StudentApplication app : applications) {
            if (app.getInternship().equals(internships)) {

                // Check if the application status is SUCCESSFUL
                if (app.getAppStatus() == enums.ApplicationStatus.SUCCESSFUL) {
                    acceptedapplications = internships.getTitle(); // update accepted applications
                    System.out.println("Internship placement accepted for: " + internships.getTitle());
                    applications.remove(app); // remove other applications
                    return;
                } else {
                    System.out.println("No offer available for this internship.");
                    return;
                }

            }
        }

        // If no application found for the given internship
        System.out.println("No application found for this internship.");
    }



    // Student can withdraw their application
    public void requestWithdrawal(Internships internships) {

        for (StudentApplication app : applications) {
            if (app.getInternship().equals(internships)) {

                if (app.getAppStatus() == enums.ApplicationStatus.PENDING) {
                    app.setWithdrawDecision(enums.WithdrawalDecision.PENDING); // need approval from CareerCentStaff ( need to figure out)
                    System.out.println("Withdrawal request submitted for internship: " + internships.getTitle());
                    return;
                } else {
                    System.out.println("Cannot withdraw application. It has already been processed.");
                    return;
                }
            }
        }

        System.out.println("No application found for this internship.");
    }
}
