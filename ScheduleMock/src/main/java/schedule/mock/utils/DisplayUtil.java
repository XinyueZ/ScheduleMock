package schedule.mock.utils;

import android.content.Context;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.view.Display;


public final class DisplayUtil {

	public static DisplayMetrics getDisplayMetrics(Context _context) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display[] displays = DisplayManagerCompat.getInstance(_context).getDisplays();
		Display display = displays[0];
		display.getMetrics(displaymetrics);
		return displaymetrics;
	}
}
