package schedule.mock.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import schedule.mock.R;
import schedule.mock.utils.LL;


public final class AnimiImageView extends ImageView {

	Task mTask;
	boolean mIsAnim;


	public AnimiImageView(Context _context, AttributeSet _attrs, int _defStyle) {
		super(_context, _attrs, _defStyle);
		init(_context);
	}


	public AnimiImageView(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
		init(_context);
	}


	public AnimiImageView(Context _context) {
		super(_context);
		init(_context);
	}


	private void init(Context _cxt) {
		mTask = new Task(this);
	}


	public void startAnim() {
		if (!mIsAnim) {
			post(mTask);
			mIsAnim = true;
			LL.d("Started Task runs");
		}
	}


	public void stopAnim() {
		if (mIsAnim) {
			mTask.stopAnim();
			removeCallbacks(mTask);
			mIsAnim = false;
			LL.d("Removed Task runs");
		}
	}


	public void toggle() {
		if (!mIsAnim) {
			startAnim();
		} else {
			stopAnim();
		}
	}


	static class Task implements Runnable {

		ImageView mImageView;


		Task(ImageView _imageView) {
			mImageView = _imageView;
		}


		@Override
		public void run() {
			if (mImageView != null) {
				LL.d("ImageView Task runs");
				AnimationDrawable frameAnimation = (AnimationDrawable) mImageView.getDrawable();
				frameAnimation.setCallback(mImageView);
				frameAnimation.setVisible(true, true);
			}
		}

		void stopAnim() {
			if (mImageView != null) {
				LL.d("ImageView Task runs");
				AnimationDrawable frameAnimation = (AnimationDrawable) mImageView.getDrawable();
				frameAnimation.setCallback(null);
				frameAnimation.setVisible(true, false);
				mImageView.setImageResource(R.drawable.anim_radar);
			}
		}
	}
}
