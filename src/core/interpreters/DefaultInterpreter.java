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

/**
 * Um interpretador que receberá mensagens e realizará funções a partir dos dados recebidos.
 * An interpreter that will receive messages and perform functions from the data received.
 * @author jhonesconrado
 */
public abstract class DefaultInterpreter {
    public final String key;

    public DefaultInterpreter(String key) {
        this.key = key;
        InterpreterMemory.addInterpreter(this);
    }
    
    public synchronized String interpret(String msg){
        return interpret(msg, null);
    }
    
    public synchronized String interpret(String msg, byte[] bytes){
        if(msg.startsWith(key)){
            String t = onInterpret(msg.substring(key.length()), bytes);
            if(t != null){
                return t;
            }
            return "";
        }
        return null;
    }
    
    protected abstract String onInterpret(String msg, byte[] bytes);
    
}
