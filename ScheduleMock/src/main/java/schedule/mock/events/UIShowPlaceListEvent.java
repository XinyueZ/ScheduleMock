package schedule.mock.events;

import schedule.mock.data.DONearByResult;


public final class UIShowPlaceListEvent {

	private DONearByResult[] mNearByResults;


	public UIShowPlaceListEvent(DONearByResult[] _nearByResults) {
		mNearByResults = _nearByResults;
	}


	public DONearByResult[] getNearByResults() {
		return mNearByResults;
	}
}
