package com.instras.model;

/**
 * Class stores information about a virtual button
 * 
 * @author nathan
 */
public class VirtualButton {
    private int number;
    private int xCoordinate;
    private int yCoordinate;
    private String value;
    
    /**
     * The default constructor
     * 
     * @param number
     * @param xCoordinate
     * @param yCoordinate
     * @param value 
     */
    public VirtualButton(int number, int xCoordinate, int yCoordinate, String value) {
        this.number = number;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.value = value;
    }

    public int getNumber() {
        return number;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return number + "\t" + xCoordinate + "\t" + yCoordinate + "\t" + value;
    }
}
