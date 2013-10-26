package schedule.mock.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DONearByResult;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.LL;


public final class PlaceListAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.entry_nearby_place;
	private Context mContext;
	private DONearByResult[] mNearByResults;


	public PlaceListAdapter(Context _context, DONearByResult[] _nearByResults) {
		mContext = _context;
		mNearByResults = _nearByResults;
	}


	@Override
	public int getCount() {
		return mNearByResults.length;
	}


	@Override
	public Object getItem(int _position) {
		return mNearByResults[_position];
	}


	@Override
	public long getItemId(int _position) {
		return _position;
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _viewGroup) {
		ViewHolder holder;
		DONearByResult nearByResult = mNearByResults[_position];
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
			holder = new ViewHolder((NetworkImageView) _convertView.findViewById(R.id.iv_place_icon),
					(TextView) _convertView.findViewById(R.id.tv_place_name),
					(TextView) _convertView.findViewById(R.id.tv_geolocation),
					(NetworkImageView) _convertView.findViewById(R.id.iv_place_preview));
			_convertView.setTag(holder);
		} else {
			holder = (ViewHolder) _convertView.getTag();
		}
		holder.PlaceIcon.setImageUrl(nearByResult.getIcon(), TaskHelper.getImageLoader());
		holder.PlaceName.setText(nearByResult.getName());
		try {
			String url = String.format(App.API_STATIC_MAP,
					nearByResult.getGeometry().getNearByLocation().getLatitude(), nearByResult.getGeometry()
							.getNearByLocation().getLongitude(), "A", nearByResult.getGeometry().getNearByLocation().getLatitude(), nearByResult.getGeometry()
					.getNearByLocation().getLongitude());
			LL.d("Preview:" + url);
			holder.PlacePreview.setImageUrl(url, TaskHelper.getImageLoader());
		} catch (NullPointerException e1) {
		}
		try {
			holder.Geolocation.setVisibility(View.VISIBLE);
			holder.Geolocation.setText(new StringBuilder()
					.append(nearByResult.getGeometry().getNearByLocation().getLatitude()).append(',')
					.append(nearByResult.getGeometry().getNearByLocation().getLongitude()).toString());
		} catch (NullPointerException e2) {
			holder.Geolocation.setVisibility(View.GONE);
		}
		return _convertView;
	}


	static class ViewHolder {

		NetworkImageView PlaceIcon;
		TextView PlaceName;
		TextView Geolocation;
		NetworkImageView PlacePreview;


		ViewHolder(NetworkImageView _placeIcon, TextView _placeName, TextView _geolocation,
				NetworkImageView _placePreview) {
			PlaceIcon = _placeIcon;
			PlaceName = _placeName;
			Geolocation = _geolocation;
			PlacePreview = _placePreview;
		}
	}
}