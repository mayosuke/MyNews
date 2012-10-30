package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class CategoryListFragment extends ListFragment {
    private static final String TAG = CategoryListFragment.class.getSimpleName();

    private final OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.i(TAG, "onQueryTextSubmit():query=" + query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.i(TAG, "onQueryTextChange():newText=" + newText);
            return true;
        }
    };

    public CategoryListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        final ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Constants.CATEGORIES);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.category_list, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(mQueryTextListener);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick(position=" + position + ",id=" + id + ")");

        final Intent intent = new Intent(getActivity(), NewsListActivity.class);
        intent.putExtra(Constants.TAG_NEWS_CATEGORY_ID, position);
        startActivity(intent);
    }
}
