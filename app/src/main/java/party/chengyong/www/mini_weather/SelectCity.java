package party.chengyong.www.mini_weather;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

//    當Activity準備要產生時，先呼叫onCreate方法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        initViews();

    }

//    初始化介面
    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

//        實例化 MyApplication
        app = (MyApplication) getApplication();
        mCityList = app.getCityList();
        for (City city:mCityList){
            Log.d("sex",city.getCity());
        }
        listView = (ListView) findViewById(R.id.title_list);

//        ArrayAdapter可將一組數組傳入ListView中去顯示出來
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                mCityList);
        listView.setAdapter(adapter);

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

                //退棧（返回前一個Activity）
                finish();
                //startActivity(appInfo);


            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_back){
            Intent i = new Intent();
            i.putExtra("cityCode","101280101");
            setResult(RESULT_OK, i);
            finish();
        }
    }

}
