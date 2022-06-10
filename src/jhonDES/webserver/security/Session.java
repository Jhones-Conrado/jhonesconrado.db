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
package jhonDES.webserver.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Chave de sessão para o servidor.
 * @author jhonesconrado
 */
public class Session {
    
    public final String session;
    public final long init;

    public Session(String ip) throws NoSuchAlgorithmException {
        Date now = new Date();
        byte[] digest = (ip+now.toString()).getBytes();
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-1");
        this.session = new String(md.digest(digest));
        this.init = System.nanoTime();
    }
    
}
