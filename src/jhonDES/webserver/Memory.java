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

import jhonDES.webserver.core.Reflection;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhonDES.db.interfaces.Entity;

/**
 *
 * @author jhonesconrado
 */
public class Memory {
    private static Memory instance;
    
    private Map<String, Class> entidades;
    
    /**
     * Adiciona uma entidade ao mapa.
     * @param classe 
     */
    public void addEntity(Class classe){
        entidades.put(classe.getSimpleName(), classe);
    }
    
    /**
     * Remove uma entidade do mapa a partir de sua classe.
     * @param classe 
     */
    public void removeEntity(Class classe){
        entidades.remove(classe.getSimpleName());
    }
    
    /**
     * Remove uma entidade do mapa a partir do seu nome.
     * @param classe 
     */
    public void removeEntity(String classe){
        entidades.remove(classe);
    }
    
    /**
     * Retorna uma entidade a partir do seu nome.
     * @param className
     * @return 
     */
    public Class getByName(String className){
        if(entidades.containsKey(className)){
            return entidades.get(className);
        }
        return null;
    }
    
    private Memory(){
        entidades = new HashMap<>();
        try {
            System.out.println("Carregando entidades.");
            initEntities();
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Memory get(){
        if(instance == null){
            instance = new Memory();
        }
        return instance;
    }
    
    /**
     * Adiciona todas as entidades do projeto na memória.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    private void initEntities() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        List<Class> filter = Reflection.getInstance().getEntities();
        for(Class c : filter){
            addEntity(c);
        }
    }
    
    /**
     * Retorna uma nova instância da entidade a partir do nome.
     * @param name Nome da entidade, deve ser o nome da classe.
     * @return Nova instância da entidade.
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public Entity getNewInstanceOf(String name) throws InstantiationException, IllegalAccessException{
        if(entidades.containsKey(name)){
            return (Entity) entidades.get(name).newInstance();
        }
        return null;
    }
}
