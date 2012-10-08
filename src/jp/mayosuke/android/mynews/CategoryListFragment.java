package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CategoryListFragment extends ListFragment {
    private static final String TAG = CategoryListFragment.class.getSimpleName();

    public CategoryListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
        super.onActivityCreated(savedInstanceState);

        final ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Constants.CATEGORIES);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick(position=" + position + ",id=" + id + ")");

        final Intent intent = new Intent(getActivity(), NewsListActivity.class);
        intent.putExtra(Constants.TAG_NEWS_CATEGORY_ID, position);
        startActivity(intent);
    }
}
