package com.shine.demo.viewpager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.shine.demo.R;
import com.shine.demo.viewpager.smartTabLayout.SmartTabLayout;

import java.util.ArrayList;

public class SampleActivity extends FragmentActivity {
//    private ListView mListView;
    private DirectionalViewPager mViewPager;
    private SmartTabLayout mTabLayout;

    private ArrayAdapter<String> mListAdapter;
    ListAdapter adapter;
    private FragmentAdapter mPagerAdapter;
    private String[] mTitles = new String[]{"first","second","third","fourth","fifth"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_list);


        //Set up the pager
        mViewPager = (DirectionalViewPager)findViewById(R.id.pager);
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        mTabLayout = (SmartTabLayout) findViewById(R.id.smart_tab);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOrientation(DirectionalViewPager.VERTICAL);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("first");titles.add("second");titles.add("third");
        titles.add("fourth");
        //Bind to control buttons
      /*  ((Button)findViewById(R.id.horizontal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbar.setTitle("HORIZONTAL");
                pager.setOrientation(DirectionalViewPager.HORIZONTAL);
            }
        });
        ((Button)findViewById(R.id.vertical)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbar.setTitle("VERTICAL");
                pager.setOrientation(DirectionalViewPager.VERTICAL);
            }
        });*/
//        mListView = (ListView)this.findViewById(R.id.list);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,mTitles);
        adapter = new ListAdapter(this);
        adapter.setData(titles);
//        mListView.setAdapter(adapter);
       /* mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager.setCurrentItem(position);
            }
        });*/
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                mListView.setSelection(position);
                adapter.setSelectedPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
