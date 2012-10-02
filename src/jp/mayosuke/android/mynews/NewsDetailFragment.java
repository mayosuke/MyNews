package jp.mayosuke.android.mynews;

import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

public class NewsDetailFragment extends ListFragment {
    private static final String TAG = NewsDetailFragment.class.getSimpleName();

    private Map<String, String> mItem;
    private Document mJsoup;

    public NewsDetailFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated(savedInstanceState=" + savedInstanceState + ")");
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();

        getActivity().getActionBar().setTitle(Constants.CATEGORIES[args.getInt(Constants.TAG_NEWS_CATEGORY_ID)]);

        mItem = (Map<String, String>) args.getSerializable(Constants.TAG_NEWS_ITEM);
        final String html = "<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\"></head><body>" +
                    mItem.get("description") + "</body></html>";
        mJsoup = Jsoup.parse(html);
        final Integer[] layouts = {
                Integer.valueOf(R.layout.summary),
                Integer.valueOf(R.layout.content_text),
                Integer.valueOf(R.layout.content),
                Integer.valueOf(R.layout.content_text),
                Integer.valueOf(R.layout.content_text),
        };
        final ListAdapter adapter = new ArrayAdapter<Integer>(getActivity(), 0, layouts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = View.inflate(getContext(), getItem(position).intValue(), null);
                switch (position) {
                case 0: {
                    
                    break;
                }
                case 1: {
                    final TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(Html.fromHtml(mJsoup.toString()));
                    break;
                }
                case 2: {
                    final WebView content = (WebView) view.findViewById(R.id.content);
                    content.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.i(TAG, "shouldOverrideUrlLoading(url=" + url + ")");
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            Log.i(TAG, "onPageStarted(url=" + url + ")");
                        }
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            Log.i(TAG, "onPageFinished(url=" + url + ")");
                        }
                    });
                    content.getSettings().setJavaScriptEnabled(true);
                    content.getSettings().setPluginsEnabled(true);
                    content.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                    break;
                }
                case 3: {
                    final TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(mJsoup.toString());
                    break;
                }
                case 4: {
                    final TextView content = (TextView) view.findViewById(R.id.content);
                    Elements aTags = mJsoup.getElementsByTag("a");
                    content.setText(aTags.toString());
                    break;
                }
                default:
                    // DO NOTHING.
                    break;
                }
                return view;
            }
        };
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView(savedInstanceState=" + savedInstanceState + ")");
        final View view = super.onCreateView(inflater, container, savedInstanceState);
//        final View view = inflater.inflate(R.layout.news_detail, null);
//        final View view = inflater.inflate(R.layout.activity_main, null);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    private static class Summary {
        private final String mTitle;
        private final String mSource;
        private final long mTime;
        private final String mSummary;
        private final Uri mImageUri;
        private final String mImageSource;
        private final Uri mOtherSourcesUri;
        private final String mOtherSoucesLabel;

        private Summary(String title, String source, long time, String summary, Uri imageUri, String imageSource, Uri otherSourcesUri, String otherSourcesLabel) {
            mTitle = title;
            mSource = source;
            mTime = time;
            mSummary = summary;
            mImageUri = imageUri;
            mImageSource = imageSource;
            mOtherSourcesUri = otherSourcesUri;
            mOtherSoucesLabel = otherSourcesLabel;
        }
    }
}
