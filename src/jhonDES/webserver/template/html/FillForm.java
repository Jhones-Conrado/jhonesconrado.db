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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jhonDES.db.core.JsonUtils;
import jhonDES.db.interfaces.Entity;

/**
 * Responsável por preencher um formulário a partir dos dados de uma entidade.\n
 * Após preenchido o formulário receberá o método PUT, pois agora se trata de uma
 * atualização de alguma entidade já existente.
 * @author jhonesconrado
 */
public class FillForm {
    
    private final Entity entidade;
    private final String form;

    private List<String> campos;
    
    public FillForm(Entity entidade, String form) {
        this.entidade = entidade;
        this.form = form;
        this.campos = new ArrayList<>();
    }
    
    /**
     * Passa todos os campos do formulário para uma lista.
     */
    private void carregaCampos(){
        int inicioDosInputs = form.indexOf(">")+1;
        int finalDosInputs = form.indexOf("</form");
        String temp = form.substring(inicioDosInputs, finalDosInputs);
        temp = temp.replaceAll(">", "");
        String[] tempArr = temp.split("<input ");
        for (String tempArr1 : tempArr) {
            if (!tempArr1.isBlank()) {
                campos.add(tempArr1.replaceAll("  ", "").replaceAll("\n", ""));
            }
        }
    }
    
    /**
     * Vai preparar o formulário de acordo com a entidade passada, preenchendo
     * cada campo do formulário.\n
     * O "name" do input deve coincidir com nome da variável da entidade.
     * @return Formulário preenchido com as informações da entidade.
     */
    public String getFilled(){
        carregaCampos();
        List<String> filleds = new ArrayList<>();
        Map<String, String> att = JsonUtils.getAttributes(entidade.getAsJSON());
        
        //Percorre a lista de campos do formulário, buscando correspondente na entidade.
        for(String campoAtual : campos){
            if(campoAtual.contains("name=\"")){
                int inicio = campoAtual.indexOf("name=\"")+"name=\"".length();
                int fim = campoAtual.indexOf("\"", inicio);
                String campo = campoAtual.substring(inicio, fim);
                
                if(att.containsKey(campo)){
                    String name = 
                            //anterior ao valor do campo
                            campoAtual.substring(0, campoAtual.indexOf("value=\"") + "value=\"".length()) +
                            //valor do campo
                            att.get(campo) +
                            //posterior ao valor do campo
                            campoAtual.substring(campoAtual.indexOf("\"", campoAtual.indexOf("value=\"") + "value=\"".length()));
                    
                    filleds.add(name);
                } else {
                    filleds.add(campoAtual);
                }
                
            } else {
                filleds.add(campoAtual);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(String a : filleds){
            sb.append("<input " + a + ">\n");
        }
        
        String form = this.form.substring(0, this.form.indexOf("method=\"")+"method=\"".length())
                + "PUT"
                + this.form.substring(this.form.indexOf("\"", this.form.indexOf("method=\"")+"method=\"".length()));
        
        return HTMLManager.clearEntitiesFields(form.substring(0, form.indexOf(">")+1) + "\n" 
                + sb.toString() + form.substring(form.indexOf("</form")));
    }
    
}
