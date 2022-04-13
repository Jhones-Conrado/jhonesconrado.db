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
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhonDES.template.populate.HTMLManager;
import jhonDES.template.populate.EntitiesFiller;

/**
 *
 * @author jhonesconrado
 */
public class WebCommunication extends Thread{

    private final String CRLF = "\n\r";
    
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    
    private String typeRequest;
    private String path;
    private int Content_Length;
    
    public WebCommunication(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }
    
    private String readHeader() throws IOException{
//        Scanner s = new Scanner(in);
        
        InputStreamReader isReader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isReader);
        
        
//        StringBuilder sb = new StringBuilder();
        String temp = null;
//        int i = 0;
        
        StringBuilder header = new StringBuilder();
        while((temp = br.readLine()).length() != 0){
            header.append(temp+"\n");
        }
        
        String[] basics = header.toString().split(" ");
        typeRequest = basics[0];
        path = basics[1];
        
        System.out.println(header.toString());
        
        StringBuilder corpo = new StringBuilder();
        if(typeRequest.equals("POST")){
            while(br.ready()){
                corpo.append((char) br.read());
            }
            System.out.println(corpo.toString());
        }

        
        
//        while(true){
//            try {
//                temp = s.nextLine();
//            } catch (Exception e) {
//            }
//            if(i == 0){
//                String[] basics = temp.split(" ");
//                typeRequest = basics[0];
//                path = basics[1];
//                i = 1;
//            }
//            sb.append(temp+"\n");
//            
//            if(temp.startsWith("Content-Length: ")){
//                Content_Length = Integer.valueOf(temp.substring("Content-Length: ".length()));
//            }
//            if(temp.isEmpty()){
//                break;
//            }
//            
//        }
//        try {
//            System.out.println("AQUI AAAAAAAAAAAAAAAAAAAAAAAAAA");
//            byte[] bytes = in.readNBytes(Content_Length);
//            sb.append(new String(bytes));
//        } catch (IOException ex) {
//            Logger.getLogger(WebCommunication.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println(sb.toString());
        
        return header.toString()+"\n"+corpo.toString();
    }
    
    @Override
    public void run() {
        
        if(socket != null){
            try {
                readHeader();
                
                //Direciona requisições na raiz do domínio para o arquivo index.
                if(path.equals("/")){
                    path = "index";
                }
                
                EntitiesFiller ef = new EntitiesFiller();
                String html = ef.fillEntities(HTMLManager.getHTML(path));
                
                String saida = 
                        "HTTP/1.1 200 OK" + CRLF +
                        "Content-Language: pt-BR" + CRLF +
                        "Content-Length: " + html.getBytes().length + CRLF +
                        "Content-Type: text/html;charset=UTF-8" + CRLF +
                        "Date: " + new Date().toString() + CRLF +
                        "Keep-Alive: timeout=60" + CRLF +
                        CRLF +
                        html +
                        CRLF + CRLF;
                out.write(saida.getBytes("UTF-8"));
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(WebCommunication.class.getName()).log(Level.SEVERE, null, ex);
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
