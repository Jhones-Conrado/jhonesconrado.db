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
 *
 * @author jhonesconrado
 */
public class InterpreterMemory {
    private static final Map<String, DefaultInterpreter> interpreters = new HashMap<>();
    
    /**
     * Add a new interpreter.
     * @param interpreter 
     */
    public static void addInterpreter(DefaultInterpreter interpreter){
        interpreters.put(interpreter.key, interpreter);
    }
    
    /**
     * Remove a interpreter.
     * @param key 
     */
    public static void removeInterpreter(String key){
        interpreters.remove(key);
    }
    
    /**
     * Gets a interpreter.
     * @param key Interpreter key.
     * @return A Interpreter.
     */
    public static DefaultInterpreter getFromKey(String key){
        return interpreters.get(key);
    }
    
    /**
     * Interprete a message and return a String of result.
     * @param key Interpreter key.
     * @param msg Message to be analised.
     * @return Result of the interpretation.
     */
    public static String interpreter(String key, String msg){
        return interpreters.get(key).interpret(msg);
    }
    
    public static String interpreter(String msg){
        if(msg.contains(":")){
            int index = msg.indexOf(":");
            String key = msg.substring(0, index+1);
            if(interpreters.containsKey(key)){
                return interpreters.get(key).interpret(msg);
            } else {
                return "No interpreter found.";
            }
        }
        return "No key found in message.";
    }
}
