package com.pzhao.pzhaoweather.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pzhao.pzhaoweather.db.PzhaoWeatherOpenHelper;

public class PzhaoWeatherDB {

	public static final String DB_NAME="pzhaoweather";
	
	public static final int VERSION=1;
	
	private static PzhaoWeatherDB pzhaoWeatherDB;
	private static SQLiteDatabase db;
	
	private PzhaoWeatherDB (Context context){
		PzhaoWeatherOpenHelper pzhaoWeatherOpenHelper=new PzhaoWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		Log.d("TAG", "private PzhaoWeatherDB (Context context)");
		db=pzhaoWeatherOpenHelper.getWritableDatabase();
	}
	//单例模式
	public synchronized static PzhaoWeatherDB getInstance(Context context){
		Log.d("TAG", "getInstance");
		if(pzhaoWeatherDB==null){
			pzhaoWeatherDB=new PzhaoWeatherDB(context);
		}
		return pzhaoWeatherDB;
		
	}
	
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	
	public List<City> loadCities(int province_id){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String []{
				String.valueOf(province_id)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setProvinceId(province_id);
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
	
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
//	public List<County> loadCounties(int cityId) {
//		List<County> list = new ArrayList<County>();
//		Cursor cursor = db.query("County", null, "city_id = ?",
//				new String[] { String.valueOf(cityId) }, null, null, null);
//		if (cursor.moveToFirst()) {
//			do {
//				County county = new County();
//				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
//				county.setCountyName(cursor.getString(cursor
//						.getColumnIndex("county_name")));
//				county.setCountyCode(cursor.getString(cursor
//						.getColumnIndex("county_code")));
//				county.setCityId(cityId);
//				list.add(county);
//			} while (cursor.moveToNext());
//		}
//		return list;
//	}
	
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		Log.d("TAG", "list.size(): "+list.size());
		return list;
	}
	
	
	
}
