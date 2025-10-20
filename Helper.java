import enums.RepRegistrationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Helper {
    public static boolean csvAuth(String id, String password, String domain,
                                  String studentsCsv, String repsCsv, String staffCsv) {
        // fields[0] = id, fields[1] = pw for ALL csvs, fields[7] = approval for repsCsv ONLY

        String file;
        switch (domain) {
            case "student" -> file = studentsCsv;
            case "companyrep" -> file = repsCsv;
            case "staff" -> file = staffCsv;
            default -> {
                System.out.println("Invalid Domain!");
                return false;
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue; // check if csv row empty

                String[] fields = line.split(",", -1); // splits by "," and keep empty columns
                if (fields.length < 2) continue; // invalid row

                String firstRow = fields[0].trim().toLowerCase();
                if (firstRow.equals("id")) continue; // skips header

                if (!domain.equals("companyrep")){ // basic validation
                    if (fields[0].trim().equals(id) && fields[1].trim().equals(password))
                        return true;
                } else { // advanced validation for account approval check
                    if (fields[0].trim().equals(id)
                        && fields[1].trim().equals(password)
                        && RepRegistrationStatus.APPROVED.name().equals(fields[7].trim()))
                        return true;
                }

            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return false;
    }
}