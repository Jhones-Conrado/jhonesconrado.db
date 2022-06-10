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
package jhonDES.webserver.crud;

import java.io.IOException;
import jhonDES.webserver.template.html.HTMLManager;

/**
 *
 * @author jhonesconrado
 */
public class Create {
    
    private String referer;
    private String path;

    public Create(String referer, String path) {
        this.referer = referer;
        this.path = path;
    }
    
    public void create() throws IOException{
        referer = referer.substring(referer.indexOf("/", 7));
        String html = HTMLManager.getHTML(referer);
        int oc = countOccurrencies(html, "<form");
        int ent = countOccurrencies(html, "referer=\"");
    }
    
    /**
     * Conta quantas vezes um texto se repete em uma string.
     * @param str
     * @param key
     * @return 
     */
    private int countOccurrencies(String str, String key){
        int count = 0;
        while(str.contains(key)){
            count++;
            str = str.substring(str.indexOf(key)+key.length());
        }
        return count;
    }
    
}
