package party.chengyong.www.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

import party.chengyong.www.app.MyApplication;
import party.chengyong.www.bean.City;


public class Locate extends Activity{
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private Button locateBtn;
    private String mLocationCode;
    private List<City> mCityList;
    private MyApplication myApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate);
        locateBtn = (Button) findViewById(R.id.locate_btn);

        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener(locateBtn);
        mLocationClient.registerLocationListener(myLocationListener);
        initLocation();
        mLocationClient.start();

        final Intent intent = new Intent(this,MainActivity.class);
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication = (MyApplication)getApplication();
                mCityList = myApplication.getCityList();
                for(City city:mCityList){
                    String locationName = locateBtn.getText().toString();
                    if (city.getCity().equals(locationName.substring(0,locationName.length()-1))){
                        mLocationCode = city.getNumber();
                        Log.d("locate",locationName.substring(0,locationName.length()-1));
                    }
                }
                SharedPreferences sharedPreferences = getSharedPreferences("config",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("main_city_code",mLocationCode);
                editor.commit();
                intent.putExtra("cityCode",mLocationCode);
                setResult(RESULT_OK, intent);
                Log.d("third",mLocationCode);
                //finish();
                startActivity(intent);
            }
        });
    }
    void initLocation()
    {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
    }

}

class MyLocationListener implements BDLocationListener{
    Button locateBtn;
    MyLocationListener(Button locateBtn)
    {
        this.locateBtn = locateBtn;
    }
    String cityName;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String cityName = bdLocation.getCity();
        Log.d("Locate",cityName);
        locateBtn.setText(cityName);
    }
}

