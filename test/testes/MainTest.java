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
package testes;

import core.interpreters.Alerta;
import core.interpreters.Cloner;
import core.interpreters.OnlyReceive;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Connection;
import server.Server;

/**
 *
 * @author jhonesconrado
 */
public class MainTest {
    
    static Connection clientecon;
    static Server servidor;
    
    public MainTest() {
        new Alerta();
        new OnlyReceive();
        new Cloner();
        
        new Thread(new servidor()).start();
        new Thread(new cliente()).start();
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        
        new MainTest();
        
        try {
            Thread.sleep(400);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File f = new File("db/arquivo");
        FileInputStream in = new FileInputStream(f);
        
        servidor.close();
        
        clientecon.say("onlyreceive:mensagem pre arquivo");
        
        clientecon.say("clone:", f);
        
        in.close();
//        
//        f = new File("db/arquivo2");
//        in = new FileInputStream(f);

//        clientecon.say("onlyreceive:mensagem pós arquivo -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        
//        clientecon.say("clone:", f);
        
//        in.close();

        clientecon.say("onlyreceive:mensagem pós arquivo");
        
        clientecon.close();
        
    }
    
    
    private class cliente implements Runnable {

        @Override
        public void run() {
            clientecon = new Connection();
            try {
                clientecon.startAsClient();
            } catch (IOException ex) {
                Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    private class servidor implements Runnable {

        @Override
        public void run() {
            servidor = new Server();
            try {
                servidor.start();
            } catch (IOException ex) {
                Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
