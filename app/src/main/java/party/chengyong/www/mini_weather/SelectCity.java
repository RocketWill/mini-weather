package party.chengyong.www.mini_weather;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import party.chengyong.www.app.MyApplication;
import party.chengyong.www.bean.City;

public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private MyApplication app;
    private List<City> mCityList;
    private ListView listView;
    private ListAdapter listAdapter;
    private String cityCode = "101010100";
    private EditText mSearchEditText;
    private SearchCityAdaptor mSearchCityAdaptor;
    private TextView navCityName;

//    當Activity準備要產生時，先呼叫onCreate方法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initViews();
        GenerateListView(mCityList);
    }

//    初始化介面
    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        navCityName = (TextView) findViewById(R.id.title_name);

//        實例化 MyApplication
        app = (MyApplication) getApplication();
        mCityList = app.getCityList();
        for (City city:mCityList){
            Log.d("sex",city.getCity());

        }


        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchCityAdaptor = new SearchCityAdaptor(SelectCity.this, mCityList);
                listView.setTextFilterEnabled(true);
                if (mCityList.size()<1 ||TextUtils.isEmpty(s)){
                    listView.setAdapter(listAdapter);
                }else{
                    listView.setAdapter(mSearchCityAdaptor);
                    mSearchCityAdaptor.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mSearchEditText = (EditText) findViewById(R.id.search_edit);
        mSearchEditText.addTextChangedListener(mTextWatcher);


//        獲取來自MainActivity的程式名稱
        Intent getIntent = getIntent();
        String cityName = getIntent.getStringExtra("cityName");
        if (cityName != null){
            navCityName.setText(cityName+"天氣");
        }else{
            navCityName.setText("北京天氣");
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_back){
//            Intent i = new Intent();
//            i.putExtra("cityCode","101280101");
//            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void GenerateListView(List<City> cityList){
        listView = (ListView) findViewById(R.id.title_list);
        CustomAdapter customAdapter = new CustomAdapter(SelectCity.this,mCityList);
        listView.setAdapter(customAdapter);

        //        ListView 內容的點擊事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                獲取該項item的內容（City類型）
                City city = (City)parent.getItemAtPosition(position);
//
//              將點擊內容用Toast顯示出來
                Toast toast = Toast.makeText(SelectCity.this,
                        "你選中的是: "+city.getCity()+" "+city.getNumber(), Toast.LENGTH_LONG);
                toast.show();

//                Intent(意圖)代表使用者與應用程式的互動，互動通常會產生變化，
//                例如按下一個圖示後進行撥出電話，或者按下一個按鈕後轉換到另一個畫面(也就是另一個Activity)
                Intent appInfo = new Intent(SelectCity.this, MainActivity.class);

//                putExtra方法能傳遞簡單的值，將cityCode傳回給 MainActivity
                appInfo.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK, appInfo);

//                將cityCode寫入SharedPreferences
                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                pref.edit().putString("main_city_code", city.getNumber()).commit();

                //退棧（返回前一個Activity）
                finish();
                //startActivity(appInfo);


            }
        });

    }

    class CustomAdapter extends BaseAdapter{

        private LayoutInflater myInflater;
        private List<City> cityList;

        public CustomAdapter(Context context,List<City> cityList){
            myInflater = LayoutInflater.from(context);
            this.cityList = cityList;
        }

        @Override
        public int getCount() {
            return mCityList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mCityList.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return mCityList.indexOf(getItem(position));
        }

        private class ViewHolder {
            TextView txtProvince;
            TextView txtCity;
            public ViewHolder(TextView txtProvince, TextView txtCity){
                this.txtProvince = txtProvince;
                this.txtCity = txtCity;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                convertView = myInflater.inflate(R.layout.custom_list_layout, null);
                holder = new ViewHolder(
                        (TextView) convertView.findViewById(R.id.listview_province),
                        (TextView) convertView.findViewById(R.id.listview_cityname)
                );
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            City city = (City) getItem(position);

            if (city.getPrivince().equals(city.getCity())){
                holder.txtProvince.setText(city.getPrivince());
                holder.txtCity.setText(city.getCity());
                holder.txtProvince.setTextColor(Color.BLACK);
                convertView.setBackgroundColor(Color.parseColor("#e4e4e4"));

            }else{
                holder.txtProvince.setText(city.getPrivince());
                holder.txtCity.setText(city.getCity());
                holder.txtProvince.setTextColor(Color.BLACK);
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            return convertView;
        }
    }

}
