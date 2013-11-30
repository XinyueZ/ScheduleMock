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
	private DatabaseHelper mHelper;

	public AppDB(Context _context) {
		mHelper = new DatabaseHelper(_context);
	}

	public synchronized boolean insertHistory(String _latlng, String _name) {
		long rowId = -1;
		SQLiteDatabase DB = mHelper.getWritableDatabase();
		Cursor c = null;
		try {
			assert DB != null;
			c = DB.rawQuery(TblHistory.STMT_SELECT_BY_LATLNG, new String[] { _latlng });
			if (c.getCount() < 1) {
				SQLiteStatement stm = DB.compileStatement(TblHistory.STMT_INSERT);
				assert stm != null;
				stm.bindString(1, _latlng);
				stm.bindString(2, _name);
				stm.bindLong(3, System.currentTimeMillis());
				rowId = stm.executeInsert();
				stm.clearBindings();
				stm.releaseReference();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			assert c != null && DB != null;
			c.close();
			DB.close();
		}
		return rowId != -1;
	}

	public synchronized List<DOHistoryRecorder> getHistoryList() {
		return getList(-1);
	}

	public synchronized boolean deleteHistoryByLatLng(String _latlng) {
		int count = 0;
		SQLiteDatabase DB = mHelper.getWritableDatabase();
		if (!TextUtils.isEmpty(_latlng)) {
			Cursor c = null;
			try {
				assert DB != null;
				c = DB.rawQuery(TblHistory.STMT_SELECT_BY_LATLNG, new String[] { _latlng });
				if (c.getCount() > 0) {
					count = DB.delete(TblHistory.TABLE_NAME, TblHistory.COL_LATLNG + "=?", new String[] { _latlng });
				}
				c.close();
			} catch (Exception _e) {
				_e.printStackTrace();
			} finally {
				assert c != null && DB != null;
				c.close();
				DB.close();
			}
		}
		return count > 0;
	}

	public List<DOHistoryRecorder> getHistoryList(int _count) {
		return getList(_count);
	}

	private synchronized  List<DOHistoryRecorder> getList(int _count) {
		List<DOHistoryRecorder> results = null;
		SQLiteDatabase DB = mHelper.getReadableDatabase();
		Cursor c = null;
		try {
			assert DB != null;
			if (_count > 0) {// 1, 2, 3, 4, 5......
				c = DB.rawQuery(TblHistory.STMT_SELECT_LAST_COUNT + _count, null);
			} else {// All
				c = DB.rawQuery(TblHistory.STMT_SELECT_ALL, null);
			}
			int count = c.getCount();
			results = new ArrayList<DOHistoryRecorder>(count);
			DOHistoryRecorder item;
			while (c.moveToNext()) {
				item = new DOHistoryRecorder(c.getString(c.getColumnIndex(TblHistory.COL_LATLNG)), c.getString(c
						.getColumnIndex(TblHistory.COL_NAME)), c.getLong(c.getColumnIndex(TblHistory.COL_CREATE_TIME)));
				results.add(item);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			assert c != null && DB != null;
			c.close();
			DB.close();
		}
		return results;
	}

}
