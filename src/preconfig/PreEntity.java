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
package preconfig;

import interfaces.Entity;

/**
 * Uma simples entidade pre implementada para facilitar o seu trabalho e para
 * você saber como implementar uma entidade.
 * A simple pre implemented entity to facilitate your job and to know how to do
 * a new entity implementation.
 * @author jhonesconrado
 */
public class PreEntity implements Entity{
    
    /**
     * The ID of entity.
     */
    private long id = -1l;
    
    /**
     * @return The ID number of this entity.
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * @param id Set a new ID for this entity.
     */
    @Override
    public void onSetId(long id) {
        this.id = id;
    }
    
}
