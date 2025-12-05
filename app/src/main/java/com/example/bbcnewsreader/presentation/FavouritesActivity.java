package com.example.bbcnewsreader.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.business.NewsRepository;
import com.example.bbcnewsreader.data.NewsArticle;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * Activity displaying saved favourite articles.
 * Allows viewing and deleting favourites.
 */
public class FavouritesActivity extends AppCompatActivity {

    private ListView lvFavourites;
    private TextView tvEmpty;
    private NewsAdapter adapter;
    private NewsRepository repository;
    private List<NewsArticle> favourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.favourites_title) + " " +
                getString(R.string.version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvFavourites = findViewById(R.id.lv_favourites);
        tvEmpty = findViewById(R.id.tv_empty);
        repository = new NewsRepository(this);

        loadFavourites();

        lvFavourites.setOnItemClickListener((parent, view, position, id) -> {
            NewsArticle article = favourites.get(position);
            Intent intent = new Intent(FavouritesActivity.this, ArticleDetailActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("from_favourites", true);
            startActivity(intent);
        });

        lvFavourites.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    /**
     * Load favourite articles from database
     */
    private void loadFavourites() {
        favourites = repository.getAllFavourites();

        if (favourites.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvFavourites.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvFavourites.setVisibility(View.VISIBLE);
            adapter = new NewsAdapter(this, favourites);
            lvFavourites.setAdapter(adapter);
        }
    }

    /**
     * Show dialog to confirm deletion
     * @param position Position of article to delete
     */
    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_favourite)
                .setMessage(favourites.get(position).getTitle())
                .setPositiveButton(R.string.ok, (dialog, which) -> deleteArticle(position))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Delete article with undo option via Snackbar
     * @param position Position of article to delete
     */
    private void deleteArticle(int position) {
        NewsArticle article = favourites.get(position);
        long articleId = article.getId();

        favourites.remove(position);
        adapter.notifyDataSetChanged();

        if (favourites.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvFavourites.setVisibility(View.GONE);
        }

        Snackbar.make(lvFavourites, R.string.favourite_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    // Note: In a real app, you'd need to re-insert the article
                    // For simplicity, we just reload
                    loadFavourites();
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            // Actually delete from database
                            repository.deleteFavourite(articleId);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavourites();
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
                .setMessage(R.string.help_favourites)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
