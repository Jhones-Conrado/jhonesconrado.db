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
package jhonDES.db.interfaces;

import jhonDES.db.filter.Filter;
import jhonDES.db.core.IO;
import jhonDES.db.core.IdManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Uma interface com todos os métodos mais básicos de uma entidade, permite 
 * salvar, carregar, deletar e filtrar.
 * An interface with all the most basic methods of an entity, allows saving, 
 * loading, deleting and filtering.
 * @author jhonesconrado
 */
public interface Entity extends Serializable{
    
    /**
     * @return Id da entidade.
     */
    long getId();
    
    /**
     * Verifica se o ID é igual a -1l, somente se for, então um novo ID será aceito.
     * Checks if the ID is equal to -1l, only if it is, then a new ID will be accepted.
     * @param id Novo ID.
     */
    default void setId(long id){
        if(getId() == -1l){
            onSetId(id);
        }
    }
    
    /**
     * Deve ser implementado para atribuir o valor do ID a alguma variável.
     * It must be implemented to assign the ID value to some variable.
     * @param id 
     */
    void onSetId(long id);
    
    /**
     * Salva a entidade no banco de dados.
     * Saves the entity to Database.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException 
     */
    default void save() throws IOException, FileNotFoundException, ClassNotFoundException{
        if(getId() == -1l){
            setId(IdManager.getInstance().getNewId(this.getClass()));
        }
        IO.save(this);
    }
    
    /**
     * Carrega uma entidade, do banco de dados, a partir do seu ID.
     * Loads an entity from database by ID.
     * @param <T>
     * @param id
     * @return
     * @throws IOException 
     */
    default <T extends Entity> T load(long id) throws IOException{
        return (T) IO.load(this, id);
    }
    
    /**
     * Carrega todas as entidades salvas desse tipo.
     * Loads all entities of this type.
     * @param <T>
     * @return
     * @throws IOException 
     */
    default <T extends Entity> List<T> loadAll() throws IOException{
        return (List<T>) IO.loadAll(this.getClass());
    }
    
    /**
     * Carrega todas as entidades salvas desse tipo que contenham determinado trecho de informação.
     * Loads all saved entities of this type that contain a certain piece of information.
     * @param <T>
     * @param findKey Trecho de informação que deverá ser usado como base de filtro.
     * @return
     * @throws IOException 
     */
    default <T extends Entity> List<T> loadAll(String findKey) throws IOException{
        return (List<T>) IO.loadAll(this.getClass(), findKey);
    }
    
    /**
     * Carrega todas as entidades que passarem em um filtro informado.
     * Loads all entities that pass a given filter.
     * @param <T>
     * @param filter Mapa de "chave" "valor" que deverá ser usado como filtro.
     * @param convergence O valor deve coincidir inteiramente ou apenas em parte? true para inteiro, false para parte.
     * @param allfilters Precisa passar em todos os filtros, ou apenas um já basta? true para todos, false para pelo menos um.
     * @return Lista filtrada de entidades.
     * @throws IOException 
     */
    @Deprecated
    default <T extends Entity> List<T> loadAll(Map<String, String> filter, boolean convergence, boolean allfilters) throws IOException{
        return (List<T>) IO.loadAll(this.getClass(), filter, convergence, allfilters);
    }
    
    /**
     * Carrega todas as entidades que passarem em um filtro informado.
     * Loads all entities that pass a given filter.
     * @param <T>
     * @param filter
     * @return
     * @throws IOException 
     */
    default <T extends Entity> List<T> loadAll(Filter filter) throws IOException{
        return (List<T>) IO.loadAll(this.getClass(), filter);
    }
    
    /**
     * Deleta a entidade.
     * Delete this entity.
     * @param <T> 
     */
    default <T extends Entity> void delete(){
        IO.delete(this);
    }
    
    /**
     * Deleta TODAS as entidades desse tipo!
     * Delete all entities of this type.
     * @param <T> 
     */
    default <T extends Entity> void deleteAll(){
        IO.deleteAll(this);
    }
    
    /**
     * Aplica um filtro nas entidades e depois deleta todas que tenham passado no filtro.
     * Apply a filter to entities and then delete all that passed the filter.
     * @param <T>
     * @param filter Mapa de "chave" "valor" que deverá ser usado como filtro.
     * @param convergence O valor deve coincidir inteiramente ou apenas em parte? true para inteiro, false para parte.
     * @param allfilters Precisa passar em todos os filtros, ou apenas um já basta? true para todos, false para pelo menos um.
     * @throws IOException 
     */
    @Deprecated
    default <T extends Entity> void deleteAllByFilter(Map<String, String> filter, boolean convergence, boolean allfilters) throws IOException{
        IO.deleteAllByFilter(this.getClass(), filter, convergence, allfilters);
    }
    
    /**
     * Aplica um filtro nas entidades e depois deleta todas que tenham passado no filtro.
     * Apply a filter to entities and then delete all that passed the filter.
     * @param <T>
     * @param filter Filtro a ser aplicado na busca por entidades a serem deletadas.
     * @throws IOException 
     */
    default <T extends Entity> void deleteAllByFilter(Filter filter) throws IOException{
        IO.deleteAllByFilter(this.getClass(), filter);
    }
    
}
