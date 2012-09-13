package jp.mayosuke.android.mynews;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            final URL url = new URL("http://www.google.com/");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);

            Log.i(TAG, "cnnecting to url=" + url);
            connection.connect();

            final Map<String, List<String>> headers = connection.getHeaderFields();
            Log.i(TAG, "  headers=" + headers);

            final String contentType = connection.getContentType();
            Log.i(TAG, "  contentType=" + contentType);

            final long date = connection.getDate();
            Log.i(TAG, "  date=" + date);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException=" + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException=" + e);
            e.printStackTrace();
        } finally {
            // Do finally thing.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
