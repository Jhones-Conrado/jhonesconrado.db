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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import jhonDES.webserver.Memory;
import jhonDES.webserver.errors.NullHTML;

/**
 * Ferramentas para manusear um HTML, como carregar um tamplate, listar tags, etc...
 * @author jhonesconrado
 */
public class HTMLManager {
    
    /**
     * Conta as ocorrências de uma string no html informado e retorna uma lista
     * com os indices de onde começa cada ocorrência.
     * @param html Template base.
     * @param key Chave para busca.
     * @return Lista com a posição das ocorrências.
     */
    public static List<Integer> list(String html, String key){
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
     * Recebe um html e retorna o valor do primeiro parâmetro campo.
     * @param html
     * @return Valor do campo.
     */
    public static String getValueFromCampo(String html){
        return getValueFrom(html, "campo");
    }
    
    /**
     * Recebe um html e retorna o valor do primeiro parâmetro entity.
     * @param html
     * @return 
     */
    public static String getValueFromEntityField(String html){
        return getValueFrom(html, "entity");
    }
    
    /**
     * Recebe uma taghtml, procura algum parâmetro e retorna a chave.
     * Retornará a chave da primeira ocorrência de parâmetro.
     * @param taghtml TagHTML.
     * @return 
     */
    public static String getValueFrom(String taghtml, String field){
        if(!field.endsWith("=\"")){
            field = field+"=\"";
        }
        if(taghtml.contains(field)){
            // local da aspa de fechamento do parâmetro campo.
            int indice = taghtml.indexOf("\"", taghtml.indexOf(field)+field.length());
            // Chave do parâmetro campo.
            return taghtml.substring(taghtml.indexOf(field)+field.length(), indice);
        }
        return null;
    }
    
    /**
     * Limpa do HTML todos os parâmetros "campo".
     * @param base
     * @return 
     */
    public static String clearCampos(String base){
        return clear(base, "campo");
    }
    
    /**
     * Limpa do HTML todos os parâmetros "entity".
     * @param base
     * @return 
     */
    public static String clearEntitiesFields(String base){
        return clear(base, "entity");
    }
    
    /**
     * Recebe um HTML e um parâmetro a ser removido do HTML.\n
     * por exemplo, pode remover todos os parâmetros href, link, etc...
     * @param base HTML a ser limpo.
     * @param field Parâmetro a ser removido.
     * @return HTML limpo.
     */
    public static String clear(String base, String field){
        if(base != null && field != null){
            if(!field.endsWith("=\"")){
                field = field + "=\"";
            }
            if(base.contains(field)){
                List<Integer> campos = new ArrayList<>();
                int from = 0;
                while((from = base.indexOf(field, from)) != -1){
                    campos.add(base.indexOf(field, from));
                    from = base.indexOf(field, from)+field.length();
                }
                for(int i = campos.size()-1 ; i >= 0 ; i--){
                    String pre = base.substring(0, campos.get(i));
                    String pos = base.substring(base.indexOf("\"", campos.get(i)+field.length())+1);
                    base = pre+pos;
                }
            }
            base = base.replaceAll(" >", ">");
            return base;
        }
        return base;
    }
    
    
    /**
     * Verifica se todas as tags do HTML foram fechadas corretamente.
     * @param html HTML para verificar.
     * @return resultado da verificação.
     */
    public static boolean allTagsClosed(String html){
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
    
    /**
     * Procura um arquivo HTML dentro do pacote de HTMLs.
     * @param name Nome do arquivo, não é necessário usar o .html
     * @return HTML do arquivo, caso o arquivo seja encontrando.
     * @throws IOException Caso não seja encontrado o HTML.
     */
    public static String getHTML(String name) throws IOException{
        if(name.contains(".") && !name.contains("html")){
            return new HTMLManager().getHtml(name);
        } else if(!name.endsWith(".html")){
            name = name+".html";
        }
        if(name.startsWith("/") && name.length() > 1){
            name = name.substring(1);
        }
        return new HTMLManager().getHtml(name);
    }
    
    /**
     * Procura um arquivo HTML dentro do pacote de HTMLs.
     * @param name
     * @return
     * @throws IOException 
     */
    private String getHtml(String name) throws IOException{
        String retorno = null;
        InputStream in = getClass().getResourceAsStream("/templates/"+name);
        if(in != null){
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String temp;
            while((temp = r.readLine()) != null){
                sb.append(temp);
                sb.append("\n");
            }
            return sb.toString();
        } else {
//            System.out.println("Template does not exist.");
        }
        
        return retorno;
    }
    
    /**
     * Retorna a tag html a partir do seu ID;
     * @param html Para buscar a tag.
     * @param id Da tag a ser buscada.
     * @return Tag encontrada.
     */
    public static String getTagByID(String html, String id){
        if(html != null){
            if(html.contains(id)){ //verifica se contém o id ou nome.
                int idindex = html.indexOf(id); // indice da primeira ocorrência da busca.
                int init = idindex; // variável que irá contar o retorno até a abertura da tag.
                while(!html.substring(init-1, init).equals("<")){ //contagem de retornos.
                    init--; // decremento do init a partir do indice do ID ou nome buscado.
                }
                init--; // após achar a abertura da tag, decrementa mais uma vez para ajustar o ponteiro.
                
                /**
                 * Começando da abertura da tag, procurará o tipo da tag, tentando
                 * localizar o próximo espaço em branco ou fechamento >. \n
                 * Isso acontece porque uma tag pode iniciar de duas formas: \n
                 * <minhatag param="alguma coisa"> ou <minhatag>.
                 */
                int fechamento = init; //conterá o indice do final do nome da tag.
                while(true){
                    String temp = html.substring(fechamento, fechamento+1);
                    if(temp.equals(" ") || temp.equals(">")){
                        break;
                    }
                    fechamento++;
                }
                
                //Nome da tag propriamente dito.
                String tagType = html.substring(init+1, fechamento);
                
                int contagem = 1;
                int finalDaTag = fechamento;//Guardará o final da tag.
                while(contagem > 0){
                    int index = html.indexOf(tagType, finalDaTag);
                    if(html.substring(index-1, index).equals("/")){
                        contagem--;
                    } else if(html.substring(index-1, index).equals("<")){
                        contagem++;
                    }
                    finalDaTag = index+tagType.length();
                }
                finalDaTag++;

                return html.substring(init, finalDaTag);
            }
        }
        return null;
    }
    
    /**
     * Retorna a primeira ocorrência de uma tag html.
     * @param html Para busca da tag.
     * @param tag A ser buscada.
     * @return Tag localizada.
     */
    public static String getFirstTag(String html, String tag){
        return getTagByID(html, tag);
    }
    
    /**
     * Localiza a tag main dentro do html e retorna a entidade encontrada dentro de main.
     * @param htmlName Nome do arquivo html a ser buscado.
     * @return Classe main do html localizado.
     * @throws IOException Arquivo do html não localizado.
     */
    public static Class getMainEntity(String htmlName) throws IOException, NullHTML{
        String html;
        if(!htmlName.contains("<")){
            html = getHTML(htmlName);
        } else {
            html = htmlName;
        }
        EntitiesFiller ef = new EntitiesFiller();
        String main = getFirstTag(html, "main");
        String type = ef.getEntityType(main);
        return Memory.get().getByName(type);
    }
    
    /**
     * Verifica se o HTML é um formulário de cadastramento ou alteração.
     * @param html A ser verificado.
     * @return Resultado da verificação. Verdadeiro para caso o html contiver
     * uma tag "form" dentro de uma tag "main".
     */
    public static boolean isAForm(String html){
        if(html.contains("main") && html.contains("form")){
            if(getFirstTag(html, "main").contains("<form")){
                return true;
            }
        }
        
        return false;
    }
    
}
