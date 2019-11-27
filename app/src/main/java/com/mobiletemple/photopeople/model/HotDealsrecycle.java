package com.mobiletemple.photopeople.model;

import java.util.List;

/**
 * Created by Ishan Puranik on 23/04/2018.
 */

public class HotDealsrecycle {

    private int id;
    List<Hotdeals> hotdeals;
    public HotDealsrecycle() {
    }
    public HotDealsrecycle(int id,List<Hotdeals> hotdeals)
    {
        this.id = id;
        this.hotdeals=hotdeals;
    }

    public List<Hotdeals> getMedia() {
        return hotdeals;
    }

    public void setMedia(List<Hotdeals> media) {
        this.hotdeals = media;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
