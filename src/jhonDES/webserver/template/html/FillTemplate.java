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
package jhonDES.webserver.template.html;

import java.util.List;
import java.util.Map;
import jhonDES.db.core.JsonUtils;
import jhonDES.db.interfaces.Entity;

/**
 * Recebe um HTML template e passa as informações de uma entidade para essa base.
 * @author jhonesconrado
 */
public class FillTemplate {
    
    /**
     * Preenche um HTML com as informações de um JSON.
     * @param template
     * @param json
     * @return 
     */
    public String fill(String template, String json){
        template = putAttributes(template, json);
        template = putLinks(template, json);
        template = HTMLManager.clearCampos(template);
        return template;
    }
    
    /**
     * Preenche um HTML com as informações de uma entidade.
     * @param template
     * @param entity
     * @return 
     */
    public String fill(String template, Entity entity){
        template = putAttributes(template, entity);
        template = putLinks(template, entity);
        template = HTMLManager.clearCampos(template);
        template = HTMLManager.clearEntitiesFields(template);
        return template;
    }
    
    /**
     * Cria um HTML preenchido com uma lista de entidades, usando um modelo
     * template como base.
     * O código passado no template será duplicado para cada entidade da lista.
     * @param template
     * @param entities
     * @return 
     */
    public String fill(String template, List<Entity> entities){
        StringBuilder html = new StringBuilder();
        for(Entity e : entities){
            html.append(fill(template, e));
            html.append("\n");
        }
        return html.toString();
    }
    
    /**
     * Cria um HTML preenchido com uma lista de entidades, usando um modelo
     * template como base.
     * O código passado no template será duplicado para cada JSON da lista.
     * @param template
     * @param Jsons
     * @return 
     */
    public String fillByJsonList(String template, List<String> Jsons){
        StringBuilder html = new StringBuilder();
        for(String e : Jsons){
            html.append(FillTemplate.this.fill(template, e));
            html.append("\n");
        }
        return html.toString();
    }
    
    /**
     * Preenche um HTML template a partir dos dados de um JSON.
     * @param base HTML templete.
     * @param json Objeto com informações.
     * @return HTML preenchido.
     */
    private String putAttributes(String base, String json){
        Map<String, String> att = JsonUtils.getAttributes(json);
        if(base.contains("campo=\"")){
            List<Integer> campos = HTMLManager.list(base, "campo=\"");
            //Loop para fill os campos, começando do último para o primeiro.
            for(int i = campos.size()-1 ; i >= 0 ; i--){
                // Faz um recuo permanente no HTML até encontrar a abertura da tag.
                int indice = campos.get(i);
                while(!base.substring(indice-1, indice).equals("<")){
                    if(indice-1 == 0){
                        throw new IndexOutOfBoundsException("Tag HTML não foi aberta corretamente.\n"+base);
                    }
                    indice--;
                }
                // Verifica se é diferente de um link.
                if(!base.substring(indice-1, indice+2).equals("<a ")){
                    //Extrai a chave do campo que está sendo preenchido na volta atual do loop.
                    String chave = HTMLManager.getValueFromCampo(base.substring(campos.get(i)));
                    //Verifica se o Objeto informado como referência possui esse achado no HTML.
                    if(att.containsKey(chave)){
                        //HTML que ficará antes do valor do campo informado.
                        String pre = base.substring(0, base.indexOf(">", campos.get(i))+1);
                        //HTML que ficará após o valor do campo informado.
                        String pos = base.substring(base.indexOf("<", campos.get(i)));
                        //União do HTML inicial, valor do campo e HTML final.
                        base = pre + att.get(chave) + pos;
                    }
                }
            }
            return base;
        }
        return base;
    }
    
    /**
     * Converte uma entidade em JSON e encaminha para o método putAttributes.
     * @param base
     * @param entity
     * @return 
     */
    private String putAttributes(String base, Entity entity){
        return putAttributes(base, entity.getAsJSON());
    }
    
    /**
     * Preenche os links de um HTML template a partir de um JSON de referência.
     * @param base
     * @param JSON
     * @return
     */
    private String putLinks(String base, String JSON){
        Map<String, String> att = JsonUtils.getAttributes(JSON);
        List<Integer> links = HTMLManager.list(base, "<a");
        
        //Percorre todos os links, preenchendo-os do último para o primeiro.
        for(int i = links.size()-1 ; i >= 0 ; i--){
            //Fechamento da tag "a" do link.
            int ind = base.indexOf(">", links.get(i))+1;
            
            // Corpo da tag "a"
            String tag = base.substring(links.get(i), ind);
            
            // Verifica se contém um link a ser completado.
            if(tag.contains("href=\"")){
                String ref = null;
                //Verifica se o link deve ser completado a partir de algum campo específico.
                if(tag.contains("campo=\"")){
                    String key = HTMLManager.getValueFromCampo(tag);
                    //verifica se o objeto informado como referência possui o campo pedido.
                    if(att.containsKey(key)){
                        ref = att.get(key);
                    } else {
                        //Em caso so campo da tag não ser encontrado, o ID será passado por padrão.
                        ref = att.get("id");
                    }
                } else {
                    ref = att.get("id");
                }
                base = base.substring(0, links.get(i)) + completeLink(tag, ref) + base.substring(ind);
            }
        }
        return base;
    }
    
    /**
     * Converte uma entidade em JSON e encaminha para o método putLinks.
     * @param base HTML template.
     * @param entity Entidade de referência.
     * @return HTML preenchido.
     */
    private String putLinks(String base, Entity entity){
        return putLinks(base, entity.getAsJSON());
    }
    
    /**
     * Adiciona uma informação ao final de determinado parâmetro da tag.<br/>
     * Por exemplo: Se tenho href="meulink" e uma informação "joão", o retorno
     * será href="meulink/joão".
     * @param taghtml HTML da tag.
     * @param param Parâmetro a ser completado.
     * @param info Informação a ser adicionada no final do parâmetro.
     * @return Tag com parâmetro preenchido.
     */
    private String completeParam(String taghtml, String param, String info){
        if(!param.endsWith("=\"")){
            param = param + "=\"";
        }
        if(taghtml.contains(param)){
            int finalDoParametro = taghtml.indexOf("\"", taghtml.indexOf(param)+param.length());
            if(!taghtml.substring(finalDoParametro-1, finalDoParametro).equals("/")){
                info = "/"+info;
            }
            return taghtml = taghtml.substring(0, finalDoParametro) + info + taghtml.substring(finalDoParametro);
        }
        return taghtml;
    }
    
    /**
     * Completa o link de um parâmetro href com a informação passada.
     * @param taghtml Tag HTML com o parâmetro href.
     * @param info Informação que ficará ao final do link.
     * @return Tag com parâmetro completo.
     */
    private String completeLink(String taghtml, String info){
        return completeParam(taghtml, "href", info);
    }
    
    /**
     * Completa o link de um parâmetro src com a informação passada.
     * @param taghtml Tag HTML com o parâmetro src.
     * @param info Informação que ficará ao final do link.
     * @return Tag com parâmetro completo.
     */
    private String completeSRC(String taghtml, String info){
        return completeParam(taghtml, "src", info);
    }
    
}
