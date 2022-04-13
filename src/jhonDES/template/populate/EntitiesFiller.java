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
package jhonDES.template.populate;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhonDES.db.filter.Filter;
import jhonDES.db.interfaces.Entity;

/**
 * Responsável por identificar entidades no HTML e preencher os campos exigidos.
 * @author jhonesconrado
 */
public class EntitiesFiller {
    
    /**
     * Recebe um HTML, identifica qual a entidade a ser preenchida, identifica
     * qual o template a ser utilizado, identifica o local onde deverá ser
     * inserido os templates preenchidos para cada entidade, por fim, insere
     * todas as entidades no HTML e o retorna preenchido.
     * @param html Base HTML a ser preenchida.
     * @return HTML preenchido.
     */
    public String fillEntities(String html){
        return fillEntities(html, null);
    }
    
    /**
     * Recebe um HTML, identifica qual a entidade a ser preenchida, identifica
     * qual o template a ser utilizado, identifica o local onde deverá ser
     * inserido os templates preenchidos para cada entidade, por fim, insere
     * todas no HTML todas as entidades que passaram no filtro e o retorna 
     * preenchido.
     * @param html Base HTML a ser preenchida.
     * @param filter Filtro a ser aplicado nas entidades que serão adicionadas
     * ao HTML.
     * @return HTML preenchido.
     */
    public String fillEntities(String html, Filter filter){
        while(html.contains("entity=\"")){
            String template = getTemplate(html);
            if(template != null){
                try {
                    String entityType = getEntityType(template);
                    Class<?> forName = Class.forName(entityType);
                    Entity e = (Entity) forName.newInstance();
                    List<Entity> all;
                    if(filter != null){
                        all = e.loadAll(filter);
                    } else {
                        all = e.loadAll();
                    }
                    String filledHtml = new FillTemplate().fill(template, all);
                    
                    int parentStart = getParentStartIndex(html, "entity=\"");
                    html = clearTagAndPut(html, parentStart, filledHtml);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException ex) {
                    Logger.getLogger(EntitiesFiller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return html;
    }
    
    /**
     * Localiza a primeira ocorrência do parâmetro "entity" retornando o corpo
     * da tag em que o parâmetro foi localizado.
     * @param html
     * @return 
     */
    public String getTemplate(String html){
        if(html != null){
            if(html.contains("entity=\"")){
                int indice = html.indexOf("entity=\"");
                if(indice > 0){
                    int starttag = getTagStartIndex(html, indice);
                    int finaltag = getTagFinalIndex(html, starttag);
                    return html.substring(starttag, finaltag);
                }
            }
        }
        return null;
    }
    
    public String getEntityType(String html){
        if(html.contains("entity=\"")){
            int indice = html.indexOf("entity=\"");
            int ind2 = html.indexOf("\"", indice+8);
            return html.substring(indice+8, ind2);
        }
        return null;
    }
    
    /**
     * Limpa todo o conteúdo de uma tag e insere um novo html dentro.
     * @param html HTML contendo a tag a ser limpa.
     * @param tagStart Indice da tag, posição imediatamente anterior ao <.
     * @param toPutIn HTML a ser inserido dentro da tag.
     * @return HTML com a tag preenchida.
     */
    public String clearTagAndPut(String html, int tagStart, String toPutIn){
        if(html.substring(tagStart, tagStart+1).equals("<")){
            int count = tagStart+1;
            while(!html.substring(count, count+1).equals(">") && !html.substring(count, count+1).equals(" ")){
                count++;
            }
            
            String type = html.substring(tagStart+1, count);
            
            int tags = 1;
            while(tags>0){
                count = html.indexOf(type, count)+type.length();
                if(html.substring(count-type.length()-1, count).startsWith("<")){
                    tags++;
                } else if(html.substring(count-type.length()-1, count).startsWith("/")){
                    tags--;
                }
            }
            tagStart = html.indexOf(">", tagStart) + 1;
            count = count - type.length() - 2;
            return html.substring(0, tagStart) + "\n" + toPutIn + html.substring(count);
        }
        return html;
    }
    
    /**
     * Limpa todo o conteúdo de uma tag.
     * @param html HTML contendo a tag a ser limpa.
     * @param index Indice da tag, posição imediatamente anterior ao <.
     * @return HTML com a tag limpa.
     */
    public String clearTag(String html, int index){
        return clearTagAndPut(html, index, "");
    }
    
    /**
     * Irá encontrar a primeira ocorrência do parâmetro "value" e a partir disso
     * retroceder no HTML até localizar o início da tag pai, retornando o seu
     * indice.
     * @param html HTML completo para busca.
     * @param value Valor de referência da TAG. Pode ser o id, uma classe, o valor
     * de um campo, etc...
     * @return Indice da abertura da tag pai.
     */
    public int getParentStartIndex(String html, String value){
        int index = html.indexOf(value);
        int count = 0;
        while(count < 2){
            if((html.substring(index-1, index)).equals("<")){
                if(html.substring(index, index+1).equals("/")){
                    count--;
                } else {
                    count++;
                }
            }
            index--;
        }
        return index;
    }
    
    /**
     * 
     * @param html
     * @param start Posição do início da tag, imediatamente anterior a abertura.
     * @return 
     */
    public int getTagFinalIndex(String html, int start){
        String type = getTagType(html, start+1);
        start = html.indexOf(type, start+type.length());
        int count = 1;
        while(count > 0){
            start = html.indexOf(type, start);
            if(html.substring(start-1, start).equals("<")){
                count++;
            } else {
                count--;
            }
            start+= type.length();
        }
        start = html.indexOf(">", start)+1;
        return start;
    }
    
    /**
     * Faz um recuo a partir da posição informada no html, até encontrar a abertura
     * da tag em que o mid está inserido.
     * @param html
     * @param mid
     * @return 
     */
    public int getTagStartIndex(String html, int mid){
        while(!html.substring(mid-1, mid).equals("<")){
            mid--;
        }
        mid--;
        return mid;
    }
    
    /**
     * Retorna o tipo de tag de acordo com a posição do texto informada.
     * @param html
     * @param index
     * @return 
     */
    public String getTagType(String html, int index){
        //Retrocesso até achar a abertura da tag.
        while(!html.substring(index-1, index).equals("<")){
            index--;
        }
        index --;

        //Avanço até achar o tipo da tag.
        int ind2 = index;
        while(!html.substring(ind2, ind2+1).equals(" ") && !html.substring(ind2, ind2+1).equals(">")){
            ind2++;
        }
        //Tipo da tag, o nome após o <.
        return html.substring(index+1, ind2);
    }
    
}
