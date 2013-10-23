package schedule.mock.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import schedule.mock.R;


/**
 * A customized Toast for adding items, delete items.
 * 
 * @author Chris. X.Y Zhao(czhao@cellular.de)
 * */
public final class MyToast extends Toast {

	private static final int LAYOUT = R.layout.my_toast;


	public enum ToastType {
		ADD_EVENT, ADD_ITEM, DELETE_ITEM, LOCATION
	}


	private MyToast(Context _context, ToastType _type) {
		super(_context);
		setGravity(Gravity.CENTER, 0, 0);
		setDuration(Toast.LENGTH_SHORT);
		setView(getLayout(_context, _type));
	}


	public static void showInstance(Context _context, ToastType _type) {
		new MyToast(_context, _type).show();
	}


	private View getLayout(Context _context, ToastType _type) {
		View ret = View.inflate(_context, LAYOUT, null);
		ImageView iv = (ImageView) ret.findViewById(R.id.iv_image);
		int image = -1;
		switch (_type) {
			case LOCATION:
				image = R.drawable.ic_finding_location;
				break;
		}
		if (image != -1 && iv != null) {
			iv.setImageResource(image);
		}
		return ret;
	}
}
