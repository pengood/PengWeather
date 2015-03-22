package com.pzhao.pzhaoweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	
	public static void SendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("TAG", "SendHttpRequest: "+address);
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					
					if(listener!=null){
						listener.onFinish(response.toString());
						Log.d("TAG", "getResponse: "+response.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null){
						listener.onError(e);
						Log.d("TAG", "getResponse:Error");
					}
				}
				finally{
					if(connection!=null)
						connection.disconnect();
					Log.d("TAG", "Http disconnect");
				}
			}
		}).start();
	}

}
