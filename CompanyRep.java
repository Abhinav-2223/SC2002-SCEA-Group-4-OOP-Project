import database.FilePaths;
import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.RepRegistrationStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class CompanyRep extends User {

    // instance variables
    private final String companyName;
    @SuppressWarnings("unused")
    private final String department;
    @SuppressWarnings("unused")
    private final String position;
    private int internshipsCreated;

    private final RepRegistrationStatus regStatus;

    // constructor
    // NOTE: repId === email
    // internships will be created separately into csv, with repId tied to each internship.
    public CompanyRep(String repId, String name, String password, String companyName,
                      String department, String position, int internshipsCreated, RepRegistrationStatus regStatus) {
        super(repId, name, password, "companyrep");
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.internshipsCreated = internshipsCreated;
        this.regStatus = regStatus;
    }

    // getters and setters
    public String getRepId() { return this.userId; }
    public String getRepName() { return this.name; }
    public String getCompanyName() { return this.companyName; }
    public RepRegistrationStatus getRegStatus() { return this.regStatus; }


    // class methods
    // User inputs fields to create an account with a default password (can change upon login once approved)
    public static void registerCompany(Scanner scanner) {
        // create an entry in company_reps_list.csv, let staff approve it (change enum val in csv)
        System.out.println("# - Company Representative Registration");
        System.out.print("Enter ID (Company email): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Company's name: ");
        String companyName = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Enter Job Position: ");
        String position = scanner.nextLine().trim();

        String regStatus = RepRegistrationStatus.PENDING.name();

        // append the new entry to reps csv
        try (FileWriter fw = new FileWriter(FilePaths.REPS_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Follow the exact CSV header order:
            // appends based on sequence (as in csv file): ID,Password,Name,CompanyName,Department,Position,regStatus
            out.println(String.join(",",
                    id,
                    "password", // default password
                    name,
                    companyName,
                    dept,
                    position,
                    regStatus
            ));

            System.out.println("Registration submitted!");
            System.out.println("Your account is now pending approval by Career Center Staff.");
        } catch (IOException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    // instance methods
    @Override
    public void runUserUi(Scanner scanner){
        boolean session = true;

        while(session) {
            System.out.println("# Company Rep Dashboard - Welcome: " + this.getRepName() + " #");
            System.out.println("1. View created internships");
            System.out.println("2. Create internships");
            System.out.println("3. Approve/Reject internships");
            System.out.println("4. Toggle internship visibility");
            System.out.println("0. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // flush buffer
            switch (choice) {
                case 1: {
                    viewCreatedInternships();
                    break;
                }
                case 2: {
                    if (Integer.parseInt(Objects.requireNonNull(Helper.csvExtractFields("InternshipsCreated", getRepId(), "companyrep"))) < 5)
                        createInternships(scanner);
                    else {
                        System.out.println("Internship limit reached! Cannot create any more internships");
                        break;
                    }
                }
                case 3: {
                    approveRejectInternship(scanner);
                    break;
                }
                case 4: {
                    toggleVisibility(scanner);
                    break;
                }
                case 0: {
                    session = false;
                    break;
                }
                default: {
                    System.out.println("Invalid option!");
                    break;
                }
            }
        }
    }

    // get all internships created by this company
    public List<Internships> getInternships() {
        return CSVUtils.readInternshipsFromCSV(
            internship -> internship.getCompanyName().equalsIgnoreCase(this.companyName)
        );
    }
    
    // helper method to parse date string (YYYY-MM-DD) to integer (YYYYMMDD)


    @Override
    public List<Internships> filteringInternships(String filterType, String filterValue) {
        List<Internships> allInternships = CSVUtils.readInternshipsFromCSV(null);
        List<Internships> filtered = new ArrayList<>();
        
        for (Internships internship : allInternships) {
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

    // max 5 internships/company, max 10 slots each
    public void createInternships(Scanner scanner){

        switch (regStatus){
            case RepRegistrationStatus.APPROVED -> {
                System.out.println("# - Create Internship Opportunity");
                System.out.print("Enter internship title: ");
                String title = scanner.nextLine().trim();
                System.out.print("Enter description: ");
                String desc = scanner.nextLine().trim();
                System.out.print("Enter internship level (Basic/Intermediate/Advanced): ");
                String internshipLevelInput = scanner.nextLine().trim();
                InternshipLevel internshipLevel = InternshipLevel.valueOf(internshipLevelInput);

                System.out.print("Enter major preference: ");
                String preferredMajor = scanner.nextLine().trim();
                System.out.print("Enter Year of Study preference: ");
                String preferredYear = scanner.nextLine().trim();
                System.out.print("Enter internship opening date (YYYY-MM-DD): ");
                String openingDate = scanner.nextLine().trim();
                System.out.print("Enter internship closing date (YYYY-MM-DD): ");
                String closingDate = scanner.nextLine().trim();

                String regStatus = OpportunityStatus.PENDING.name();

                System.out.println("Enter internship slots (up to 10 maximum)");
                int slots = scanner.nextInt();
                scanner.nextLine(); // clear buffer after nextInt()

                boolean visibility = true;

                // append the new entry to reps csv
                try (FileWriter fw = new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV, true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {

                    // Follow the exact CSV header order:
                    // appends based on sequence (as in csv file): ID,Password,Name,CompanyName,Department,Position,regStatus
                    out.println(String.join(",",
                            title,
                            desc,
                            internshipLevel.name(),
                            preferredMajor,
                            preferredYear,
                            openingDate,
                            closingDate,
                            regStatus, // initially PENDING
                            getCompanyName(),
                            String.valueOf(slots),
                            String.valueOf(visibility)
                    ));

                    internshipsCreated++;
                    CompanyRepHelper.updateRepField(getRepId(), "InternshipsCreated", Integer.toString(internshipsCreated));
                    System.out.println("Internship created! Pending approval by Career Center Staff.");
                } catch (IOException e) {
                    System.out.println("Creation error: " + e.getMessage());
                }
            }
            case RepRegistrationStatus.PENDING -> System.out.println("Unable to create internship. Registration PENDING from Career Staff!");
            case RepRegistrationStatus.REJECTED -> System.out.println("Unable to create internship. Registration REJECTED from Career Staff!");
        }
    }

    // Display all internships created by this rep
    public void viewCreatedInternships() {
        System.out.println("# - Your Created Internships");
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            int repCol = -1, titleCol = -1;
            String[] header = null;
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase("CompanyName")) repCol = i;
                    if (header[i].equalsIgnoreCase("Title")) titleCol = i;
                }
            }
            if (repCol == -1 || titleCol == -1) {
                System.out.println("CSV header missing required columns.");
                return;
            }
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                if (row[repCol].equalsIgnoreCase(getCompanyName())) {
                    found = true;
                    // Print all fields for this internship
                    System.out.println("------------------------------");
                    for (int i = 0; i < row.length; i++) {
                        System.out.printf("%s: %s\n", header[i], row[i]);
                    }
                }
            }
            if (!found) {
                System.out.println("No internships found for your company.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // flips value of Visibility in csv for internships created by this rep
    public void toggleVisibility(Scanner scanner) {
        System.out.println("# - Toggle Internship Visibility");
        // List internships created by this rep
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            List<String[]> internships = new ArrayList<>();
            int idx = 1;
            int repCol = -1, titleCol = -1, visCol = -1;
            String[] header = null;
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase("CompanyName")) repCol = i;
                    if (header[i].equalsIgnoreCase("Title")) titleCol = i;
                    if (header[i].equalsIgnoreCase("Visibility") || header[i].equalsIgnoreCase("isVisible")) visCol = i;
                }
            }
            if (repCol == -1 || titleCol == -1 || visCol == -1) {
                System.out.println("CSV header missing required columns.");
                return;
            }
            // Collect internships for this rep
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                if (row[repCol].equalsIgnoreCase(getCompanyName())) {
                    internships.add(row);
                    System.out.printf("%d. %s (Currently: %s)\n", idx++, row[titleCol], row[visCol]);
                }
            }
            if (internships.isEmpty()) {
                System.out.println("No internships found for your company.");
                return;
            }
            System.out.print("Select internship to toggle visibility (number): ");
            int sel = scanner.nextInt();
            scanner.nextLine();
            if (sel < 1 || sel > internships.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            String[] selected = internships.get(sel - 1);
            // Flip visibility
            selected[visCol] = selected[visCol].equalsIgnoreCase("true") ? "false" : "true";
            // Now update the CSV file
            // Read all rows
            List<String[]> allRows = new ArrayList<>();
            try (BufferedReader br2 = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
                String l;
                while ((l = br2.readLine()) != null) {
                    allRows.add(l.split(",", -1));
                }
            }
            // Update the row
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row[titleCol].equals(selected[titleCol]) && row[repCol].equals(selected[repCol])) {
                    row[visCol] = selected[visCol];
                    break;
                }
            }
            // Write back
            try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
                for (String[] row : allRows) {
                    pw.println(String.join(",", row));
                }
            }
            System.out.println("Visibility toggled for internship: " + selected[titleCol] + ". Now: " + selected[visCol]);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Approve or reject student applications for this rep's internships
    public void approveRejectInternship(Scanner scanner) {
        System.out.println("# - Approve/Reject Student Applications");
        // Read internships for this rep
        List<String> myInternships = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            int repCol = -1, titleCol = -1;
            String[] header = null;
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase("CompanyName")) repCol = i;
                    if (header[i].equalsIgnoreCase("Title")) titleCol = i;
                }
            }
            if (repCol == -1 || titleCol == -1) {
                System.out.println("CSV header missing required columns.");
                return;
            }
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                if (row[repCol].equalsIgnoreCase(getCompanyName())) {
                    myInternships.add(row[titleCol]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading internships: " + e.getMessage());
            return;
        }
        if (myInternships.isEmpty()) {
            System.out.println("You have no internships to manage applications for.");
            return;
        }
        // Read applications for these internships
        List<String[]> applications = new ArrayList<>();
        int appliedInternshipCol = -1, statusCol = -1;
        String[] appHeader = null;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            if ((line = br.readLine()) != null) {
                appHeader = line.split(",", -1);
                for (int i = 0; i < appHeader.length; i++) {
                    if (appHeader[i].equalsIgnoreCase("AppliedInternship")) appliedInternshipCol = i;
                    if (appHeader[i].equalsIgnoreCase("ApplicationStatus")) statusCol = i;
                }
            }
            // If ApplicationStatus column is missing, add it
            if (statusCol == -1) {
                // Add ApplicationStatus column to header and all rows
                List<String[]> allRows = new ArrayList<>();
                allRows.add(appHeader);
                while ((line = br.readLine()) != null) {
                    String[] row = line.split(",", -1);
                    String[] newRow = new String[row.length + 1];
                    System.arraycopy(row, 0, newRow, 0, row.length);
                    newRow[newRow.length - 1] = "PENDING";
                    allRows.add(newRow);
                }
                // Write back with new header
                try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
                    String[] newHeader = new String[appHeader.length + 1];
                    System.arraycopy(appHeader, 0, newHeader, 0, appHeader.length);
                    newHeader[newHeader.length - 1] = "ApplicationStatus";
                    pw.println(String.join(",", newHeader));
                    for (int i = 1; i < allRows.size(); i++) {
                        pw.println(String.join(",", allRows.get(i)));
                    }
                }
                System.out.println("ApplicationStatus column added. Please rerun this option.");
                return;
            }
            // Collect applications for this rep's internships
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                if (myInternships.contains(row[appliedInternshipCol]) && row[statusCol].equalsIgnoreCase("PENDING")) {
                    applications.add(row);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading applications: " + e.getMessage());
            return;
        }
        if (applications.isEmpty()) {
            System.out.println("No pending applications for your internships.");
            return;
        }
        // List applications
        for (int i = 0; i < applications.size(); i++) {
            String[] app = applications.get(i);
            System.out.printf("%d. Student: %s, Internship: %s, Status: %s\n", i + 1, app[0], app[appliedInternshipCol], app[statusCol]);
        }
        System.out.print("Select application to approve/reject (number): ");
        int sel = scanner.nextInt();
        scanner.nextLine();
        if (sel < 1 || sel > applications.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        String[] selected = applications.get(sel - 1);
        System.out.print("Approve (A) or Reject (R) this application? ");
        String action = scanner.nextLine().trim().toUpperCase();
        String newStatus = null;
        if (action.equals("A")) newStatus = "SUCCESSFUL";
        else if (action.equals("R")) newStatus = "UNSUCCESSFUL";
        else {
            System.out.println("Invalid action.");
            return;
        }
        // Update the application status in the CSV
        try {
            List<String[]> allRows = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
                String line;
                while ((line = br.readLine()) != null) {
                    allRows.add(line.split(",", -1));
                }
            }
            // Find and update the selected application
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row[appliedInternshipCol].equals(selected[appliedInternshipCol]) && row[0].equals(selected[0])) {
                    row[statusCol] = newStatus;
                    break;
                }
            }
            // Write back
            try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
                for (String[] row : allRows) {
                    pw.println(String.join(",", row));
                }
            }
            System.out.println("Application status updated to: " + newStatus);
        } catch (IOException e) {
            System.out.println("Error updating application: " + e.getMessage());
        }
    }
}
