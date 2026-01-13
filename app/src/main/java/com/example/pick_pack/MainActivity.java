package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pick_pack.ui.ClientProductsListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Pick_pack);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        StockManager.init(this);

        Button btnEmployee = findViewById(R.id.btnEmployee);
        Button btnClient = findViewById(R.id.btnClient);
        Button btnTrackOrder = findViewById(R.id.btnTrackOrder);


        btnEmployee.setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeChoiceActivity.class)));
        

        btnClient.setOnClickListener(v ->
                startActivity(new Intent(this, ClientProductsListActivity.class)));

        btnTrackOrder.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new TrackOrderFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}