package schedule.mock.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.db.tables.TblHistory;

public final class AppDB {

	private static volatile AppDB sInstance;
	private final SQLiteDatabase mDB;
	private final Context mContext;

	private AppDB(Context _context) {
		DatabaseHelper dh = new DatabaseHelper(_context);
		mDB = dh.getReadableDatabase();
		mContext = _context;
	}

	public synchronized static AppDB getInstance(Context _context) {
		if (sInstance == null) {
			sInstance = new AppDB(_context);
		}
		return sInstance;
	}

	public synchronized boolean insertHistory(String _latlng, String _name) {
		long rowId = -1;
		Cursor c = mDB.rawQuery(TblHistory.STMT_SELECT_BY_LATLNG, new String[] { _latlng });
		if (c.getCount() < 1) {
			SQLiteStatement stm = mDB.compileStatement(TblHistory.STMT_INSERT);
			stm.bindString(1, _latlng);
			stm.bindString(2, _name);
			stm.bindLong(3, System.currentTimeMillis());
			rowId = stm.executeInsert();
			stm.clearBindings();
			stm.releaseReference();
		}
		c.close();
		return rowId != -1;
	}

	public synchronized List<DOHistoryRecorder> getHistoryList() {
		List<DOHistoryRecorder> results = null;
		Cursor c = null;
		try {
			c = mDB.rawQuery(TblHistory.STMT_SELECT_ALL, null);
			int count = c.getCount();
			results = new ArrayList<DOHistoryRecorder>(count);
			DOHistoryRecorder item = null;
			while (c.moveToNext()) {
				item = new DOHistoryRecorder(c.getString(c.getColumnIndex(TblHistory.COL_LATLNG)), c.getString(c
						.getColumnIndex(TblHistory.COL_NAME)), c.getLong(c.getColumnIndex(TblHistory.COL_CREATE_TIME)));
				results.add(item);
			}
		} catch (Exception _e) {
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return results;
	}

	public synchronized boolean deleteHistoryByLatLng(String _latlng) {
		int count = 0;
		if (!TextUtils.isEmpty(_latlng)) {
			Cursor c = mDB.rawQuery(TblHistory.STMT_SELECT_BY_LATLNG, new String[] { _latlng });
			if (c.getCount() > 0) {
				count = mDB.delete(TblHistory.TABLE_NAME, TblHistory.COL_LATLNG + "=?", new String[] { _latlng });
			}
			c.close();
		}
		return count > 0;
	}
}
