package com.qingfengweather.app.util;

import android.text.TextUtils;

import com.qingfengweather.app.model.City;
import com.qingfengweather.app.model.County;
import com.qingfengweather.app.model.Province;
import com.qingfengweather.app.model.QingFengWeatherDB;

public class Utility {
	public synchronized static boolean handleProvincesResponse(QingFengWeatherDB qingFengWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces= response.split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p: allProvinces){
					String[]array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					qingFengWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean handleCitiesResponse(QingFengWeatherDB qingFengWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities !=null && allCities.length>0){
				for(String c:allCities){
					String[] array= c.split("\\|");
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					qingFengWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	
	
	public static boolean handleCountiesResponse(QingFengWeatherDB qingFengWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties =response.split(",");
			if (allCounties!= null && allCounties.length>0){
				for(String c: allCounties){
					String[] array= c.split("\\|");
					County county =new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					qingFengWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

}
