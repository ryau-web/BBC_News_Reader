package com.example.bbcnewsreader.presentation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbcnewsreader.R;

/**
 * Activity for managing app settings.
 * Uses SharedPreferences to persist settings.
 */
public class SettingsActivity extends AppCompatActivity {

    private CheckBox cbAutoLoad;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.settings_title) + " " +
                getString(R.string.version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbAutoLoad = findViewById(R.id.cb_auto_load);
        prefs = getSharedPreferences("BBCNewsPrefs", MODE_PRIVATE);

        // Load saved setting
        boolean autoLoad = prefs.getBoolean("auto_load", false);
        cbAutoLoad.setChecked(autoLoad);

        // Save setting when changed
        cbAutoLoad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_load", isChecked).apply();
            Toast.makeText(this, R.string.setting_saved, Toast.LENGTH_SHORT).show();
        });
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
                .setMessage(R.string.help_settings)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
