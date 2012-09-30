package jp.mayosuke.android.mynews;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_CATEGORY_LIST) == null) {
            final Fragment categoryList = new CategoryListFragment();
            getFragmentManager().beginTransaction().
                    add(android.R.id.content, categoryList, Constants.TAG_NEWS_CATEGORY_LIST).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
