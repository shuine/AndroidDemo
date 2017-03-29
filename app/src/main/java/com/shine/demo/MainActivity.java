package com.shine.demo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shine.demo.listact.ListAct;
import com.shine.demo.scratch.ScrathActivity;
import com.shine.demo.searchcity.ChooseCityAct;
import com.shine.demo.statusbar.StatusBarAct;
import com.shine.demo.viewpager.SampleActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.main_listview);
        initListView();
    }


    private void initListView() {

        BaseListAdapter adapter = new BaseListAdapter(this);
        ArrayAdapter arrayAdapter;
        mListView.setAdapter(adapter);

    }

    private List<DData> getData() {
        List<DData> params = new ArrayList<>();
        params.add(new DData("城市选择",ChooseCityAct.class));
        params.add(new DData("状态",StatusBarAct.class));
        params.add(new DData("列表",ListAct.class));
        params.add(new DData("分类",SampleActivity.class));
        params.add(new DData("画画",ScrathActivity.class));
        return params;

    }

    private class DData {
        public String title;
        public Class target;

        public DData(String str, Class cl) {

            title = str;
            target = cl;
        }

    }

    private class BaseListAdapter extends BaseAdapter{

        private Context mContext;
        private LayoutInflater inflater;
        private List<DData> mData = new ArrayList<>();

        public BaseListAdapter(Context mContext) {
            this.mContext = mContext;
            inflater = LayoutInflater.from(mContext);
            mData = getData();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.layout_listview_item, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(mData.get(position).title);
            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext,mData.get(position).target);
                    mContext.startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView title;
        }

    }
}
