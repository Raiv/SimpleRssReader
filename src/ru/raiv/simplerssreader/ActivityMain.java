package ru.raiv.simplerssreader;





import ru.raiv.simplerssreader.db.DataProvider;
import ru.raiv.simplerssreader.db.RssRecord;
import ru.raiv.simplerssreader.network.NetworkManager;
import ru.raiv.simplerssreader.utils.LocalIntents;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;




public class ActivityMain extends Activity {
	
	private BroadcastReceiver dataReadyReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(LocalIntents.DATA_READY))
			{
				refillListView();
			}
			
		}};
	private IntentFilter iFilter = new IntentFilter();
	
	
	private Button buttonUpgrade;
	
	private ListView listViewRssData;
	private RssDataAdapter listViewAdapter;
	
	private static class ViewHolder{
		TextView textViewDate;
		TextView textViewHeader;
		TextView textViewDescription;
		String link;
		
	}
	
	// to remove nasty squares instead of pics
	ImageGetter imgget;
	
	private class RssDataAdapter extends ArrayAdapter<RssRecord>
	{
		
	    
		public RssDataAdapter() {
			super(ActivityMain.this, R.layout.rss_item);
		}
		

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // ViewHolder буферизирует оценку различных полей шаблона элемента

	        ViewHolder holder;
	        // Очищает сущетсвующий шаблон, если параметр задан
	        // Работает только если базовый шаблон для всех классов один и тот же
	        View rowView = convertView;
	        if (rowView == null) {
	            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//.getLayoutInflater();
	            rowView = inflater.inflate(R.layout.rss_item, null);
	            holder = new ViewHolder();
	            holder.textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
	            holder.textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);
	            holder.textViewHeader = (TextView)rowView.findViewById(R.id.textViewHeader);
	            rowView.setTag(holder);
	        } else {
	            holder = (ViewHolder) rowView.getTag();
	        }

	        
	       // OrderDisplayer od = getItem (position);
	        
	        RssRecord r = getItem (position);
	        holder.textViewDate.setText(r.getTimeAsString());
	        
	        holder.textViewDescription.setText(Html.fromHtml(r.getDescription(),imgget,null));
	        holder.textViewHeader.setText(r.getTitle());
	        holder.link=r.getLink();
	        rowView.setClickable(true);
	        rowView.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					ViewHolder holder = (ViewHolder) v.getTag();
					/*start browser intent*/
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(holder.link));
					startActivity(i);
				}
	        	
	        });
	        
	        return rowView;
	    }



	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		
		
		iFilter.addAction(LocalIntents.DATA_READY);

		buttonUpgrade=(Button) findViewById(R.id.buttonUpgrade);
		buttonUpgrade.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NetworkManager.getInstance(getApplication()).queryRecaching();
				
			}
		});
		
		listViewRssData=(ListView) findViewById(R.id.listViewRssData);
		listViewAdapter = new RssDataAdapter();
		listViewRssData.setAdapter(listViewAdapter);
		imgget = new ImageGetter()
		{

			Drawable placeholder = getResources().getDrawable(R.drawable.empty_placeholder);
			@Override
			public Drawable getDrawable(String source) {
				// TODO Auto-generated method stub
				return placeholder;
			}
			
		};
	}
	

	
	
	
	

	
	protected void refillListView() {
		DataProvider provider = DataProvider.getInstance(getApplication());
		RssRecord[] records = provider.getData();
		listViewAdapter.clear();
		listViewAdapter.addAll(records);
		listViewAdapter.notifyDataSetChanged();
		
	}


	boolean cleanupNeeded=false;
	
	private void cleanup()
	{
		if(cleanupNeeded)
		{
			cleanupNeeded=false;
			unregisterReceiver(dataReadyReceiver);
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(dataReadyReceiver, iFilter);
		cleanupNeeded=true;
		refillListView();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		cleanup();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();	
	}
	


}
