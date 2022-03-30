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

import core.interpreters.InterpreterMemory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uma conexão que pode ser inicializada como servidor ou como cliente.
 * Permite a comunicação por envio e recebimento de Strings.
 * A connection that can be initialized as a server or as a client.
 * Allows communication by sending and receiving Strings.
 * @author jhonesconrado
 */
public class Connection {
    
    private final Object lock = new Object();
    
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
     * Cria uma nova instância de conexão.
     * Creates a new instance of connection.
     */
    public Connection() {
        isResponse = false;
        initialized = false;
    }
    
    /**
     * Inicializa a conexão como um cliente.
     * Starts a connection as client.
     * @throws IOException 
     */
    public void startAsClient() throws IOException{
        starAsClient(config.Config.serverIP);
    }
    
    /**
     * Inicializa a conexão como um cliente.
     * Starts a connection as client.
     * @param ip Server IP.
     * @throws IOException 
     */
    public void starAsClient(String ip) throws IOException{
        startAsClient(ip, config.Config.serverPort);
    }
    
    /**
     * Inicializa a conexão como um cliente.
     * Starts a connection as client.
     * @param ip Server IP.
     * @param port Server port.
     * @throws IOException 
     */
    public void startAsClient(String ip, int port) throws IOException{
        startAsClient(ip, port, config.Config.clientPass);
    }
    
    /**
     * Inicializa a conexão como um cliente.
     * Starts a connection as client.
     * @param ip Server IP.
     * @param port Server port.
     * @param hash Hash to authenticate.
     * @throws IOException 
     */
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
                    System.out.println("Connected as client to IP server: "+getIp());
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
    
    /**
     * Inicia a conexão como um servidor, utilizando os valores padrões de porta
     * e hash.
     * Start the connection as a server, using the default port values and hash.
     * @param socket
     * @throws IOException 
     */
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
                System.out.println("New connection with client: "+getIp());
            } else {
                say(config.Config.msgToRefused);
                close();
            }
        }
    }
    
    private void setResponse(String msg){
        synchronized (lock) {
            nextResponse = msg;
        }
    }
    
    private void setIsResponse(boolean value){
        synchronized (lock) {
            isResponse = value;
        }
    }
    
    private boolean getIsResponse(){
        synchronized (lock) {
            return isResponse;
        }
    }
    
    private String getNextResponse(){
        synchronized (lock) {
            return nextResponse;
        }
    }
    
    /**
     * Envia uma mensagem.
     * Send a message.
     * @param msg 
     */
    public synchronized void say(String msg){
        if(!msg.equals("") && msg != null){
            String toSend = msg;
            try {
                toSend = toSend.replaceAll("\n", ";;;");
            } catch (Exception e) {
                System.out.println(e);
            }
            if(getIsResponse()){
                setIsResponse(false);
                toSend = "nextResponse:"+toSend;
            }
            writer.println(toSend);
            writer.flush();
        }
    }
    
    /**
     * Envia uma mensagem e fica aguardando a resposta.
     * Sends a message and return a response for this message.
     * @param msg Message to be sent.
     * @return Message received from client.
     */
    public String sayAndListenNextResponse(String msg){
        setResponse(null);
        say("requestResponse:"+msg);
        while(getNextResponse() == null){
            
        }
        return getNextResponse();
    }
    
    /**
     * Tenta encerrar a conexão.
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
     * @return The connection IP as String.
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
     * Isso ficará ouvindo as mensagens da conexão.
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
                    setResponse(msg.substring("nextResponse:".length()));
                } else if(msg.startsWith("requestResponse:")){
                    setIsResponse(true);
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
