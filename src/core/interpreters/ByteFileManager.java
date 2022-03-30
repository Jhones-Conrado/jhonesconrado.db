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
package core.interpreters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Um simples gestor que envia e recebe arrays de bytes para a troca de arquivos.
 * A simple manager that sends and receives arrays of bytes to exchange files.
 * @author jhonesconrado
 */
public class ByteFileManager {
    
    private InputStream in;
    private OutputStream out;

    public ByteFileManager(Socket socket) throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }
    
    /**
     * Lê o próximo array de bytes recebido.
     * Reads the next byte array received.
     * @return Byte array received.
     * @throws IOException 
     */
    public byte[] read(long length) throws IOException{
        InterpreterMemory.canClose.addAndGet(1);
        DataInputStream ins = new DataInputStream(new BufferedInputStream(in));
        
        //Reads all bytes and put it on a list.
        List<Byte> bytes = new ArrayList<>();
        byte b = -1;
        for(long i = 0 ; i < length ; i++){
            bytes.add((byte)ins.read());
        }
        
        //Convert a list<byte> to a byte array.
        byte[] received = new byte[bytes.size()];
        for(int i = 0 ; i < bytes.size() ; i++){
            received[i] = bytes.get(i);
        }
        System.out.println("Received: "+received.length);
        InterpreterMemory.canClose.addAndGet(-1);
        return received;
    }
    
    /**
     * Envia um array de bytes.
     * Sends a byte array.
     * @param bytes Byte array to be send.
     * @return the same array of bytes sent.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public byte[] send(byte[] bytes) throws FileNotFoundException, IOException {
        InterpreterMemory.canClose.addAndGet(1);
        System.out.println("Sendind: "+bytes.length);
        DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(this.out));
        fileOut.write(bytes);
        fileOut.write((byte)-128);
        fileOut.flush();
        InterpreterMemory.canClose.addAndGet(-1);
        return bytes;
    }
    
    /**
     * Envia um arquivo.
     * Sends a file.
     * @param file File to be sent.
     * @return File as byte array.
     * @throws IOException 
     */
    public byte[] send(File file) throws IOException{
        if(file.exists()){
            FileInputStream fileIn = new FileInputStream(file);
            return send(fileIn.readAllBytes());
        }
        return null;
    }
    
    /**
     * Envia um arquivo a partir do seu path.
     * Sends a file from path.
     * @param filePath
     * @return
     * @throws IOException 
     */
    public byte[] send(String filePath) throws IOException{
        File file = new File(filePath);
        return send(file);
    }
    
}
