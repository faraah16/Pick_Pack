package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_choice);

        findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeLoginActivity.class))
        );

        findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeRegisterActivity.class))
        );
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


    }
}
