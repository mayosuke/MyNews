package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
        getActivity().getActionBar().setTitle(Constants.CATEGORIES[args.getInt(Constants.TAG_NEWS_CATEGORY_ID)]);
        loadXmlInBackground(args.getInt(Constants.TAG_NEWS_CATEGORY_ID));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "onListItemClick(position=" + position + ",id=" + id + ")");

//        final Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setData(data);

        final Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.TAG_NEWS_CATEGORY_ID, getArguments().getInt(Constants.TAG_NEWS_CATEGORY_ID, -1));
        intent.putExtra(Constants.TAG_NEWS_ITEM, (Serializable) mNews.getItems().get(position));
        startActivity(intent);
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

                    final XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(reader);
                    logBackgroundWork("parsing xml with pull parser...");
                    for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
                        switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            logBackgroundWork("  xml:start document");
                            break;
                        case XmlPullParser.START_TAG:
                            logBackgroundWork("  xml:start tag=" + parser.getName());
                            mNews.onXmlEvent(eventType, parser.getName());
                            break;
                        case XmlPullParser.END_TAG:
                            logBackgroundWork("  xml:end tag=" + parser.getName());
                            mNews.onXmlEvent(eventType, parser.getName());
                            break;
                        case XmlPullParser.TEXT:
                            logBackgroundWork("  xml:text=" + parser.getText());
                            mNews.onXmlEvent(eventType, parser.getText());
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
//                final ListAdapter adapter = new SimpleAdapter(getActivity(),
//                        result.getItems(),
//                        android.R.layout.simple_list_item_2,
//                        new String[] {"title", "pubDate"},
//                        new int[] {android.R.id.text1, android.R.id.text2});
                final ListAdapter adapter = new ArrayAdapter<Map<String, String>>(getActivity(),
                        android.R.layout.simple_list_item_1, result.getItems());
                setListAdapter(adapter);
            }
            private void logBackgroundWork(String message) {
                Log.i(TAG, message);
//                publishProgress(message);
            }
        }.execute();
    }
}
