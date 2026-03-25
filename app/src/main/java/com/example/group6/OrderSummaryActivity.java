package com.example.group6;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {
    private ListView lvOrderItems;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int orderId = -1;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        lvOrderItems = findViewById(R.id.lvOrderItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        loadOrderSummary();

        btnCheckout.setOnClickListener(v -> {
            if (orderId != -1) {
                checkoutOrder();
            } else {
                Toast.makeText(this, "No active order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderSummary() {
        int userId = sessionManager.getUserId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Find current OPEN order
        Cursor orderCursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE user_id = ? AND status = 'OPEN'", new String[]{String.valueOf(userId)});
        if (orderCursor.moveToFirst()) {
            orderId = orderCursor.getInt(0);
        }
        orderCursor.close();

        if (orderId == -1) {
            tvTotalAmount.setText("Total: $0.0");
            return;
        }

        // Load details joined with product info
        List<String> items = new ArrayList<>();
        String query = "SELECT p.name, od.quantity, od.price FROM " + DatabaseHelper.TABLE_ORDER_DETAILS + " od " +
                "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";
        Cursor detailCursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        
        totalAmount = 0;
        if (detailCursor.moveToFirst()) {
            do {
                String name = detailCursor.getString(0);
                int qty = detailCursor.getInt(1);
                double price = detailCursor.getDouble(2);
                double subtotal = qty * price;
                totalAmount += subtotal;
                items.add(name + " x" + qty + " = $" + subtotal);
            } while (detailCursor.moveToNext());
        }
        detailCursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvOrderItems.setAdapter(adapter);
        tvTotalAmount.setText("Total: $" + totalAmount);
    }

    private void checkoutOrder() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "PAID");
        db.update(DatabaseHelper.TABLE_ORDERS, values, "id = ?", new String[]{String.valueOf(orderId)});
        
        Toast.makeText(this, "Checkout successful!", Toast.LENGTH_LONG).show();
        
        Intent intent = new Intent(this, InvoiceActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
        finish();
    }
}
