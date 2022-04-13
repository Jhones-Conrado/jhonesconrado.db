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
package jhonDES.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jhonesconrado
 */
public class JhonWebServer {
    
    private int port;
    private ServerSocket server;

    public JhonWebServer() throws IOException {
        port = 8080;
        server = new ServerSocket(port);
    }

    public JhonWebServer(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
    }
    
    public void start(){
        while(server.isBound() && !server.isClosed()){
            try {
                new WebCommunication(server.accept()).start();
            } catch (IOException ex) {
                Logger.getLogger(JhonWebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
