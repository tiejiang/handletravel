package com.xiangxun.ework.legwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener{
	private EditText mLogin;
	private Button mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		mLogin = (EditText)findViewById(R.id.login);
		mLoginButton = (Button)findViewById(R.id.mLoginBtn);
		mLoginButton.setOnClickListener(this);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, DrawerActivity.class);
		startActivity(intent);
		finish();
	}
	
	

}
