package com.shine.demo.listact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.shine.demo.R;
import java.util.ArrayList;

/**
 * @author yeshuxin on 16-12-6.
 */

public class ListAct extends Activity {

    private ListView mListView;
    private Button mBtnChange;
    private  ArrayAdapter<String> adapter;
    private String[] str_name = new String[] { "jack", "debb", "robin", "kikt",
            "dog", "cat", "elep" };
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
    }

    private void initView(){
        mListView = (ListView)findViewById(R.id.act_list);
        mBtnChange = (Button) findViewById(R.id.act_btn_change);

        changeAdapter(50);
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position +=10;
                mListView.smoothScrollToPosition(position);
//                mListView.smoothScrollBy(10000,2000);

            }
        });
    }

    private void changeAdapter(int size){
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0;i<size;i++){
            list.add("hello:"+i);
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        mListView.setAdapter(adapter);
    }
}
