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
package core.interpreters;

import java.util.HashMap;
import java.util.Map;

/**
 * Armazena todos os interpretadores de comunicação, recebendo mensagens e 
 * direcionando-as para os seus devidos interpretadores.
 * It stores all the communication interpreters, receiving messages and 
 * directing them to their proper interpreters.
 * @author jhonesconrado
 */
public class InterpreterMemory {
    private static final Map<String, DefaultInterpreter> interpreters = new HashMap<>();
    
    /**
     * Adiciona um interpretador.
     * Add a new interpreter.
     * @param interpreter 
     */
    public static void addInterpreter(DefaultInterpreter interpreter){
        interpreters.put(interpreter.key, interpreter);
    }
    
    /**
     * Remove um interpretador.
     * Remove a interpreter.
     * @param key 
     */
    public static void removeInterpreter(String key){
        interpreters.remove(key);
    }
    
    /**
     * Retorna um interpretador a partir de sua chave.
     * Gets a interpreter from key.
     * @param key Interpreter key.
     * @return A Interpreter.
     */
    public static DefaultInterpreter getFromKey(String key){
        return interpreters.get(key);
    }
    
    /**
     * Interpreta uma mensagem e retorna a String do resultado.
     * Interprete a message and return a String of result.
     * @param msg Interpreter key.
     * @param bytes Byte array.
     * @return Result of the interpretation.
     */
    public static String interpreter(String msg, byte[] bytes){
        if(msg.contains(":")){
            int index = msg.indexOf(":");
            String key = msg.substring(0, index+1);
            if(interpreters.containsKey(key)){
                return interpreters.get(key).interpret(msg, bytes);
            }
        }
        return "No key found in message.";
        
    }
    
    /**
     * Identifica automaticamente o interpretador e direciona a mensagem, 
     * retornando a resposta.
     * Automatically identifies the interpreter and routes the message, 
     * returning the response.
     * @param msg
     * @return 
     */
    public static String interpreter(String msg){
        return interpreter(msg, null);
    }
}
