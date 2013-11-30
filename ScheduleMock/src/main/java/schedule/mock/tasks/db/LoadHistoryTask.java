package schedule.mock.tasks.db;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.util.List;

import schedule.mock.App;
import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.prefs.Prefs;
import schedule.mock.utils.BusProvider;

public abstract class LoadHistoryTask extends AsyncTask<Void, List<DOHistoryRecorder>, List<DOHistoryRecorder>> {
	private Context mContext;
	private boolean mShowLastRows;

	public LoadHistoryTask(Context _context) {
		mContext = _context;
	}

	public LoadHistoryTask(Context _context, boolean _showLastRows) {
		mContext = _context;
		mShowLastRows = _showLastRows;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		BusProvider.getBus().post(new UIShowLoadingEvent(getClass()));
	}

	@Override
	protected List<DOHistoryRecorder> doInBackground(Void ... _params) {
		if(!mShowLastRows) {
			return App.getDB().getHistoryList();
		} else {
			return App.getDB().getHistoryList(Prefs.getInstance().getLastRowsCount());
		}
	}

	@Override
	protected void onPostExecute(List<DOHistoryRecorder> _historyRecorders) {
		if (_historyRecorders != null) {
			onShowList(mContext, _historyRecorders);
			super.onPostExecute(_historyRecorders);
		}
		BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
	}

	public void exec(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		} else {
			execute();
		}
	}

	protected abstract void onShowList(Context _context, List<DOHistoryRecorder> _historyRecorders);
}
