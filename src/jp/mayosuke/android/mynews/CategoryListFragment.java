package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class CategoryListFragment extends ListFragment {
    private static final String TAG = CategoryListFragment.class.getSimpleName();

    private final OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {
        // onQueryTextSubmit()が二回連続で呼ばれるため、二回目に呼ばれた時にstartActivity()を行わないためのフラグ。
        private boolean mIsQueryOngoing = false;
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.i(TAG, "onQueryTextSubmit():query=" + query);
            if (mIsQueryOngoing) {
                mIsQueryOngoing = false;
                return true;
            }
            mIsQueryOngoing = true;
            final Intent intent = new Intent(getActivity(), NewsListActivity.class);
            intent.putExtra(Constants.TAG_NEWS_CATEGORY_ID, -1);
            intent.putExtra(Constants.TAG_NEWS_KEYWORD, query);
            startActivity(intent);

            mAdapter.add(query);

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.i(TAG, "onQueryTextChange():newText=" + newText);
            return true;
        }
    };

    private final Callback mActionModeCallback = new Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.i(TAG, "onCreateActionMode()");
            mode.getMenuInflater().inflate(R.menu.category_list_edit, menu);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.i(TAG, "onPrepareActionMode()");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.i(TAG, "onActionItemClicked()");
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.i(TAG, "onDestroyActionMode()");
            getListView().clearChoices();
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
        }
    };

    private ArrayAdapter<String> mAdapter;

    public CategoryListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1);
        mAdapter.addAll(Constants.CATEGORIES);
        setListAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.category_list, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(mQueryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_edit:
            getActivity().startActionMode(mActionModeCallback);
            return true;
        default:
            return false;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick(position=" + position + ",id=" + id + ")");

        if (getListView().getChoiceMode() != ListView.CHOICE_MODE_NONE) {
            return;
        }

        final Intent intent = new Intent(getActivity(), NewsListActivity.class);
        intent.putExtra(Constants.TAG_NEWS_CATEGORY_ID, position);
        startActivity(intent);
    }
}
