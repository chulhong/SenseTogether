package com.cjmch.sensetogether;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjmch.sensetogether.network.NetworkUtil;
import com.cjmch.sensetogether.network.Server;

import config.Config;
import config.HandlerMsg;

public class HostActivity extends Activity {
	
	// UI
	private ListView mListView = null;
	private ArrayList<String> mArrayList = null;
	private ArrayAdapter<String> mAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		
		Thread serverThread = new Thread(new Server(Config.SERVER_PORT, handler));
		serverThread.start();
		
		mListView = (ListView)findViewById(R.id.guest_list_view);
		mArrayList = new ArrayList<String>();
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		String apName = NetworkUtil.getAPName(this);
		TextView apView = (TextView)findViewById(R.id.text_connected_ap);
		apView.setText("Currently connected AP: " + apName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host, menu);
		return true;
	}
	
	public void goToSensing(View view) {
//		finish();
	}
	
	public void changeAP(View view) {
		startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HandlerMsg.HOST_SERVERIP_LOOKUP_ERROR:
				Toast.makeText(HostActivity.this, "Fail to get local ip address", Toast.LENGTH_SHORT).show();
				break;
			case HandlerMsg.HOST_FOUND_GUEST:
				String ip = (String)msg.obj;
				mAdapter.add(ip);
				break;
			}
		}
	};
}
