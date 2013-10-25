package schedule.mock.utils.net;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;


public abstract class AbstractGsonRequest<T> extends Request<T> {

	protected static final String COOKIE_KEY = "Cookie";
	protected final Context mContext;
	private final Class<T> mClazz;
	private final Listener<T> mListener;


	public AbstractGsonRequest(Context _context, int method, String url, Class<T> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		mContext = _context;
	}


	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}


	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(TaskHelper.getGson().fromJson(json, mClazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}


	public void execute() {
		TaskHelper.getRequestQueue().add(this);
	}
}
