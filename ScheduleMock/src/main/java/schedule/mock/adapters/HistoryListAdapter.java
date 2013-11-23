package schedule.mock.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.events.UIShowNetworkImageEvent;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.DisplayUtil;
import schedule.mock.utils.LL;


public final class HistoryListAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.entry_history_list;
	private Context mContext;
	private List<DOHistoryRecorder> mRecorder;


	public HistoryListAdapter(Context _context, List<DOHistoryRecorder> _recorder) {
		mContext = _context;
		mRecorder = _recorder;
	}


	@Override
	public int getCount() {
		return mRecorder.size();
	}


	@Override
	public Object getItem(int _position) {
		return mRecorder.get(_position);
	}


	@Override
	public long getItemId(int _position) {
		return _position;
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _viewGroup) {
		ViewHolder holder;
		final DOHistoryRecorder nearByResult = mRecorder.get(_position);
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
		holder.PlaceName.setText(nearByResult.getName());

		try {
			String url = String.format(App.API_STATIC_MAP_S, nearByResult.getLatLng(), "80x80", "13", "A", nearByResult.getLatLng());
			LL.d("Preview:" + url);
			holder.PlacePreview.setImageUrl(url, TaskHelper.getImageLoader());
			holder.PlacePreview.setVisibility(View.VISIBLE);
			holder.PlacePreview.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DisplayMetrics displayMetrics = DisplayUtil.getDisplayMetrics(mContext);
					BusProvider.getBus().post(
							new UIShowNetworkImageEvent(String.format(App.API_STATIC_MAP_S, nearByResult.getLatLng(), displayMetrics.widthPixels + "x" + displayMetrics.heightPixels,"17",
									"A", nearByResult.getLatLng())));
				}
			});
		} catch (NullPointerException e1) {
			holder.PlacePreview.setVisibility(View.GONE);
		}

		holder.Geolocation.setText(nearByResult.getLatLng());

		_convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
//				BusProvider.getBus().post(new StartLocationMockTrackingEvent(nearByResult.getName(), nearByResult.getGeometry().getLocation()));
			}
		});
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
			PlaceIcon.setVisibility(View.GONE);
			PlaceName = _placeName;
			Geolocation = _geolocation;
			PlacePreview = _placePreview;
		}
	}
}
