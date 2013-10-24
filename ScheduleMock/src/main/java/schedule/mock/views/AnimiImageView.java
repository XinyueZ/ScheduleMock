package schedule.mock.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimiImageView extends ImageView {

    public AnimiImageView(Context _context, AttributeSet _attrs, int _defStyle) {
        super( _context, _attrs, _defStyle );
        init( _context );
    }

    public AnimiImageView(Context _context, AttributeSet _attrs) {
        super( _context, _attrs );
        init( _context );
    }

    public AnimiImageView(Context _context) {
        super( _context );
        init( _context );
    }

    private void init( Context _cxt ) {
        try {
            anim( this );
        }
        catch( Exception _e ) {
        }
        finally {
        }
    }

    private static void anim( final ImageView progress ) {
        progress.post( new Runnable() {
            @Override
            public void run() {
                if( progress != null ) {
                    AnimationDrawable frameAnimation = (AnimationDrawable) progress.getDrawable();
                    frameAnimation.setCallback( progress );
                    frameAnimation.setVisible( true, true );
                }
            }
        } );
    }
}
