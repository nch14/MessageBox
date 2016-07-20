package com.chenh.messagebox;

import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chenh on 2016/7/17.
 */
public class LocalItem {
    public ArrayList<Item> items;

    public Item endItem;

    public long weiboIdEnd;

    public long weiboIdStart;


    private static LocalItem localItem;
    public static LocalItem getLocalItem(){
        if (localItem==null){
            localItem=new LocalItem();
        }
        return localItem;
    }

    private LocalItem(){
        items=new ArrayList<>();
        endItem=new Item();
        endItem.where=Item.TIP;
        //items.add(endItem);
    }

    public void addItem(Item item){
        if(items.contains(item))
            return;
        else
            items.add(item);
    }

    public void addItem(ArrayList<Status> statuses){
        //items.remove(endItem);

        if (statuses==null)
            return;

        long groupStart=Long.parseLong(statuses.get(statuses.size()-1).mid);
        long groupEnd=Long.parseLong(statuses.get(0).mid);

        if (weiboIdEnd ==0){
            for(int i=0;i<statuses.size();i++){
                items.add(new Item(statuses.get(i)));
            }
            weiboIdStart =groupStart-1;
            weiboIdEnd =groupEnd;
        }else {
            if (groupEnd> weiboIdEnd){
                weiboIdEnd =groupEnd;
                for(int i=statuses.size();i>0;i--){
                    items.add(0,new Item(statuses.get(i-1)));
                }
            }else if (groupStart< weiboIdStart){
                weiboIdStart =groupStart-1;
                for(int i=0;i<statuses.size();i++){
                    items.add(new Item(statuses.get(i)));
                }
            }
        }
        sort();
        //items.add(endItem);
    }

    public void sort(){
        Collections.sort(items,comparator);
    }

    Comparator<Item> comparator = new Comparator<Item>(){
        @Override
        public int compare(Item lhs, Item rhs) {
            if (lhs.rawDate.getTime()!=rhs.rawDate.getTime())
                return (int)(rhs.rawDate.getTime()-lhs.rawDate.getTime());
            else
                return rhs.where-lhs.where;
        }
    };
}
