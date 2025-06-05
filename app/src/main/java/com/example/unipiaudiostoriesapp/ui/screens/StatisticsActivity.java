package com.example.unipiaudiostoriesapp.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.unipiaudiostoriesapp.R;
import com.example.unipiaudiostoriesapp.adapters.StatisticsAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.example.unipiaudiostoriesapp.data.FirebaseHelper;
import com.example.unipiaudiostoriesapp.utils.AppConfig;

import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;
    private FirebaseHelper firebaseHelper;
    private TextView statisticsHeader, titleHeader, numReadHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply the correct locale
        AppConfig.applyLocale(this, AppConfig.selectedLanguage);

        setContentView(R.layout.activity_statistics);

        statisticsHeader = findViewById(R.id.statisticsHeader);
        titleHeader = findViewById(R.id.titleHeader);
        numReadHeader = findViewById(R.id.numReadHeader);

        // Set localized text for headers
        if (AppConfig.selectedLanguage.equals("gr")) {
            statisticsHeader.setText(getString(R.string.statistics_header_gr));
            titleHeader.setText(getString(R.string.title_label_gr));
            numReadHeader.setText(getString(R.string.num_read_label_gr));
        } else if (AppConfig.selectedLanguage.equals("fr")) {
            statisticsHeader.setText(getString(R.string.statistics_header_fr));
            titleHeader.setText(getString(R.string.title_label_fr));
            numReadHeader.setText(getString(R.string.num_read_label_fr));
        } else {
            statisticsHeader.setText(getString(R.string.statistics_header)); // default English
            titleHeader.setText(getString(R.string.title_label));
            numReadHeader.setText(getString(R.string.num_read_label));
        }

        recyclerView = findViewById(R.id.statisticsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StatisticsAdapter(AppConfig.selectedLanguage);
        recyclerView.setAdapter(adapter);

        firebaseHelper = new FirebaseHelper();

        loadStatistics();
    }

    // Load statistics from firebase "statistics" collection
    private void loadStatistics() {
        firebaseHelper.getStatistics(new FirebaseHelper.StatisticsCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> statisticsList) {
                adapter.updateData(statisticsList);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("StatisticsActivity", "Error loading statistics", e);
                Toast.makeText(StatisticsActivity.this, "Failed to load statistics", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
