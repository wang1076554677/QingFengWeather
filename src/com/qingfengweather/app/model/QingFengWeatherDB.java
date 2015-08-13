package com.qingfengweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.qingfengweather.app.db.QingFengWeatherOpenHelper;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class QingFengWeatherDB {
	public static final String DB_NAME="qingfeng_weather";
	public static final int VERSION=1;
	private static QingFengWeatherDB qingFengWeatherDB;
	private SQLiteDatabase db;
	private QingFengWeatherDB(Context context){
		QingFengWeatherOpenHelper dbHelper=new QingFengWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	public synchronized static QingFengWeatherDB getInstance(Context context){
		if (qingFengWeatherDB==null){
			qingFengWeatherDB=new QingFengWeatherDB(context);
			}
		return qingFengWeatherDB;
	}
	
	/**
	 * 将province实例存储到数据库。
	 */
    public void saveProvince(Province province){
    	if (province!=null){
    		ContentValues values=new ContentValues();
    		values.put("province_name",province.getProvinceName());
    		values.put("province_code",province.getProvinceCode());
    		db.insert("province", null, values);
    	}
    }
    
    
    /**
     * 从数据库读取全国所有省份信息。
     */
    public List<Province> loadProvinces(){
    	List<Province>List= new ArrayList<Province>();
    	Cursor cursor=db.query("Province", null, null,null,null,null,null);
    	if(cursor.moveToFirst()){
    		do{
    			Province province=new Province();
    			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
    			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
    			List.add(province);
    		}
    		while(cursor.moveToNext());
    	}
    	return List;
    }
    public void saveCity(City city){
    	if (city!=null){
    		ContentValues values=new ContentValues();
    		values.put("city_name",city.getCityName());
    		values.put("city_code",city.getCityCode());
    		values.put("province_id", city.getProvinceId());
    		db.insert("City", null, values);
    }  	
}
    
    
    public List<City>loadCities(int provinceId){
    	List<City> List= new ArrayList<City>();
    	Cursor cursor=db.query("City",null,"province_id=?",new String[] {String.valueOf(provinceId)},null,null,null);
    	if (cursor.moveToFirst()){
    		do{
    			City city=new City();
    			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
    			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
    			city.setProvinceId(provinceId);
    			List.add(city);
    		}
    		while(cursor.moveToNext());
    	}
    	return List;
    }
    
    
    public void saveCounty(County county){
    if (county!=null){
    	ContentValues values=new ContentValues();
		values.put("county_name",county.getCountyName());
		values.put("county_code",county.getCountyCode());
		db.insert("county", null, values);
    }	
    }
    
    
    public List<County> loadCounties(int cityId){
    	List<County>List= new ArrayList<County>();
    	Cursor cursor=db.query("County",null,"city_id=?",new String[] {String.valueOf(cityId)},null,null,null);
    	if (cursor.moveToFirst()){
    		do{
    			County county=new County();
    			county.setId(cursor.getInt(cursor.getColumnIndex("id")));
    			county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
    			county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
    			List.add(county);
    		}
    		while(cursor.moveToNext());
    	}
    	return List;
    }
    }
    