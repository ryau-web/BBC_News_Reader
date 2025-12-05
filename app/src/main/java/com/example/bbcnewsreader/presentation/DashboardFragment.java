package com.example.bbcnewsreader.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bbcnewsreader.R;

/**
 * Dashboard fragment displayed on the main screen.
 * Shows last viewed article and provides quick navigation buttons.
 */
public class DashboardFragment extends Fragment {

    private TextView tvLastArticle;
    private Button btnSearchNews;
    private Button btnViewFavourites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvLastArticle = view.findViewById(R.id.tv_last_article);
        btnSearchNews = view.findViewById(R.id.btn_search_news);
        btnViewFavourites = view.findViewById(R.id.btn_view_favourites);

        loadLastArticle();

        btnSearchNews.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchNewsActivity.class);
            startActivity(intent);
        });

        btnViewFavourites.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavouritesActivity.class);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Load the last viewed article title from SharedPreferences
     */
    private void loadLastArticle() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("BBCNewsPrefs",
                getActivity().MODE_PRIVATE);
        String lastArticle = prefs.getString("last_article_title",
                getString(R.string.no_articles_yet));
        tvLastArticle.setText(lastArticle);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLastArticle();
    }
}
