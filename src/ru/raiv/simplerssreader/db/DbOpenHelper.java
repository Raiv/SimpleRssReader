package ru.raiv.simplerssreader.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "rssCache.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = "ru.raiv.simplerssreader.db.DbOpenHelper";

	DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DataProvider.TABLE_NAME + " ("
				
				+ DataProvider.C_GUID + " TEXT PRIMARY KEY,"
				+ DataProvider.C_TITLE + " TEXT,"
				+ DataProvider.C_DESCRIPTION + " TEXT,"
				+ DataProvider.C_LINK + " TEXT,"
				+ DataProvider.C_TIME + " INTEGER" + ");");
		
		db.execSQL("CREATE INDEX IDX_"+DataProvider.TABLE_NAME+DataProvider.C_TIME+" ON " 
				+ DataProvider.TABLE_NAME + " ("+DataProvider.C_TIME+" ); ");
		
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DataProvider.TABLE_NAME);

		onCreate(db);
	}

}
