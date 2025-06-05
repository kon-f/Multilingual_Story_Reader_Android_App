package com.example.unipiaudiostoriesapp.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unipiaudiostoriesapp.R;
import com.example.unipiaudiostoriesapp.utils.AppConfig;

public class WelcomeActivity extends AppCompatActivity {
    private Spinner languageSpinner;
    private Button btnContinue;
    private String selectedLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView textWelcome = findViewById(R.id.textWelcome);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Add choices in Choose Language Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Change welcome message dynamically
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedLanguage = "en";
                        textWelcome.setText(getString(R.string.welcome_message));
                        btnContinue.setText(getString(R.string.continue_button));
                        break;
                    case 1:
                        selectedLanguage = "gr";
                        textWelcome.setText(getString(R.string.welcome_message_gr));
                        btnContinue.setText(getString(R.string.continue_button_gr));
                        break;
                    case 2:
                        selectedLanguage = "fr";
                        textWelcome.setText(getString(R.string.welcome_message_fr));
                        btnContinue.setText(getString(R.string.continue_button_fr));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Continue button logic
        btnContinue.setOnClickListener(v -> {
            AppConfig.selectedLanguage = selectedLanguage;
            Intent intent = new Intent(WelcomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private String getLanguageCode(int position) {
        switch (position) {
            case 1: return "el";
            case 2: return "fr";
            default: return "en";
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
