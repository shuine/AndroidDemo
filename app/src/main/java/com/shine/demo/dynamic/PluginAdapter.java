package com.shine.demo.dynamic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shine.demo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * @author yeshuxin on 17-3-31.
 */

public class PluginAdapter extends BaseAdapter {

    private ArrayList<PluginItem> mData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public PluginAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(ArrayList<PluginItem> data) {
        if (data == null && data.size() < 1) {
            mData.clear();
        } else {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PluginItem item = mData.get(position);
        holder.setData(item);

        return convertView;
    }

    public static class ViewHolder {
        public TextView mTvTitle;
        public TextView mTvDesc;
        public ImageView mIvIcon;

        public ViewHolder(View view) {
            if (view != null) {
                mTvTitle = (TextView) view.findViewById(R.id.dynamic_item_name_tv);
                mTvDesc = (TextView) view.findViewById(R.id.dynamic_item_desc_tv);
                mIvIcon = (ImageView) view.findViewById(R.id.dynamic_item_icon_iv);
            }
        }

        public void setData(PluginItem data) {
            if (data != null) {
                if (mTvTitle != null) {
                    mTvTitle.setText(data.path.substring(data.path.lastIndexOf(File.separatorChar) + 1));
                }
                if (mIvIcon != null) {
                    mIvIcon.setBackground(data.mAppIcon);
                }

                if (mTvDesc != null) {
                    mTvDesc.setText(data.packageInfo.packageName + "\n" + data.launcherActivityName);
                }
            }
        }
    }
}
