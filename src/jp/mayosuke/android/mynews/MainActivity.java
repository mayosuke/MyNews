package jp.mayosuke.android.mynews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] CATEGORIES = {
        "トップニュース",
        "ピックアップ",
        "社会",
        "国際",
        "ビジネス",
        "政治",
        "エンタメ",
        "スポーツ",
        "テクノロジー",
        "話題のニュース",
        "阪神",
        "Android",
        "iPhone",
    }; 
    private static final Uri[] URIS = {
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=ir"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=y"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=w"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=b"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=p"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=e"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=s"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=t"),
        Uri.parse("http://news.google.com/news?hl=ja&ie=UTF-8&oe=UTF-8&output=rss&topic=po"),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("阪神")),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("Android")),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("iPhone")),
    }; 

    private static final String TAG_NEWS_CATEGORY_LIST = "categoryList";
    private static final String TAG_NEWS_LIST = "newsList";
    private static final String TAG_NEWS_CATEGORY_ID = "newsCategoryId";
    private static final String TAG_NEWS_DETAIL = "newsDetail";
    private static final String TAG_NEWS_ITEM = "newsItem";

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

            final ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, CATEGORIES);
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
            final Bundle args = new Bundle();
            args.putInt(TAG_NEWS_CATEGORY_ID, position);
            newsList.setArguments(args);
            getFragmentManager().beginTransaction().add(android.R.id.content, newsList, TAG_NEWS_LIST).
                    addToBackStack(TAG_NEWS_LIST).
                    commit();
        }
    }

    public static class NewsListFragment extends ListFragment {
        private static final String TAG = NewsListFragment.class.getSimpleName();

        private final GoogleNews mNews = new GoogleNews();

        public NewsListFragment() {}

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
            super.onActivityCreated(savedInstanceState);

            getListView().setFastScrollEnabled(true);
            final Bundle args = getArguments();
            loadXmlInBackground(args.getInt(TAG_NEWS_CATEGORY_ID));
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
            if (getFragmentManager().findFragmentByTag(TAG_NEWS_DETAIL) == null) {
                Log.i(TAG, "  newsList fragment is already created.");
                newsList = new NewsDetailFragment();
            } else {
                Log.i(TAG, "  newsList fragment is not created.");
                newsList = (NewsDetailFragment) getFragmentManager().findFragmentByTag(TAG_NEWS_DETAIL);
            }
            final Bundle args = new Bundle();
            args.putSerializable(TAG_NEWS_ITEM, (Serializable) mNews.mItems.get(position));
            newsList.setArguments(args);
            getFragmentManager().beginTransaction().add(android.R.id.content, newsList, TAG_NEWS_DETAIL).
                    addToBackStack(TAG_NEWS_DETAIL).
                    commit();
        }

        private void loadXmlInBackground(final int newsCategoryId) {
            new AsyncTask<Void, String, GoogleNews>() {
                @Override
                protected GoogleNews doInBackground(Void... params) {
                    Log.i(TAG, "doInBackground()");
                    try {
                        final URL url = new URL(URIS[newsCategoryId].toString());
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
//                    Log.i(TAG, "onProgressUpdate()");
//                    for (String progress : progresses) {
//                        mText.append(progress + "\n");
//                    }
                }
                @Override
                protected void onPostExecute(GoogleNews result) {
                    final ListAdapter adapter = new SimpleAdapter(getActivity(),
                            result.mItems,
                            android.R.layout.simple_list_item_2,
                            new String[] {"title", "pubDate"},
                            new int[] {android.R.id.text1, android.R.id.text2});
                    setListAdapter(adapter);
                }
                private void logBackgroundWork(String message) {
                    Log.i(TAG, message);
//                    publishProgress(message);
                }
            }.execute();
        }
    }

    public static class NewsDetailFragment extends Fragment {
        private static final String TAG = NewsDetailFragment.class.getSimpleName();

        public NewsDetailFragment() {}

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
            super.onActivityCreated(savedInstanceState);

            final Bundle args = getArguments();
            final Map<String, String> item = (Map<String, String>) args.getSerializable(TAG_NEWS_ITEM);
//            final WebView content = (WebView) getActivity().findViewById(R.id.content);
            final TextView content = (TextView) getActivity().findViewById(R.id.content);
            final String html = "<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\"></head><body>" +
                        item.get("description") + "</body></html>";
//            content.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
//            content.setText(item.get("description"));
            content.setText(html);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView(savedInstanceState=" + savedInstanceState + ")");

//            final View view = inflater.inflate(R.layout.news_detail, null);
            final View view = inflater.inflate(R.layout.activity_main, null);
            view.setBackgroundColor(Color.WHITE);
            return view;
        }

    }

    private static class GoogleNews {
        private static final String TAG_CHANNEL = "channel";
        private static final String TAG_IMAGE = "image";
        private static final String TAG_ITEM = "item";

        private final Stack<String> mState = new Stack<String>();
        private final Map<String, String> mInfo = new HashMap<String, String>();
        private final Map<String, String> mImageInfo = new HashMap<String, String>();
        private final List<Map<String, String>> mItems = new ArrayList<Map<String, String>>();

        private String mTag;

        private void onXmlEvent(final int eventType, final String value) {
            switch (eventType) {
            case XmlPullParser.START_TAG:
                mTag = value;
                if (value.equalsIgnoreCase(TAG_CHANNEL) ||
                        value.equalsIgnoreCase(TAG_IMAGE) ||
                        value.equalsIgnoreCase(TAG_ITEM)) {
                    mState.push(value);
                }
                if (value.equalsIgnoreCase(TAG_ITEM)) {
                    mItems.add(new HashMap<String, String>());
                }
                break;
            case XmlPullParser.TEXT:
                if (mState.peek().equalsIgnoreCase(TAG_CHANNEL)) {
                    mInfo.put(mTag, value);
                    break;
                }
                if (mState.peek().equalsIgnoreCase(TAG_IMAGE)) {
                    mImageInfo.put(mTag, value);
                }
                if (mState.peek().equalsIgnoreCase(TAG_ITEM)) {
                    mItems.get(mItems.size() - 1).put(mTag, value);
                }
                break;
            case XmlPullParser.END_TAG:
                if (value.equalsIgnoreCase(TAG_CHANNEL) ||
                        value.equalsIgnoreCase(TAG_IMAGE) ||
                        value.equalsIgnoreCase(TAG_ITEM)) {
                    mState.pop();
                }
                break;
            default:
                break;
            }
        }
    }
}
