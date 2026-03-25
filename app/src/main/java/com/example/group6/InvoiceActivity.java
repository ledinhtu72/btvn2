package com.example.group6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private TextView tvInvoiceDetails, tvInvoiceTotal;
    private ListView lvInvoiceItems;
    private Button btnBackHome;
    private DatabaseHelper dbHelper;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        dbHelper = new DatabaseHelper(this);
        tvInvoiceDetails = findViewById(R.id.tvInvoiceDetails);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        lvInvoiceItems = findViewById(R.id.lvInvoiceItems);
        btnBackHome = findViewById(R.id.btnBackHome);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        loadInvoice();

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void loadInvoice() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Load Order info
        Cursor orderCursor = db.rawQuery("SELECT o.order_date, u.username FROM " + DatabaseHelper.TABLE_ORDERS + " o " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON o.user_id = u.id " +
                "WHERE o.id = ?", new String[]{String.valueOf(orderId)});
        
        if (orderCursor.moveToFirst()) {
            String date = orderCursor.getString(0);
            String user = orderCursor.getString(1);
            tvInvoiceDetails.setText("Order ID: " + orderId + "\nDate: " + date + "\nCustomer: " + user);
        }
        orderCursor.close();

        // Load Items
        List<String> items = new ArrayList<>();
        double total = 0;
        String query = "SELECT p.name, od.quantity, od.price FROM " + DatabaseHelper.TABLE_ORDER_DETAILS + " od " +
                "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";
        Cursor detailCursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        
        if (detailCursor.moveToFirst()) {
            do {
                String name = detailCursor.getString(0);
                int qty = detailCursor.getInt(1);
                double price = detailCursor.getDouble(2);
                double subtotal = qty * price;
                total += subtotal;
                items.add(name + " x" + qty + " = $" + subtotal);
            } while (detailCursor.moveToNext());
        }
        detailCursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvInvoiceItems.setAdapter(adapter);
        tvInvoiceTotal.setText("Total Paid: $" + total);
    }
}
