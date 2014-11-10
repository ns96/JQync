package com.instras.sync;

import com.instras.JQync;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is used to read data from the BoogieBoard Sync device using a
 * small python script which uses the pywin USB library
 *
 * @author nathan
 */
public class SyncReaderThread implements Runnable {

    private String pythonStringCmd;
    private JQync jQync;
    private boolean stop = false;

    // Conctruction that takes python script
    public SyncReaderThread(String pythonScriptCmd) {
        this.pythonStringCmd = pythonScriptCmd;
    }

    /**
     * Method to set the jQync object to pass data do
     *
     * @param jQync
     */
    public void setJQync(JQync jQync) {
        this.jQync = jQync;
    }

    /**
     * Method to read the data
     */
    @Override
    public void run() {
        try {
            // Run ls command
            Process process = Runtime.getRuntime().exec(pythonStringCmd);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = in.readLine()) != null && !stop) {
                parseData(line);
            }
            
            process.destroy();
            
            // alert an listeners that the devices was disconnected
            jQync.setConnected(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to parse the return data by first converting the String returned
     * into a byte array
     *
     * @param stringData
     */
    private void parseData(String stringData) {
        if (stringData.startsWith("CONNECTED:")) {
            jQync.setConnected(true);
        } else if (stringData.startsWith("[")) {
            String[] sa = stringData.substring(1, stringData.length() - 1).split("\\s*,\\s*");
            byte[] payload = new byte[sa.length];

            // get a byte array by converting the int values to a signed byte
            // first
            for (int i = 0; i < sa.length; i++) {
                payload[i] = (byte) Integer.parseInt(sa[i]);
            }

            // Parse the x-coordinate.
            int mX = payload[1] & 0xFF;
            mX += (payload[2] & 0xFF) << 8;
            
            // Parse the y-coordinate.
            int mY = payload[3] & 0xFF;
            mY += (payload[4] & 0xFF) << 8;
            
            // Parse the pressure.
            int mPressure = payload[5] & 0xFF;
            mPressure += (payload[6] & 0xFF) << 8;
            
            // Parse the flags.
            int mFlags = payload[7];
            
            if(mPressure > 50) {
                jQync.setData(mX, mY, mPressure);
            } else if(mFlags == -96) {
                jQync.setEraseButtonPressed();
            } else if(mFlags == 64) {
                jQync.setSaveButtonPressed();
            }
        } else {
            System.out.println(stringData);
        }
    }

    /**
     * Method to stop read data from the program
     */
    public void stop() {
        stop = true;
    }
}
