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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jhonesconrado
 */
public class ConnectionManager {
    
    /**
     * Map of connected clients where you are the server.
     */
    private static final Map<String, Connection> connections = new HashMap<>();
    
    /**
     * Connection map where you are the client.
     */
    private static final Map<String, Connection> clientConnections = new HashMap<>();
    
    /**
     * Sends a message for all servers connected.
     * @param msg Message to be sent.
     */
    public void sayToServer(String msg){
        for(Connection cs : clientConnections.values()){
            cs.say(msg);
        }
    }
    
    /**
     * Sends a message for a specific server.
     * @param msg Message to be sent.
     * @param ip Server IP that message will be sent.
     */
    public void sayToServer(String msg, String ip){
        clientConnections.get(ip).say(msg);
    }

    /**
     * Sends a message for all connected clients.
     * @param msg Message to be sent.
     */
    public void sayToClient(String msg){
        for(Connection cs : connections.values()){
            cs.say(msg);
        }
    }
    
    /**
     * Sends a message to a specific client.
     * @param msg Message to be sent.
     * @param ip Client IP that message will be sent.
     */
    public void seyToClient(String msg, String ip){
        connections.get(ip).say(msg);
    }

    /**
     * Puts a new socket in the Connections Map.
     * @param socket 
     */
    public static void addConnection(Connection connection){
        connections.put(connection.getIp(), connection);
    }
    
    /**
     * Remove a connection from IP.
     * @param ip The connection IP to remove.
     * @return Result of operation.
     */
    public static boolean removeConnection(String ip){
        if(connections.containsKey(ip)){
            connections.remove(ip);
        }
        return false;
    }
    
    /**
     * Puts a new socket in the Client Connections Map.
     * @param socket 
     */
    public static void addClientConnection(Connection connection){
        clientConnections.put(connection.getIp(), connection);
    }
    
    /**
     * Close and remove a client connection from server IP.
     * @param ip The connection IP to close and remove.
     * @return Result of operation.
     */
    public static boolean removeClientConnection(String ip){
        if(clientConnections.containsKey(ip)){
            clientConnections.remove(ip);
        }
        return false;
    }
    
    /**
     * Gets a IP list of all connected clients.
     * @return Connected Client IP List.
     */
    public List<String> getConnectedClients(){
        return clientConnections.keySet().stream().collect(Collectors.toList());
    }
    
    /**
     * Gets a IP list of all connected servers.
     * @return Connected server IP List.
     */
    public List<String> getConnectedServer(){
        return connections.keySet().stream().collect(Collectors.toList());
    }
    
}
