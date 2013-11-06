package schedule.mock.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DONearByResult;
import schedule.mock.events.UIShowNetworkImageEvent;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.DisplayUtil;
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
		final DONearByResult nearByResult = mNearByResults[_position];
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
					nearByResult.getGeometry().getLocation().getLatitude(), nearByResult.getGeometry()
							.getLocation().getLongitude(), "80x80", "13", "A", nearByResult.getGeometry()
							.getLocation().getLatitude(), nearByResult.getGeometry().getLocation()
							.getLongitude());
			LL.d("Preview:" + url);
			holder.PlacePreview.setImageUrl(url, TaskHelper.getImageLoader());
			holder.PlacePreview.setVisibility(View.VISIBLE);
			holder.PlacePreview.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DisplayMetrics displayMetrics = DisplayUtil.getDisplayMetrics(mContext);
					BusProvider.getBus().post(
							new UIShowNetworkImageEvent(String.format(App.API_STATIC_MAP, nearByResult.getGeometry()
									.getLocation().getLatitude(), nearByResult.getGeometry().getLocation()
									.getLongitude(), displayMetrics.widthPixels + "x" + displayMetrics.heightPixels,"17",
									"A", nearByResult.getGeometry().getLocation().getLatitude(), nearByResult
											.getGeometry().getLocation().getLongitude())));
				}
			});
		} catch (NullPointerException e1) {
			holder.PlacePreview.setVisibility(View.GONE);
		}
		try {
			holder.Geolocation.setVisibility(View.VISIBLE);
			holder.Geolocation.setText(new StringBuilder()
					.append(nearByResult.getGeometry().getLocation().getLatitude()).append(',')
					.append(nearByResult.getGeometry().getLocation().getLongitude()).toString());
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
