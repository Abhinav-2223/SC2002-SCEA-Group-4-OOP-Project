import enums.ApplicationStatus;
import enums.WithdrawalDecision;
import database.FilePaths;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class StudentApplication {
    private static int counter = 1;

    private String applicationID;
    private Student student;
    private Internships internships;
    private ApplicationStatus appStatus;
    private WithdrawalDecision withdrawDecision;

    public StudentApplication(Student student, Internships internships) {
        this.applicationID = "APP" + String.format("%03d", counter++);
        this.student = student;
        this.internships = internships;
        this.appStatus = ApplicationStatus.PENDING;
        this.withdrawDecision = WithdrawalDecision.NONE;
        
        // Write application to CSV
        writeToCSV();
    }
    
    // Private constructor for loading from CSV (doesn't write back)
    private StudentApplication(Student student, Internships internships, boolean skipCSVWrite) {
        this.applicationID = "APP" + String.format("%03d", counter++);
        this.student = student;
        this.internships = internships;
        this.appStatus = ApplicationStatus.PENDING;
        this.withdrawDecision = WithdrawalDecision.NONE;
    }

    //getters
    public String getApplicationID() { return applicationID; }
    public Student getStudent() { return student; }
    public Internships getInternship() { return internships; }
    public ApplicationStatus getAppStatus() { return appStatus; }
    public WithdrawalDecision getWithdrawDecision() { return withdrawDecision; }

    //setters
    public void setAppStatus(ApplicationStatus appStatus) { this.appStatus = appStatus; }
    public void setWithdrawDecision(WithdrawalDecision withdrawDecision) { this.withdrawDecision = withdrawDecision; }

    //display info
    public void displayApplicationDetails() {
        System.out.println("Application ID: " + applicationID);
        System.out.println("Internship: " + internships.getTitle());
        System.out.println("Company: " + internships.getCompanyName());
        System.out.println("Description: " + internships.getDescription());
        System.out.println("Level: " + internships.getInternshipLevel());
        System.out.println("Preferred Major: " + internships.getPreferredMajor());
        System.out.println("Preferred Year: " + internships.getPreferredYear());
        System.out.println("Slots: " + internships.getSlots());
        System.out.println("Application Status: " + appStatus);
        System.out.println("Withdrawal Decision: " + withdrawDecision);
    }
    
    // Write application to CSV file
    private void writeToCSV() {
        try (FileWriter fw = new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            // CSV format: ID,Name,Major,Year,Email,AppliedInternship,ApplicationStatus
            out.println(String.join(",",
                student.getUserId(),
                student.getName(),
                student.getMajor(),
                String.valueOf(student.getStudyYear()),
                student.getUserId(), // Using userId as email (based on CSV structure)
                internships.getTitle(),
                appStatus.name()
            ));
        } catch (IOException e) {
            System.out.println("Error writing application to CSV: " + e.getMessage());
        }
    }
    
    // Load all applications for a specific student from CSV
    public static java.util.List<StudentApplication> loadApplicationsFromCSV(String studentId) {
        java.util.List<StudentApplication> applications = new java.util.ArrayList<>();
        
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            String[] header = null;
            int idCol = -1, nameCol = -1, majorCol = -1, yearCol = -1, internshipCol = -1, statusCol = -1;
            
            // Read header
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase("ID")) idCol = i;
                    else if (header[i].equalsIgnoreCase("Name")) nameCol = i;
                    else if (header[i].equalsIgnoreCase("Major")) majorCol = i;
                    else if (header[i].equalsIgnoreCase("Year")) yearCol = i;
                    else if (header[i].equalsIgnoreCase("AppliedInternship")) internshipCol = i;
                    else if (header[i].equalsIgnoreCase("ApplicationStatus")) statusCol = i;
                }
            }
            
            if (idCol == -1 || internshipCol == -1 || statusCol == -1) {
                return applications; // Missing required columns
            }
            
            // Read data rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                // Check if this row belongs to the student
                if (row.length > idCol && row[idCol].equals(studentId)) {
                    // Create Student object
                    String name = row.length > nameCol ? row[nameCol] : "";
                    String major = row.length > majorCol ? row[majorCol] : "";
                    int year = row.length > yearCol ? Integer.parseInt(row[yearCol]) : 1;
                    Student student = new Student(studentId, name, "password", major, year);
                    
                    // Find the internship
                    String internshipTitle = row[internshipCol];
                    Internships internship = CSVUtils.findInternshipByTitle(
                        CSVUtils.readInternshipsFromCSV(null), internshipTitle
                    );
                    
                    if (internship != null) {
                        // Create application without triggering writeToCSV
                        StudentApplication app = new StudentApplication(student, internship, true);
                        app.setAppStatus(ApplicationStatus.valueOf(row[statusCol]));
                        applications.add(app);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading applications from CSV: " + e.getMessage());
        }
        
        return applications;
    }
    
    // Update application status in CSV
    public static void updateApplicationStatus(String studentId, String internshipTitle, String newStatus) {
        java.util.List<String[]> allRows = new java.util.ArrayList<>();
        
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            String[] header = null;
            int idCol = -1, internshipCol = -1, statusCol = -1;
            
            // Read header
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                allRows.add(header);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase("ID")) idCol = i;
                    else if (header[i].equalsIgnoreCase("AppliedInternship")) internshipCol = i;
                    else if (header[i].equalsIgnoreCase("ApplicationStatus")) statusCol = i;
                }
            }
            
            // Read all rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                // Update status if this is the matching row
                if (row.length > idCol && row[idCol].equals(studentId) && 
                    row.length > internshipCol && row[internshipCol].equals(internshipTitle)) {
                    row[statusCol] = newStatus;
                }
                allRows.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error reading applications: " + e.getMessage());
            return;
        }
        
        // Write back all rows
        try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            for (String[] row : allRows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error updating application status: " + e.getMessage());
        }
    }
}
