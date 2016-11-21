package com.shine.demo.statusbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shine.demo.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * @author yeshuxin on 16-11-21.
 */

public class StatusBarAct extends AppCompatActivity {

    private StatusBar mStatusBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_bar_act);
        initView();
        loadData();
    }

    private void initView(){
        //添加状态自定义View
        FrameLayout statusLayout = (FrameLayout) findViewById(R.id.service_detail_status_layout);
        mStatusBar = new StatusBar(this);
        FrameLayout.LayoutParams statuParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        statusLayout.addView(mStatusBar,statuParams);
    }

    private void loadData(){  List<String> titles = new ArrayList<String>();
        List<StatusData> data = StatusData.getDemoData(4);
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            StatusData status = data.get(i);
            titles.add(status.desc);
            if (status.isValid()) {
                index = i + 1;
            }
        }
        mStatusBar.setStatusTitle(titles);
        mStatusBar.setProgress(index);

    }
}
