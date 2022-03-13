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

/**
 * Um item de filtro que deverá ser usado por um filtro na busca por entidades.
 * A filter item that will be used in an filter to find entities.
 * @author jhonesconrado
 */
public class FilterItem {
    public static final int ALONG = 0;
    public static final int STARTS_WITH = 1;
    public static final int ENDS_WITH = 2;
    
    public final boolean fullyMatch;
    public final String key;
    public final String value;
    public final int matchMethod;
    
    /**
     * Verifica um attributo. Por padrão, caso seja uma verificação parcial, o
     * fragmento de valor será buscado em qualquer parte do valor do JSON.
     * @param fullyMatch Deverá coicidir totalmente ou parcialmente.
     * @param key Chave do atributo.
     * @param value Valor ou fragmento de valor a ser comparado.
     */
    public FilterItem(boolean fullyMatch, String key, String value) {
        this.fullyMatch = fullyMatch;
        this.key = key;
        this.value = value;
        matchMethod = 0;
    }
    
    /**
     * Verifica um attributo. Por padrão, caso seja uma verificação parcial, o
     * fragmento de valor será buscado em qualquer parte do valor do JSON.
     * @param key Chave do atributo.
     * @param value Valor ou fragmento de valor a ser comparado.
     * @param matchMethod O fragmento de valor deverá ser buscado no começo,
     * fim, ou em qualquer parte do valor do JSON?
     */
    public FilterItem(int matchMethod, String key, String value) {
        this.fullyMatch = false;
        this.key = key;
        this.value = value;
        if(matchMethod >= 0 && matchMethod < 3){
            this.matchMethod = matchMethod;
        } else {
            this.matchMethod = 0;
        }
    }
    
    /**
     * Compara se o valor passado bate com o filtro criado.
     * Compare if the value passed match with the created filter.
     * @param value Value to verify.
     * @return Result of verification.
     */
    public boolean match(String value){
        if(fullyMatch){
            return this.value.equals(value);
        } else {
            switch (matchMethod) {
                case ALONG:
                    return value.contains(this.value);
                case STARTS_WITH:
                    return value.startsWith(this.value);
                default:
                    return value.endsWith(this.value);
            }
        }
    }
    
}
