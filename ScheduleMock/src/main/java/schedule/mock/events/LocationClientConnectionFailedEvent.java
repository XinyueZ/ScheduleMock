package schedule.mock.events;


import com.google.android.gms.common.ConnectionResult;

public final class LocationClientConnectionFailedEvent {
	private ConnectionResult mConnectionResult;

	public LocationClientConnectionFailedEvent(ConnectionResult _connectionResult) {
		mConnectionResult = _connectionResult;
	}

	public ConnectionResult getConnectionResult() {
		return mConnectionResult;
	}
}
