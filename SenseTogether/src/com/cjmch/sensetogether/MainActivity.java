package com.cjmch.sensetogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// UI functions
	
	public void performStandaloneSensing(View v) {
		
	}
	
	public void createRoom(View v) {
		Intent intentHostActivity = new Intent(MainActivity.this, HostActivity.class);
		startActivity(intentHostActivity);
	}
	
	public void joinRoom(View v) {
		
	}
}
