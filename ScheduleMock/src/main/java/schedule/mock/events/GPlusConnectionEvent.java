package schedule.mock.events;

import com.google.android.gms.plus.PlusClient;

public final class GPlusConnectionEvent {
	private PlusClient mPlusClient;

	public GPlusConnectionEvent(PlusClient _plusClient ) {
		mPlusClient = _plusClient;

	}

	public PlusClient getPlusClient() {
		return mPlusClient;
	}




}
