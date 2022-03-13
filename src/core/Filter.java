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
package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simples e funcional filtro para validação de entidades.
 * @author jhonesconrado
 */
public class Filter {
    
    private List<FilterItem> itens;
    private boolean allFiltersNeedMatch;

    public Filter(boolean allFiltersNeedMatch) {
        itens = new ArrayList<>();
        this.allFiltersNeedMatch = allFiltersNeedMatch;
    }
    
    /**
     * Add a new filter item to filter.
     * @param item To be add.
     */
    public void addItem(FilterItem item){
        if(!itens.contains(item)){
            itens.add(item);
        }
    }
    
    /**
     * Remove an filter item from filters list.
     * @param item To be removed.
     */
    public void removeItem(FilterItem item){
        if(itens.contains(item)){
            itens.remove(item);
        }
    }
    
    /**
     * Passa o JSON pelos filtros para validação.
     * Pass a JSON in the filters to validate.
     * @param json
     * @return 
     */
    public boolean match(String json){
        List<Boolean> ifs = new ArrayList<>();
        Map<String, String> map = JsonUtils.getAttributes(json);
        
        if(allFiltersNeedMatch){
            for(FilterItem i : itens){
                if(!map.containsKey(i.key)){
                    return false;
                }
            }
        }
        
        for(FilterItem i : itens){
            if(map.containsKey(i.key)){
                ifs.add(i.match(map.get(i.key)));
            } else {
                ifs.add(false);
            }
        }
        
        if(allFiltersNeedMatch){
            return !ifs.contains(false);
        } else {
            return ifs.contains(true);
        }
    }
    
}
