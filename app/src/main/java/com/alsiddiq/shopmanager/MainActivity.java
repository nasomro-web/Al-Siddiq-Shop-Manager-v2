
package com.alsiddiq.shopmanager;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    final int GREEN = Color.rgb(20, 125, 75);
    final int DARK = Color.rgb(15, 85, 55);
    final int LIGHT = Color.rgb(235, 247, 239);
    DB db;
    LinearLayout main;
    TextView summary;

    String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        db = new DB(this);
        home();
    }

    TextView text(String s, int size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(12, 10, 12, 10);
        return t;
    }

    void home() {
        main = new LinearLayout(this);
        main.setOrientation(1);
        main.setPadding(16, 12, 16, 16);
        main.setBackgroundColor(LIGHT);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(main);
        setContentView(scroll);

        TextView heading = text("AL-SIDDIQ SHOP MANAGER", 23, Color.WHITE);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setBackgroundColor(DARK);
        main.addView(heading);

        main.addView(text("Shop Accounting & Management", 14, GREEN));

        summary = text("", 16, DARK);
        summary.setBackgroundColor(Color.WHITE);
        main.addView(summary);
        refreshSummary();

        addButton("＋ NEW BOOKING", () -> bookingForm());
        addButton("＋ NEW EXPENSE", () -> expenseForm());

        addButton("CUSTOMER KHATA", () -> khata());
        addButton("DAILY REPORT", () -> report("Daily"));
        addButton("WEEKLY REPORT", () -> report("Weekly"));
        addButton("MONTHLY REPORT", () -> report("Monthly"));
        addButton("ALL BOOKINGS", () -> allBookings());
        addButton("EXPORT ACCOUNTS (CSV)", () -> exportCSV());
        addButton("BACKUP & SETTINGS", () -> settings());
    }

    void addButton(String name, Runnable action) {
        Button b = new Button(this);
        b.setText(name);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(GREEN));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            -1, -2);
        p.setMargins(0, 7, 0, 7);
        main.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    void refreshSummary() {
        SQLiteDatabase d = db.getReadableDatabase();

        double fees = sum(d, "SELECT IFNULL(SUM(service_fee),0) FROM bookings");
        double expenses = sum(d, "SELECT IFNULL(SUM(amount),0) FROM expenses");
        double due = sum(d, "SELECT IFNULL(SUM(due),0) FROM bookings");
        double received = sum(d, "SELECT IFNULL(SUM(paid),0) FROM bookings");
        double govt = sum(d, "SELECT IFNULL(SUM(gov_fee),0) FROM bookings");

        summary.setText(
            "\nTODAY: " + today() +
            "\n\nService Charges: Rs. " + fees +
            "\nGovernment Fees: Rs. " + govt +
            "\nCustomer Payments: Rs. " + received +
            "\nCustomer Remaining Due: Rs. " + due +
            "\nShop Expenses: Rs. " + expenses +
            "\nEstimated Business Profit: Rs. " + (fees - expenses) + "\n"
        );
    }

    double sum(SQLiteDatabase d, String q) {
        Cursor c = d.rawQuery(q, null);
        double value = 0;
        if (c.moveToFirst()) value = c.getDouble(0);
        c.close();
        return value;
    }

    EditText field(LinearLayout box, String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        box.addView(e);
        return e;
    }

    double number(EditText e) {
        try { return Double.parseDouble(e.getText().toString().trim()); }
        catch (Exception ex) { return 0; }
    }

    void bookingForm() {
        LinearLayout box = new LinearLayout(this);
        box.setPadding(15, 5, 15, 5);
        box.setOrientation(1);

        EditText name = field(box, "Customer Name *");
        EditText mobile = field(box, "Mobile Number");
        EditText village = field(box, "Village");
        EditText service = field(box, "Service Name (NADRA, B-Form, Printing...)");
        EditText gov = field(box, "Government Fee (Rs.)");
        EditText fee = field(box, "Service Charges (Rs.)");
        EditText paid = field(box, "Amount Received (Rs.)");

        gov.setInputType(2);
        fee.setInputType(2);
        paid.setInputType(2);

        new AlertDialog.Builder(this)
            .setTitle("New Customer Booking")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("SAVE BOOKING", (dialog, which) -> {
                String n = name.getText().toString().trim();
                if (n.isEmpty()) {
                    msg("Customer name is required");
                    return;
                }

                double g = number(gov);
                double f = number(fee);
                double p = number(paid);
                double total = g + f;

                if (g < 0 || f < 0 || p < 0 || p > total) {
                    msg("Please check the amounts");
                    return;
                }

                SQLiteDatabase d = db.getWritableDatabase();
                android.content.ContentValues v = new android.content.ContentValues();
                v.put("date", today());
                v.put("name", n);
                v.put("mobile", mobile.getText().toString());
                v.put("village", village.getText().toString());
                v.put("service", service.getText().toString());
                v.put("gov_fee", g);
                v.put("service_fee", f);
                v.put("total", total);
                v.put("paid", p);
                v.put("due", total - p);

                d.insert("bookings", null, v);
                msg("Booking saved successfully");
                home();
            })
            .show();
    }

    void expenseForm() {
        LinearLayout box = new LinearLayout(this);
        box.setPadding(15, 5, 15, 5);
        box.setOrientation(1);

        EditText category = field(box, "Expense Category");
        EditText details = field(box, "Details");
        EditText amount = field(box, "Amount (Rs.)");
        amount.setInputType(2);

        new AlertDialog.Builder(this)
            .setTitle("New Shop Expense")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("SAVE", (dialog, which) -> {
                double a = number(amount);
                if (a <= 0) {
                    msg("Enter a valid amount");
                    return;
                }

                android.content.ContentValues v = new android.content.ContentValues();
                v.put("date", today());
                v.put("category", category.getText().toString());
                v.put("details", details.getText().toString());
                v.put("amount", a);

                db.getWritableDatabase().insert("expenses", null, v);
                msg("Expense saved");
                home();
            })
            .show();
    }

    void report(String period) {
        String start = today();

        if (period.equals("Weekly"))
            start = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(new Date(System.currentTimeMillis() - 6L * 86400000));

        if (period.equals("Monthly"))
            start = today().substring(0, 8) + "01";

        SQLiteDatabase d = db.getReadableDatabase();

        String condition = "date >= ? AND date <= ?";
        String[] args = {start, today()};

        double service = querySum(d,
            "SELECT IFNULL(SUM(service_fee),0) FROM bookings WHERE " + condition, args);
        double govt = querySum(d,
            "SELECT IFNULL(SUM(gov_fee),0) FROM bookings WHERE " + condition, args);
        double paid = querySum(d,
            "SELECT IFNULL(SUM(paid),0) FROM bookings WHERE " + condition, args);
        double due = querySum(d,
            "SELECT IFNULL(SUM(due),0) FROM bookings WHERE " + condition, args);
        double expense = querySum(d,
            "SELECT IFNULL(SUM(amount),0) FROM expenses WHERE " + condition, args);

        String result =
            period + " Report\n\n" +
            "From: " + start + "\nTo: " + today() +
            "\n\nService Charges: Rs. " + service +
            "\nGovernment Fees: Rs. " + govt +
            "\nCustomer Payments: Rs. " + paid +
            "\nRemaining Customer Due: Rs. " + due +
            "\nShop Expenses: Rs. " + expense +
            "\n\nBusiness Profit: Rs. " + (service - expense);

        new AlertDialog.Builder(this)
            .setTitle(period + " Accounts")
            .setMessage(result)
            .setPositiveButton("OK", null)
            .show();
    }

    double querySum(SQLiteDatabase d, String q, String[] args) {
        Cursor c = d.rawQuery(q, args);
        double x = 0;
        if (c.moveToFirst()) x = c.getDouble(0);
        c.close();
        return x;
    }

    void khata() {
        SQLiteDatabase d = db.getReadableDatabase();
        Cursor c = d.rawQuery(
            "SELECT name,mobile,SUM(total),SUM(paid),SUM(due) " +
            "FROM bookings GROUP BY name,mobile ORDER BY name", null);

        StringBuilder s = new StringBuilder();
        while (c.moveToNext()) {
            s.append("Customer: ").append(c.getString(0))
             .append("\nMobile: ").append(c.getString(1))
             .append("\nTotal: Rs. ").append(c.getDouble(2))
             .append("\nPaid: Rs. ").append(c.getDouble(3))
             .append("\nRemaining: Rs. ").append(c.getDouble(4))
             .append("\n--------------------\n");
        }
        c.close();

        if (s.length() == 0) s.append("No customer records yet.");

        new AlertDialog.Builder(this)
            .setTitle("Customer Khata")
            .setMessage(s.toString())
            .setPositiveButton("Close", null)
            .show();
    }

    void allBookings() {
        Cursor c = db.getReadableDatabase().rawQuery(
            "SELECT date,name,service,total,paid,due FROM bookings ORDER BY id DESC", null);

        StringBuilder s = new StringBuilder();
        while (c.moveToNext()) {
            s.append(c.getString(0)).append(" | ")
             .append(c.getString(1)).append("\n")
             .append(c.getString(2)).append("\n")
             .append("Total: ").append(c.getDouble(3))
             .append("  Paid: ").append(c.getDouble(4))
             .append("  Due: ").append(c.getDouble(5))
             .append("\n--------------------\n");
        }
        c.close();

        if (s.length() == 0) s.append("No bookings found.");

        new AlertDialog.Builder(this)
            .setTitle("All Bookings")
            .setMessage(s.toString())
            .setPositiveButton("Close", null)
            .show();
    }

    void exportCSV() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "AlSiddiq_Accounts.csv");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                StringBuilder csv = new StringBuilder();
                csv.append("Date,Customer,Mobile,Village,Service,Government Fee,Service Fee,Total,Paid,Due\n");

                Cursor c = db.getReadableDatabase().rawQuery(
                    "SELECT date,name,mobile,village,service,gov_fee,service_fee,total,paid,due FROM bookings ORDER BY id", null);

                while (c.moveToNext()) {
                    for (int i = 0; i < 10; i++) {
                        String value = c.getString(i);
                        if (value == null) value = "";
                        csv.append("\"").append(value.replace("\"", "\"\"")).append("\"");
                        if (i < 9) csv.append(",");
                    }
                    csv.append("\n");
                }
                c.close();

                java.io.OutputStream out = getContentResolver()
                    .openOutputStream(data.getData());
                out.write(csv.toString().getBytes("UTF-8"));
                out.close();
                msg("CSV export completed");
            } catch (Exception e) {
                msg("Export failed: " + e.getMessage());
            }
        }
    }

    void settings() {
        new AlertDialog.Builder(this)
            .setTitle("Shop Settings")
            .setMessage("Al-Siddiq Shop Manager\n\nGreen & White Edition\n\nYour records are stored locally on this phone.\n\nExport CSV regularly to keep an extra copy.")
            .setPositiveButton("OK", null)
            .show();
    }

    void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    class DB extends SQLiteOpenHelper {
        DB(Context c) {
            super(c, "alsiddiq.db", null, 2);
        }

        @Override
        public void onCreate(SQLiteDatabase d) {
            createTables(d);
        }

        @Override
        public void onUpgrade(SQLiteDatabase d, int oldVersion, int newVersion) {
            createTables(d);
        }

        void createTables(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT,name TEXT,mobile TEXT,village TEXT,service TEXT," +
                "gov_fee REAL,service_fee REAL,total REAL,paid REAL,due REAL)");

            d.execSQL("CREATE TABLE IF NOT EXISTS expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT,category TEXT,details TEXT,amount REAL)");
        }
    }
}
