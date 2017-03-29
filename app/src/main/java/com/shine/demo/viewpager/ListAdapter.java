package com.shine.demo.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.shine.demo.R;

import java.util.ArrayList;

/**
 * @author yeshuxin on 17-2-28.
 */

public class ListAdapter extends BaseAdapter {

    private ArrayList<String> mData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    private int mPosition = 0;

    public ListAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setSelectedPosition(int position){
        mPosition = position;
        notifyDataSetChanged();
    }
    public void setData(ArrayList<String> data){
        if(data != null && data.size() > 0){
            mData.clear();
            mData.addAll(data);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item,null);
            holder = new AdapterHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (AdapterHolder) convertView.getTag();
        }
        holder.mText.setText(mData.get(position));

        if(position == mPosition){
            convertView.setSelected(true);
        }else {
            convertView.setSelected(false);
        }

        return convertView;
    }

    public class AdapterHolder{

        public View holderView;
        public TextView mText;
        public AdapterHolder(View view){
            holderView = view;
            mText = (TextView) holderView.findViewById(R.id.viewpager_list_item_tv);
        }
    }


}
