package com.example.group6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {
    private ListView lvCategories;
    private DatabaseHelper dbHelper;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        lvCategories = findViewById(R.id.lvCategories);
        dbHelper = new DatabaseHelper(this);
        categoryList = new ArrayList<>();

        loadCategories();

        List<String> categoryNames = new ArrayList<>();
        for (Category c : categoryList) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryNames);
        lvCategories.setAdapter(adapter);

        lvCategories.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
            intent.putExtra("CATEGORY_ID", categoryList.get(position).getId());
            startActivity(intent);
        });
    }

    private void loadCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categoryList.add(new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
