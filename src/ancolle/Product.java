/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancolle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author samuel
 */
public class Product {
    
    final int id;
    private final List<Album> albums;
    
    public Product(int id, Collection<Album> albums) {
        this.id = id;
        this.albums = new ArrayList<>(albums);
    }
    
    public List<Album> albums() {
        return Collections.unmodifiableList(albums);
    }
}
