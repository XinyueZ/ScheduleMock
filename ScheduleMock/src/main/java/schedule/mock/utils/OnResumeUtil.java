package schedule.mock.utils;

import android.support.v4.app.FragmentActivity;

import schedule.mock.fragments.dialog.AskOpeningLoctionSystemDialogFragment;
import schedule.mock.fragments.dialog.AskOpeningMockLoctionSystemDialogFragment;

public final class OnResumeUtil {


	public static void onResume(FragmentActivity _activity) {
		if( !AskOpeningLoctionSystemDialogFragment.showInstance(_activity) ) {
			if( !AskOpeningMockLoctionSystemDialogFragment.showInstance(_activity)) {

			}
		}
	}

}
