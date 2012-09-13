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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG, "doInBackground()");
                try {
                    final URL url = new URL("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("阪神"));
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(false);

                    Log.i(TAG, "connecting to url=" + url);
                    connection.connect();

                    final Map<String, List<String>> headers = connection.getHeaderFields();
                    Log.i(TAG, "  headers=" + headers);

                    final String contentType = connection.getContentType();
                    Log.i(TAG, "  contentType=" + contentType);

                    final long date = connection.getDate();
                    Log.i(TAG, "  date=" + date);

                    final String contentEncoding = connection.getContentEncoding();
                    Log.i(TAG, "  contentEncoding=" + contentEncoding);

                    final String responseMessage = connection.getResponseMessage();
                    Log.i(TAG, "  responseMessage=" + responseMessage);

                    final int responseCode = connection.getResponseCode();
                    Log.i(TAG, "  responseCode=" + responseCode);

                    final InputStream content = (InputStream)connection.getContent();
                    Log.i(TAG, "  content=" + content);

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
                    Log.i(TAG, "parsing xml with pull parser...");
                    for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                        switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            Log.i(TAG, "  xml:start document");
                            break;
                        case XmlPullParser.START_TAG:
                            Log.i(TAG, "  xml:start tag=" + xmlPullParser.getName());
                            break;
                        case XmlPullParser.END_TAG:
                            Log.i(TAG, "  xml:end tag=" + xmlPullParser.getName());
                            break;
                        case XmlPullParser.TEXT:
                            Log.i(TAG, "  xml:text=" + xmlPullParser.getText());
                            break;
                        }
                    }
                    Log.i(TAG, "  xml:end document");

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
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
