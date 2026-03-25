package com.example.group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnLogout, btnViewCategories, btnViewProducts;
    private TextView tvWelcome;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewCategories = findViewById(R.id.btnViewCategories);
        btnViewProducts = findViewById(R.id.btnViewProducts);

        updateUI();

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            updateUI();
        });

        btnViewCategories.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoryListActivity.class)));

        btnViewProducts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProductListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            tvWelcome.setText("Welcome, " + sessionManager.getUsername() + "!");
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvWelcome.setText("Welcome to Shopping App");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }
}
