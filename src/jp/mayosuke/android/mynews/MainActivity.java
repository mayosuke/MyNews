package jp.mayosuke.android.mynews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final Uri[] URIS = {
        // トップニュース
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss"),
        // ピックアップ
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=ir"),
        // 社会
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=y"),
        // 国際
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=w"),
        // ビジネス
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=b"),
        // 政治
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=p"),
        // エンタメ
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=e"),
        // スポーツ
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=s"),
        // テクノロジー
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=t"),
        // 話題のニュース
        Uri.parse("http://news.google.com/news?hl=ja&ie=UTF-8&oe=UTF-8&output=rss&topic=po"),
        // 阪神という単語の入ったニュース
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("阪神")),
    };

    private static final String TAG_NEWS_CATEGORY_LIST = "categoryList";
    private static final String TAG_NEWS_LIST = "newsList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(TAG_NEWS_CATEGORY_LIST) == null) {
            final Fragment categoryList = new CategoryListFragment();
            getFragmentManager().beginTransaction().
                    add(android.R.id.content, categoryList, TAG_NEWS_CATEGORY_LIST).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public static class CategoryListFragment extends ListFragment {
        private static final String TAG = CategoryListFragment.class.getSimpleName();

        public CategoryListFragment() {}

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
            super.onActivityCreated(savedInstanceState);

            final ListAdapter adapter = new ArrayAdapter<Uri>(getActivity(), android.R.layout.simple_list_item_1, URIS);
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
            if (getFragmentManager().findFragmentByTag(TAG_NEWS_LIST) == null) {
                Log.i(TAG, "  newsList fragment is already created.");
                newsList = new NewsListFragment();
            } else {
                Log.i(TAG, "  newsList fragment is not created.");
                newsList = (NewsListFragment) getFragmentManager().findFragmentByTag(TAG_NEWS_LIST);
            }
            getFragmentManager().beginTransaction().add(android.R.id.content, newsList, TAG_NEWS_LIST).
                    addToBackStack(TAG_NEWS_LIST).
                    commit();
        }
    }

    public static class NewsListFragment extends Fragment {
        private static final String TAG = NewsListFragment.class.getSimpleName();

        private TextView mText;

        public NewsListFragment() {}

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
            super.onActivityCreated(savedInstanceState);

            mText = (TextView) getActivity().findViewById(R.id.text);
            loadXmlInBackground();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView(savedInstanceState=" + savedInstanceState + ")");

            final View view = inflater.inflate(R.layout.activity_main, null);
            view.setBackgroundColor(Color.WHITE);
            return view;
        }

        private void loadXmlInBackground() {
            new AsyncTask<Void, String, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Log.i(TAG, "doInBackground()");
                    try {
                        final URL url = new URL("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("阪神"));
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
//                        while (true) {
//                            final String line = reader.readLine();
//                            if (line == null) {
//                                break;
//                            }
//                            Log.i(TAG, line);
//                        }
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
                                break;
                            case XmlPullParser.END_TAG:
                                logBackgroundWork("  xml:end tag=" + xmlPullParser.getName());
                                break;
                            case XmlPullParser.TEXT:
                                logBackgroundWork("  xml:text=" + xmlPullParser.getText());
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
                    return null;
                }
                @Override
                protected void onProgressUpdate(String... progresses) {
                    Log.i(TAG, "onProgressUpdate()");
                    for (String progress : progresses) {
                        mText.append(progress + "\n");
                    }
                }
                private void logBackgroundWork(String message) {
                    Log.i(TAG, message);
                    publishProgress(message);
                }
            }.execute();
        }
    }

    public static class NewsDetailFragment extends ListFragment {
        private static final String TAG = NewsDetailFragment.class.getSimpleName();
        public NewsDetailFragment() {}
    }
}
