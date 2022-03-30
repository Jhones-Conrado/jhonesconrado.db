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

import core.filter.Filter;
import com.google.gson.Gson;
import config.Config;
import interfaces.Entity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsável por salvar e ler objetos.
 * @author jhonesconrado
 */
public class IO {
    
    public static final Gson gson = new Gson();
    
    public static final boolean COMPLETE = true;
    public static final boolean PARCIAL = false;
    public static final boolean ALL_FILTERS = true;
    public static final boolean ONLY_ONE = false;
    
    /**
     * Utilizado para salvar entidades no banco de dados.
     * @param entidade A ser salvo no banco de dados.
     * @throws IOException 
     */
    public static void save(Entity entidade) throws IOException{
        //Cria as pastas necessárias.
        File f = new File(Config.root+"/"+entidade.getClass().getName());
        f.mkdirs();
        
        
        String path = Config.root+"/"+entidade.getClass().getName()+"/"+entidade.getId();
        String json = gson.toJson(entidade);
        save(json, path);
    }
    
    /**
     * Salva um objeto no banco de dados em formato JSON.
     * @param obj Objeto a ser salvo.
     * @param name Nome do objeto a ser salvo.
     * @throws IOException 
     */
    public static void save(Object obj, String name) throws IOException{
        //Cria as pastas necessárias.
        File f = new File(Config.root+"/"+obj.getClass().getName());
        f.mkdirs();
        
        String path = Config.root+"/"+obj.getClass().getName()+"/"+name;
        String json = gson.toJson(obj);
        save(json, path);
    }
    
