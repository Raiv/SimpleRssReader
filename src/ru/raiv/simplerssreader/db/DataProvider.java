package ru.raiv.simplerssreader.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// Yes I know about ContentProviders, but here they'll be unnecessary complicated.
public class DataProvider {

	static final String TABLE_NAME = "rssCache";
	static final String C_GUID = "GUID";
	static final String C_TITLE = "TITLE";
	static final String C_DESCRIPTION = "DESCRIPTION";
	static final String C_LINK = "LINK";
	static final String C_TIME = "TIME";

	private static DataProvider instance = null;

	public static DataProvider getInstance(Application app) {
		if (instance == null) {
			instance = new DataProvider(app);
		}
		return instance;
	}

	private Application context;
	private DbOpenHelper dbHelper;

	private DataProvider(Application app) {
		context = app;
		dbHelper = new DbOpenHelper(context);
	}

	public synchronized void refresh(RssRecord[] data) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		db.beginTransaction();
		try {

			db.delete(TABLE_NAME, null, null);
			if (data != null) {
				for (RssRecord r : data) {
					cv.put(C_TITLE, r.getTitle());
					cv.put(C_DESCRIPTION, r.getDescription());
					cv.put(C_LINK, r.getLink());
					cv.put(C_GUID, r.getGuid());
					cv.put(C_TIME, r.getTime());
					db.insert(TABLE_NAME, null, cv);
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public synchronized RssRecord[] getData() {
		RssRecord[] data = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			Cursor c = db.query(TABLE_NAME, new String[] { C_GUID, C_TITLE,
					C_DESCRIPTION, C_LINK, C_TIME }, null, null, null, null,
					C_TIME + " DESC", null);
			data = new RssRecord[c.getCount()];
			int current = 0;

			int guid_idx = c.getColumnIndexOrThrow(C_GUID);
			int title_idx = c.getColumnIndexOrThrow(C_TITLE);
			int desc_idx = c.getColumnIndexOrThrow(C_DESCRIPTION);
			int link_idx = c.getColumnIndexOrThrow(C_LINK);
			int time_idx = c.getColumnIndexOrThrow(C_TIME);

			while (c.moveToNext()) {
				RssRecord row = new RssRecord();
				row.setGuid(c.getString(guid_idx));
				row.setTitle(c.getString(title_idx));
				row.setDescription(c.getString(desc_idx));
				row.setLink(c.getString(link_idx));
				row.setTime(c.getLong(time_idx));
				data[current] = row;
				current++;
			}
			c.close();

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return data;

	}
}
