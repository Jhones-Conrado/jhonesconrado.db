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
package jhonDES.webserver.template.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jhonDES.webserver.template.html.HTMLManager;

/**
 * Recebe um link na criação para retornar um array ou lista dos parâmetros achados no link.
 * @author jhonesconrado
 */
public class ParameterSpliter {
    
    private final String path;
    private String link;

    public ParameterSpliter(String path) {
        this.path = path;
    }
    
    /**
     * Converte os parâmetros do link em um array de Strings.
     * @return Array de String dos parâmetros.
     * @throws IOException 
     */
    public String[] getAsArray() throws IOException{
        String parameters = "";
        String path = this.path;
        while(HTMLManager.getHTML(path) == null){ //Verifica se existe algum parâmetro no link.
            int last = path.lastIndexOf("/"); //Ultima barra do link.
            if(last != -1){ //verifica se realmente existe uma barra no link.
                parameters = path.substring(last) + parameters; //Adiciona o parâmetro.
                path = path.substring(0, last); //Remove o parâmetro encontrado do link.
                if(path.isEmpty()){ //Caso não seja encontrado nenhuma página, deverá retornar um erro.
                    //Depois mudar para página de erro.
                    path = "index";
                    break;
                }
            } else {
                break;
            }
        }
        if(parameters.startsWith("/")){
            parameters = parameters.substring(1);
        }
        this.link = path;
        return parameters.split("/");
    }
    
    /**
     * Converte o link em uma lista de String dos parâmetros.
     * @return Lista dos parâmetros.
     * @throws IOException 
     */
    public List<String> getAsList() throws IOException{
        return Arrays.asList(getAsArray());
    }
    
    /**
     * Separa os atributos passados no link, mantendo somente a raiz do link, aquela
     * que aponta para algum arquivo HTML.
     * @return Link para um arquivo HTML.
     * @throws IOException 
     */
    public String getLinkRoot() throws IOException{
        if(link == null){
            getAsArray();
        }
        return link;
    }
    
}