    /**
     * Método utilizado para efetivamente gravar os dados no banco de dados.
     * É chamado pelos outros dois métodos "save" a cima.
     * @param json
     * @param path
     * @throws IOException 
     */
    private static void save(String json, String path) throws IOException{
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            w.write(json);
            w.flush();
        }
    }
    
    /**
     * Carrega uma entidade apartir da sua classe e id.
     * @param entidade Classe da entidade a ser carregada.
     * @param id Da entidade que deseja carregar.
     * @return Entidade recuperada do Bando de Dados.
     * @throws IOException 
     */
    public static Entity load(Entity entidade, long id) throws IOException{
        
        if(entidade instanceof Entity){
            String path = Config.root+"/"+entidade.getClass().getName()+"/"+String.valueOf(id);
            return (Entity) load(path, entidade.getClass());
        }
        return null;
    }
    
    /**
     * Carrega um objeto a partir da sua classe e um nome
     * @param classe Do objeto a ser carregado.
     * @param name Do objeto a ser carregado.
     * @return Objeto carregado a partir do Banco de Dados.
     * @throws IOException 
     */
    public static Object load(Class classe, String name) throws IOException{
        String path = Config.root+"/"+classe.getName()+"/"+name;
        return load(path, classe);
    }
    
    /**
     * Método que efetivamente lê os dados do banco de dados. É chamado pelos
     * dois outros métodos "load".
     * @param path
     * @param classe
     * @return
     * @throws IOException 
     */
    private static Object load(String path, Class classe) throws IOException{
        if(new File(path).exists()){
            BufferedReader r = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
            return gson.fromJson(r.readLine(), classe);
        }
        return null;
    }
    
    /**
     * @param <T>
     * @param classe Da entidade que deseja recuperar a lista.
     * @return Uma lista com todas as entidades da classe informada.
     * @throws IOException 
     */
    public static <T extends Entity> List<T> loadAll(Class<T> classe) throws IOException{
        return loadAll(classe, "");
    }
    
    /**
     * Carrega todas as entidades que passaram no filtro. O campo filter não pode ser nulo.
     * @param <T> Tipo de entidade
     * @param classe Classe da entidade.
     * @param filter Mapa do tipo "chave x valor", exemplo, "cpf" "123.456.789-00".
     * @param convergence Se a verificação deve ser por totalidade ou parcialidade. 
     * Se for true, então o valor deve ser inteiramente e exatamente igual; 
     * se for false, bastará coincidir parcialmente para ser contado como verdadeiro.
     * @param allfilters Será necessário passar em todos os filtros, ou somente um basta?
     * Se true, só será contado caso todos os filtros retornarem verdadeiro.
     * Se false, será contado caso pelo menos um dos filtros retorne verdadeiro.
     * @return Lista com todas as entidades que passaram no teste.
     * @throws IOException 
     */
    @Deprecated
    public static <T extends Entity> List<T> loadAll(Class<T> classe, Map<String, String> filter, boolean convergence, boolean allfilters) throws IOException{
        if(filter != null){
            File pasta = new File(Config.root+"/"+classe.getName());
            //Verifica se a pasta das entidades existe.
            if(pasta.exists()){

                //Lista as entidades da pasta.
                File[] entidades = pasta.listFiles();

                //Cria a lista que receberá as entidades achadas.
                List<T> lista = new ArrayList<>();

                //Percorre todas as entidades uma a uma.
                for(File f : entidades){

                    //Lê o arquivo e converte para JSON.
                    BufferedReader r = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8);
                    String jsonOriginal = r.readLine();

                    //Remove a primeira e última chave do JSON
                    String json = jsonOriginal.substring(1, jsonOriginal.length()-1);

                    String[] dados;

                    // Verifica se o JSON tem mais de uma informação, se tiver, quebra
                    // o JSON em um array de informações.
                    if(json.contains(",")){
                        dados = json.split(",");
                    } else {
                        dados = new String[1];
                        dados[0] = json;
                    }

                    // mapa que receberá os dados do JSON.
                    Map<String, String> mapaJson = new HashMap<>();

                    //Quebra os dados em "chave" "valor" e os coloca no mapa.
                    for(String t : dados){
                        String[] temp = t.split(":");
                        mapaJson.put(temp[0].substring(1, temp[0].length()-1), temp[1]);
                    }

                    // boleano que verificará se o item passou no teste.
                    boolean ok = true;
                    boolean okmaster = false;

                    // Percorre cada verificação desejada.
                    for(String verificacaoAtual : filter.keySet()){
                        boolean ok2 = true;
                        //verifica se o item possui a chave de verificação desejada.
                        if(mapaJson.containsKey(verificacaoAtual)){
                            //Verifica se o valor a ser verificado é texto ou número.
                            if(mapaJson.get(verificacaoAtual).contains("\"")){
                                //verifica se o valor deve coincidir totalmente ou parcialmente.
                                if(convergence){
                                    if(!("\""+filter.get(verificacaoAtual)+"\"").equals(mapaJson.get(verificacaoAtual))){
                                        //Se os valores foram diferentes para a mesma chave, o item é descartado.
                                        if(allfilters){
                                            ok = false;
                                            break;
                                        } else {
                                            ok2 = false;
                                        }
                                    }
                                } else if(!mapaJson.get(verificacaoAtual).contains(filter.get(verificacaoAtual))){
                                    if(allfilters){
                                        ok = false;
                                        break;
                                    } else {
                                        ok2 = false;
                                    }
                                }
                            } else if(mapaJson.get(verificacaoAtual).contains(".")){ //verifica se é decimal.
                                //verifica se o valor deve coincidir totalmente ou parcialmente.
                                if(convergence){
                                    double a = Double.parseDouble(mapaJson.get(verificacaoAtual));
                                    double b = Double.parseDouble(filter.get(verificacaoAtual));
                                    if(a != b){
                                        //Se forem decimais diferentes para a mesma chave, o item é descartado.
                                        if(allfilters){
                                            ok = false;
                                            break;
                                        } else {
                                            ok2 = false;
                                        }
                                    }
                                } else if(!mapaJson.get(verificacaoAtual).contains(filter.get(verificacaoAtual))){
                                    if(allfilters){
                                        ok = false;
                                        break;
                                    } else {
                                        ok2 = false;
                                    }
                                }
                            } else { //Último caso, trata de um inteiro.
                                //verifica se o valor deve coincidir totalmente ou parcialmente.
                                if(convergence){
                                    long a = Long.parseLong(mapaJson.get(verificacaoAtual));
                                    long b = Long.parseLong(filter.get(verificacaoAtual));
                                    if(a != b){
                                        //Se o inteiro for diferente para a mesma chave, o item é descartado.
                                        if(allfilters){
                                            ok = false;
                                            break;
                                        } else {
                                            ok2 = false;
                                        }
                                    }
                                } else if(!mapaJson.get(verificacaoAtual).contains(filter.get(verificacaoAtual))){
                                    if(allfilters){
                                        ok = false;
                                        break;
                                    } else {
                                        ok2 = false;
                                    }
                                }
                            }

                        if(ok2){
                            if(!allfilters){
                                okmaster = true;
                                ok = false;
                            }
                        }

                        } else {
                            if(allfilters){
                                ok = false;
                                break;
                            }
                        }
                    }

                    //Verifica se o item passou no teste e o adiciona a lista.

                    //É para verificar levando em conta todos os filtros? Ou passando em somente um está ok?
                    if(allfilters){
                        //Está ok para ter passado em todos os filtros?
                        if(ok){
                            lista.add(gson.fromJson(jsonOriginal, classe));
                        }
                    } else { //Não é para verificar todos os filtros, passando em um está ok!
                        if(okmaster){ // Passou em pelo menos um filtro?
                            lista.add(gson.fromJson(jsonOriginal, classe));
                        }
                    }
                }
                return lista;
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * Busca entidades que contenham, em qualquer campo, um valor parcial.
     * @param <T> Tipo da entidade.
     * @param classe Classe da entidade a ser buscada.
     * @param findKey Valor a ser buscado em todos os campos da entidade.
     * @return Entidade que coincidiu com o valor buscado.
     * @throws IOException 
     */
    public static <T extends Entity> List<T> loadAll(Class<T> classe, String findKey) throws IOException{
        File pasta = new File(Config.root+"/"+classe.getName());
        if(pasta.exists()){
            File[] entidades = pasta.listFiles();
            List<T> lista = new ArrayList<>();
            for(File f : entidades){
                BufferedReader r = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8);
                String json = r.readLine();
                if(json.contains(findKey)){
                    lista.add((T) gson.fromJson(json, classe));
                }
            }
            return lista;
        }
        return new ArrayList<>();
    }
    
    /**
     * Carrega todas as entidades que passarem no teste do filtro informado como parâmetro.
     * @param <T> Tipo da entidade.
     * @param classe Classe da entidade a ser buscada.
     * @param filter Filtro a ser usado na validação.
     * @return Lista de entidades que passaram no teste.
     * @throws IOException 
     */
    public static <T extends Entity> List<T> loadAll(Class<T> classe, Filter filter) throws IOException{
        File pasta = new File(Config.root+"/"+classe.getName());
        if(pasta.exists()){
            File[] entidades = pasta.listFiles();
            List<T> lista = new ArrayList<>();
            for(File f : entidades){
                BufferedReader r = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8);
                String json = r.readLine();
                if(filter.match(json)){
                    lista.add((T) gson.fromJson(json, classe));
                }
            }
            return lista;
        }
        return new ArrayList<>();
    }
    
    /**
     * Deleta uma entidade.
     * @param entity Entidade a ser deletada.
     */
    public static void delete(Entity entity){
        File arquivo = new File(Config.root+"/"+entity.getClass().getName()+"/"+String.valueOf(entity.getId()));
        if(arquivo.exists()){
            arquivo.delete();
        }
    }
    
    /**
     * Deleta todos os registros de um determinado tipo de entidade.
     * @param entity 
     */
    public static void deleteAll(Entity entity){
        File arquivo = new File(Config.root+"/"+entity.getClass().getName());
        File[] ar = arquivo.listFiles();
        for(File a : ar){
            a.delete();
        }
    }
    
    /**
     * Deleta todas as entidades que passaram em um determinado filtro. CUIDADO!!!
     * @param <T> Tipo da entidade
     * @param classe Classe da entidade
     * @param filter Filtros a serem aplicados.
     * @param convergence O filtro deverá coincidir totalmente ou parcialmente? True para totalmente, false para parcialmente.
     * @param allfilters Pricesa passar em todos os filtros? True para sim, False para contar aqueles que passarem em pelo menos um filtro.
     * @throws IOException 
     */
    @Deprecated
    public static <T extends Entity> void deleteAllByFilter(Class<T> classe, Map<String, String> filter, boolean convergence, boolean allfilters) throws IOException{
        List<T> loadAll = loadAll(classe, filter, convergence, allfilters);
        for(T a : loadAll){
            delete(a);
        }
    }
    
    /**
     * Deleta todas as entidades que passaram em um determinado filtro. CUIDADO!!!
     * @param <T> Tipo da entidade
     * @param classe Classe da entidade
     * @param filter Filtro a ser usado na busca pelas entidades a ser deletadas.
     * @throws IOException 
     */
    public static <T extends Entity> void deleteAllByFilter(Class<T> classe, Filter filter) throws IOException{
        List<T> la = loadAll(classe, filter);
        for(T a : la){
            delete(a);
        }
    }
    
}
