package com.alsiddiq.shopmanager;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.*;
import android.graphics.Typeface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    SQLiteDatabase db;
    LinearLayout layout;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        DB helper = new DB(this);
        db = helper.getWritableDatabase();

        showHome();
    }

    class DB extends SQLiteOpenHelper {
        DB(Context c) {
            super(c, "alsiddiq.db", null, 1);
        }

        public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE booking(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,name TEXT,mobile TEXT,village TEXT,service TEXT,copies TEXT,amount TEXT,advance TEXT,due TEXT,notes TEXT)");
            d.execSQL("CREATE TABLE nadra(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,name TEXT,mobile TEXT,cnic TEXT,village TEXT,tracking TEXT,training TEXT,govfee TEXT,servicefee TEXT,status TEXT,notes TEXT)");
            d.execSQL("CREATE TABLE printer(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,opening TEXT,closing TEXT,black TEXT,color TEXT,income TEXT)");
            d.execSQL("CREATE TABLE stock(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,item TEXT,opening TEXT,purchased TEXT,used TEXT,closing TEXT,cost TEXT)");
            d.execSQL("CREATE TABLE expenses(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,category TEXT,details TEXT,amount TEXT)");
        }

        public void onUpgrade(SQLiteDatabase d,int oldV,int newV) {}
    }

    void showHome() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        TextView title = new TextView(this);
        title.setText("Al-Siddiq Shop Manager");
        title.setTextSize(25);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        addButton("New Customer Booking", () -> booking());
        addButton("NADRA / E-Sahulat Record", () -> nadra());
        addButton("Printer Daily Reading", () -> printer());
        addButton("Paper & Stock Record", () -> stock());
        addButton("Shop Expenses", () -> expense());
        addButton("View All Records", () -> records());

        setContentView(scroll);
    }

    void addButton(String text,Runnable action) {
        Button b = new Button(this);
        b.setText(text);
        layout.addView(b);
        b.setOnClickListener(v -> action.run());
    }

    EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        layout.addView(e);
        return e;
    }

    String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    void save(String table,String[] columns,EditText[] fields) {
        ContentValues values = new ContentValues();
        values.put("date", today());

        for(int i=0;i<columns.length;i++)
            values.put(columns[i], fields[i].getText().toString());

        db.insert(table,null,values);
        Toast.makeText(this,"Record Saved Successfully",Toast.LENGTH_LONG).show();
        showHome();
    }

    void booking() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        EditText[] f = {
            field("Customer Name"),
            field("Mobile Number"),
            field("Village"),
            field("Service"),
            field("Copies"),
            field("Total Amount"),
            field("Advance Paid"),
            field("Remaining Due"),
            field("Notes")
        };

        addButton("Save Booking", () ->
            save("booking",
            new String[]{"name","mobile","village","service","copies","amount","advance","due","notes"},f));

        addButton("Back",()->showHome());
        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void nadra() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        EditText[] f = {
            field("Customer Name"),
            field("Mobile Number"),
            field("CNIC Number"),
            field("Village"),
            field("Tracking Number"),
            field("Training ID"),
            field("Government Fee"),
            field("Service Charges"),
            field("Status"),
            field("Notes")
        };

        addButton("Save NADRA Record", () ->
            save("nadra",
            new String[]{"name","mobile","cnic","village","tracking","training","govfee","servicefee","status","notes"},f));

        addButton("Back",()->showHome());
        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void printer() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        EditText[] f = {
            field("Opening Reading"),
            field("Closing Reading"),
            field("Black Copies"),
            field("Color Copies"),
            field("Copy Income")
        };

        addButton("Save Printer Reading", () ->
            save("printer",
            new String[]{"opening","closing","black","color","income"},f));

        addButton("Back",()->showHome());
        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void stock() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        EditText[] f = {
            field("Item Name"),
            field("Opening Stock"),
            field("Purchased Quantity"),
            field("Used Quantity"),
            field("Closing Stock"),
            field("Unit Cost")
        };

        addButton("Save Stock", () ->
            save("stock",
            new String[]{"item","opening","purchased","used","closing","cost"},f));

        addButton("Back",()->showHome());
        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void expense() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        EditText[] f = {
            field("Expense Category"),
            field("Details"),
            field("Amount")
        };

        addButton("Save Expense", () ->
            save("expenses",
            new String[]{"category","details","amount"},f));

        addButton("Back",()->showHome());
        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void records() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        addButton("Bookings",()->showRecords("booking"));
        addButton("NADRA Records",()->showRecords("nadra"));
        addButton("Printer Records",()->showRecords("printer"));
        addButton("Stock Records",()->showRecords("stock"));
        addButton("Expenses",()->showRecords("expenses"));
        addButton("Back",()->showHome());

        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }

    void showRecords(String table) {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(20,20,20,20);

        Cursor c=db.rawQuery("SELECT * FROM "+table+" ORDER BY id DESC",null);

        while(c.moveToNext()) {
            TextView t=new TextView(this);
            StringBuilder row=new StringBuilder();
            for(int i=1;i<c.getColumnCount();i++)
                row.append(c.getColumnName(i)).append(": ")
                   .append(c.getString(i)).append("\n");

            t.setText(row.toString()+"----------------");
            t.setTextSize(16);
            layout.addView(t);
        }

        c.close();
        addButton("Back",()->records());

        ScrollView s=new ScrollView(this);
        s.addView(layout);
        setContentView(s);
    }
                      }
