package schedule.mock.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import schedule.mock.R;
import schedule.mock.enums.MenuItem;

public final class MenuAdapter extends BaseAdapter {
	public static final int LAYOUT = R.layout.entry_menu;
	private Context mContext;
	private MenuItem[] mMenuItems;

	public MenuAdapter(Context _context, MenuItem... _menuItems) {
		mContext = _context;
		mMenuItems = _menuItems;

	}

	@Override
	public int getCount() {
		return mMenuItems.length;
	}

	@Override
	public Object getItem(int _position) {
		return mMenuItems[_position];
	}

	@Override
	public long getItemId(int _position) {
		return _position;
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder holder;
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
			_convertView.setTag(holder = new ViewHolder((ImageView) _convertView.findViewById(R.id.iv_menu_icon),
					(TextView) _convertView.findViewById(R.id.tv_menu_text)));
		} else {
			holder = (ViewHolder) _convertView.getTag();
		}
		MenuItem menuItem = mMenuItems[_position];
		_convertView.setEnabled(menuItem.isEnable());
//		_convertView.setSelected(!menuItem.isEnable());

		holder.Text.setText(getItem(_position).toString());
		return _convertView;
	}

	static class ViewHolder {
		ImageView Icon;
		TextView Text;

		ViewHolder(ImageView _icon, TextView _text) {
			Icon = _icon;
			Text = _text;
		}
	}
}
