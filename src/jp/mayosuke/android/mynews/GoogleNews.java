package jp.mayosuke.android.mynews;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class GoogleNews {
    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_ITEM = "item";

    private final Stack<String> mState = new Stack<String>();
    private final Map<String, String> mInfo = new HashMap<String, String>();
    private final Map<String, String> mImageInfo = new HashMap<String, String>();
    private final List<Map<String, String>> mItems = new ArrayList<Map<String, String>>();

    private String mTag;

    List<Map<String, String>> getItems() {
        return mItems;
    }

    void onXmlEvent(final int eventType, final String value) {
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

    @Override
    public String toString() {
        return "mInfo=" + mInfo.toString() + ",mImageInfo=" + mImageInfo.toString() + ",mItems=" + mItems.toString();
    }
}
