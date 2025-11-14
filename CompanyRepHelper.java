import database.FilePaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyRepHelper {
    public static void updateRepField(String repId, String fieldToUpdate, String newValue) {
        List<String[]> rows = new ArrayList<>();
        int targetColumn = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.REPS_CSV))) {
            String line;
            // read header first
            if ((line = br.readLine()) != null) {
                String[] header = line.split(",", -1);
                rows.add(header);

                // Find the column index of fieldToUpdate
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase(fieldToUpdate)) {
                        targetColumn = i;
                        break;
                    }
                }
            }
            // column not found
            if (targetColumn == -1) {
                System.out.println("Field \"" + fieldToUpdate + "\" not found in CSV header.");
                return;
            }

            // Read remaining rows
            while ((line = br.readLine()) != null) {
                rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // === Update the specified field ===
        for (int i = 1; i < rows.size(); i++) {   // skip header
            String[] row = rows.get(i);

            if (row[0].equals(repId)) {           // match ID
                row[targetColumn] = newValue;
                break;
            }
        }

        // === Write back to CSV ===
        try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.REPS_CSV))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public static void updateInternshipField(String internshipTitle, String fieldToUpdate, String newValue) {
        List<String[]> rows = new ArrayList<>();
        int targetColumn = -1;

        // === Read CSV ===
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;

            // Read header first
            if ((line = br.readLine()) != null) {
                String[] header = line.split(",", -1);
                rows.add(header);

                // Find column index for the field to update
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase(fieldToUpdate)) {
                        targetColumn = i;
                        break;
                    }
                }
            }

            if (targetColumn == -1) {
                System.out.println("Field \"" + fieldToUpdate + "\" not found in internships CSV.");
                return;
            }

            // Read the rest of the rows
            while ((line = br.readLine()) != null) {
                rows.add(line.split(",", -1));
            }

        } catch (IOException e) {
            System.out.println("Error reading internships CSV: " + e.getMessage());
            return;
        }

        // === Update the specified field (matching by Internship Title) ===
        for (int i = 1; i < rows.size(); i++) {   // skip header
            String[] row = rows.get(i);

            // Title is column 0
            if (row[0].equalsIgnoreCase(internshipTitle)) {
                row[targetColumn] = newValue;
                break;
            }
        }

        // === Write back to the internships CSV ===
        try (PrintWriter pw = new PrintWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error writing internships CSV: " + e.getMessage());
        }
    }
}
