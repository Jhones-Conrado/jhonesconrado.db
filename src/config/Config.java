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
package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configurações básicas do banco de dados.
 * @author jhonesconrado
 */
public class Config {
    
    /**
     * If you are server side this is the pass you will request
     * of your customers to enable a new connection.
     */
    private static String pass = null;
    
    /**
     * If you are an client and want to get a new connection with a server,
     * this is the hash you will send to server to prove your credential.
     */
    public static String clientPass = "pass";
    
    /**
     * The IP of the server you want to connect to.
     */
    public static String serverIP = "127.0.0.1";
    
    /**
     * The port of the server you want to connect to.
     */
    public static int serverPort = 15200;
    
    /**
     * This is a root folder of the Database.
     */
    public static String root = "db";
    
    /**
     * If you want to cache results. Used only if you are a client.
     * You can be client and server at the same time.
     */
    public static String cache = null;
    
    /**
     * The port that server will listen.
     * Default is 15200.
     */
    public static int myServerPort = 15200;
    
    /**
     * If you want to shutdown the server, just put "true" here.
     */
    public static boolean serverShutDown = false;
    
    /**
     * The message that will be sent when a connection is declined.
     */
    public static String msgToRefused = "Invalid pass. Connection refused.";
    
    /**
     * Lê o arquivo de configuração de senha e compara se a chave recebida é
     * válida.
     * @param pass Senha para ser verificada.
     * @return resultado da verificação de igualdade das senhas.
     */
    public static boolean validatePass(String pass){
        File file = new File(root+"/pass");
        if(file.exists()){
            try (BufferedReader r = Files.newBufferedReader(file.toPath())){
                Config.pass = r.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(Config.pass != null){
            if(Config.pass.equals(pass)){
                return true;
            }
        }
        return false;
    }
    
}
