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
package jhonDES.webserver.template.preparers;

import java.io.IOException;
import jhonDES.db.interfaces.Entity;
import jhonDES.webserver.Memory;
import jhonDES.webserver.errors.NullHTML;
import jhonDES.webserver.template.html.EntitiesFiller;
import jhonDES.webserver.template.html.FillForm;
import jhonDES.webserver.template.html.HTMLManager;
import jhonDES.webserver.template.tools.ParameterSpliter;

/**
 * Usado para preparar um formulário de uma página, preenchendo-o caso necessário
 * com as informações de alguma entidade.
 * @author jhonesconrado
 */
public class FormPrepare {
    /**
     * Link do arquivo HTML que possui o formulário, seguido do ID da entidade caso
     * necessário.
     */
    private final String path;
    
    /**
     * Instancia um novo preparador de formulário.
     * @param path 
     */
    public FormPrepare(String path) {
        this.path = path;
    }
    
    /**
     * Vai tentar converter o parâmetro seguinte ao path do arquivo html em ID,
     * caso a conversão seja um sucesso, carregará a entidade a partir do banco
     * de dados.
     * @param spliter um ParapemterSpliter iniciado com o path da página do formulário.
     * @param ef um EntitiesFiller para poder ler o template e tipo de entidade do html.
     * @return Entidade a partir do tipo achado no HTML e ID passado como parâmetro no link.
     * @throws IOException
     * @throws NullHTML
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    private Entity getEntity(ParameterSpliter spliter, EntitiesFiller ef) throws IOException, NullHTML, InstantiationException, IllegalAccessException{
        String html = HTMLManager.getHTML(spliter.getLinkRoot());
        if(!spliter.getAsArray()[0].isBlank()){
            long id = Long.valueOf(spliter.getAsArray()[0]);
            if(id != -1){
                return Memory.get().getNewInstanceOf(ef.getEntityType(ef.getMainTemplate(html))).load(id);
            }
        }
        return null;
    }
    
    /**
     * Preenche o formulário com os dados da entidade passada no link, caso exista.
     * @return Formulário preenchido.
     * @throws IOException
     * @throws NullHTML 
     */
    public String getPrepared() throws IOException, NullHTML{
        ParameterSpliter spliter = new ParameterSpliter(path);
        EntitiesFiller ef = new EntitiesFiller();
        
        String html = HTMLManager.getHTML(spliter.getLinkRoot());
        if(html != null){
            if(HTMLManager.isAForm(html)){
                Class mainEntity = HTMLManager.getMainEntity(spliter.getLinkRoot());
                Entity ente = null;
                try {
                    ente = getEntity(spliter, ef);
                    if(ente != null){
                        String template = ef.getMainTemplate(html);
                        FillForm fillform = new FillForm(ente, template);
                        return ef.clearTagAndPut(html, html.indexOf("main")-1, fillform.getFilled());
                    }
                } catch (Exception e) {
                    System.out.println("FormPrepare\nErro na preparação de um formulário: "+e);
                }
            }
        }
        return null;
    }
    
}
