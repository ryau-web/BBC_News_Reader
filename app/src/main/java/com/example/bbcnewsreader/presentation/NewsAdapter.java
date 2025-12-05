package com.example.bbcnewsreader.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.data.NewsArticle;

import java.util.List;

/**
 * Custom adapter for displaying news articles in a ListView.
 */
public class NewsAdapter extends ArrayAdapter<NewsArticle> {

    /**
     * Constructor
     * @param context Application context
     * @param articles List of articles to display
     */
    public NewsAdapter(Context context, List<NewsArticle> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsArticle article = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_news, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tv_item_title);
        TextView tvDate = convertView.findViewById(R.id.tv_item_date);

        if (article != null) {
            tvTitle.setText(article.getTitle());
            tvDate.setText(article.getPubDate());
        }

        return convertView;
    }
}
