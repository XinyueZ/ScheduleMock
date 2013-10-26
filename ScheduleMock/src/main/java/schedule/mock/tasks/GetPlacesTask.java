package schedule.mock.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.android.volley.Request;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import schedule.mock.App;
import schedule.mock.data.DONearBy;
import schedule.mock.data.DOPlace;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.prefs.Prefs;
import schedule.mock.tasks.net.GsonRequest;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;


public class GetPlacesTask extends AsyncTask<Void, Void, DOPlace> {

	private Context mContext;
	private String mName;


	public GetPlacesTask(Context _context, String _name) {
		mContext = _context;
		mName = _name;
	}


	@Override
	protected void onPreExecute() {
		BusProvider.getBus().post(new UIShowLoadingEvent());
	}


	@Override
	protected DOPlace doInBackground(Void... _params) {
		Geocoder geocoder = new Geocoder(mContext);
		try {
			List<Address> addresses = geocoder.getFromLocationName(mName, App.COUNT_GET_ADDRESS);
			if (addresses != null) {
				Address adr = addresses.get(0);
				DOPlace place = new DOPlace(mName, adr.getLatitude(), adr.getLongitude());
				LL.d("Get an address construct for DOPlace:" + adr.toString());
				LL.d(place.toString());
				return place;
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	protected void onPostExecute(DOPlace _place) {
		if (_place != null) {
			String url = String.format(App.API_NEAR_BY, _place.getLatitude(), _place.getLongitude(), Prefs
					.getInstance().getRadius(), Locale.getDefault().getLanguage());
			BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
			new GsonRequest<DONearBy>(mContext, Request.Method.GET, url, DONearBy.class).execute();
		}
	}
}
