package com.shine.demo.dynamic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shine.demo.R;
import com.shine.demo.scratch.ScrathActivity;

import java.io.File;
import java.util.ArrayList;

import tomato.pluginlib.LoaderUtils;
import tomato.pluginlib.PluginIntent;
import tomato.pluginlib.PluginManager;
import tomato.pluginlib.ProxyActivity;

import static tomato.pluginlib.LoaderUtils.pluginFolder;

/**
 * @author yeshuxin on 17-3-31.
 */

public class DynamicActivity extends Activity {

    private ListView mListView;
    private ArrayList<PluginItem> mPluginItems = new ArrayList<>();
    private PluginAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_activity);
        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.dynamic_listview);
    }

    private void initData() {

        ArrayList<PluginItem> list = loadApkInfo();
        if (list != null && list.size() > 0) {
            mPluginItems.addAll(list);
        }
        mAdapter = new PluginAdapter(this);
        mAdapter.setData(mPluginItems);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PluginItem item = mPluginItems.get(position);
                PluginManager pluginManager = PluginManager.getInstance(DynamicActivity.this);
                PluginIntent intent = new PluginIntent(item.packageInfo.packageName,item.launcherActivityName);
                pluginManager.startPluginActivity(DynamicActivity.this,intent);
            }
        });
    }

    private ArrayList<PluginItem> loadApkInfo() {
        File direct = new File(pluginFolder);
        ArrayList<PluginItem> arrayList = new ArrayList<>();
        PackageManager pm = this.getPackageManager();
        if (direct.exists()) {
            File[] plugins = direct.listFiles();
            if (plugins != null && plugins.length > 0) {
                for (File file : plugins) {
                    PluginItem item = new PluginItem();
                    item.path = file.getPath();
                    item.packageInfo = LoaderUtils.getPackageInfo(this, item.path);
                    if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
                        item.launcherActivityName = item.packageInfo.activities[0].name;
                    }
                    item.mAppIcon = pm.getApplicationIcon(item.packageInfo.applicationInfo);
                    arrayList.add(item);
                    PluginManager.getInstance(this).loadApp(item.path);
                }
            }
        }
        return arrayList;
    }
}
