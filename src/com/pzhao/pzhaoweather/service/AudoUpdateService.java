package com.pzhao.pzhaoweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pzhao.pzhaoweather.receiver.AutoUpdateReceiver;
import com.pzhao.pzhaoweather.util.HttpCallbackListener;
import com.pzhao.pzhaoweather.util.HttpUtil;
import com.pzhao.pzhaoweather.util.Utility;

public class AudoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		});
		Log.d("TAG", "I'm in service");
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
		int inTime=8*60*60*1000;
		long triggerAtTime=SystemClock.elapsedRealtime()+inTime;
		Intent intent2=new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pendingIntent=PendingIntent.getBroadcast(this, 0, intent2, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
		
		
		
		
	}
	
	public void updateWeather(){
		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode=preferences.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.SendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AudoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		});
	}
	
	

}
