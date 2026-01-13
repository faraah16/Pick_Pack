package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EmployeeRegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etFirstName = findViewById(R.id.firstName);
        EditText etLastName = findViewById(R.id.lastName);
        EditText etEmail = findViewById(R.id.email);
        EditText etPassword = findViewById(R.id.password);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, EmployeeChoiceActivity.class));
            finish();
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {

            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()
                    || email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        String uid = auth.getCurrentUser().getUid();

                        HashMap<String, Object> emp = new HashMap<>();
                        emp.put("firstName", firstName);
                        emp.put("lastName", lastName);
                        emp.put("email", email);
                        emp.put("role", "employee");

                        db.collection("employees")
                                .document(uid)
                                .set(emp)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(
                                            this,
                                            "Employee registered",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // 👉 Retour login
                                    startActivity(new Intent(
                                            this,
                                            EmployeeLoginActivity.class
                                    ));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // ❌ Nettoyage si Firestore échoue
                                    auth.getCurrentUser().delete();
                                    Toast.makeText(
                                            this,
                                            "Firestore error",
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );

        });
    }
}
