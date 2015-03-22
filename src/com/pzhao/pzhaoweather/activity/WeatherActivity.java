package com.pzhao.pzhaoweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pzhao.pengweather.R;
import com.pzhao.pzhaoweather.service.AudoUpdateService;
import com.pzhao.pzhaoweather.util.HttpCallbackListener;
import com.pzhao.pzhaoweather.util.HttpUtil;
import com.pzhao.pzhaoweather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private TextView cityNameTextView;
	private TextView publishTextView;
	private TextView weatherDestTextView;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDaTextView;

	private ImageButton switchButton;
	private ImageButton refreshButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.pzhao.pengweather.R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(com.pzhao.pengweather.R.id.weather_info_layout);
		cityNameTextView = (TextView) findViewById(com.pzhao.pengweather.R.id.city_name);
		publishTextView = (TextView) findViewById(com.pzhao.pengweather.R.id.publish_text);
		weatherDestTextView = (TextView) findViewById(com.pzhao.pengweather.R.id.weather_desp);
		temp1 = (TextView) findViewById(com.pzhao.pengweather.R.id.temp1);
		temp2 = (TextView) findViewById(com.pzhao.pengweather.R.id.temp2);
		currentDaTextView = (TextView) findViewById(com.pzhao.pengweather.R.id.current_date);
		switchButton = (ImageButton) findViewById(com.pzhao.pengweather.R.id.home);
		refreshButton = (ImageButton) findViewById(com.pzhao.pengweather.R.id.refresh);
		String countyCode = getIntent().getStringExtra("county_code");
		Log.d("TAG", "getIntent().getStringExtra(county_code): "+countyCode);
		if (!TextUtils.isEmpty(countyCode)) {
			publishTextView.setText("同步中..");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTextView.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}

		else {
			showWeather();
		}

		switchButton.setOnClickListener(this);
		refreshButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		case R.id.home : Intent intent=new Intent(this,ChooseAreaActivity.class);
						intent.putExtra("from weather_activity", true);
						startActivity(intent);
						finish();break;
		case R.id.refresh :
						 publishTextView.setText("同步中..");
						 SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
						 String weatherCode=preferences.getString("weather_code", "");
						 if(!TextUtils.isEmpty(weatherCode)){
							 queryWeatherInfo(weatherCode);
						 }
						 break;
						 
		default:
			break;
		}

	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryWeatherFromServer(address, "countyCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryWeatherFromServer(address, "weatherCode");
	}

	private void queryWeatherFromServer(final String address, final String type) {
		HttpUtil.SendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							Log.d("TAG", "county.equals(type) "+weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}

				else if ("weatherCode".equals(type)) {
					Log.d("TAG", "else if (weatherCode.equals(type))"+response);
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});

				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishTextView.setText("同步失败");
					}
				});
			}
		});
	}

	private void showWeather() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameTextView.setText(preferences.getString("city_name", ""));
		temp1.setText(preferences.getString("temp1", ""));
		temp2.setText(preferences.getString("temp2", ""));
		publishTextView.setText("今天"
				+ preferences.getString("publish_time", "") + "发布");
		weatherDestTextView.setText(preferences.getString("weather_desp", ""));
		currentDaTextView.setText(preferences.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTextView.setVisibility(View.VISIBLE);
		
		Intent intent=new Intent(this,AudoUpdateService.class);
		startService(intent);
		
	}

}
