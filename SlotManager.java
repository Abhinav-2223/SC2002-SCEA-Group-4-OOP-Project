import database.FilePaths;
import enums.OpportunityStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing internship slot counts
 */
public class SlotManager {
    
    /**
     * Update slot count for an internship in CSV
     * @param internshipTitle Title of the internship
     * @param delta Change in slots (-1 to decrement, +1 to increment)
     * @return true if successful, false otherwise
     */
    public static boolean updateSlotCount(String internshipTitle, int delta) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                lines.add(line); // keep header
            }
            
            if (header == null) return false;
            
            int titleCol = -1, slotsCol = -1, statusCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim();
                if (h.equalsIgnoreCase("Title")) titleCol = i;
                else if (h.equalsIgnoreCase("Slots")) slotsCol = i;
                else if (h.equalsIgnoreCase("OpportunityStatus")) statusCol = i;
            }
            
            if (titleCol == -1 || slotsCol == -1) return false;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                String title = row.length > titleCol ? row[titleCol].trim() : "";
                
                if (title.equalsIgnoreCase(internshipTitle)) {
                    found = true;
                    try {
                        int currentSlots = Integer.parseInt(row[slotsCol].trim());
                        int newSlots = currentSlots + delta;
                        
                        if (newSlots < 0) newSlots = 0;
                        
                        row[slotsCol] = String.valueOf(newSlots);
                        
                        // Update status to FILLED if slots reach 0
                        if (newSlots == 0 && statusCol != -1) {
                            row[statusCol] = OpportunityStatus.FILLED.name();
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                lines.add(String.join(",", row));
            }
        } catch (IOException e) {
            return false;
        }
        
        if (!found) return false;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
