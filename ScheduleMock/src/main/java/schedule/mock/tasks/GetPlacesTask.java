package schedule.mock.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import schedule.mock.App;
import schedule.mock.data.DOPlace;


public class GetPlacesTask extends AsyncTask<Void, Void, List<DOPlace>> {

	private Context mContext;
	private String mName;


	public GetPlacesTask(Context _context, String _name) {
		mContext = _context;
		mName = _name;
	}


	@Override
	protected List<DOPlace> doInBackground(Void... _params) {
		Geocoder geocoder = new Geocoder(mContext);
		List<DOPlace> placeList = new ArrayList<DOPlace>();
		try {
			List<Address> addresses = geocoder.getFromLocationName(mName, App.COUNT_GET_ADDRESS);
			for(Address address : addresses) {
				placeList.add(new DOPlace(mName, address.getLatitude(), address.getLongitude()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return placeList;
	}
}
