package com.example.group6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_PRODUCTS = "Products";
    public static final String TABLE_ORDERS = "Orders";
    public static final String TABLE_ORDER_DETAILS = "OrderDetails";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Tables
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, email TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, category_id INTEGER, description TEXT, FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, order_date TEXT, status TEXT, FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id))");
        db.execSQL("CREATE TABLE " + TABLE_ORDER_DETAILS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER, product_id INTEGER, quantity INTEGER, price REAL, FOREIGN KEY(order_id) REFERENCES " + TABLE_ORDERS + "(id), FOREIGN KEY(product_id) REFERENCES " + TABLE_PRODUCTS + "(id))");

        // Seed Data
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // Users
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, email) VALUES ('admin', 'admin', 'admin@example.com')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, email) VALUES ('user', 'admin123', 'user@example.com')");

        // Categories
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name) VALUES ('Electronics')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name) VALUES ('Clothing')");

        // Products
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price, category_id, description) VALUES ('Laptop', 1200.0, 1, 'High performance laptop')");
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price, category_id, description) VALUES ('Smartphone', 800.0, 1, 'Latest smartphone')");
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " (name, price, category_id, description) VALUES ('T-Shirt', 20.0, 2, 'Cotton t-shirt')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
