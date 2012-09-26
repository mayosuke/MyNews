package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView(savedInstanceState=" + savedInstanceState + ")");
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick(position=" + position + ",id=" + id + ")");

        final NewsListFragment newsList;
        if (getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_LIST) == null) {
            Log.i(TAG, "  newsList fragment is already created.");
            newsList = new NewsListFragment();
        } else {
            Log.i(TAG, "  newsList fragment is not created.");
            newsList = (NewsListFragment) getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_LIST);
        }
        final Bundle args = new Bundle();
        args.putInt(Constants.TAG_NEWS_CATEGORY_ID, position);
        newsList.setArguments(args);
        getFragmentManager().beginTransaction().add(android.R.id.content, newsList, Constants.TAG_NEWS_LIST).
                addToBackStack(Constants.TAG_NEWS_LIST).
                commit();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.app_name);
    }
}
