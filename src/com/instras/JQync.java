/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.instras;

import com.instras.sync.SyncReaderThread;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author nathan
 */
public class JQync {
    private final int SCALING = 5;
    private final int MAX_X = 20280/SCALING;
    private final int MAX_Y = 13942/SCALING;

    // stores the x,y, pressure data from the sync device
    private ArrayList<Integer[]> syncData = new ArrayList<>();

    /**
     * Method to run the python script that will connect to the sync device
     *
     * @param scriptCmd
     */
    public void connect(String scriptCmd) {
        SyncReaderThread syncReaderThread = new SyncReaderThread(scriptCmd);
        syncReaderThread.setJQync(this);

        // now start the thread
        Thread thread = new Thread(syncReaderThread);
        thread.start();
    }

    /**
     * This stores the data which comes from the sync device and divide by 10,
     * so we can create an image that a reasonable size
     *
     * @param mX
     * @param mY
     * @param mPressure
     */
    public void setData(int mX, int mY, int mPressure) {
        Integer[] data = new Integer[3];
        data[0] = mX/SCALING;
        data[1] = mY/SCALING;
        data[2] = mPressure;

        syncData.add(data);

        System.out.println("X: " + mX + ", Y: " + mY + ", P: " + mPressure);
    }

    /**
     * Method to indicated that the Erase button was pressed
     */
    public void setEraseButtonPressed() {
        syncData.clear();
        System.out.println("Erase button pressed ...");
    }

    /**
     * Method to indicate the save button was pressed
     */
    public void setSaveButtonPressed() {
        // now save this image
        System.out.println("Save button pressed ... " + syncData.size());
        
        BufferedImage bufferedImage = new BufferedImage(MAX_X, MAX_Y, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = bufferedImage.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setStroke(new BasicStroke(4f));
        g2.setColor(Color.RED);
        
        // now draw lines between the points
        for (int i = 1; i < syncData.size(); i++) {
            Integer[] pdata = syncData.get(i -1);
            Integer[] data = syncData.get(i);
            
            // check the distance and if it's less than 25 draw the line
            int distance = getDistance(pdata[0], pdata[1], data[0], data[1]);
            if(distance < 50) {
                g2.drawLine(pdata[0], pdata[1], data[0], data[1]);
            }
        }
        
        saveImage(bufferedImage);
    }
    
    /**
     * Method to indicate the Sync device has been disconnected connected
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        System.out.println("Sync Connected: " + connected);
    }
    
    /**
     * Method to calculate the distance between points
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 
     */
    private int getDistance(int x1, int y1, int x2, int y2) {  
        return (int) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)); 
    }
    
    /**
     * Method for saving an image
     *
     * @param bufferedImage
     */
    private void saveImage(BufferedImage bufferedImage) {
        try {
            File outputfile = new File("/Users/nathan/temp/syncData.png");
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String pythonExe = "C:\\Python34\\python.exe -u ";
        String pythonScript = System.getProperty("user.dir") + "/bin/sync_reader.py";
        String cmd = pythonExe + pythonScript;

        JQync jQync = new JQync();
        jQync.connect(cmd);
    }
}
