package schedule.mock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.StartLocationTrackingEvent;
import schedule.mock.events.StopLocationTrackingEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.fragments.HomeFragment;
import schedule.mock.services.StartLocationTrackingService;
import schedule.mock.views.AnimiImageView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;


public final class MainActivity extends BaseActivity implements
		uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener, View.OnClickListener {

	public static final int LAYOUT = R.layout.activity_main;
	private PullToRefreshAttacher mPullToRefreshAttacher;


	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(LAYOUT);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		setRefreshableView(this);
		if (_savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(App.MAIN_CONTAINER, HomeFragment.newInstance(getApplicationContext())).commit();
		}
		View customView = getSupportActionBar().getCustomView();
		View btnLocationTracking = customView.findViewById(R.id.btn_location_tracking);
		btnLocationTracking.setOnClickListener(this);
	}


	@Override
	protected void onDestroy() {
		mPullToRefreshAttacher = null;
		super.onDestroy();
	}


	@Subscribe
	public void onUIShowLoadingEvent(UIShowLoadingEvent _event) {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshing(true);
		}
	}


	@Subscribe
	public void onUIShowLoadingCompleteEvent(UIShowLoadingCompleteEvent _event) {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshComplete();
		}
	}


	public void setRefreshableView(
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener _refreshListener) {
		View view = findViewById(App.MAIN_CONTAINER);
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.addRefreshableView(view, _refreshListener);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, _menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem _item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (_item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(_item);
	}


	@Override
	public void onRefreshStarted(View _view) {
	}


	@Override
	public void onClick(View _v) {
		((AnimiImageView) _v).toggle();
	}


	@Subscribe
	public void onStartLocationTracking(StartLocationTrackingEvent _e) {
		startService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
	}


	@Subscribe
	public void onStopLocationTracking(StopLocationTrackingEvent _e) {
		stopService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
	}


	@Subscribe
	public void onServiceLocationChanged(ServiceLocationChangedEvent _e) {
		View customView = getSupportActionBar().getCustomView();
		TextView currentLoction = (TextView) customView.findViewById(R.id.tv_current_location);
		double lat = _e.getLocation().getLatitude();
		double lng = _e.getLocation().getLongitude();
		currentLoction.setText(lat + "," + lng);
	}
}
