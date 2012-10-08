package jp.mayosuke.android.mynews;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.io.Serializable;

public class NewsDetailActivity extends Activity {
    private static final String TAG = NewsDetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_DETAIL) == null) {
            Log.i(TAG, "  newsList fragment is already created.");
            final NewsDetailFragment newsList = new NewsDetailFragment();
            final Bundle args = new Bundle();
            args.putSerializable(Constants.TAG_NEWS_CATEGORY_ID, getIntent().getIntExtra(Constants.TAG_NEWS_CATEGORY_ID, -1));
            args.putSerializable(Constants.TAG_NEWS_ITEM, (Serializable) getIntent().getSerializableExtra(Constants.TAG_NEWS_ITEM));
            newsList.setArguments(args);
            getFragmentManager().beginTransaction().add(android.R.id.content, newsList, Constants.TAG_NEWS_DETAIL).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
