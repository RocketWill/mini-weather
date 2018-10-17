package party.chengyong.www.mini_weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import party.chengyong.www.bean.TodayWeather;
import party.chengyong.www.mini_weather.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mCitySelect;

    private ImageView mUpdateBtn;

    private TextView cityTV, timeTV, humidityTV, weekTV, pmDataTV, pmQualityTV, temperatureTV, climateTV, windTV, cityNameTV;
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView)findViewById(R.id.title_refresh);
        mUpdateBtn.setOnClickListener(this);

        NetUtil netutil = new NetUtil();
        if (netutil.getNetworkState(this) != netutil.NETWORN_NONE){
            Log.d("mini_weather","網路ok");
            //queryWeatherCode("101010100");
        }else{
            Log.d("mini_weather","網路掛了");
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }

        if (view.getId() == R.id.title_refresh){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("mini_weather",cityCode);

            NetUtil netutil = new NetUtil();
            if (netutil.getNetworkState(this) != netutil.NETWORN_NONE){
                Log.d("mini_weather","網路ok");
                queryWeatherCode(cityCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("mini_weather", "The city code you select is " + newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("mini_weather", "網路OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("mini_weather","網路掛了");
                Toast.makeText(MainActivity.this, "Please Check the Network", Toast.LENGTH_LONG).show();
            }
        }
    }

    //初始化控件内容
    void initView() {
        cityNameTV = (TextView) findViewById(R.id.title_city_name);
        cityTV = (TextView) findViewById(R.id.city_name);
        timeTV = (TextView) findViewById(R.id.announce_time);
        humidityTV = (TextView) findViewById(R.id.humidity);
        weekTV = (TextView) findViewById(R.id.week_today);
        pmDataTV = (TextView) findViewById(R.id.pm2_5_index);
        pmQualityTV = (TextView) findViewById(R.id.pm2_5_quility);
        pmImg = (ImageView) findViewById(R.id.pm_2_5_img);
        temperatureTV = (TextView) findViewById(R.id.temperature);
        climateTV = (TextView) findViewById(R.id.climate);
        windTV = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_image);

        cityNameTV.setText("N/A");
        cityTV.setText("N/A");
        timeTV.setText("N/A");
        humidityTV.setText("N/A");
        weekTV.setText("N/A");
        pmDataTV.setText("N/A");
        pmQualityTV.setText("N/A");
        temperatureTV.setText("N/A");
        climateTV.setText("N/A");
        windTV.setText("N/A");
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("mini_weather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("mini_weather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("mini_weather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null){
                        Log.d("mini_weather",todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){

        //将解析的数据存入TodayWeather对象中
        TodayWeather todayWeather = null;

        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int evenType = xmlPullParser.getEventType();
            Log.d("mini_weather","parseXML");
            while (evenType != XmlPullParser.END_DOCUMENT){
                switch (evenType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null){
                            if (xmlPullParser.getName().equals("city")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","city: " + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("updatetime")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","updatetime: " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("shidu")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","shidu: " + xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("wendu")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","wendu: " + xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("pm25")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","pm25: " + xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("quality")){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","quality: " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","fengxiang: " + xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount ++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","fengli: " + xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount ++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","date: " + xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount ++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","high: " + xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount ++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","low: " + xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount ++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 0){
                                evenType = xmlPullParser.next();
                                Log.d("mini_weather","type: " + xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount ++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                evenType = xmlPullParser.next();
            }
        }
        catch (XmlPullParserException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    //编写 updateTodayWeather 函数
    void updateTodayWeather(TodayWeather todayWeather){
        cityNameTV.setText(todayWeather.getCity()+"天氣");
        cityTV.setText(todayWeather.getCity());
        timeTV.setText(todayWeather.getUpdatetime()+ " 發佈");
        humidityTV.setText("濕度："+todayWeather.getShidu());
        pmDataTV.setText(todayWeather.getPm25());
        pmQualityTV.setText(todayWeather.getQuality());
        weekTV.setText(todayWeather.getDate());
        temperatureTV.setText(trimTemperatureString(todayWeather.getLow())+" ~ "+trimTemperatureString(todayWeather.getHigh()));
        climateTV.setText(todayWeather.getType());
        windTV.setText("風力："+todayWeather.getFengli());

        //設置 pm 2.5 圖標
        int pm25Index = Integer.parseInt(todayWeather.getPm25());
        if ( pm25Index > 300){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }else if ( pm25Index > 200){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }else if (pm25Index > 150){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        }else if (pm25Index > 100){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        }else if (pm25Index > 50){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }else{
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }

        //設置天氣圖標
        String weatherText = todayWeather.getType();
        if (weatherText.equals("暴雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if (weatherText.equals("暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        }else if (weatherText.equals("大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if (weatherText.equals("大雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if (weatherText.equals("大雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if (weatherText.equals("多云")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if (weatherText.equals("雷阵雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if (weatherText.equals("雷阵雨冰雹")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if (weatherText.equals("晴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if (weatherText.equals("沙尘暴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if (weatherText.equals("特大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if (weatherText.equals("雾")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if (weatherText.equals("小雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if (weatherText.equals("小雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if (weatherText.equals("阴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if (weatherText.equals("雨加雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if (weatherText.equals("阵雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if (weatherText.equals("阵雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if (weatherText.equals("中雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if (weatherText.equals("中雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }else{
            weatherImg.setImageResource(R.drawable.pm2_5);
        }


        Toast.makeText(MainActivity.this,"Update Successfully",Toast.LENGTH_SHORT).show();
    }

    //去除 “高溫” 和 “低溫”
    private String trimTemperatureString(String getTem){
        char[] chrArr = getTem.toCharArray();
        String result = "";
        for (int i=3;i<chrArr.length;i++){
            result += chrArr[i];
        }
        return result;
    }
}
