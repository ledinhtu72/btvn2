package com.example.group6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private ListView lvProducts;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private List<Product> productList;
    private int categoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        lvProducts = findViewById(R.id.lvProducts);
        tvTitle = findViewById(R.id.tvTitle);
        dbHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();

        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        loadProducts();

        List<String> productDisplay = new ArrayList<>();
        for (Product p : productList) {
            productDisplay.add(p.getName() + " - $" + p.getPrice());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productDisplay);
        lvProducts.setAdapter(adapter);

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", productList.get(position).getId());
            startActivity(intent);
        });
    }

    private void loadProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS;
        String[] args = null;

        if (categoryId != -1) {
            query += " WHERE category_id = ?";
            args = new String[]{String.valueOf(categoryId)};
            tvTitle.setText("Category Products");
        }

        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                productList.add(new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
