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
package jhonDES.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhonDES.webserver.errors.NullHTML;
import jhonDES.webserver.template.html.HTMLManager;
import jhonDES.webserver.template.html.EntitiesFiller;
import jhonDES.webserver.template.preparers.FormPrepare;
import jhonDES.webserver.template.tools.ParameterSpliter;

/**
 * Recebe uma solicitação de um navegador, analisa e retorna o resultado.
 * @author jhonesconrado
 */
public class WebCommunication extends Thread{

    private final String CRLF = "\n\r";
    
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final BufferedReader br;
    
    private String typeRequest;
    private String path;
    private String referer;
    private String[] parameters;
    
    private String header;
    private String body;
    
    private String retHeader;
    private String retHtml;
    
    /**
     * Comunicação entre o servidor e o navegador, responsável por ler a requisição
     * e devolver um resultado.
     * @param socket
     * @throws IOException 
     */
    public WebCommunication(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
//        this.parameters = "";
        
        InputStreamReader isReader = new InputStreamReader(in);
        this.br = new BufferedReader(isReader);
    }
    
    /**
     * Lê as informações básicas da requisição, como o cabeçalho.
     * @return Cabeçalho e corpo da requisição.
     * @throws IOException 
     */
    private String readHeader() throws IOException{
        String temp = null;
        
        //Lê o header da soliticação http.
        StringBuilder header = new StringBuilder();
        while(!(temp = br.readLine()).isEmpty()){
            header.append(temp+"\n");
        }
        
        //Extrai as informações básicas do header, como tipo de solicitação e caminho.
        String[] basics = header.toString().split(" ");
        typeRequest = basics[0];
        path = basics[1];
        this.header = header.toString();
        if(getHeaderFieldValue("Referer") != null){
            referer = getHeaderFieldValue("Referer").substring(
            getHeaderFieldValue("Referer").indexOf("/", 
                    getHeaderFieldValue("Referer").indexOf("/", getHeaderFieldValue("Referer").indexOf("/")+1)+1)
            );
        }
        return header.toString();
    }
    
    /**
     * Lê o corpo da requisição, se houver.
     * @return
     * @throws IOException 
     */
    private String readBody() throws IOException{
        //Carrega o corpo da solicitação, se tiver algum.
        StringBuilder body = new StringBuilder();
        if(header.toString().contains("Content-Length")){
            while(br.ready()){
                body.append((char) br.read());
            }
        }
        this.body = body.toString();
        return body.toString();
    }
    
    /**
     * Lê os parâmetros inseridos no link.
     * @throws IOException 
     */
    private void readParameters() throws IOException{
        this.parameters = new ParameterSpliter(path).getAsArray();
    }
    
    /**
     * Recebe o nome do campo do cabeçalho da solicitação HTTP e retorna o valor
     * deste campo.\n
     * Por exemplo, Content-Length: 500\n
     * Posso pedir o valor de Content-Length e o método retornará 500.
     * @param field HTTP header field.
     * @return HTTP header field value.
     */
    private String getHeaderFieldValue(String field){
        if(header.contains(field)){
            int init = header.indexOf(field)+field.length();
            if(field.endsWith(":")){
                init++;
            }
            
            int fim = header.indexOf("\n", init);
            String value = header.substring(init, fim);
            if(value.startsWith(" ")){
                value = value.substring(1);
            }
            return value;
        }
        return null;
    }
    
    /**
     * Chamado caso seja uma requisição GET.
     * @throws IOException 
     */
    private void getMethod() throws IOException, NullHTML{
        setResponseHTML();
        setResponseHeader();
        sendResponse();
    }
    
    /**
     * Chamado caso seja uma requisição POST.
     * @throws IOException 
     */
    private void postMethod() throws IOException, NullHTML{
        setResponseHTML();
        setResponseHeader();
        
        try {
            Class main = HTMLManager.getMainEntity(referer);
            System.out.println(main.getName());
        } catch (IOException iOException) {
            System.out.println("Erro ao buscar referência no método post.");
            System.out.println(iOException);
        }
        
        sendResponse();
    }
    
    private void deleteMethod(){
        
    }
    
    private void putMethod(){
        
    }
    
    private void unsuportedMethod(){}
    
    private void errorMethod(String msg) throws IOException{
        String base = 
                "<!DOCTYPE html>" + 
                "<html lang=\"pt-br\" dir=\"ltr\">\n" +
                "   <head>"
                + "<meta charset=\"utf-8\">\n" +
                "    <title>Erro de servidor</title>"
                + "</head>\n" +
                "   <body>\n" +
                "      <h1>Erro de servidor</h1>" +
                "      <p> replace </p>\n" +
                "   </body>\n" +
                "</html>";
        base = base.replace("replace", msg);
        retHtml = base;
        setResponseHeader();
        sendResponse();
    }
    
    /**
     * Prepara o header da resposta.
     */
    private void setResponseHeader(){
        retHeader = 
                "HTTP/1.1 200 OK" + CRLF +
                "Content-Language: pt-BR" + CRLF +
                "Content-Length: " + retHtml.getBytes().length + CRLF +
                "Content-Type: text/html;charset=UTF-8" + CRLF +
                "Date: " + new Date().toString() + CRLF +
                "Keep-Alive: timeout=60" + CRLF +
                CRLF;
    }
    
    /**
     * Prepara o HTML da resposta.
     * @throws IOException 
     */
    private void setResponseHTML() throws IOException, NullHTML{
        EntitiesFiller ef = new EntitiesFiller();
        String html = HTMLManager.getHTML(new ParameterSpliter(path).getLinkRoot());
        
        //Caso seja um formulário e existir o parâmetro id, então tentará preencher.
        if(HTMLManager.isAForm(html)){
            FormPrepare fp = new FormPrepare(path);
            String ht = fp.getPrepared();
            if(ht != null){
                html = ht;
            }
        } 
        retHtml = ef.fillEntities(html);
    }
    
    /**
     * Envia a resposta de volta para o cliente.
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    private void sendResponse() throws UnsupportedEncodingException, IOException{
        String saida = retHeader + retHtml + CRLF + CRLF;
        out.write(saida.getBytes("UTF-8"));
        out.flush();
    }
    
    /**
     * Lê a requisição do navegador, prepara e retorna a resposta.
     */
    @Override
    public void run() {
        
        if(socket != null){
            try {
                readHeader();
                readBody();
                readParameters();
                
                if(typeRequest.equals("GET")){
                    getMethod();
                } else if(typeRequest.equals("POST")){
                    postMethod();
                } else if(typeRequest.equals("PUT")){
                    putMethod();
                } else if(typeRequest.equals("DELETE")){
                    deleteMethod();
                } else {
                    unsuportedMethod();
                }
            } catch (IOException | NullHTML ex) {
                Logger.getLogger(WebCommunication.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    errorMethod("Erro ao tentar gerar a resposta</br></br>"+ex);
                } catch (IOException ex1) {
                    Logger.getLogger(WebCommunication.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(WebCommunication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
}
