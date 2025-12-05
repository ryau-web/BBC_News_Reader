package com.example.bbcnewsreader.presentation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.data.NewsArticle;
import com.example.bbcnewsreader.utils.RSSParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Activity for searching and loading BBC news articles.
 * Uses AsyncTask to fetch RSS feed from BBC News.
 */
public class SearchNewsActivity extends AppCompatActivity {

    private static final String BBC_RSS_URL =
            "https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";

    private Button btnLoadNews;
    private ProgressBar progressBar;
    private NewsAdapter adapter;
    private List<NewsArticle> allArticles;
    private List<NewsArticle> filteredArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.search_title) + " " +
                getString(R.string.version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText etSearch = findViewById(R.id.et_search);
        btnLoadNews = findViewById(R.id.btn_load_news);
        progressBar = findViewById(R.id.progress_bar);
        ListView lvNews = findViewById(R.id.lv_news);

        allArticles = new ArrayList<>();
        filteredArticles = new ArrayList<>();
        adapter = new NewsAdapter(this, filteredArticles);
        lvNews.setAdapter(adapter);

        btnLoadNews.setOnClickListener(v -> loadNews());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvNews.setOnItemClickListener((parent, view, position, id) -> {
            NewsArticle article = filteredArticles.get(position);
            Intent intent = new Intent(SearchNewsActivity.this, ArticleDetailActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("from_favourites", false);
            startActivity(intent);
        });
    }

    /**
     * Load news articles from BBC RSS feed
     */
    private void loadNews() {
        new LoadNewsTask().execute(BBC_RSS_URL);
    }

    /**
     * Filter articles based on search query
     * @param query Search query
     */
    private void filterArticles(String query) {
        filteredArticles.clear();
        if (query.isEmpty()) {
            filteredArticles.addAll(allArticles);
        } else {
            String lowerQuery = query.toLowerCase();
            for (NewsArticle article : allArticles) {
                if (article.getTitle().toLowerCase().contains(lowerQuery) ||
                        article.getDescription().toLowerCase().contains(lowerQuery)) {
                    filteredArticles.add(article);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display help dialog
     */
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_search)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * AsyncTask for loading news articles from RSS feed
     */
    private class LoadNewsTask extends AsyncTask<String, Void, List<NewsArticle>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnLoadNews.setEnabled(false);
        }

        @Override
        protected List<NewsArticle> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                List<NewsArticle> articles = RSSParser.parse(inputStream);
                inputStream.close();
                connection.disconnect();

                return articles;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<NewsArticle> articles) {
            progressBar.setVisibility(View.GONE);
            btnLoadNews.setEnabled(true);

            if (articles != null && !articles.isEmpty()) {
                allArticles.clear();
                allArticles.addAll(articles);
                filteredArticles.clear();
                filteredArticles.addAll(articles);
                adapter.notifyDataSetChanged();
                Toast.makeText(SearchNewsActivity.this,
                        R.string.news_loaded, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SearchNewsActivity.this,
                        R.string.error_loading_news, Toast.LENGTH_SHORT).show();
            }
        }
    }
}