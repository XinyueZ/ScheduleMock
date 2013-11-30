package schedule.mock.tasks.db;

import android.os.AsyncTask;
import android.os.Build;

import schedule.mock.App;

public abstract class DeleteHistoryTask extends AsyncTask<Void, Boolean, Boolean> {
	private String mLatLng;

	public DeleteHistoryTask(String _latLng) {
		mLatLng = _latLng; 
	}

	@Override
	protected Boolean doInBackground(Void... _params) {
		return App.getDB().deleteHistoryByLatLng(mLatLng);
	}

	@Override
	protected void onPostExecute(Boolean _result) {
		if(_result) {
			onHistoryDeleted();
		}
	}

	protected abstract void onHistoryDeleted();

	public void exec(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		} else {
			execute();
		}
	}

}