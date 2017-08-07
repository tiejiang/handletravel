package com.xiangxun.ework.legwork;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xiangxun.ework.legwork.constant.Constant;
import com.xiangxun.ework.legwork.provider.DBHelper;


public class RoutineDisplay extends Activity implements OnClickListener {
	private Button show;
	private Button confirm;
	private Button routeshow;
	
	private ListView listview;
	private DBHelper dbHelper;
	private SQLiteDatabase sqlDB;
	private Cursor mCursor;
	private SimpleCursorAdapter mSimpleCursorAdapter;
	
	public String latitudeString = null;
	public String longitudeString = null;
	public String startAddressString = null;
	public String endAddressString = null;
	public String[] addressString = null;
	
	public float latitude; 
	public float longitude;
	
	public static boolean isDisplayRoute = false;
	public static boolean isSearch = false;
	
	private Vector<String > mAddressName;
	public ArrayList<HashMap<String, Object>> dataListItem = new ArrayList<HashMap<String, Object>>();
	public static ArrayList<HashMap<String, Object>> dataListFromSQLite = new ArrayList<HashMap<String, Object>>();
	
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_routine_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		
		show = (Button)findViewById(R.id.today);
		confirm = (Button)findViewById(R.id.confirm);
		routeshow = (Button)findViewById(R.id.routeshow);
		listview = (ListView)findViewById(R.id.listview);
		
		confirm.setVisibility(View.INVISIBLE);
		routeshow.setVisibility(View.INVISIBLE);
		
		confirm.setOnClickListener(this);
		show.setOnClickListener(this);
		routeshow.setOnClickListener(this);
		
		mAddressName = new Vector<String>();
		
		// 初始化搜索模块，注册事件监听
		listview.setOnItemLongClickListener(new onLongClickListener());
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				view.setBackgroundColor(Color.BLACK);
//				latitudeString = mCursor.getString(Constant.CUR_LATITUDDE);
				
				mAddressName.add(mCursor.getString(Constant.CUR_ADDRESS_NAME));
				addressString = new String[mAddressName.size()];
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	public void showRoutinePlanData(){
		dbHelper = new DBHelper(RoutineDisplay.this);
		sqlDB = dbHelper.getWritableDatabase();
		String[] colums = new String[] {"_id", 
				DBHelper.COL_PLAN_STAY_TIME, 
				DBHelper.COL_PLAN_ARRIVE_TIME, 
				DBHelper.COL_CURRENT_LATITUDE, 
				DBHelper.COL_CUTTENT_LONGITUDE,
				DBHelper.COL_CUTTENT_ADDRESS};
		mCursor = sqlDB.query(DBHelper.TABLE_NAME, colums, null, null, null, null, null);
		String[] headers = new String[] {
				DBHelper.COL_PLAN_STAY_TIME, 
				DBHelper.COL_PLAN_ARRIVE_TIME, 
				DBHelper.COL_CURRENT_LATITUDE, 
				DBHelper.COL_CUTTENT_LONGITUDE,
				DBHelper.COL_CUTTENT_ADDRESS};
		mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.daily_routine_item, mCursor, headers, 
				new int[]{R.id.text1, R.id.text2,  R.id.text3, R.id.text4, R.id.text5});
		listview.setAdapter(mSimpleCursorAdapter);
		
	}
	class onLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			Toast.makeText(getApplicationContext(), "长按删除此条记录", Toast.LENGTH_LONG).show();
			mCursor.moveToPosition(position);
			String rowId = mCursor.getString(0);
			
			sqlDB.delete(DBHelper.TABLE_NAME, "_id=?", new String[]{rowId});
			mCursor.requery();
			mSimpleCursorAdapter.notifyDataSetChanged();
			return false;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.today:
			showRoutinePlanData();
			show.setVisibility(View.GONE);
			confirm.setVisibility(View.VISIBLE);
			routeshow.setVisibility(View.VISIBLE);
			break;

		case R.id.confirm:
			//通过intent的bundle传值到主界面
			isSearch = true;
			try {
				for (int i = 0; i < mAddressName.size(); i++) {
					addressString[i] = (String)mAddressName.get(i);
				}
			} catch (Exception e) {
				// TODO: handle exception
				addressString[0] = "厦门市集美区";
				addressString[1] = "厦门市集美区";
			} finally {
				mAddressName.removeAllElements();
			}
			Intent intent = new Intent();
			intent.setClass(RoutineDisplay.this, DrawerActivity.class);
    		Bundle bundle = new Bundle();
    		bundle.putString("start", addressString[0]);    		
    		bundle.putString("end", addressString[1]);
    		intent.putExtras(bundle);
    		startActivity(intent);
			finish();	
			break;
		case R.id.routeshow:
			//展示路线到地图上
			queryDatabase();
			
			isDisplayRoute = true;
			Intent intentToDra = new Intent();
			intentToDra.setClass(getApplicationContext(), DrawerActivity.class);
			startActivity(intentToDra);
			finish();
			
			break;
		}
	}
	//查询数据库得到每个点的latitude、longitude并装入arraylist
	public void queryDatabase(){
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			String latitude = mCursor.getString(Constant.CUR_LATITUDDE);
			String longitude = mCursor.getString(Constant.CUR_LONGITUDE);
			String startTimePlan = mCursor.getString(Constant.CUR_START_TIME_PLAN);
			String startTimeActual = mCursor.getString(Constant.CUR_START_TIME_ACTUAL);
			String currentAddress = mCursor.getString(Constant.CUR_ADDRESS_NAME);
			double latituded = Double.parseDouble(latitude);
			double longituded = Double.parseDouble(longitude);
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("latitude", latituded);
			map.put("longitude", longituded);
			map.put("startTimePlan", startTimePlan);
			map.put("startTimeActual", startTimeActual);
			map.put("address", currentAddress);
			dataListFromSQLite.add(map);
			
			mCursor.moveToNext();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCursor.close();
		sqlDB.close();
		dbHelper.close();
	}
	
}
