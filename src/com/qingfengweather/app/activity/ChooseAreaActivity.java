package com.qingfengweather.app.activity;

import java.util.ArrayList;
import java.util.List;



import com.qingfengweather.app.R;
import com.qingfengweather.app.model.City;
import com.qingfengweather.app.model.County;
import com.qingfengweather.app.model.Province;
import com.qingfengweather.app.model.QingFengWeatherDB;
import com.qingfengweather.app.util.HttpCallbackListener;
import com.qingfengweather.app.util.HttpUtil;
import com.qingfengweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	public static final  int LEVEL_PROVINCE = 0;
	public static final  int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private QingFengWeatherDB qingFengWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		qingFengWeatherDB = QingFengWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public  void onItemClick(AdapterView<?> arg0, View view, int index, long arg3){
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}
				else if (currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}
			}


			
		});
		queryProvinces();
	}
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ����ȥ��������
	 */
	
	private void queryProvinces(){
		provinceList = qingFengWeatherDB.loadProvinces();
		if (provinceList.size()>0){
			dataList.clear();
			for (Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromServer(null,"province");
		}
		
	}
	
	/**
	 * ��ѯ�У��������ݿ⣬�ٷ�������
	 */
	
	
	private void queryCities(){
		cityList = qingFengWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for (City city: cityList){
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else 
		{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
        /**
         * ��ѯ�ء� 
         */
	
	
	private void queryCounties(){
		countyList = qingFengWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ������Ϣ��
	 */
	
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		} else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
	
			public void onFinish(String response){
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(qingFengWeatherDB, response);
				} else if ("city".equals(type)){
					result = Utility.handleCitiesResponse(qingFengWeatherDB, response, selectedProvince.getId());
				} else if("county".equals(type)){
					result = Utility.handleCountiesResponse(qingFengWeatherDB, response, selectedCity.getId());
				}
				if (result){
					runOnUiThread(new Runnable(){
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							} else if ("city".equals(type)){
								queryCities();
							} else if ("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
	
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	
	
	private void showProgressDialog(){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رնԻ���
	 */
	
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Back����
	 */
	
	@Override
	
	public void onBackPressed(){
		if (currentLevel == LEVEL_COUNTY){
			queryCities();
		} else if (currentLevel == LEVEL_CITY){
			queryProvinces();
		} else {
			finish();
		}
	}
}
