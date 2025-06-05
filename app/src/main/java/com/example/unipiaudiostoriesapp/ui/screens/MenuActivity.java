package com.example.unipiaudiostoriesapp.ui.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unipiaudiostoriesapp.R;
import com.example.unipiaudiostoriesapp.adapters.StoryAdapter;
import com.example.unipiaudiostoriesapp.data.FirebaseHelper;
import com.example.unipiaudiostoriesapp.models.Story;
import com.example.unipiaudiostoriesapp.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private TextView btnStatistics;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the correct language
        AppConfig.applyLocale(this, AppConfig.selectedLanguage);

        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnStatistics = findViewById(R.id.btnStatistics);

        // Set the localized text for the button
        if (AppConfig.selectedLanguage.equals("gr")) {
            btnStatistics.setText(getString(R.string.statistics_header_gr));
        } else if (AppConfig.selectedLanguage.equals("fr")) {
            btnStatistics.setText(getString(R.string.statistics_header_fr));
        } else {
            btnStatistics.setText(getString(R.string.statistics_header)); // Default to English
        }

        // Button to go to StatisticsActivity
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        // Load stories from Firestore
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getStories(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<Story> storyList) {
                storyAdapter = new StoryAdapter(MenuActivity.this, storyList);
                recyclerView.setAdapter(storyAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MenuActivity.this, "Error while loading data", Toast.LENGTH_SHORT).show();
            }
        });

        // Request microphone permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }
    }

    // Handle speech recognition
    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MenuActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }
            //Check what was said
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0).toLowerCase(Locale.ROOT);
                    // If user asked for statistics, redirect
                    if (recognizedText.contains("statistics")) {
                        Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                        // Otherwise notify command wasn't recognised
                    } else {
                        Toast.makeText(MenuActivity.this, "Command not recognized", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override public void onError(int error) {
                Toast.makeText(MenuActivity.this, "Recognition Error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onEvent(int eventType, Bundle params) {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onRmsChanged(float rmsdB) {}
        });

        startVoiceRecognition();
    }

    //Start voice recognition
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    // Check if we have permissions for microphone usage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer();
            } else {
                Toast.makeText(this, "Permission for microphone denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
