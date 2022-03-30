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

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author jhonesconrado
 */
public class Server {
    
    private ServerSocket serverSocket;
    
    /**
     * Creates a new instance of server.
     */
    public Server(){
        
    }
    
    /**
     * Starts this server.
     * @throws IOException 
     */
    public void start() throws IOException{
        ServerSocket server = new ServerSocket(config.Config.serverPort);
        System.out.println("Server initialized at port "+config.Config.serverPort);
        while(!config.Config.serverShutDown){
            try {
                new Connection().startAsServer(server.accept());
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
    
}
