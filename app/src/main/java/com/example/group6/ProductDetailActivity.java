package com.example.group6;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPrice, tvDescription;
    private EditText etQuantity;
    private Button btnAddToCart;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvDescription = findViewById(R.id.tvProductDescription);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        loadProductDetail();

        btnAddToCart.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            String qtyStr = etQuantity.getText().toString();
            if (TextUtils.isEmpty(qtyStr) || Integer.parseInt(qtyStr) <= 0) {
                etQuantity.setError("Enter valid quantity");
                return;
            }

            addToCart(Integer.parseInt(qtyStr));
        });
    }

    private void loadProductDetail() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE id = ?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description"))
            );
            tvName.setText(product.getName());
            tvPrice.setText("$" + product.getPrice());
            tvDescription.setText(product.getDescription());
        }
        cursor.close();
    }

    private void addToCart(int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int userId = sessionManager.getUserId();

        // 1. Find or Create an OPEN order
        int orderId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE user_id = ? AND status = 'OPEN'", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            orderId = cursor.getInt(0);
        } else {
            ContentValues orderValues = new ContentValues();
            orderValues.put("user_id", userId);
            orderValues.put("order_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            orderValues.put("status", "OPEN");
            orderId = (int) db.insert(DatabaseHelper.TABLE_ORDERS, null, orderValues);
        }
        cursor.close();

        // 2. Add to OrderDetails
        ContentValues detailValues = new ContentValues();
        detailValues.put("order_id", orderId);
        detailValues.put("product_id", productId);
        detailValues.put("quantity", quantity);
        detailValues.put("price", product.getPrice());
        db.insert(DatabaseHelper.TABLE_ORDER_DETAILS, null, detailValues);

        showDecisionDialog();
    }

    private void showDecisionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Added to Cart")
                .setMessage("Product added successfully. Do you want to continue shopping or checkout?")
                .setPositiveButton("Checkout", (dialog, which) -> {
                    startActivity(new Intent(ProductDetailActivity.this, OrderSummaryActivity.class));
                    finish();
                })
                .setNegativeButton("Continue Shopping", (dialog, which) -> {
                    finish();
                })
                .show();
    }
}
