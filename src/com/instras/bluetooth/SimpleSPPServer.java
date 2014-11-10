/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.instras.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.*;
import javax.microedition.io.*;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.ResponseCodes;

/**
 * Class that implements an SPP Server which accepts single line of message from
 * an SPP client and sends a single line of response to the client.
 */
public class SimpleSPPServer {

    //start server
    private void startServer() throws IOException {
        //Create a UUID for SPP
        //UUID uuid = new UUID("1101", true);
        //UUID uuid = new UUID("d6a56f8088f811e3baa80800200c9a66", false);
        UUID uuid = new UUID("d6a56f8188f811e3baa80800200c9a66", false);

        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection = streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: " + dev.getBluetoothAddress());
        System.out.println("Remote device name: " + dev.getFriendlyName(true));
        
        //read string from spp client
        InputStream inStream = connection.openInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
        String lineRead = bReader.readLine();
        System.out.println(lineRead);

        //send response to spp client
        OutputStream outStream = connection.openOutputStream();
        PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write("Response String from SPP Server\r\n");
        pWriter.flush();

        pWriter.close();
        streamConnNotifier.close();
    }

    public void connectToClient() {
        //connect to the server and send a line of text
        try {
            String connectionURL = "btspp://0017EC558162:2;authenticate=false;encrypt=false;master=false";
            StreamConnection streamConnection = (StreamConnection) Connector.open(connectionURL);
            

            //send string
            OutputStream outStream = streamConnection.openDataOutputStream();
            PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream));
            pWriter.write("Test String from SPP Client\r\n");
            pWriter.flush();

            //read response
            InputStream inStream = streamConnection.openInputStream();
            byte buffer[] = new byte[80];
            int bytes_read = inStream.read( buffer );
            String received = new String(buffer, 0, bytes_read);
            System.out.println("received: " + received);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());

        SimpleSPPServer sampleSPPServer = new SimpleSPPServer();
        //sampleSPPServer.startServer();
        sampleSPPServer.connectToClient();
    }
}
