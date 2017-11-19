package io.phoenyx.tapstreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Terrance on 11/18/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.back_button);
    }
}
