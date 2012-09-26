package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class NewsListFragment extends ListFragment {
    private static final String TAG = NewsListFragment.class.getSimpleName();

    private final GoogleNews mNews = new GoogleNews();

    public NewsListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
        super.onActivityCreated(savedInstanceState);

        getListView().setFastScrollEnabled(true);
        final Bundle args = getArguments();
        loadXmlInBackground(args.getInt(Constants.TAG_NEWS_CATEGORY_ID));
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

        final NewsDetailFragment newsList;
        if (getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_DETAIL) == null) {
            Log.i(TAG, "  newsList fragment is already created.");
            newsList = new NewsDetailFragment();
        } else {
            Log.i(TAG, "  newsList fragment is not created.");
            newsList = (NewsDetailFragment) getFragmentManager().findFragmentByTag(Constants.TAG_NEWS_DETAIL);
        }
        final Bundle args = new Bundle();
        args.putSerializable(Constants.TAG_NEWS_ITEM, (Serializable) mNews.getItems().get(position));
        newsList.setArguments(args);
        getFragmentManager().beginTransaction().add(android.R.id.content, newsList, Constants.TAG_NEWS_DETAIL).
                addToBackStack(Constants.TAG_NEWS_DETAIL).
                commit();
    }

    private void loadXmlInBackground(final int newsCategoryId) {
        new AsyncTask<Void, String, GoogleNews>() {
            @Override
            protected GoogleNews doInBackground(Void... params) {
                Log.i(TAG, "doInBackground()");
                try {
                    final URL url = new URL(Constants.URIS[newsCategoryId].toString());
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(false);

                    logBackgroundWork("connecting to url=" + url);
                    connection.connect();

                    final Map<String, List<String>> headers = connection.getHeaderFields();
                    logBackgroundWork("  headers=" + headers);

                    final String contentType = connection.getContentType();
                    logBackgroundWork("  contentType=" + contentType);

                    final long date = connection.getDate();
                    logBackgroundWork("  date=" + date);

                    final String contentEncoding = connection.getContentEncoding();
                    logBackgroundWork("  contentEncoding=" + contentEncoding);

                    final String responseMessage = connection.getResponseMessage();
                    logBackgroundWork("  responseMessage=" + responseMessage);

                    final int responseCode = connection.getResponseCode();
                    logBackgroundWork("  responseCode=" + responseCode);

                    final InputStream content = (InputStream)connection.getContent();
                    logBackgroundWork("  content=" + content);

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                    while (true) {
//                        final String line = reader.readLine();
//                        if (line == null) {
//                            break;
//                        }
//                        Log.i(TAG, line);
//                    }
                    final XmlPullParser xmlPullParser = Xml.newPullParser();
                    xmlPullParser.setInput(reader);
                    logBackgroundWork("parsing xml with pull parser...");
                    for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                        switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            logBackgroundWork("  xml:start document");
                            break;
                        case XmlPullParser.START_TAG:
                            logBackgroundWork("  xml:start tag=" + xmlPullParser.getName());
                            mNews.onXmlEvent(eventType, xmlPullParser.getName());
                            break;
                        case XmlPullParser.END_TAG:
                            logBackgroundWork("  xml:end tag=" + xmlPullParser.getName());
                            mNews.onXmlEvent(eventType, xmlPullParser.getName());
                            break;
                        case XmlPullParser.TEXT:
                            logBackgroundWork("  xml:text=" + xmlPullParser.getText());
                            mNews.onXmlEvent(eventType, xmlPullParser.getText());
                            break;
                        }
                    }
                    logBackgroundWork("  xml:end document");

                    reader.close();
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "MalformedURLException=" + e);
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "IOException=" + e);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "XmlPullParserException=" + e);
                    e.printStackTrace();
                } finally {
                    // Do finally thing.
                }
                return mNews;
            }
            @Override
            protected void onProgressUpdate(String... progresses) {
//                Log.i(TAG, "onProgressUpdate()");
//                for (String progress : progresses) {
//                    mText.append(progress + "\n");
//                }
            }
            @Override
            protected void onPostExecute(GoogleNews result) {
                final ListAdapter adapter = new SimpleAdapter(getActivity(),
                        result.getItems(),
                        android.R.layout.simple_list_item_2,
                        new String[] {"title", "pubDate"},
                        new int[] {android.R.id.text1, android.R.id.text2});
                setListAdapter(adapter);
            }
            private void logBackgroundWork(String message) {
                Log.i(TAG, message);
//                publishProgress(message);
            }
        }.execute();
    }
}