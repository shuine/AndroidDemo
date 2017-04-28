package com.shine.demo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shine.demo.dynamic.DynamicActivity;
import com.shine.demo.listact.ListAct;
import com.shine.demo.scratch.ScrathActivity;
import com.shine.demo.searchcity.ChooseCityAct;
import com.shine.demo.statusbar.StatusBarAct;
import com.shine.demo.viewpager.SampleActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.main_listview);
        initListView();
        Log.e("shine","time:"+getFlashTimeTitleList(1491702152000l,1491615752000l));
        Log.e("shine","time:"+getFlashTimeTitleList(1493516552000l,1493602952000l));
        Log.e("shine","time:"+getFlashTimeTitleList(1493516552000l,1493775752000l));
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
        params.add(new DData("加载",DynamicActivity.class));
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

    public String getFlashTimeTitleList(long current, long data) {

        if (current < 1) {
            current = System.currentTimeMillis();
        }
        Date nowDate = new Date(current);
        Calendar calendar = Calendar.getInstance();
        String time;
        Date date = new Date(data);
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours < 10) {
            time = "0" + hours;
        } else {
            time = String.valueOf(hours);
        }
        int min = calendar.get(Calendar.MINUTE);
        if (min < 10) {
            time += ":0" + min;
        } else {
            time += ":" + min;
        }

        Calendar nowCalendar = new GregorianCalendar();
        nowCalendar.setTime(nowDate);
        nowCalendar.add(Calendar.DAY_OF_MONTH,1);

        if (date.getDate() > nowDate.getDate() || date.getMonth() > nowDate.getMonth()) {

            //判断条件 月份相等,日期比现在时间大1
            if ((date.getMonth() == nowDate.getMonth() && date.getDate() == (nowDate.getDate() + 1))
                    ||((nowCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
                    && nowCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))) {
                time = "明日 " + time;
            } else {
                time = date.getDate() + "日" + time;
            }
        }

        return time;
    }
}
