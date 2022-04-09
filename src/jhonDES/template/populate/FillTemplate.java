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

import java.util.ArrayList;
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
        template = clearCampos(template);
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
        template = clearCampos(template);
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
            List<Integer> campos = list(base, "campo=\"");
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
                    String chave = getKeyFromCampo(base.substring(campos.get(i)));
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
        List<Integer> links = list(base, "<a");
        
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
                    String key = getKeyFromCampo(tag);
                    //verifica se o objeto informado como referência possui o campo pedido.
                    if(att.containsKey(key)){
                        ref = att.get(key);
                    } else {
                        //Em caso so campo da tag não ser encontrado, o ID será passado por padrão.
                        ref = att.get("id");
                    }
                } else {
                    //Assume o ID como valor padrão caso nenhum campo tenha sido passado no corpo da tag "a".
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
     * Limpa do HTML todos os parâmetros "campo".
     * @param base
     * @return 
     */
    private String clearCampos(String base){
        if(base != null){
            if(base.contains("campo=\"")){
                List<Integer> campos = new ArrayList<>();
                int from = 0;
                while((from = base.indexOf("campo=\"", from)) != -1){
                    campos.add(base.indexOf("campo=\"", from));
                    from = base.indexOf("campo=\"", from)+7;
                }
                for(int i = campos.size()-1 ; i >= 0 ; i--){
                    String pre = base.substring(0, campos.get(i));
                    String pos = base.substring(base.indexOf("\"", campos.get(i)+7)+1);
                    base = pre+pos;
                }
            }
            base = base.replaceAll(" >", ">");
            return base;
        }
        return base;
    }
    
    /**
     * Conta as ocorrências de uma string no html informado.
     * @param html Template base.
     * @param key Chave para busca.
     * @return Lista com a posição das ocorrências.
     */
    private List<Integer> list(String html, String key){
        List<Integer> links = new ArrayList<>();
        if(html != null){
            int from = 0;
            while((from = html.indexOf(key, from)) != -1){
                links.add(html.indexOf(key, from));
                from = html.indexOf(key, from)+key.length();
            }
        }
        return links;
    }
    
    /**
     * Recebe um html, procura algum parâmetro "campo" e retorna a chave.
     * Retornará a chave da primeira ocorrência de parâmetro "campo".
     * @param taghtml TagHTML.
     * @return 
     */
    private String getKeyFromCampo(String taghtml){
        if(taghtml.contains("campo=\"")){
            // local da aspa de fechamento do parâmetro campo.
            int indice = taghtml.indexOf("\"", taghtml.indexOf("campo=\"")+7);
            // Chave do parâmetro campo.
            return taghtml.substring(taghtml.indexOf("campo=\"")+7, indice);
        }
        return null;
    }
    
    /**
     * Completa o link de um parâmetro href com a informação passada.
     * @param taghtml Tag HTML com o parâmetro href.
     * @param info
     * @return 
     */
    private String completeLink(String taghtml, String info){
        if(taghtml.contains("href=\"")){
            //Local de fechamento da aspa do parâmetro href.
            int finalhref = taghtml.indexOf("\"", taghtml.indexOf("href=\"")+6);
            //Verifica se o link foi informado com ou sem a barra final.
            if(!taghtml.substring(finalhref-1, finalhref).equals("/")){
                info = "/"+info;
            }
            return taghtml = taghtml.substring(0, finalhref) + info + taghtml.substring(finalhref);
        }
        return taghtml;
    }
    
    /**
     * Verifica se todas as tags do HTML foram fechadas corretamente.
     * @param html HTML para verificar.
     * @return resultado da verificação.
     */
    private boolean allTagsClosed(String html){
        if(html.length()>0){
            int o = 0;
            for(int i = 0 ; i < html.length()-1 ; i++){
                if(html.substring(i, i+1).equals("<")){
                    o++;
                } else if(html.substring(i, i+1).equals(">")){
                    o--;
                }
                if(o == 2 || o == -1){
                    return false;
                }
            }
            if(o != 0){
                return false;
            }
        }
        return true;
    }
    
}
