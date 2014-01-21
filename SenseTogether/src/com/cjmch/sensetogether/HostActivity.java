package com.cjmch.sensetogether;

import java.net.SocketException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjmch.sensetogether.config.Config;
import com.cjmch.sensetogether.config.HandlerMsg;
import com.cjmch.sensetogether.config.SensingMode;
import com.cjmch.sensetogether.network.NetworkUtil;
import com.cjmch.sensetogether.network.UDPServer;


public class HostActivity extends Activity {
	
	// UI
	private ListView mListView = null;
	private ArrayList<String> mArrayList = null;
	private ArrayAdapter<String> mAdapter = null;

	private UDPServer mUDPServer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		
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
		
		try {
			mUDPServer = new UDPServer(Config.UDP_SERVER_PORT, handler);
			mUDPServer.start();
		} catch (SocketException e) {
			e.printStackTrace();
			Toast.makeText(HostActivity.this, "Fail to create a server socket", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mUDPServer != null)
			mUDPServer.stop();
		mUDPServer = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host, menu);
		return true;
	}
	
	public void goToSensing(View view) {
		boolean success = NetworkUtil.broadcastUDP(Config.UDP_SERVER_PORT, Config.UDP_CMD_GO_TO_SENSING);
		if (!success) {
			Toast.makeText(HostActivity.this, "Fail to broadcast 'go_to_sensing'", Toast.LENGTH_SHORT).show();
			return ;
		}
			
		Intent intent = new Intent(HostActivity.this, SensorConfigActivity.class);
		intent.putExtra(Config.EXTRA_SENSING_MODE, SensingMode.HOST.toString());
		startActivity(intent);
	}
	
	public void changeAP(View view) {
		startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	}
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HandlerMsg.UDPCOMM_PACKET_RECV:
				Pair<String, String> packetMsg = (Pair<String, String>)msg.obj;
				String ip = packetMsg.first;
				String payload = packetMsg.second;
				
				if (payload.equals(Config.UDP_CMD_IP_REQUEST)) {
					mAdapter.add(ip);
					
					String localIP = NetworkUtil.getLocalIpAddress(NetworkUtil.INET4ADDRESS).getHostAddress();
					String response = Config.UDP_CMD_IP_RESPONSE + "\t" + localIP;
					
					boolean success = NetworkUtil.unicastUDP(ip, Config.UDP_SERVER_PORT, response);
					if (!success)
						Toast.makeText(HostActivity.this, "Fail to send an IP response to " + ip, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
}
