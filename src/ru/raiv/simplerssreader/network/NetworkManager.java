package ru.raiv.simplerssreader.network;



import ru.raiv.simplerssreader.R;
import ru.raiv.simplerssreader.service.NetworkService;
import ru.raiv.simplerssreader.utils.LocalIntents;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkManager {


	private static final String TAG = "ru.raiv.simplerssreader.network";

	private Application context;
	
	private static NetworkManager instance = null;
	

	private boolean needCacheData=true;
	
//	private Intent requestIntent;
	
	private NetworkManager(Application app)
	{
		this.context=app;

		performCaching();
	}
	
	public static NetworkManager getInstance(Application app){
		if(instance == null)
		{
			instance = new NetworkManager(app);
		}
		return instance;
		
	};
	
	
	private boolean checkOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	

	
	 void performCaching()
	{
		if(needCacheData)
		{
			if(checkOnline())
			{
				needCacheData=false;
				sendCacheRequest();
			}else
			{
				Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
			}
		}
	}
	

	
	
	private void sendCacheRequest()
	{
		Intent requestIntent=new Intent(context, NetworkService.class);
		requestIntent.setAction(LocalIntents.REQUEST_DATA);
		context.startService(requestIntent);
		Log.d(TAG, "Sending recache intent");
	}

	
	public void queryRecaching()
	{
		needCacheData=true;
		performCaching();
	}
}
