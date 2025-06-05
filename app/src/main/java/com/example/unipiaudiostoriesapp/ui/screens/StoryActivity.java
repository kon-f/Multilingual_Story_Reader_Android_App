package com.example.unipiaudiostoriesapp.ui.screens;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.unipiaudiostoriesapp.utils.AppConfig;
import com.example.unipiaudiostoriesapp.R;
import java.util.Locale;
import android.util.Log;

import com.example.unipiaudiostoriesapp.data.FirebaseHelper;


public class StoryActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private TextView titleTextView, authorTextView, yearTextView, contentTextView;
    private ImageView storyImageView;
    private Button playButton, stopButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show it in the correct language
        AppConfig.applyLocale(this, AppConfig.selectedLanguage);

        setContentView(R.layout.activity_story);

        titleTextView = findViewById(R.id.textViewTitle);
        authorTextView = findViewById(R.id.textViewAuthor);
        yearTextView = findViewById(R.id.textViewYear);
        contentTextView = findViewById(R.id.textViewContent);
        storyImageView = findViewById(R.id.imageViewStory);
        playButton = findViewById(R.id.buttonPlay);
        stopButton = findViewById(R.id.buttonStop);

        // Get data from Intent
        String storyId = getIntent().getStringExtra("storyId");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String year = getIntent().getStringExtra("year");
        String content = getIntent().getStringExtra("content");
        String imageName = getIntent().getStringExtra("imageName");

        FirebaseHelper firebaseHelper = new FirebaseHelper();

        // Increment num_read for this story
        if (storyId != null) {
            firebaseHelper.incrementStoryReadCount(storyId);
        }

        // Display data on screen
        titleTextView.setText(title);
        authorTextView.setText(author);
        yearTextView.setText(year);
        contentTextView.setText(content);

        // Load image with Glide
        int imageResourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (imageResourceId != 0) {
            Glide.with(this)
                    .load(imageResourceId)
                    .into(storyImageView);
        } else {
            Log.e("Glide", "Image not found in drawable: " + imageName);
        }

        // Display Play & Stop buttons in the correct language
        switch (AppConfig.selectedLanguage) {
            case "gr":
                playButton.setText(R.string.play_button_gr);
                stopButton.setText(R.string.stop_button_gr);
                titleTextView.setText(getString(R.string.title_label_gr) + ": " + title);
                authorTextView.setText(getString(R.string.author_label_gr) + ": " + author);
                yearTextView.setText(getString(R.string.year_label_gr) + ": " + year);
                break;
            case "fr":
                playButton.setText(R.string.play_button_fr);
                stopButton.setText(R.string.stop_button_fr);
                titleTextView.setText(getString(R.string.title_label_fr) + ": " + title);
                authorTextView.setText(getString(R.string.author_label_fr) + ": " + author);
                yearTextView.setText(getString(R.string.year_label_fr) + ": " + year);
                break;
            default:
                playButton.setText(R.string.play_button);
                stopButton.setText(R.string.stop_button);
                titleTextView.setText(getString(R.string.title_label) + ": " + title);
                authorTextView.setText(getString(R.string.author_label) + ": " + author);
                yearTextView.setText(getString(R.string.year_label) + ": " + year);
                break;
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = getLocaleForLanguage(AppConfig.selectedLanguage);
                int result = textToSpeech.setLanguage(locale);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported!");
                } else {
                    textToSpeech.setPitch(1.0f); // Manipulate voice
                    textToSpeech.setSpeechRate(1.0f); // Speech speed
                }
            } else {
                Log.e("TTS", "Initialization failed!");
            }
        });

        // Play Button (text to speech)
        playButton.setOnClickListener(v -> {
            if (content != null && !content.trim().isEmpty()) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop(); // Stop active speaker in case of pressing play twice before second starts
                }
                if (content.length() > 4000) {
                    for (int i = 0; i < content.length(); i += 4000) {
                        int end = Math.min(content.length(), i + 4000);
                        Log.d("TTS_DEBUG", "Reading content: " + content);
                        textToSpeech.speak(content.substring(i, end), TextToSpeech.QUEUE_ADD, null, null);
                    }
                } else {
                    Log.d("TTS_DEBUG", "Reading content: " + content);
                    textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                Log.e("TTS", "No content to read!");
            }
        });

        // Stop Button
        stopButton.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        });
    }

    // Helper method to get the correct Locale for each language
    private Locale getLocaleForLanguage(String language) {
        switch (language.toLowerCase()) {
            case "gr":
                return new Locale("el", "GR");
            case "fr":
                return Locale.FRENCH;
            default:
                return Locale.ENGLISH;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}