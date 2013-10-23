package schedule.mock.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.fragments.HomeFragment;

public final class MainActivity extends BaseActivity {

	public static final int LAYOUT = R.layout.activity_main;

	@Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(LAYOUT);

        if (_savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(App.MAIN_CONTAINER, HomeFragment.newInstance(getApplicationContext()))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (_item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(_item);
    }



}
