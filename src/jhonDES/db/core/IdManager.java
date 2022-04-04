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
package jhonDES.db.core;

import config.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsável por gerenciar o ID dos objetos, criando novos ids únicos.
 * @author jhonesconrado
 */
public class IdManager{
    
    private static IdManager instance;
    private Map<Class, Long> keys;
    
    /**
     * Cria uma nova instância do IdManager.
     * Ao criar, verifica se já existe um arquivo de configurações de chaves, se
     * houver, carrega-o.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException 
     */
    private IdManager() throws IOException, FileNotFoundException, ClassNotFoundException {
        keys = new HashMap<>();
        File file = new File(Config.root+"/"+keys.getClass().getName()+"/keys");
        if(file.exists()){
            loadKeys();
        } else {
            saveKeys();
        }
    }
    
    /**
     * Salva o arquivo de chaves no banco de dados.
     * @throws IOException 
     */
    private void saveKeys() throws IOException{
        File file = new File(Config.root+"/"+keys.getClass().getName());
        file.mkdirs();
        File key = new File(file.getPath()+"/keys");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(key))) {
            out.writeObject(keys);
            out.flush();
        }
    }
    
    /**
     * Carrega, do banco de dados, o arquivo de chaves.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private void loadKeys() throws FileNotFoundException, IOException, ClassNotFoundException{
        File file = new File(Config.root+"/"+keys.getClass().getName()+"/keys");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            keys = (Map<Class, Long>) in.readObject();
        }
    }
    
    /**
     * Garante que nenhum ID será repetido.
     * @return Uma instância fixa do IdManager.
     */
    public static IdManager getInstance() throws IOException, FileNotFoundException, ClassNotFoundException{
        if(instance == null){
            instance = new IdManager();
        }
        return instance;
    }
    
    /**
     * Gera um novo ID para a classe informada.
     * @param classe Para contagem da chave.
     * @return Nova chave para a classe informada.
     * @throws IOException 
     */
    public long getNewId(Class classe) throws IOException{
        long ret = 0l;
        if(keys.containsKey(classe)){
            ret = keys.get(classe);
            keys.put(classe, ret+1l);
            saveKeys();
        } else {
            keys.put(classe, 1l);
            saveKeys();
        }
        return ret;
    }
    
}
