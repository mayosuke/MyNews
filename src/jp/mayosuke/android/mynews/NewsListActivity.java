package jp.mayosuke.android.mynews;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class NewsListActivity extends Activity {
    private static final String TAG = NewsListActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_LIST) == null) {
            Log.i(TAG, "  newsList fragment is already created.");
            final NewsListFragment newsList = new NewsListFragment();
            final Bundle args = new Bundle();
            final int categoryId = getIntent().getIntExtra(Constants.TAG_NEWS_CATEGORY_ID, -1);
            args.putInt(Constants.TAG_NEWS_CATEGORY_ID, categoryId);
            args.putString(Constants.TAG_NEWS_KEYWORD, getIntent().getStringExtra(Constants.TAG_NEWS_KEYWORD));
            newsList.setArguments(args);
            getFragmentManager().beginTransaction().add(android.R.id.content, newsList, Constants.TAG_NEWS_LIST).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
