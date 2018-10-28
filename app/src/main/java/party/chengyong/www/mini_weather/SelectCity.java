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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        initViews();

    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        app = (MyApplication) getApplication();
        mCityList = app.getCityList();
        for (City city:mCityList){
            Log.d("sex",city.getCity());
        }
        listView = (ListView) findViewById(R.id.title_list);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                mCityList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City)parent.getItemAtPosition(position);
                Toast toast = Toast.makeText(SelectCity.this,
                        "你選中的是: "+city.getCity()+" "+city.getNumber(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_back){
            Intent i = new Intent();
            i.putExtra("cityCode",cityCode);
            setResult(RESULT_OK, i);
            finish();
        }
    }

}
