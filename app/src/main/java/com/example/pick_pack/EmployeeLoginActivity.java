package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployeeLoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etEmail = findViewById(R.id.email);
        EditText etPassword = findViewById(R.id.password);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, EmployeeChoiceActivity.class));
            finish();
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        String uid = auth.getCurrentUser().getUid();

                        // 🔥 VÉRIFICATION FIRESTORE
                        db.collection("employees")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(doc -> {

                                    if (doc.exists()) {
                                        // ✅ C'EST UN EMPLOYEE
                                        startActivity(new Intent(
                                                this,
                                                EmployeeDashboardActivity.class
                                        ));
                                        finish();
                                    } else {
                                        // ❌ PAS EMPLOYEE
                                        auth.signOut();
                                        Toast.makeText(
                                                this,
                                                "Access denied (not an employee)",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    auth.signOut();
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
                                    "Wrong credentials",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
        });
    }
}
