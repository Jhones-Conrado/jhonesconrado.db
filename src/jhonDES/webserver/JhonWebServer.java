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
 * Cria um servidor web que atenderá por padrão na porta 8080.
 * @author jhonesconrado
 */
public class JhonWebServer {
    
    private int port;
    private ServerSocket server;
    
    /**
     * Cria um servidor com a porta padrão 8080.
     * @throws IOException 
     */
    public JhonWebServer() throws IOException {
        this.port = 8080;
        init();
    }
    
    /**
     * Cria um servidor que responderá em uma porta definida.
     * @param port
     * @throws IOException 
     */
    public JhonWebServer(int port) throws IOException {
        this.port = port;
        init();
    }
    
    /**
     * Instancia o servidor e carrega as classes do projeto.
     * @throws IOException 
     */
    private void init() throws IOException{
        System.out.println("***************************************************************\n" +
                            "@@@@@@@@*@@****@@**@@@@@@@@**@@@@@*****@@@*@@@@@@****@@@@@@@@@****@@@@@@\n" +
                            "***@@****@@****@@*@@@@@@@@@@*@@@@@@****@@@*@@@@@@@@@*@@@*******@@@@@@@@@\n" +
                            "***@@****@@@@@@@@*@@@****@@@*@@@*@@@***@@@*@@@***@@@*@@@@@@@@@*@@@******\n" +
                            "@@*@@****@@@@@@@@*@@@****@@@*@@@**@@@**@@@*@@@***@@@*@@@@@@@@@***@@@@@@*\n" +
                            "@@@@@****@@****@@*@@@@@@@@@@*@@@***@@@@@@@*@@@@@@@@@*@@@*************@@@\n" +
                            "**@@*****@@****@@**@@@@@@@@**@@@****@@@@@@*@@@@@@****@@@@@@@@@*@@@@@@@**\n" +
                            "***********************************************************************");
        System.out.println("Instanciando servidor na porta: "+port);
        server = new ServerSocket(port);
        Memory.get(); //Instancia o memory do projeto, que por sua vez instancia o Refleciton.
    }
    
    /**
     * Inicia o servidor que passará a ouvir na porta definida.
     */
    public void start(){
        System.out.println("Servidor iniciado.");
        while(server.isBound() && !server.isClosed()){
            try {
                new WebCommunication(server.accept()).start();
            } catch (IOException ex) {
                Logger.getLogger(JhonWebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
