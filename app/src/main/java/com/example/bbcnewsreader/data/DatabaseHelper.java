package com.example.bbcnewsreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Layer - Database helper for managing favourite articles.
 * Extends SQLiteOpenHelper to provide CRUD operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bbcnews.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_FAVOURITES = "favourites";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_PUB_DATE = "pub_date";

    /**
     * Constructor
     * @param context Application context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_FAVOURITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LINK + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PUB_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }

    /**
     * Insert a news article into favourites
     * @param article The article to save
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertArticle(NewsArticle article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, article.getTitle());
        values.put(COLUMN_DESCRIPTION, article.getDescription());
        values.put(COLUMN_LINK, article.getLink());
        values.put(COLUMN_PUB_DATE, article.getPubDate());

        long id = db.insert(TABLE_FAVOURITES, null, values);
        db.close();
        return id;
    }

    /**
     * Get all favourite articles from database
     * @return List of all favourite articles
     */
    public List<NewsArticle> getAllArticles() {
        List<NewsArticle> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, null, null, null, null, null,
                COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                NewsArticle article = new NewsArticle();
                article.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                article.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                article.setLink(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK)));
                article.setPubDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUB_DATE)));
                articles.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return articles;
    }

    /**
     * Get a single article by ID
     * @param id The article ID
     * @return The article, or null if not found
     */
    public NewsArticle getArticleById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        NewsArticle article = null;
        if (cursor.moveToFirst()) {
            article = new NewsArticle();
            article.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            article.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            article.setLink(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK)));
            article.setPubDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUB_DATE)));
        }
        cursor.close();
        db.close();
        return article;
    }

    /**
     * Delete an article from favourites
     * @param id The article ID to delete
     * @return Number of rows affected
     */
    public int deleteArticle(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_FAVOURITES, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    /**
     * Check if an article already exists in favourites by link
     * @param link The article link
     * @return true if exists, false otherwise
     */
    public boolean articleExists(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{COLUMN_ID},
                COLUMN_LINK + "=?", new String[]{link}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
