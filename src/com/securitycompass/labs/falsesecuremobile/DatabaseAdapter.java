package com.securitycompass.labs.falsesecuremobile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    private static final String TAG = "DatabaseAdapter";
    private final static String CREATE_ACCOUNTS = "CREATE TABLE accounts (_id integer primary key autoincrement, account_number integer not null, type text not null, balance real not null)";
    private final static String CREATE_USERS = "CREATE TABLE users (_id integer primary key autoincrement, username text not null, password text not null)";
    private final static String DATABASE_NAME = "FalseSecureMobile";
    private final static int DATABASE_VERSION = 1;

    private Context mCtx;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Initialises the adapter
     * 
     * @param ctx
     *            The application context
     */
    public DatabaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Opens the database for use
     * 
     * @return A working hook into the database
     * @throws SQLException
     */
    public DatabaseAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            throw new AssertionError("Database failed immediately after being opened");
        }
        return this;
    }

    /**
     * Closes the database
     */
    public void close() {
        dbHelper.close();
    }

    /** Gets a list of accounts, including all of their details */
    public List<Account> getAccounts() {
        String[] parameters = new String[0];
        Cursor c = db.rawQuery("SELECT * FROM accounts", parameters);
        c.moveToFirst();
        List<Account> accountList = new ArrayList<Account>();
        while (!c.isAfterLast()) {
            int accountNumber = c.getInt(1);
            String accountType = c.getString(2);
            double balance = c.getDouble(3);
            Account currentAccount = new Account(accountNumber, accountType, balance);
            accountList.add(currentAccount);
            c.moveToNext();
        }
        c.close();
        return accountList;
    }

    /** Returns a the username and password stored in the database. 
     * @return 2 element array containing the username and password
     * */
    public String[] getUserPass(){
        String[] parameters=new String[0];
        Cursor c = db.rawQuery("SELECT * FROM users LIMIT 1", parameters);
        c.moveToFirst();
        String[] results={null, null};
        if(!c.isAfterLast()){
            results[0]=c.getString(1);
            results[1]=c.getString(2);
        }
        c.close();
        return results;
    }
    
    /** Sets the balance for the given account number to the specified amount. 
     * @param accountNumber The account number to set the balance for.
     * @param newBalance The new value of the account's balance
     * */
    public void setBalance(int accountNumber, double newBalance) {
        // TODO: Set the DB value
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Helper class that deals with database creation, upgrade, and opening */
    private class DatabaseHelper extends SQLiteOpenHelper {

        /** Simple constructor. */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /** Create our tables in the DB
         * @param db The database being created */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ACCOUNTS);
            db.execSQL(CREATE_USERS);
        }

        /** Called when upgrading the DB 
         * @param db The database being upgraded
         * @param oldVersion The old database version
         * @param newVersion The new database version
         * */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS accounts");
            onCreate(db);
        }

        /** Called when the DB is opened
         * @param dataB The database which is being opened
         */
        @Override
        public void onOpen(SQLiteDatabase dataB) {
            Log.i(TAG, "Opened database");
        }

    }
}
