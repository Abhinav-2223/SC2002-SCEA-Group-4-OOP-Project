
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import database.FilePaths;

public class Student extends User {
    private final String major;
    private final int studyYear;
    private final List<StudentApplication> applications; // store student's applications
    private String acceptedapplications; // store accepted internship title

    // constructor
    public Student(String studentId, String name, String password, String major, int studyYear) {
        super(studentId, name, password, "student"); // initialize shared fields
        this.major = major;
        this.studyYear = studyYear;
        this.applications = new ArrayList<>();
        this.acceptedapplications = "NONE";
    }

    // getters and setters
    public String getMajor() { return this.major; }
    public int getStudyYear() { return this.studyYear; }

    // implemented inherited abstract methods
    @Override
    public void runUserUi(Scanner scanner) {
        // Initialize accepted applications status from CSV
        initializeAcceptedApplications();
        
        int choice = -1;
        do {
            System.out.println("\n=== Student Dashboard ===");
            System.out.println("Welcome, " + this.getName() + " (" + this.getUserId() + ")");
            System.out.println("1. View Internship List");
            
            // Only show Apply option if student hasn't accepted an internship
            if (acceptedapplications.equals("NONE")) {
                System.out.println("2. Apply for Internship");
            } else {
                System.out.println("2. [LOCKED] Apply for Internship (You have accepted: " + acceptedapplications + ")");
            }
            System.out.println("3. View Application Status");
            System.out.println("4. Accept Internship Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> viewInternshipList();
                case 2 -> {
                    // Block access if student has accepted an internship
                    if (!acceptedapplications.equals("NONE")) {
                        System.out.println("Cannot apply for new internships. You have already accepted an internship placement: " + acceptedapplications);
                    } else {
                        applyInternship(scanner);
                    }
                }
                case 3 -> viewApplicationStatus();
                case 4 -> acceptInternship(scanner);
                case 5 -> withdrawApplication(scanner);
                case 6 -> changePassword(this.getUserId(), "student", scanner);
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
    }
    
    // Initialize accepted applications from CSV (check if student has accepted any internship)
    private void initializeAcceptedApplications() {
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        for (StudentApplication app : allApplications) {
            if (app.getAppStatus() == enums.ApplicationStatus.ACCEPTED) {
                acceptedapplications = app.getInternship().getTitle();
                break;
            }
        }
        // Also load all current applications into memory for reference
        applications.clear();
        applications.addAll(allApplications);
    }

    private void applyInternship(Scanner scanner) {
        System.out.println("\n--- Apply for Internship ---");
        
        // show available internships before prompting
        viewInternshipList();
        
        System.out.println("\nEnter the title of the internship you want to apply for:");
        System.out.print("Internship title: ");
        String title = scanner.nextLine().trim();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            Internships.getAllVisibleInternships(), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found or not available.");
            return;
        }
        
        applyForInternship(targetInternship);
    }

    private void acceptInternship(Scanner scanner) {
        System.out.println("\n--- Accept Internship ---");
        
        // Check if student has already accepted an internship
        if (!acceptedapplications.equals("NONE")) {
            System.out.println("You have already accepted an internship placement: " + acceptedapplications);
            return;
        }
        
        // Load all applications for this student
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        List<StudentApplication> acceptableApps = new ArrayList<>();
        
        // Filter for SUCCESSFUL applications (approved by Company Rep)
        for (StudentApplication app : allApplications) {
            if (app.getAppStatus() == enums.ApplicationStatus.SUCCESSFUL) {
                acceptableApps.add(app);
            }
        }
        
        // Display acceptable internships
        if (acceptableApps.isEmpty()) {
            System.out.println("No internship offers available to accept.");
            System.out.println("You need to wait for Company Representatives to approve your applications.");
            return;
        }
        
        System.out.println("\n=== Internships Available to Accept ===");
        for (StudentApplication app : acceptableApps) {
            Internships internship = app.getInternship();
            System.out.println("------------------------------");
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getInternshipLevel());
            System.out.println("Major: " + internship.getPreferredMajor());
            System.out.println("Application Status: " + app.getAppStatus());
        }
        System.out.println("------------------------------");
        
        System.out.print("\nEnter internship title to accept: ");
        String title = scanner.nextLine().trim();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            CSVUtils.readInternshipsFromCSV(null), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found or not available.");
            return;
        }
        acceptInternshipPlacement(targetInternship);
    }

    private void withdrawApplication(Scanner scanner) {
        System.out.println("\n--- Request Withdrawal ---");
        
        // show current withdrawable applications
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        List<StudentApplication> withdrawableApps = new ArrayList<>();
        
        System.out.println("\nYour current applications:");
        System.out.println("-------------------------");
        
        for (StudentApplication app : allApplications) {
            // Only show applications that can be withdrawn (PENDING or SUCCESSFUL)
            // Exclude WITHDRAWN, ACCEPTED, UNSUCCESSFUL, and PENDING_WITHDRAWAL (already requested)
            if (app.getAppStatus() == enums.ApplicationStatus.PENDING || 
                app.getAppStatus() == enums.ApplicationStatus.SUCCESSFUL) {
                withdrawableApps.add(app);
                System.out.println("Title: " + app.getInternship().getTitle());
                System.out.println("Company: " + app.getInternship().getCompanyName());
                System.out.println("Status: " + app.getAppStatus());
                System.out.println("-------------------------");
            } else if (app.getAppStatus() == enums.ApplicationStatus.PENDING_WITHDRAWAL) {
                // Show PENDING_WITHDRAWAL status but don't allow re-requesting
                System.out.println("Title: " + app.getInternship().getTitle());
                System.out.println("Company: " + app.getInternship().getCompanyName());
                System.out.println("Status: " + app.getAppStatus() + " (Awaiting Staff Approval)");
                System.out.println("-------------------------");
            }
        }
        
        if (withdrawableApps.isEmpty()) {
            System.out.println("No applications available to withdraw.");
            return;
        }
        
        System.out.println("\nEnter the title of the internship you want to withdraw from:");
        System.out.print("Internship title: ");
        String title = scanner.nextLine().trim();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            CSVUtils.readInternshipsFromCSV(null), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found.");
            return;
        }
        
        requestWithdrawal(targetInternship);
    }

    // instance methods
    // Student can view the list of available internships --> need to figure out how to store internships first
    private void viewInternshipList() {

        List<Internships> visibleInternships = Internships.getAllVisibleInternships();
        
        // check if there are any visible internships
        if (visibleInternships == null || visibleInternships.isEmpty()) {
            System.out.println("No internships available at the moment.");
            return;
        }

        System.out.println("Available internships for year " + this.studyYear + " " + this.major + ":");
        boolean foundMatchingInternship = false;

        for (Internships internship : visibleInternships) {
            // Filter by major and study year
            if (internship.getPreferredMajor().equalsIgnoreCase(this.major) &&
                internship.getPreferredYear() == this.studyYear &&
                internship.canApply()) {

                // filter by internship level based on student year
                if (this.studyYear <= 2 && internship.getInternshipLevel() != enums.InternshipLevel.BASIC) {
                    continue;
                }

                // Display internship details
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Description: " + internship.getDescription());
                System.out.println("Level: " + internship.getInternshipLevel());
                System.out.println("Slots: " + internship.getSlots());
                System.out.println("Closing Date: " + internship.getClosingDate());
                
                // Display managing reps
                displayManagingReps(internship.getTitle());
                
                System.out.println("-------------------------");
                foundMatchingInternship = true;
            }
        }

        if (!foundMatchingInternship) {
            System.out.println("No internships available for year " + this.studyYear + " " + this.major + " at the moment.");
        }
    }


    // Student can apply for an internship --> sent to StudentApplication class --> send to CarreerCenStaff for approval
    private void applyForInternship(Internships internship) {

        if (!acceptedapplications.equals("NONE")) {
            System.out.println("Cannot apply for new internships. You have already accepted an internship placement: " + acceptedapplications);
            return;
        }

        if (!internship.isVisible()) {
            System.out.println("Cannot apply for this internship. Visibility is currently OFF.");
            return;
        }

        if (!internship.getPreferredMajor().equalsIgnoreCase(this.major)) {
            System.out.println("Cannot apply for this internship. Your major (" + this.major + ") does not match the required major (" + internship.getPreferredMajor() + ").");
            return;
        }

        if (internship.getPreferredYear() != this.studyYear) {
            System.out.println("Cannot apply for this internship. Your year (" + this.studyYear + ") does not match the required year (" + internship.getPreferredYear() + ").");
            return;
        }

        // Load current applications from CSV to check for duplicates and count
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        int activeApplicationCount = 0;
        
        // Check for duplicates and count active applications (not WITHDRAWN)
        for (StudentApplication app : allApplications) {
            // Check if already applied to this internship
            if (app.getInternship().getTitle().equals(internship.getTitle())) {
                if (app.getAppStatus() != enums.ApplicationStatus.WITHDRAWN) {
                    System.out.println("You have already applied for this internship: " + internship.getTitle());
                    return;
                }
            }
            // Count active applications (exclude WITHDRAWN, ACCEPTED, and PENDING_WITHDRAWAL)
            if (app.getAppStatus() != enums.ApplicationStatus.WITHDRAWN && 
                app.getAppStatus() != enums.ApplicationStatus.ACCEPTED &&
                app.getAppStatus() != enums.ApplicationStatus.PENDING_WITHDRAWAL) {
                activeApplicationCount++;
            }
        }
        
        // check if student has reached max application limit of 3
        if (activeApplicationCount >= 3) {
            System.out.println("Maximum application limit of 3 reached. Cannot apply for more internships.");
            return;
        }

        // check if internship can be applied to
        if (!internship.canApply()) {
            System.out.println("Cannot apply for this internship. It may be closed or you do not meet the criteria.");
            return;
        }


        if (studyYear <= 2) { // Year 1 and 2 students can only apply for BASIC level internships
            if (internship.getInternshipLevel() == enums.InternshipLevel.BASIC) {
                StudentApplication newApplication1n2 = new StudentApplication(this, internship);
                applications.add(newApplication1n2);
                System.out.println("Application submitted for internship: " + internship.getTitle());

            }else { // non-BASIC level internship cannot be applied by year 1 and 2 students
                System.out.println("You do not meet the criteria to apply for this internship. (only year 3 and above can apply)");

            }
        } else { // year 3 and above students can apply for all levels
            System.out.println("Application submitted for internship: " + internship.getTitle());
            StudentApplication newApplication3n4 = new StudentApplication(this, internship);
            applications.add(newApplication3n4);
        }

    }

    // Student can view their application status
    private void viewApplicationStatus() {
        System.out.println("\n--- Your Application Status ---");
        
        // Load applications from CSV
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        
        if (allApplications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        
        for (StudentApplication app : allApplications) {
            app.displayApplicationDetails();
            System.out.println("-------------------------");
        }
    }

    // Student can accept an internship placement offer
    private void acceptInternshipPlacement(Internships internship) {
        // Student can only accept 1 internship offer
        if (!acceptedapplications.equals("NONE")) {
            System.out.println("You have already accepted an internship placement: " + acceptedapplications);
            return;
        }

        // Load applications from CSV to get current status
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        StudentApplication targetApp = null;
        
        // Find the application for the given internship
        for (StudentApplication app : allApplications) {
            if (app.getInternship().getTitle().equals(internship.getTitle())) {
                targetApp = app;
                break;
            }
        }
        
        if (targetApp == null) {
            System.out.println("No application found for this internship.");
            return;
        }

        // Check if the application status is SUCCESSFUL
        if (targetApp.getAppStatus() != enums.ApplicationStatus.SUCCESSFUL) {
            System.out.println("No offer available for this internship. Application status: " + targetApp.getAppStatus());
            return;
        }
        
        // accept internship and auto-withdraw all other pending applications
        acceptedapplications = internship.getTitle();
        
        StudentApplication.updateApplicationStatus(this.getUserId(), internship.getTitle(), "ACCEPTED");
        
        // decrement available slot count for the accepted internship
        if (SlotManager.updateSlotCount(internship.getTitle(), -1)) {
            System.out.println("Slot reserved for internship: " + internship.getTitle());
        }
        
        System.out.println("Internship placement accepted for: " + internship.getTitle());
        
        // Withdraw all other pending applications
        for (StudentApplication app : allApplications) {
            if (!app.getInternship().getTitle().equals(internship.getTitle())) {
                if (app.getAppStatus() == enums.ApplicationStatus.PENDING || 
                    app.getAppStatus() == enums.ApplicationStatus.SUCCESSFUL) {
                    StudentApplication.updateApplicationStatus(this.getUserId(), app.getInternship().getTitle(), "WITHDRAWN");
                    System.out.println("Automatically withdrawn application for: " + app.getInternship().getTitle());
                }
            }
        }
        
        System.out.println("All other pending applications have been automatically withdrawn.");
    }



    // Student can withdraw their application
    private void requestWithdrawal(Internships internship) {
        // Load applications from CSV to get current status
        List<StudentApplication> allApplications = StudentApplication.loadApplicationsFromCSV(this.getUserId());
        
        for (StudentApplication app : allApplications) {
            if (app.getInternship().getTitle().equals(internship.getTitle())) {

                if (app.getAppStatus() == enums.ApplicationStatus.PENDING || 
                    app.getAppStatus() == enums.ApplicationStatus.SUCCESSFUL) {
                    // Update status to PENDING_WITHDRAWAL - requires staff approval
                    StudentApplication.updateApplicationStatus(this.getUserId(), internship.getTitle(), "PENDING_WITHDRAWAL");
                    System.out.println("Withdrawal request submitted for internship: " + internship.getTitle());
                    System.out.println("Status changed to PENDING_WITHDRAWAL. Awaiting Career Center Staff approval.");
                    return;
                } else if (app.getAppStatus() == enums.ApplicationStatus.WITHDRAWN) {
                    System.out.println("Application has already been withdrawn.");
                    return;
                } else if (app.getAppStatus() == enums.ApplicationStatus.PENDING_WITHDRAWAL) {
                    System.out.println("Withdrawal request is already pending approval from Career Center Staff.");
                    return;
                } else if (app.getAppStatus() == enums.ApplicationStatus.ACCEPTED) {
                    System.out.println("Cannot withdraw accepted placement. This requires special approval from Career Center Staff.");
                    return;
                } else {
                    System.out.println("Cannot withdraw application. Current status: " + app.getAppStatus());
                    return;
                }
            }
        }

        System.out.println("No application found for this internship.");
    }

    @Override
    public List<Internships> filteringInternships(String filterType, String filterValue) {
        List<Internships> visibleInternships = Internships.getAllVisibleInternships();
        List<Internships> filtered = new ArrayList<>();
        
        for (Internships internship : visibleInternships) {
            boolean matches = switch (filterType.toLowerCase()) {
                case "status" -> internship.getOpportunityStatus().name().equalsIgnoreCase(filterValue);
                case "major" -> internship.getPreferredMajor().equalsIgnoreCase(filterValue);
                case "level" -> internship.getInternshipLevel().name().equalsIgnoreCase(filterValue);
                case "company" -> internship.getCompanyName().equalsIgnoreCase(filterValue);
                default -> true;
            };
            if (matches) filtered.add(internship);
        }
        
        return filtered;
    }
    
    // Helper to display managing company reps for an internship
    private void displayManagingReps(String internshipTitle) {
        List<String> repIds = new ArrayList<>();
        
        // Read internships_reps_map.csv to find rep IDs for this internship
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_REPS_MAP_CSV))) {
            String line;
            String[] header = null;
            int titleCol = -1, repCol = -1;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].trim().equalsIgnoreCase("Title")) titleCol = i;
                    else if (header[i].trim().equalsIgnoreCase("CompanyRep")) repCol = i;
                }
            }
            
            if (titleCol == -1 || repCol == -1) return;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                if (row.length > titleCol && row.length > repCol && 
                    row[titleCol].trim().equalsIgnoreCase(internshipTitle)) {
                    repIds.add(row[repCol].trim());
                }
            }
        } catch (IOException e) {
            return; // Silently fail if mapping file doesn't exist
        }
        
        if (repIds.isEmpty()) return;
        
        // Load rep details from company_reps_list.csv
        List<String> repDetails = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.REPS_CSV))) {
            String line;
            String[] header = null;
            int idCol = -1, nameCol = -1;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].trim().equalsIgnoreCase("ID")) idCol = i;
                    else if (header[i].trim().equalsIgnoreCase("Name")) nameCol = i;
                }
            }
            
            if (idCol == -1 || nameCol == -1) return;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                if (row.length > idCol && repIds.contains(row[idCol].trim())) {
                    String email = row[idCol].trim();
                    String name = row.length > nameCol ? row[nameCol].trim() : "Unknown";
                    repDetails.add(email + " (" + name + ")");
                }
            }
        } catch (IOException e) {
            return;
        }
        
        if (!repDetails.isEmpty()) {
            System.out.println("Managed by: " + String.join(", ", repDetails));
        }
    }
}
