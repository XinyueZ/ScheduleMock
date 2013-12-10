package schedule.mock.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class Utils {

	public static String encodedKeywords(String _keywords) {
		try {
			return URLEncoder.encode(_keywords, "UTF-8");
		} catch (UnsupportedEncodingException _e1) {
			return new String(_keywords.trim().replace(" ", "%20").replace("&", "%26").replace(",", "%2c")
					.replace("(", "%28").replace(")", "%29").replace("!", "%21").replace("=", "%3D")
					.replace("<", "%3C").replace(">", "%3E").replace("#", "%23").replace("$", "%24")
					.replace("'", "%27").replace("*", "%2A").replace("-", "%2D").replace(".", "%2E")
					.replace("/", "%2F").replace(":", "%3A").replace(";", "%3B").replace("?", "%3F")
					.replace("@", "%40").replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
					.replace("_", "%5F").replace("`", "%60").replace("{", "%7B").replace("|", "%7C")
					.replace("}", "%7D"));
		}
	}

	public static void showLongToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
	}

	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider _provider,
			String _subject, String _body) {
		if (_provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, _subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, _body);
			_provider.setShareIntent(i);
			return i;
		}
		return null;
	}

}
