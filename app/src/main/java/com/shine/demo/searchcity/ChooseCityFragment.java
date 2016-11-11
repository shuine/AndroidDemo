package com.shine.demo.searchcity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.shine.demo.R;
import com.shine.demo.adapter.BaseDataAdapter;
import com.shine.demo.view.IndexBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChooseCityFragment extends Activity {

    public static int REQUEST_CODE_SEARCH = 1;
    private String gpsCity = "定位城市";
    private String[] hotCities;
    private TextView mGpsCityView;
    private ListView mCityListView;
    private IndexBar mIndexbar;
    private TextView mDialog;

    private CityListAdapter mCityListAdapter;

    //search相关
    private AutoCompleteTextView mInput;
    private ListView mSearchResultList;
    private BaseDataAdapter mSearchResultAdapter;
    private ArrayList<SpannableString> mResultList;
    private HashMap<String, ArrayList<String>> searchMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotCities = CityUtils.getHotCities();
        setContentView(R.layout.choose_allcities_fragment);
        initView();
    }

    protected void initView() {

        View headerView = LayoutInflater.from(this).inflate(R.layout.choose_city_list_header, null);
        setUpHeaderView(headerView);
        mCityListView = (ListView) findViewById(R.id.choosecity_list);
        mCityListView.addHeaderView(headerView);
        mCityListAdapter = new CityListAdapter(this);
        mCityListView.setAdapter(mCityListAdapter);
        mCityListAdapter.updateData(CityUtils.getAllCityItems());

        FrameLayout indexViewParent = (FrameLayout) findViewById(R.id.choosecity_sidebar);
        mIndexbar = new IndexBar(this);
        indexViewParent.addView(mIndexbar);
        mDialog = (TextView) findViewById(R.id.choosecity_dialog);
        mIndexbar.setTextDialog(mDialog);
        mIndexbar.setOnIndexChangedListener(new IndexBar.OnIndexChangedListener() {
            @Override
            public void onIndexChanged(int position, String item) {
                int pos = mCityListAdapter.getPositionForSection(item.charAt(0));
                mCityListView.setSelection(pos + mCityListView.getHeaderViewsCount());
            }
        });
        createSearch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void createSearch() {
        mInput = (AutoCompleteTextView) findViewById(R.id.choosecity_search_input);
        mInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString().trim();
                search(word);
            }
        });
        mSearchResultList = (ListView) findViewById(R.id.search_fragment_result_list);
        mSearchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setChooseCity(mResultList.get(position).toString());
            }
        });
        mSearchResultAdapter = new BaseDataAdapter<SpannableString>(this) {
            @Override
            public void bindView(View view, int position, SpannableString data) {
                TextView provinceTextView = (TextView) view.findViewById(R.id.choosecity_searchresult_name);
                provinceTextView.setText(data);
            }

            @Override
            public View newView(Context context, SpannableString data, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.choose_city_searchresult_listitem, null);
            }
        };
        mSearchResultList.setAdapter(mSearchResultAdapter);
        mResultList = new ArrayList<SpannableString>();
    }

    private void setUpHeaderView(View view) {
        mGpsCityView = (TextView) view.findViewById(R.id.choosecity_listheader_gpscity_txt);
        mGpsCityView.setText(TextUtils.isEmpty(gpsCity) ? "定位失败" : gpsCity);
        mGpsCityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(gpsCity)) {
                    setChooseCity(gpsCity);
                }
            }
        });

    }

    private void setChooseCity(String city) {
        Intent intent = new Intent();
        intent.putExtra("province", city);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("province");
                setChooseCity(result);
            }
        }
    }

    public class CityListAdapter extends BaseDataAdapter<CityUtils.CityItem> implements
            SectionIndexer {

        private Context mContext;

        public CityListAdapter(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public View newView(Context context, CityUtils.CityItem data, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(
                    R.layout.choose_city_list_item, parent,
                    false);
        }

        @Override
        public void bindView(View view, int position, final CityUtils.CityItem data) {
            TextView name = (TextView) view
                    .findViewById(R.id.choosecity_listitem_name);
            // 字母
            if (data.index == CityUtils.TYPE_DIVIDER) {
                //    name.setBackgroundResource(R.color.bg_main);
                name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120));
                //      name.setTextColor(mContext.getResources().getColor(R.color.t_grey_desc));
            }
            // 城市
            else {
                name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120));
                //    name.setBackgroundResource(R.drawable.bg_city_list_item);
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setChooseCity(data.text);
                    }
                });
                //      name.setTextColor(mContext.getResources().getColor(R.color.t_grey_title));
            }
            name.setText(data.text);

            View dividerView = view.findViewById(R.id.choosecity_listitem_divider);
            boolean needDivider = true;
            // 如果下一个item是首字母索引则不显示divider
            if (position < getCount() - 1) {
                CityUtils.CityItem nextItem = (CityUtils.CityItem) getItem(position + 1);
                if (nextItem.index == CityUtils.TYPE_DIVIDER) {
                    needDivider = false;
                }

            }
            if (needDivider) {
                dividerView.setVisibility(View.VISIBLE);
            } else {
                dividerView.setVisibility(View.GONE);
            }
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = mData.get(i).sortLetters;
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return mData.get(position).sortLetters.charAt(0);
        }

    }

    private void search(String word) {
        if (TextUtils.isEmpty(word)) {
            mSearchResultList.setVisibility(View.GONE);
            mResultList.clear();
            mSearchResultAdapter.updateData(mResultList);
        } else {
            List<String> hitResult = CityUtils.getCityByKeyword(word);
            mResultList.clear();
            for (String item : hitResult) {
                SpannableString span = new SpannableString(item);
                if (item.contains(word)) {
                    int startPos = item.indexOf(word);
                   /* span.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.orange)),
                            startPos, startPos + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
                }
                mResultList.add(span);
            }
            mSearchResultList.setVisibility(View.VISIBLE);
            mSearchResultAdapter.updateData(mResultList);
        }
    }
}
