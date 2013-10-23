package schedule.mock.prefs;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Preference storage
 * 
 * @author chris
 * 
 */
public class BasicPrefs {

	protected SharedPreferences preference = null;
	protected Context mContext;


	public BasicPrefs(Context _cxt) {
		mContext = _cxt;
		preference = _cxt.getSharedPreferences(getClass().getPackage().toString(), Context.MODE_PRIVATE);
	}


	public String getString(String key, String defValue) {
		return preference.getString(key, defValue);
	}


	public boolean setString(String key, String value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putString(key, value);
		return edit.commit();
	}


	public boolean getBoolean(String key, boolean defValue) {
		return preference.getBoolean(key, defValue);
	}


	public boolean setBoolean(String key, boolean value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putBoolean(key, value);
		return edit.commit();
	}


	public int getInt(String key, int defValue) {
		return preference.getInt(key, defValue);
	}


	public boolean setInt(String key, int value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putInt(key, value);
		return edit.commit();
	}


	public long getLong(String key, long defValue) {
		return preference.getLong(key, defValue);
	}


	public boolean setLong(String key, long value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putLong(key, value);
		return edit.commit();
	}


	public float getFloat(String key, float defValue) {
		return preference.getFloat(key, defValue);
	}


	public boolean setFloat(String key, float value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putFloat(key, value);
		return edit.commit();
	}


	public boolean contains(String key) {
		return preference.contains(key);
	}
}
