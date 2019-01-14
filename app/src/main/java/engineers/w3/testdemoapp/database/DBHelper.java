package engineers.w3.testdemoapp.database;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Md. Imtiyaj on 1/14/2019.
 */

/*
 * Sqlite helper class to manage database creation and version management.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = SQLiteOpenHelper.class.getSimpleName();

    //Db version
    final static int DB_VERSION = 1;
    // Db version name
    final static String DB_NAME = "item.ndb";
    // Db table
    public static final String TABLE_ITEM = "item";
    // TBL_ITEM field names----------------------------------
    private final static String FLD_ITEM_ID = "[id]";
    private final static String FLD_ITEM = "[item]";
    private final static String FLD_ITEM_IMAGE = "[image]";

    Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    /**
     * Only run when the database file did not exist and was just created.
     * Once we've created the database on first launch of our application,
     * we can perform any operation SQL offers, including arithmetics.
     * As for where to put the .sql file
     *
     * @param db SQLiteDatabase: The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {
            executeSQLScript(db, "create.sql");
        } catch (SQLException e) {
        } catch (IOException e) {
        }
    }

    /**
     * onUpgrade() is only called when the database file exists
     * but the stored version number is lower than requested in constructor.
     * The onUpgrade() should update the table schema to the requested version.
     *
     * @param db         SQLiteDatabase: The database.
     * @param oldVersion int: The old database version.
     * @param newVersion int: The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            if (newVersion > oldVersion) {
                switch (oldVersion) {
                    case 1:
                        executeSQLScript(db, "update_v2.sql");
                }
            }
        } catch (SQLException e) {
        } catch (IOException e) {
        }
    }

    /**
     * @param database SQLiteDatabase: The database.
     * @param dbname   Database name
     * @throws IOException  Constructs an IOException with null as its error detail message.
     * @throws SQLException An exception that provides information on a database access error or other errors.
     */
    private void executeSQLScript(SQLiteDatabase database, String dbname) throws IOException, SQLException {
        //Creates a new byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //Creates a newly allocated byte array.
        byte buf[] = new byte[1024];
        int len;
        //Provides access to an application's raw asset files to retrieve their resource data
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            // Used for reading
            inputStream = assetManager.open(dbname);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {
                    try {
                        database.execSQL(sqlStatement + ";");
                    } catch (SQLException se) {
                        Log.e(TAG, se.toString(), se);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Handle Script Failed to Load
            Log.e(TAG, e.toString(), e);
            throw e;
        } catch (SQLException e) {
            // TODO Handle Script Failed to Execute
            Log.e(TAG, e.toString(), e);
            throw e;
        }
    }

}
