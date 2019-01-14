package engineers.w3.testdemoapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import engineers.w3.testdemoapp.app.AppController;
import engineers.w3.testdemoapp.model.Item;

import static engineers.w3.testdemoapp.database.DBHelper.TABLE_ITEM;

/**
 * Created by Md. Imtiyaj on 1/14/2019.
 */

/*
 * Data access object class
 * Data Access Objects are the main classes where we define our database interactions
 */
public class DAO {
    private static final String TAG = DAO.class.getSimpleName();

    // Database fields
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        if (db != null && db.isOpen())
            db.close();
    }

    /*
    Open any close database object
     */
    public void open() throws SQLException {
        //Create and/or open a database that will be used for reading and writing.
        db = dbHelper.getWritableDatabase();
    }

    /*
     Close any open database object.
     */
    public void close() {
        dbHelper.close();
    }

    /*
    select query for retrieving data from table
    returns a set of rows and columns in a Cursor.
     */
    public Cursor getRecordsCursor(String sql, String[] param) {
        Cursor curs = null;
        curs = db.rawQuery(sql, param);
        return curs;
    }


    /*
    execSQL doesn't return anything and used for creating,updating, replacing
     */
    public void execSQL(String sql, String[] param) throws SQLException {
        db.execSQL(sql, param);
    }

    /*
    executeSQL doesn't return anything and used for creating,updating, replacing
    Not need to create extra DAO object .
     */
    public static void executeSQL(String sql, String[] param) {
        DAO da = new DAO(AppController.getInstance());
        da.open();
        try {
            da.execSQL(sql, param);
        } catch (Exception e) {
            throw e;
        } finally {
            da.close();
        }
    }

    /*
      Getting all item from database
     */
    public ArrayList<Item> gettingAllItem() {
        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        Item item = null;
        Cursor curs = null;

        try {
            curs = db.query(TABLE_ITEM, new String[]{"[title]", "[image]"},
                    null, null, null, null, null);

            if (curs.moveToFirst()) {
                do {
                    item = new Item(curs.getString(0), curs.getString(1));
                    itemArrayList.add(item);
                } while (curs.moveToNext());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());

        } finally {
            if (curs != null)
                curs.close();
        }
        //return item list
        return itemArrayList;
    }

    /**
     * count all row int item table
     *
     * @return total count row
     */
    public int getRowCount() {
        Cursor cursor = null;
        String countQuery = "SELECT  * FROM " + TABLE_ITEM;
        cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
