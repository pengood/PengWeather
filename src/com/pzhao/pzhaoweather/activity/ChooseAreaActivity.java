package com.pzhao.pzhaoweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pzhao.pengweather.R;
import com.pzhao.pzhaoweather.model.City;
import com.pzhao.pzhaoweather.model.County;
import com.pzhao.pzhaoweather.model.Province;
import com.pzhao.pzhaoweather.model.PzhaoWeatherDB;
import com.pzhao.pzhaoweather.util.HttpCallbackListener;
import com.pzhao.pzhaoweather.util.HttpUtil;
import com.pzhao.pzhaoweather.util.Utility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private  TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private PzhaoWeatherDB mPzhaoWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countiesList;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	
	private boolean isFromWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFromWeather=getIntent().getBooleanExtra("from weather_activity", false);
		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.getBoolean("city_selected", false)&&!isFromWeather){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
	//		return;
		}
		
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		Log.d("TAG", "set adapter");
		mPzhaoWeatherDB=PzhaoWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(arg2);
					queryCities();
				}
				else if(currentLevel==LEVEL_CITY) {
					selectedCity=cityList.get(arg2);
					queryCounties();
				}
				else if(currentLevel==LEVEL_COUNTY){
					String contyCode=countiesList.get(arg2).getCountyCode();
					Log.d("TAG1", "countyName: "+countiesList.get(arg2).getCountyName()+" countyCode: "+contyCode);
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", contyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince();
		
	}
	
	private void queryProvince() {
		provinceList = mPzhaoWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			Log.d("TAG","provinceList.size() > 0" );
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromSever(null, "province");
		}
	}

	
	
	private void queryCities(){
		cityList=mPzhaoWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			Log.d("TAG","cityList.size()>0 " );
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}
		else {
			queryFromSever(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	private void queryCounties() {
		countiesList=mPzhaoWeatherDB.loadCounties(selectedCity.getId());
		Log.d("TAG","countiesList.size(): "+countiesList.size());
		if (countiesList.size() > 0) {
			Log.d("TAG","countiesList.size() > 0 " );
			dataList.clear();
			for (County county : countiesList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromSever(selectedCity.getCityCode(), "county");
		}
	}
	
	//从服务器获取数据
	private void queryFromSever(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.SendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				 if("province".equals(type)){
					 result=Utility.handleProvincesResponse(mPzhaoWeatherDB, response);
					 Log.d("TAG", "Utility.handleProvinceResponse: "+result);
				 }
				 else if("city".equals(type)){
					 result=Utility.handleCitiesResponse(mPzhaoWeatherDB, response, selectedProvince.getId());
					 Log.d("TAG", "Utility.handleCityReponse: "+result);
				 }
				 else if("county".equals(type)){
					 result=Utility.handleCountiesResponse(mPzhaoWeatherDB, response, selectedCity.getId());
					 Log.d("TAG", "Utility.handleCountyReponseUtility.handleCountyReponse: "+result);
				 }
				 if(result){
					 runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}
							else if("city".equals(type)){
								queryCities();
							}
							else if("county".equals(type)){
									queryCounties();
							}
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
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}
		else if(currentLevel==LEVEL_CITY){
			queryProvince();
		}
		else if(isFromWeather){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
		}
		
		else {
			finish();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

