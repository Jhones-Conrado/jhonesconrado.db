/*
 * Copyright (C) 2022 jhonesconrado
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server;

import core.interpreters.DefaultInterpreter;
import core.interpreters.InterpreterMemory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jhonesconrado
 */
public class Connection {
    
    private Socket socket;
    private Scanner reader;
    private PrintWriter writer;
    private long start_time;
    
    private String nextResponse = null;
    private boolean isResponse;
    
    private final int AS_SERVER = 0;
    private final int AS_CLIENT = 1;
    
    private boolean initialized;
    private int ConnectionType;
    
    /**
     * 
     */
    public Connection() {
        isResponse = false;
        initialized = false;
    }
    
    public void startAsClient() throws IOException{
        starAsClient(config.Config.serverIP);
    }
    
    public void starAsClient(String ip) throws IOException{
        startAsClient(ip, config.Config.serverPort);
    }
    
    public void startAsClient(String ip, int port) throws IOException{
        startAsClient(ip, port, config.Config.clientPass);
    }
    
    public void startAsClient(String ip, int port, String hash) throws IOException{
        if(!initialized){
            this.socket = new Socket(ip, port);
            this.reader = new Scanner(socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream());
            start_time = System.nanoTime();

            say(hash);
            if(reader.hasNextLine()){
                String con = reader.nextLine();
                if(con.equals("connected")){
                    initialized = true;
                    ConnectionType = AS_CLIENT;
                    ConnectionManager.addClientConnection(this);
                    System.out.println("Conectado como cliente ao servidor: "+getIp());
                    new Thread(new Lister()).start();
                } else {
                    System.out.println("Connection refused");
                    close();
                }
            } else {
                close();
            }
        }
    }
    
    public void startAsServer(Socket socket) throws IOException{
        this.socket = socket;
        this.reader = new Scanner(socket.getInputStream());
        this.writer = new PrintWriter(socket.getOutputStream());
        start_time = System.nanoTime();
        
        if(reader.hasNextLine()){
            String pass = reader.nextLine();
            if(config.Config.validatePass(pass)){
                initialized = true;
                ConnectionType = AS_SERVER;
                ConnectionManager.addConnection(this);
                new Thread(new Lister()).start();
                say("connected");
                System.out.println("Nova conexão com o cliente IP: "+getIp());
            } else {
                say(config.Config.msgToRefused);
                close();
            }
        }
    }
    
    
    /**
     * Send a message to client.
     * @param msg 
     */
    public void say(String msg){
        if(!msg.equals("") && msg != null){
            String toSend = msg;
            try {
                toSend = toSend.replaceAll("\n", ";;;");
            } catch (Exception e) {
                System.out.println(e);
            }
            if(isResponse){
                isResponse = false;
                toSend = "nextResponse:"+toSend;
            }
            writer.println(toSend);
            writer.flush();
        }
    }
    
    /**
     * Sends a message to server and return a response for this message.
     * @param msg Message to be sent.
     * @return Message received from client.
     */
    public String sayAndListenNextResponse(String msg){
        this.nextResponse = null;
        say("requestResponse:"+msg);
        while(nextResponse == null){
            
        }
        return nextResponse;
    }
    
    /**
     * Try to close the connection.
     * @return The result of the socket close operation.
     */
    public boolean close(){
        reader.close();
        writer.close();
        try {
            socket.close();
            if(ConnectionType == AS_CLIENT){
                return ConnectionManager.removeClientConnection(getIp());
            } else if(ConnectionType == AS_SERVER){
                return ConnectionManager.removeConnection(getIp());
            }
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * @return The connection client IP as String.
     */
    public String getIp(){
        return socket.getInetAddress().getHostAddress();
    }
    
    /**
     * @return How long has the connection been open in seconds.
     */
    public long getConnectedTimeInSeconds(){
        return (long) ((System.nanoTime() - start_time)/1e9);
    }
    
    /**
     * This will keep listening to the messages from the server.
     */
    private class Lister implements Runnable{

        @Override
        public void run() {
            while(reader.hasNextLine()){
                String msg = reader.nextLine();
                try {
                    msg = msg.replaceAll(";;;", "\n");
                } catch (Exception e) {
                    System.out.println(e);
                }
                if(msg.startsWith("nextResponse:")){
                    nextResponse = msg.substring("nextResponse:".length());
                } else if(msg.startsWith("requestResponse:")){
                    isResponse = true;
                    say(InterpreterMemory.interpreter(msg.substring("requestResponse:".length())));
                } else {
                    InterpreterMemory.interpreter(msg);
                }
            }
            close();
            System.out.println("The client connection at IP: "+getIp()+" was closed.");
        }
        
    }
    
}
