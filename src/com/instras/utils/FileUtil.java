package com.instras.utils;

import com.instras.model.VirtualButton;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for reading/writing information to/from files
 *
 * @author nathan
 */
public class FileUtil {
    /**
     * Method to read in the lines from a file
     * 
     * @param file
     * @return 
     */
    public static ArrayList<String> getLines(String filename) {
        ArrayList<String> lineList = new ArrayList<>();
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                lineList.add(line);
                line = br.readLine();
            }
        } catch(Exception e) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {}
        }
        
        return lineList;
    }
    
    /**
     * Method to return an array list with the virtual buttons
     * @param filename
     * @return 
     */
    public static ArrayList<VirtualButton> getButtonList(String filename) {
        ArrayList<VirtualButton> buttonList = new ArrayList<>();
        
        for(String line: getLines(filename)) {
            String[] sa = line.split("\\s*,\\s*");
            
            // make sure we have button data 
            if(sa.length != 4) continue;
            
            try {
                int number = Integer.parseInt(sa[0]);
                int xCoordinate = Integer.parseInt(sa[1]);
                int yCoordinate = Integer.parseInt(sa[2]);
                String value = sa[3];
                
                VirtualButton virtualButton = new VirtualButton(number, xCoordinate, yCoordinate, value);
                buttonList.add(virtualButton);
            } catch(NumberFormatException e) {}
        }
        
        return buttonList;
    }
}
