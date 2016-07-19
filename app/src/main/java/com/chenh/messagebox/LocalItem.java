package com.chenh.messagebox;

import java.util.ArrayList;

/**
 * Created by chenh on 2016/7/17.
 */
public class LocalItem {
    public static ArrayList<Item> items=new ArrayList<>();





    public static void addItem(Item item){

        if(items.contains(item))
            return;
        else
            items.add(item);
    }

}
