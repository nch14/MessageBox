package com.chenh.messagebox.util.ListView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chenh.messagebox.Item;

import java.util.List;

/**
 * Created by linhonghong on 2015/11/13.
 */
public class PtrrvBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected LayoutInflater mInflater;
    protected Context mContext = null;
    protected List<Item> mData;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_HISVIDEO = 1;
    public static final int TYPE_MESSAGE = 2;

    public PtrrvBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public PtrrvBaseAdapter(Context context,List<Item> data) {
        mContext = context;
        mData=data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Object getItem(int position){
        return mData.get(position);
    }


}
