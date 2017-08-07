package com.xiangxun.ework.legwork.util;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;

public class TransitRoutePlan implements OnGetRoutePlanResultListener {
	
	RouteLine route = null;
	boolean useDefaultIcon = false;
	private int[] address ;

	//�������
	RoutePlanSearch mSearch = null;    // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	public TransitRoutePlan() {
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		address = new int[2];
	}

	/**
	* ����·�߹滮����ʾ��
	*
	* @param v
	*/
	public int[] SearchButtonProcess(String start, String end) {
		//��������ڵ��·������
		route = null;
		//�������յ���Ϣ������tranist search ��˵��������������
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("����", start);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("����", end);
		
	    mSearch.transitSearch((new TransitRoutePlanOption())
	            .from(stNode)
	            .city("����")
	            .to(enNode));
	    return null;
	}

	/**
	* �ڵ����ʾ��
	*
	* @param v
	*/
	public void nodeClick() {
		
	    /**test code for time and distance begin***/
	    int allTime = 0;
	    int distance = 0;
	    int allTimeSum = 0;
	    int distanceSum = 0;
	    if (route != null) {
	    	for (int j = 0; j < route.getAllStep().size(); j++) {
	        	TransitRouteLine.TransitStep allStep = (TransitStep) route.getAllStep().get(j);
				allTime = allStep.getDuration();			
				allTimeSum += allTime;
				distance = allStep.getDistance();
				distanceSum += distance;
			}
	        address[0] = allTimeSum;
	        address[1] = distanceSum;
		}
//		    Toast.makeText(getApplicationContext(), String.valueOf(address[0]), Toast.LENGTH_SHORT).show();
	    /**test code for time and distance end***/
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
	
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//		    Toast.makeText(RoutePlanDemo.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		    //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
		    //result.getSuggestAddrInfo()
		    return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.i("�ص�����ִ��~~~~~~~!!!!!", "execute ");
		    route = result.getRouteLines().get(0);
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		
	}
}
