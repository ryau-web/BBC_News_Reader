package com.example.bbcnewsreader.business;

import android.content.Context;

import com.example.bbcnewsreader.data.DatabaseHelper;
import com.example.bbcnewsreader.data.NewsArticle;

import java.util.List;

/**
 * Business Layer - Repository pattern for managing news articles.
 * Provides abstraction between presentation and data layers.
 */
public class NewsRepository {
    private DatabaseHelper dbHelper;

    /**
     * Constructor
     * @param context Application context
     */
    public NewsRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Save an article to favourites
     * @param article The article to save
     * @return true if saved successfully, false if already exists
     */
    public boolean saveFavourite(NewsArticle article) {
        if (dbHelper.articleExists(article.getLink())) {
            return false;
        }
        long id = dbHelper.insertArticle(article);
        return id != -1;
    }

    /**
     * Get all favourite articles
     * @return List of favourite articles
     */
    public List<NewsArticle> getAllFavourites() {
        return dbHelper.getAllArticles();
    }

    /**
     * Get a specific article by ID
     * @param id Article ID
     * @return The article or null
     */
    public NewsArticle getFavouriteById(long id) {
        return dbHelper.getArticleById(id);
    }

    /**
     * Delete a favourite article
     * @param id Article ID to delete
     * @return true if deleted successfully
     */
    public boolean deleteFavourite(long id) {
        return dbHelper.deleteArticle(id) > 0;
    }

    /**
     * Check if an article is already in favourites
     * @param link Article link
     * @return true if exists
     */
    public boolean isFavourite(String link) {
        return dbHelper.articleExists(link);
    }
}
