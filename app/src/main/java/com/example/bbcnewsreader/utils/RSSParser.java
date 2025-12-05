package com.example.bbcnewsreader.utils;

import com.example.bbcnewsreader.data.NewsArticle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing RSS feeds from BBC News.
 * Uses XmlPullParser to extract article information.
 */
public class RSSParser {

    /**
     * Parse RSS feed from input stream
     * @param inputStream The RSS feed input stream
     * @return List of parsed news articles
     * @throws Exception if parsing fails
     */
    public static List<NewsArticle> parse(InputStream inputStream) throws Exception {
        List<NewsArticle> articles = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        NewsArticle currentArticle = null;
        String currentTag = null;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    currentTag = tagName;
                    if ("item".equalsIgnoreCase(tagName)) {
                        currentArticle = new NewsArticle();
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (currentArticle != null && currentTag != null) {
                        String text = parser.getText();
                        switch (currentTag.toLowerCase()) {
                            case "title":
                                currentArticle.setTitle(text);
                                break;
                            case "description":
                                currentArticle.setDescription(text);
                                break;
                            case "link":
                                currentArticle.setLink(text);
                                break;
                            case "pubdate":
                                currentArticle.setPubDate(text);
                                break;
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("item".equalsIgnoreCase(tagName) && currentArticle != null) {
                        articles.add(currentArticle);
                        currentArticle = null;
                    }
                    currentTag = null;
                    break;
            }
            eventType = parser.next();
        }

        return articles;
    }
}
