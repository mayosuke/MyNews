package jp.mayosuke.android.mynews;

import android.net.Uri;

final class Constants {
    static final String[] CATEGORIES = {
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
        "農業",
        "佐賀",
        "九州",
    };

    static final Uri[] URIS = {
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
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("農業")),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("佐賀")),
        Uri.parse("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&q=" + Uri.encode("九州")),
    }; 

    static final String TAG_NEWS_CATEGORY_LIST = "categoryList";
    static final String TAG_NEWS_LIST = "newsList";
    static final String TAG_NEWS_CATEGORY_ID = "newsCategoryId";
    static final String TAG_NEWS_DETAIL = "newsDetail";
    static final String TAG_NEWS_ITEM = "newsItem";

    private Constants() {}
}
