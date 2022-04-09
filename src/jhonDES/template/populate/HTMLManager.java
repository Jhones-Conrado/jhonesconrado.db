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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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
     * Recebe um html, procura algum parâmetro e retorna a chave.
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
    
    public static String getHTML(String name) throws IOException{
        if(!name.endsWith(".html")){
            name = name+".html";
        }
        BufferedReader r = Files.newBufferedReader(new HTMLManager().getHtml(name).toPath());
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = r.readLine()) != null){
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private File getHtml(String name){
        try {
            return new File(getClass().getResource("/jhonDES/template/html/"+name).toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(HTMLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
