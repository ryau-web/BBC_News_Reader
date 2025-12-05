package com.example.bbcnewsreader.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.business.NewsRepository;
import com.example.bbcnewsreader.data.NewsArticle;

/**
 * Activity displaying detailed information about a news article.
 * Allows opening in browser and saving to favourites.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDate, tvDescription, tvLink;
    private Button btnOpenBrowser, btnSaveFavourite;
    private NewsArticle article;
    private NewsRepository repository;
    private boolean fromFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.detail_title) + " " +
                getString(R.string.version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.tv_description);
        tvLink = findViewById(R.id.tv_link);
        btnOpenBrowser = findViewById(R.id.btn_open_browser);
        btnSaveFavourite = findViewById(R.id.btn_save_favourite);

        repository = new NewsRepository(this);

        // Get article from intent
        article = (NewsArticle) getIntent().getSerializableExtra("article");
        fromFavourites = getIntent().getBooleanExtra("from_favourites", false);

        if (article != null) {
            displayArticle();
            saveLastViewedArticle();
        }

        btnOpenBrowser.setOnClickListener(v -> openInBrowser());
        btnSaveFavourite.setOnClickListener(v -> saveFavourite());

        // Update button text if viewing from favourites
        if (fromFavourites) {
            btnSaveFavourite.setText(R.string.btn_remove_favourite);
        }
    }

    /**
     * Display article details in UI
     */
    private void displayArticle() {
        tvTitle.setText(article.getTitle());
        tvDate.setText(article.getPubDate());
        tvDescription.setText(article.getDescription());
        tvLink.setText(article.getLink());
    }

    /**
     * Save article title to SharedPreferences as last viewed
     */
    private void saveLastViewedArticle() {
        SharedPreferences prefs = getSharedPreferences("BBCNewsPrefs", MODE_PRIVATE);
        prefs.edit().putString("last_article_title", article.getTitle()).apply();
    }

    /**
     * Open article link in device browser
     */
    private void openInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink()));
        startActivity(browserIntent);
    }

    /**
     * Save or remove article from favourites
     */
    private void saveFavourite() {
        if (fromFavourites) {
            // Remove from favourites
            repository.deleteFavourite(article.getId());
            Toast.makeText(this, R.string.favourite_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Add to favourites
            boolean saved = repository.saveFavourite(article);
            if (saved) {
                Toast.makeText(this, R.string.article_saved, Toast.LENGTH_SHORT).show();
                btnSaveFavourite.setEnabled(false);
            } else {
                Toast.makeText(this, R.string.article_already_saved, Toast.LENGTH_SHORT).show();
            }
        }
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
                .setMessage(R.string.help_detail)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
