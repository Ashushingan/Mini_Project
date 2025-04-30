package com.example.multicamstreamer;// MainActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons to choose Host or Client
        Button btnHost = findViewById(R.id.btnHost);
        Button btnClient = findViewById(R.id.btnClient);

        // Launch Host Activity when clicked
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HostActivity for the Host functionality
                Intent hostIntent = new Intent(MainActivity.this, HostActivity.class);
                startActivity(hostIntent);
            }
        });

        // Launch Client Activity when clicked
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ClientActivity for the Client functionality
                Intent clientIntent = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(clientIntent);
            }
        });
    }
}
