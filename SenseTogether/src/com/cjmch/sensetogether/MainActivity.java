package com.cjmch.sensetogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;

import com.cjmch.sensetogether.config.Config;
import com.cjmch.sensetogether.config.SensingMode;
import com.cjmch.sensetogether.network.NetworkUtil;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		NetworkUtil.setWifiManager((WifiManager)getSystemService(Context.WIFI_SERVICE));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// UI functions
	
	public void performStandaloneSensing(View v) {
		Intent intent = new Intent(MainActivity.this, SensorConfigActivity.class);
		intent.putExtra(Config.EXTRA_SENSING_MODE, SensingMode.STANDALONE.toString());
		startActivity(intent);
	}
	
	public void createRoom(View v) {
		boolean isWifiEnabled = NetworkUtil.isWifiOn(this);
		
		if (isWifiEnabled) {
			Intent intent = new Intent(MainActivity.this, HostActivity.class);
			startActivity(intent);
		} else {
			showWifiWarningDialog();
		}
	}
	
	public void joinRoom(View v) {
		boolean isWifiEnabled = NetworkUtil.isWifiOn(this);
		
		if (isWifiEnabled) {
			Intent intent = new Intent(MainActivity.this, GuestActivity.class);
			startActivity(intent);
		} else {
			showWifiWarningDialog();
		}		
	}
	
	// private functions
	
	private void showWifiWarningDialog() {
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
	    alt_bld.setTitle("Note");
	    alt_bld.setMessage("Wi-Fi is turned off. For the multi-sensing, note that your smartphones should connect the same access point. Do you want to go to Wi-Fi setting?").setCancelable(
	        false).setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	        }
	        }).setNegativeButton("No",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            dialog.cancel();
	        }
	        });
	    AlertDialog alert = alt_bld.create();
	    alert.show();
	}
}


