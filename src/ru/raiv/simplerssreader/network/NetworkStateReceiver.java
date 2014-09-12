package ru.raiv.simplerssreader.network;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;



public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
		{
			NetworkManager nm = NetworkManager.getInstance((Application)context.getApplicationContext());
			nm.performCaching();
		}
		
	}

}
