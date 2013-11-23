package schedule.mock.tasks.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.db.AppDB;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.utils.BusProvider;

public abstract class LoadHistoryTask extends AsyncTask<AppDB, List<DOHistoryRecorder>, List<DOHistoryRecorder>> {
	private Context mContext;

	protected LoadHistoryTask(Context _context) {
		mContext = _context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		BusProvider.getBus().post(new UIShowLoadingEvent(getClass()));
	}

	@Override
	protected List<DOHistoryRecorder> doInBackground(AppDB... _params) {
		AppDB db = _params[0];
		return db.getHistoryList();
	}

	@Override
	protected void onPostExecute(List<DOHistoryRecorder> _historyRecorders) {
		if (_historyRecorders != null) {
			onShowList(mContext, _historyRecorders);
			super.onPostExecute(_historyRecorders);
			BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
		}
	}

	protected abstract void onShowList(Context _context, List<DOHistoryRecorder> _historyRecorders);
}
