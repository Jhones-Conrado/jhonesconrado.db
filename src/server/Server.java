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
import java.net.ServerSocket;

/**
 * Um servidor básico que ficará esperando por novas conexões e criando novas 
 * Threads para cada nova conexão.
 * A basic server that will be waiting for new connections and creating new 
 * Threads for each new connection
 * @author jhonesconrado
 */
public class Server {
    
    private ServerSocket serverSocket;
    private Thread tr;
    
    /**
     * Creates a new instance of server.
     */
    public Server(){}
    
    public boolean close() throws IOException{
        while(InterpreterMemory.canClose.get() > 0){}
        config.Config.serverShutDown = true;
        System.out.println("Closing server.");
        tr.interrupt();
        serverSocket.close();
        return true;
    }
    
    /**
     * Starts this server.
     * @throws IOException 
     */
    public void start() throws IOException{
        serverSocket = new ServerSocket(config.Config.serverPort);
        serverSocket.setSoTimeout(100);
        tr = new Thread(new Observer());
        tr.start();
        System.out.println("Server initialized at port "+config.Config.serverPort);
    }
    
    private class Observer implements Runnable {

        @Override
        public void run() {
            while(!config.Config.serverShutDown){
                try {
                    new Connection().startAsServer(serverSocket.accept());
                } catch (IOException e) {
                }
            }
            System.out.println("closed");
        }
        
    }
    
}
