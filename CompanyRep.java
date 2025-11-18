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
            // appends based on sequence (as in csv file): ID,Password,Name,CompanyName,Department,Position,InternshipsCreated,RegStatus
            out.println(String.join(",",
                    id,
                    "password", // default password
                    name,
                    companyName,
                    dept,
                    position,
                    "0", // default InternshipsCreated to 0
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
            System.out.println("=== Company Rep Dashboard - Welcome: " + this.getRepName() + " ===");
            System.out.println("1. View all internships");
            System.out.println("2. View created internships");
            System.out.println("3. Create internships");
            System.out.println("4. Delete internship");
            System.out.println("5. Approve/Reject internships");
            System.out.println("6. Toggle internship visibility");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // flush buffer
            switch (choice) {
                case 1: {
                    viewAllInternships(scanner);
                    break;
                }
                case 2: {
                    viewCreatedInternships();
                    break;
                }
                case 3: {
                    if (Integer.parseInt(Objects.requireNonNull(Helper.csvExtractFields("InternshipsCreated", getRepId(), "companyrep"))) < 5) {
                        createInternships(scanner);
                    } else {
                        System.out.println("Internship limit reached! Cannot create any more internships");
                    }
                    break;
                }
                case 4: {
                    deleteInternship(scanner);
                    break;
                }
                case 5: {
                    approveRejectInternship(scanner);
                    break;
                }
                case 6: {
                    toggleVisibility(scanner);
                    break;
                }
                case 7: {
                    changePassword(this.getRepId(), "companyrep", scanner);
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

    private void viewAllInternships(Scanner scanner) {
        // Load all internships
        List<Internships> allInternships = CSVUtils.readInternshipsFromCSV(null);
        
        if (allInternships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }
        
        // Sort alphabetically by title (A-Z)
        allInternships.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        
        // Display all internships first (default alphabetical view)
        System.out.println("\n=== All Internships (Alphabetically) ===");
        for (Internships internship : allInternships) {
            System.out.println("------------------------------");
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getInternshipLevel());
            System.out.println("Major: " + internship.getPreferredMajor());
            System.out.println("Year: " + internship.getPreferredYear());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Status: " + internship.getOpportunityStatus());
            System.out.println("Slots: " + internship.getSlots());
            System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
        }
        System.out.println("------------------------------");
        
        // Ask if user wants to filter/sort
        System.out.println("\n=== Filter/Sort Options ===");
        System.out.println("1. Keep current view (Alphabetically)");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Major");
        System.out.println("4. Filter by Level");
        System.out.println("5. Sort by Closing Date");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter choice: ");
        
        int filterChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        List<Internships> displayList = new ArrayList<>(allInternships);
        
        switch (filterChoice) {
            case 1 -> {
                // Already displayed, just return
                return;
            }
            case 2 -> {
                System.out.print("Enter status (PENDING/VACANT/FILLED/REJECTED): ");
                String status = scanner.nextLine().trim().toUpperCase();
                displayList = allInternships.stream()
                    .filter(i -> i.getOpportunityStatus().name().equalsIgnoreCase(status))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("status", status);
            }
            case 3 -> {
                System.out.print("Enter major: ");
                String major = scanner.nextLine().trim();
                displayList = allInternships.stream()
                    .filter(i -> i.getPreferredMajor().equalsIgnoreCase(major))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("major", major);
            }
            case 4 -> {
                System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                String level = scanner.nextLine().trim().toUpperCase();
                displayList = allInternships.stream()
                    .filter(i -> i.getInternshipLevel().name().equalsIgnoreCase(level))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("level", level);
            }
            case 5 -> {
                // Sort by closing date (earliest first)
                displayList.sort((a, b) -> Integer.compare(a.getClosingDate(), b.getClosingDate()));
                setFilterPreference("closingdate", "applied");
            }
            case 0 -> {
                return;
            }
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }
        
        // Display filtered/sorted internships
        if (displayList.isEmpty()) {
            System.out.println("\nNo internships match the criteria.");
            return;
        }
        
        System.out.println("\n=== Filtered/Sorted Internships ===");
        for (Internships internship : displayList) {
            System.out.println("------------------------------");
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getInternshipLevel());
            System.out.println("Major: " + internship.getPreferredMajor());
            System.out.println("Year: " + internship.getPreferredYear());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Status: " + internship.getOpportunityStatus());
            System.out.println("Slots: " + internship.getSlots());
            System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
        }
        System.out.println("------------------------------");
    }

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

    // delete internship and handle all related data
    private void deleteInternship(Scanner scanner) {
        System.out.println("\n--- Delete Internship ---");
        
        // show created internships first
        viewCreatedInternships();
        
        System.out.print("\nEnter internship title to delete (or 'cancel' to abort): ");
        String title = scanner.nextLine().trim();
        
        if (title.equalsIgnoreCase("cancel")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        
        // verify internship belongs to this rep
        List<String> repInternships = Helper.getInternshipRepsFor(title);
        if (repInternships == null || !repInternships.contains(this.getRepId())) {
            System.out.println("Error: Internship not found or you do not have permission to delete it.");
            return;
        }
        
        // confirm deletion
        System.out.print("Are you sure you want to delete '" + title + "'? This will remove all related applications. (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        
        boolean internshipDeleted = false;
        // boolean mappingDeleted = false;
        // boolean applicationsHandled = false;
        
        // case 1: remove from internships_list.csv
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
                String line;
                String[] header = null;
                int titleCol = -1;
                
                if ((line = br.readLine()) != null) {
                    header = line.split(",", -1);
                    lines.add(line); // keep header
                    
                    for (int i = 0; i < header.length; i++) {
                        if (header[i].trim().equalsIgnoreCase("Title")) {
                            titleCol = i;
                            break;
                        }
                    }
                }
                
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] row = line.split(",", -1);
                    String internTitle = titleCol >= 0 && row.length > titleCol ? row[titleCol].trim() : "";
                    
                    // skip the internship to delete
                    if (!internTitle.equalsIgnoreCase(title)) {
                        lines.add(line);
                    } else {
                        internshipDeleted = true;
                    }
                }
            }
            
            // write back
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting internship: " + e.getMessage());
            return;
        }
        
        // case 2: remove from internships_reps_map.csv
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_REPS_MAP_CSV))) {
                String line;
                if ((line = br.readLine()) != null) {
                    lines.add(line); // keep header
                }
                
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",", -1);
                    String mapTitle = parts.length > 0 ? parts[0].trim() : "";
                    
                    // skip the mapping to delete
                    if (!mapTitle.equalsIgnoreCase(title)) {
                        lines.add(line);
                    } else {
                        // mappingDeleted = true;
                    }
                }
            }
            
            // write back
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIPS_REPS_MAP_CSV))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not update internship mapping: " + e.getMessage());
        }
        
        // case 3: handle student applications - mark as withdrawn
        try {
            List<String> lines = new ArrayList<>();
            int affectedStudents = 0;
            boolean hasAcceptedStudent = false;
            
            try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
                String line;
                String[] header = null;
                int internTitleCol = -1, statusCol = -1;
                
                if ((line = br.readLine()) != null) {
                    header = line.split(",", -1);
                    lines.add(line); // keep header
                    
                    for (int i = 0; i < header.length; i++) {
                        if (header[i].trim().equalsIgnoreCase("AppliedInternship")) internTitleCol = i;
                        else if (header[i].trim().equalsIgnoreCase("ApplicationStatus")) statusCol = i;
                    }
                }
                
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] row = line.split(",", -1);
                    String appliedIntern = internTitleCol >= 0 && row.length > internTitleCol ? row[internTitleCol].trim() : "";
                    String status = statusCol >= 0 && row.length > statusCol ? row[statusCol].trim() : "";
                    
                    if (appliedIntern.equalsIgnoreCase(title)) {
                        affectedStudents++;
                        if (status.equalsIgnoreCase("ACCEPTED")) {
                            hasAcceptedStudent = true;
                        }
                        // mark application as withdrawn due to internship deletion
                        if (statusCol >= 0 && row.length > statusCol) {
                            row[statusCol] = "WITHDRAWN";
                        }
                        lines.add(String.join(",", row));
                    } else {
                        lines.add(line);
                    }
                }
            }
            
            // write back
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            
            // applicationsHandled = true;
            
            if (affectedStudents > 0) {
                System.out.println("Note: " + affectedStudents + " student application(s) have been automatically withdrawn.");
                if (hasAcceptedStudent) {
                    System.out.println("WARNING: At least one student had ACCEPTED this internship. They have been withdrawn.");
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not update student applications: " + e.getMessage());
        }
        
        // step 4: decrement internshipsCreated counter
        if (internshipDeleted) {
            try {
                int currentCount = Integer.parseInt(Objects.requireNonNull(Helper.csvExtractFields("InternshipsCreated", getRepId(), "companyrep")));
                if (currentCount > 0) {
                    internshipsCreated = currentCount - 1;
                    CompanyRepHelper.updateRepField(getRepId(), "InternshipsCreated", String.valueOf(internshipsCreated));
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not update internship counter: " + e.getMessage());
            }
        }
        
        // final status
        if (internshipDeleted) {
            System.out.println("\nInternship '" + title + "' has been successfully deleted.");
            // DEBUG STUFF
            // System.out.println("- Internship listing removed");
            // if (mappingDeleted) System.out.println("- Rep mapping removed");
            // if (applicationsHandled) System.out.println("- Student applications handled");
            // System.out.println("- Internship counter updated");
        } else {
            System.out.println("Error: Internship '" + title + "' was not found.");
        }
    }

    private void createInternships(Scanner scanner) {
        switch (regStatus){
            case RepRegistrationStatus.APPROVED -> {
                // enforce maximum 5 internship opportunities per company rep
                if (internshipsCreated >= 5) {
                    System.out.println("Maximum limit of 5 internship opportunities reached. Cannot create more.");
                    return;
                }
                
                System.out.println("# - Create Internship Opportunity");
                System.out.print("Enter internship title: ");
                String title = scanner.nextLine().trim();
                
                // validate required fields are not empty
                if (title.isEmpty()) {
                    System.out.println("Error: Title cannot be empty. Internship creation cancelled.");
                    return;
                }
                
                System.out.print("Enter description: ");
                String desc = scanner.nextLine().trim();
                
                if (desc.isEmpty()) {
                    System.out.println("Error: Description cannot be empty. Internship creation cancelled.");
                    return;
                }
                
                System.out.print("Enter internship level (Basic/Intermediate/Advanced): ");
                String internshipLevelInput = scanner.nextLine().trim();
                InternshipLevel internshipLevel;
                
                // validate internship level enum value
                try {
                    internshipLevel = InternshipLevel.valueOf(internshipLevelInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Invalid internship level. Must be Basic, Intermediate, or Advanced. Internship creation cancelled.");
                    return;
                }

                System.out.print("Enter major preference (e.g. Computer Science): ");
                String preferredMajor = scanner.nextLine().trim();
                
                if (preferredMajor.isEmpty()) {
                    System.out.println("Error: Major preference cannot be empty. Internship creation cancelled.");
                    return;
                }
                
                System.out.print("Enter Year of Study preference (1/2/3/4): ");
                String preferredYear = scanner.nextLine().trim();
                
                if (preferredYear.isEmpty() || !preferredYear.matches("[1-4]")) {
                    System.out.println("Error: Year must be 1, 2, 3, or 4. Internship creation cancelled.");
                    return;
                }
                
                System.out.print("Enter internship opening date (YYYY-MM-DD): ");
                String openingDate = scanner.nextLine().trim();
                
                if (openingDate.isEmpty()) {
                    System.out.println("Error: Opening date cannot be empty. Internship creation cancelled.");
                    return;
                }
                
                System.out.print("Enter internship closing date (YYYY-MM-DD): ");
                String closingDate = scanner.nextLine().trim();
                
                if (closingDate.isEmpty()) {
                    System.out.println("Error: Closing date cannot be empty. Internship creation cancelled.");
                    return;
                }

                String regStatus = OpportunityStatus.PENDING.name();

                System.out.println("Enter internship slots (up to 10 maximum)");
                int slots;
                
                // validate slot count is between 1 and 10
                try {
                    slots = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (slots < 1 || slots > 10) {
                        System.out.println("Error: Slots must be between 1 and 10. Internship creation cancelled.");
                        return;
                    }
                } catch (Exception e) {
                    scanner.nextLine(); // clear invalid input
                    System.out.println("Error: Invalid number format for slots. Internship creation cancelled.");
                    return;
                }

                boolean visibility = true;

                // Read existing file, remove blank lines, then append
                List<String> existingLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().isEmpty()) { // only keep non-empty lines
                            existingLines.add(line);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                    return;
                }

                // Write back all lines plus the new internship
                try (PrintWriter out = new PrintWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
                    // Write existing lines
                    for (String line : existingLines) {
                        out.println(line);
                    }
                    
                    // Append new internship
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
                    
                    // Write to internships_reps_map.csv
                    try (PrintWriter mapOut = new PrintWriter(new FileWriter(FilePaths.INTERNSHIPS_REPS_MAP_CSV, true))) {
                        mapOut.println(title + "," + getRepId());
                    } catch (IOException mapErr) {
                        System.out.println("Warning: Could not update rep mapping: " + mapErr.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Creation error: " + e.getMessage());
                }
            }
            case RepRegistrationStatus.PENDING -> System.out.println("Unable to create internship. Registration PENDING from Career Staff!");
            case RepRegistrationStatus.REJECTED -> System.out.println("Unable to create internship. Registration REJECTED from Career Staff!");
        }
    }

    // Display all internships created by this rep
    private void viewCreatedInternships() {
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
                if (line.trim().isEmpty()) continue; // skip empty lines
                String[] row = line.split(",", -1);
                
                // Check if row has enough columns
                if (row.length <= repCol) continue;
                
                if (row[repCol].equalsIgnoreCase(getCompanyName())) {
                    found = true;
                    // Print all fields for this internship
                    System.out.println("------------------------------");
                    int maxIndex = Math.min(row.length, header.length);
                    for (int i = 0; i < maxIndex; i++) {
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
    private void toggleVisibility(Scanner scanner) {
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
            // flip visibility
            selected[visCol] = selected[visCol].equalsIgnoreCase("true") ? "false" : "true";
            // ppdate the CSV file
            // read all rows
            List<String[]> allRows = new ArrayList<>();
            try (BufferedReader br2 = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
                String l;
                while ((l = br2.readLine()) != null) {
                    allRows.add(l.split(",", -1));
                }
            }
            // update the row
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row[titleCol].equals(selected[titleCol]) && row[repCol].equals(selected[repCol])) {
                    row[visCol] = selected[visCol];
                    break;
                }
            }
            // write back to csv
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
    private void approveRejectInternship(Scanner scanner) {
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
                if (line.trim().isEmpty()) continue; // skip empty lines
                String[] row = line.split(",", -1);
                
                // Check if row has enough columns
                if (row.length <= repCol || row.length <= titleCol) continue;
                
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
            if (appliedInternshipCol == -1 || statusCol == -1) {
                System.out.println("CSV header missing required columns (AppliedInternship or ApplicationStatus).");
                return;
            }
            // Collect applications for this rep's internships
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines
                String[] row = line.split(",", -1);
                
                // Check if row has enough columns
                if (row.length <= appliedInternshipCol || row.length <= statusCol) continue;
                
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
            System.out.printf("%d. Student: %s, Internship: %s, Status: %s\n", i + 1, 
                app[0], 
                app.length > appliedInternshipCol ? app[appliedInternshipCol] : "N/A", 
                app.length > statusCol ? app[statusCol] : "N/A");
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
                // Check if row has enough columns
                if (row.length <= appliedInternshipCol || row.length <= statusCol) continue;
                
                if (row[appliedInternshipCol].equals(selected[appliedInternshipCol]) && row[0].equals(selected[0])) {
                    row[statusCol] = newStatus;
                    break;
                }
            }
            // Write back to csv
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
