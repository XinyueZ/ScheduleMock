package schedule.mock.events;

import com.android.volley.VolleyError;


public final class TaskErrorEvent {

	private VolleyError mVolleyError;


	public TaskErrorEvent(VolleyError _volleyError) {
		mVolleyError = _volleyError;
	}


	public VolleyError getVolleyError() {
		return mVolleyError;
	}
}
