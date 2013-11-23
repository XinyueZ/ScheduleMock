package schedule.mock.tasks.db;


import android.os.AsyncTask;

import schedule.mock.db.AppDB;

public final class InsertHistoryTask extends AsyncTask<AppDB, Void, Void>{
	private String mLatLng;
	private String mName;

	public InsertHistoryTask(String _latLng, String _name) {
		mLatLng = _latLng;
		mName = _name;
	}

	@Override
	protected Void doInBackground(AppDB... _params) {
		AppDB db = _params[0];
		db.insertHistory(mLatLng, mName);
		db = null;
		return null;
	}
}
