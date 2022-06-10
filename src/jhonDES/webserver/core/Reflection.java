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
package jhonDES.webserver.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import jhonDES.db.interfaces.Entity;

/**
 * Uma simples classe que realizada a busca por todas as classes do projeto,
 * facilitando a reflexão.
 * @author jhonesconrado
 */
public final class Reflection {
    
    
    private static Reflection instance;
    private final List<String> classes = new ArrayList<>();
    
    public static Reflection getInstance() throws IOException{
        if(instance == null){
            instance = new Reflection();
        }
        return instance;
    }

    private Reflection() throws IOException {
        loadClasses();
    }
    
    /**
     * Carrega todas as classes do projeto.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void loadClasses() throws FileNotFoundException, IOException{
        File raiz = new File("./");
        File jar = null;
        List<File> pastas = new ArrayList<>();
        
        // Tenta localizar algum arquivo jar na raiz de onde o servidor foi executado.
        File[] list = raiz.listFiles();
        for(File x : list){
            if(x.getName().endsWith(".jar")){
                jar = new File(x.getPath());
            }
        }
        
        if(jar != null){// verifica se foi encontrado o jar do servidor.
            
            //Abre o jar como um arquivo zip.
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(jar));
            ZipInputStream is = new ZipInputStream(bis);
            
            //Varre todos os arquivos dentro do jar localizando os que são classes.
            ZipEntry entry;
            while((entry = is.getNextEntry()) != null){
                if(entry.getName().endsWith(".class")){
                    classes.add(entry.getName());
                }
            }
            
            /**
             * Caso não ache nenhum arquivo jar, procura uma pasta "build", indicando
             * que se trata de um projeto sendo executado ainda na IDE.
             */
        } else {
            raiz = new File("./build");
            pastas.add(raiz);
            while(!pastas.isEmpty()){ //Enquanto houver subpastas elas serão lidas.
                List<File> nextGeneration = new ArrayList<>(); //Próxima geração de pastas.
                for(File f : pastas){
                    File[] lis = f.listFiles();
                    for(File x : lis){
                        if(x.isDirectory()){// se for uma pasta, é mandada para a próxima geração.
                            nextGeneration.add(x);
                        } else if(x.getName().endsWith(".class")){ // se for uma classe, é colocado no array de classes.
                            classes.add(x.getPath().substring(2+"build/classes/".length()));
                        }
                    }
                }
                pastas.clear();//limpa as pastas da geração atual para receber as da próxima.
                for (File t : nextGeneration) {
                    pastas.add(t);//adiciona as pastas da próxima geração no array.
                }
                nextGeneration.clear();
            }
        }
    }
    
    /**
     * Procura todas as classes que implementam entity.
     * @return Lista de entidades no projeto.
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public List<Class> getEntities() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        List<Class> ent = new ArrayList<>();
        for(String t : classes){
            Object ins = Class.forName(t.replaceAll("/", ".").replaceAll(".class", "")).newInstance();
            if(ins instanceof Entity){
                ent.add(ins.getClass());
            }
        }
        return ent;
    }
    
    /**
     * Filtra todas as classes que implementam uma interface ou extendem outra classe determinada.
     * @param classe Interface ou classe de filtro.
     * @return Listra de classes que implementam ou extendem.
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public List<Class> filterClass(Class classe) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        List<Class> ret = new ArrayList<>();
        for(String t : classes){
            Object ins = Class.forName(t.replaceAll("/", ".").replaceAll(".class", "")).newInstance();
            if(ins.getClass().isInstance(classe.newInstance())){
                ret.add(ins.getClass());
            }
        }
        return ret;
    }
    
    public List<String> getClasses(){
        return classes;
    }
    
}
