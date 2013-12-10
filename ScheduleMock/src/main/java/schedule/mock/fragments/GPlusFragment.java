package schedule.mock.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.otto.Subscribe;

import schedule.mock.R;
import schedule.mock.events.GPlusConnectionEvent;
import schedule.mock.events.GPlusInitEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.interfaces.IGooglePlusClient;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public final class GPlusFragment extends BaseFragment implements View.OnClickListener,
		PlusClient.OnAccessRevokedListener {
	public static final String TAG = GPlusFragment.class.getName();
	public static final int LAYOUT = R.layout.fragment_gplus;
	public static final int MENU_POSITION = 5;
	private PlusClient mPlusClient;

	public static GPlusFragment newInstance(Context _context) {
		return (GPlusFragment) GPlusFragment.instantiate(_context, GPlusFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			View login = _view.findViewById(R.id.btn_gplus_signin);
			login.setOnClickListener(this);
			View logout = _view.findViewById(R.id.btn_gplus_signout);
			logout.setOnClickListener(this);
			updateUI(_view);
		}
	}

	@Override
	public int getMenuItemPosition() {
		return MENU_POSITION;
	}

	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
		case R.id.btn_gplus_signin:
			_v.setEnabled(false);
			BusProvider.getBus().post(new UIShowLoadingEvent(GPlusFragment.class));
			/* Sig-in google plus */
			signIn();
			break;
		case R.id.btn_gplus_signout:
			BusProvider.getBus().post(new UIShowLoadingEvent(GPlusFragment.class));
			/* Sig-out from google plus */
			signOut();
			break;
		}

	}

	public void signOut() {
		if (mPlusClient != null && mPlusClient.isConnected()) {
			mPlusClient.revokeAccessAndDisconnect(this);
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			BusProvider.getBus().post(new GPlusConnectionEvent(mPlusClient));
		}
	}

	public void signIn() {
		if(mPlusClient != null ) {
			mPlusClient.connect();
		} else {
			BusProvider.getBus().post(new GPlusInitEvent());
		}
	}

	@Override
	public void onAttach(Activity _activity) {
		super.onAttach(_activity);
		if (_activity instanceof IGooglePlusClient) {
			IGooglePlusClient iGooglePlusClient = (IGooglePlusClient) _activity;
			mPlusClient = iGooglePlusClient.getPlusClient();
		}
	}

	private void updateUI(View _v) {
		SignInButton signInButton = (SignInButton) _v.findViewById(R.id.btn_gplus_signin);
		Button signOutButton = (Button) _v.findViewById(R.id.btn_gplus_signout);
		NetworkImageView personPhoto = (NetworkImageView) _v.findViewById(R.id.iv_gplus_person_photo);
		TextView name = (TextView) _v.findViewById(R.id.tv_gplus_name);
		if (mPlusClient != null && mPlusClient.isConnected()) {
			signInButton.setVisibility(View.INVISIBLE);
			signOutButton.setVisibility(View.VISIBLE);
			personPhoto.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			Person person = mPlusClient.getCurrentPerson();
			/*Show personal information*/
			if (person != null) {
				/*Photo*/
				if (person.getImage() != null) {
					Person.Image image = person.getImage();
					personPhoto.setImageUrl(image.getUrl(), TaskHelper.getImageLoader());
				}
				/*
				 * Show user name
				 * 
				 * Try Display name -> Nickname -> User name(formatted)
				 */
				String n = null;
				if (!TextUtils.isEmpty(person.getDisplayName())) {
					n = String.format(getString(R.string.label_hi_gplus), person.getDisplayName());
				} else {
					if (!TextUtils.isEmpty(person.getNickname())) {
						n = String.format(getString(R.string.label_hi_gplus), person.getNickname());
					} else {
						if (person.getName() != null) {
							Person.Name pn = person.getName();
							n = String.format(getString(R.string.label_hi_gplus), pn.getFormatted());
						}
					}
				}
				if (!TextUtils.isEmpty(n)) {
					name.setText(n);
				} else {
					name.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			signInButton.setVisibility(View.VISIBLE);
			signInButton.setEnabled(true);
			signOutButton.setVisibility(View.INVISIBLE);
			personPhoto.setVisibility(View.INVISIBLE);
			name.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAccessRevoked(ConnectionResult _status) {
		BusProvider.getBus().post(new GPlusConnectionEvent(mPlusClient));
	}

	@Subscribe
	public void onGPlusConnection(GPlusConnectionEvent _e) {
		mPlusClient = _e.getPlusClient();
		View view = getView();
		if (view != null) {
			updateUI(view);
		}
		BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
	}
}
