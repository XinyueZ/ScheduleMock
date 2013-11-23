package schedule.mock.tasks.net;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import schedule.mock.events.TaskErrorEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;

public final class GsonRequestTask<T> extends Request<T> {

	public static final String TAG = "GsonRequestTask";
	protected static final String COOKIE_KEY = "Cookie";
	private final Listener<T> mSuccessListener = new Listener<T>() {
		@Override
		public void onResponse(T _response) {
			BusProvider.getBus().post(_response);
			BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
		}
	};
	private static final ErrorListener sErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError _error) {
			logError(_error);
			BusProvider.getBus().post(new TaskErrorEvent(_error));
			BusProvider.getBus().post(new UIShowLoadingCompleteEvent());
		}
	};
	protected final Context mContext;
	private final Class<T> mClazz;


	public GsonRequestTask(Context _context, int _method, String _url, Class<T> _clazz) {
		super(_method, _url, sErrorListener);
		LL.d("Call: " + _url);
		setShouldCache(true);
		setTag(TAG);
		this.mClazz = _clazz;
		mContext = _context;
	}


	private static void logError(VolleyError _error) {
		NetworkResponse response = _error.networkResponse;
		Map<String, String> headers = response.headers;
		if (headers != null && headers.size() > 0) {
			Set<String> keys = headers.keySet();
			LL.e(new StringBuilder().append("Status ").append(response.statusCode).toString());
			for (String key : keys) {
				LL.e(key + " ==> "+ headers.get(key));
			}
		}
	}


	@Override
	protected void deliverResponse(T _response) {
		mSuccessListener.onResponse(_response);
	}


	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse _response) {
		try {
			String json = new String(_response.data, HttpHeaderParser.parseCharset(_response.headers));
			return Response.success(TaskHelper.getGson().fromJson(json, mClazz),
					HttpHeaderParser.parseCacheHeaders(_response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}


	public void execute() {
		BusProvider.getBus().post(new UIShowLoadingEvent(mClazz));
		RequestQueue queue = TaskHelper.getRequestQueue();
		queue.add(this);
	}
}
