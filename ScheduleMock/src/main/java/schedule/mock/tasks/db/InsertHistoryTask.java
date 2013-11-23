package schedule.mock.tasks.db;


import android.os.AsyncTask;
import android.os.Build;

import schedule.mock.App;

public final class InsertHistoryTask extends AsyncTask<Void, Boolean, Boolean>{
	private String mLatLng;
	private String mName;

	public InsertHistoryTask(String _latLng, String _name) {
		mLatLng = _latLng;
		mName = _name;
	}

	@Override
	protected Boolean doInBackground(Void... _params) {
		return App.getDB().insertHistory(mLatLng, mName);
	}

	public void exec(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		} else {
			execute();
		}
	}
}
