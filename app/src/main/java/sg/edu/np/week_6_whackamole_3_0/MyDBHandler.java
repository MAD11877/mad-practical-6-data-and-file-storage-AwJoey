package sg.edu.np.week_6_whackamole_3_0;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WhackAMole.db";
    private static final String TABLE_ACCOUNTS = "accounts";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_LEVELS = "Level";
    public static final String COLUMN_SCORES = "Score";

    /*
        The Database has the following properties:
        1. Database name is WhackAMole.db
        2. The Columns consist of
            a. Username
            b. Password
            c. Level
            d. Score
        3. Add user method for adding user into the Database.
        4. Find user method that finds the current position of the user and his corresponding
           data information - username, password, level highest score for each level
        5. Delete user method that deletes based on the username
        6. To replace the data in the database, we would make use of find user, delete user and add user

        The database shall look like the following:

        Username | Password | Level | Score
        --------------------------------------
        User A   | XXX      | 1     |    0
        User A   | XXX      | 2     |    0
        User A   | XXX      | 3     |    0
        User A   | XXX      | 4     |    0
        User A   | XXX      | 5     |    0
        User A   | XXX      | 6     |    0
        User A   | XXX      | 7     |    0
        User A   | XXX      | 8     |    0
        User A   | XXX      | 9     |    0
        User A   | XXX      | 10    |    0
        User B   | YYY      | 1     |    0
        User B   | YYY      | 2     |    0

     */

    private static final String FILENAME = "MyDBHandler.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        /* HINT:
            This is used to init the database.
         */
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " +
                TABLE_ACCOUNTS +"("
                + COLUMN_USERNAME + " STRING, "
                + COLUMN_PASSWORD + " STRING, "
                + COLUMN_LEVELS + " INTEGER, "
                + COLUMN_SCORES + " INTEGER " +
                ")";

        db.execSQL(CREATE_ACCOUNTS_TABLE);
        Log.v("Whack-A-Mole 3.0", "DB Created:"  + CREATE_ACCOUNTS_TABLE);

        /* HINT:
            This is triggered on DB creation.
            Log.v(TAG, "DB Created: " + CREATE_ACCOUNTS_TABLE);
         */
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        onCreate(db);

        /* HINT:
            This is triggered if there is a new version found. ALL DATA are replaced and irreversible.
         */
    }

    public void addUser(UserData userData)
    {
        ArrayList<Integer> userLevels =userData.getLevels();
        ArrayList<Integer> userScores = userData.getScores();
        SQLiteDatabase db = this.getWritableDatabase();

        for (int row = 0; row < 10; row++){
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, userData.getMyUserName());
            values.put(COLUMN_PASSWORD, userData.getMyPassword());
            values.put(COLUMN_LEVELS, userLevels.get(row));
            values.put(COLUMN_SCORES, userScores.get(row));
            Log.v("Whack-A-Mole 3.0", FILENAME + ": Adding data for Database: " + values.toString());
            db.insert(TABLE_ACCOUNTS, null, values);
        }
        db.close();

            /* HINT:
                This adds the user to the database based on the information given.
                Log.v(TAG, FILENAME + ": Adding data for Database: " + values.toString());
             */
    }

    public UserData findUser(String username)
    {
        String query = "SELECT * FROM " + TABLE_ACCOUNTS + " WHERE "
                        + COLUMN_USERNAME
                        + " = \"" + username + "\"";
        Log.v("Whack-A-Mole 3.0", FILENAME + ": Find user from database: " + query);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        UserData userData = new UserData();
        ArrayList<Integer> userLevels = new ArrayList<Integer>();
        ArrayList<Integer> userScores = new ArrayList<Integer>();

        if (cursor.moveToFirst()){
            do {
                userData.setMyUserName(cursor.getString(0));
                userData.setMyPassword(cursor.getString(1));
                userLevels.add(cursor.getInt(2));
                userScores.add(cursor.getInt(3));
            }
            while (cursor.moveToNext());
            userData.setLevels(userLevels);
            userData.setScores(userScores);
        }
        else{
            userData = null;
            Log.v("Whack-A-Mole 3.0", FILENAME + ": No data found!");
        }
        return userData;
        /* HINT:
            This finds the user that is specified and returns the data information if it is found.
            If not found, it will return a null.
            Log.v(TAG, FILENAME +": Find user form database: " + query);

            The following should be used in getting the query data.
            you may modify the code to suit your design.

            if(cursor.moveToFirst()){
                do{
                    ...
                    .....
                    ...
                }while(cursor.moveToNext());
                Log.v(TAG, FILENAME + ": QueryData: " + queryData.getLevels().toString() + queryData.getScores().toString());
            }
            else{
                Log.v(TAG, FILENAME+ ": No data found!");
            }
         */
    }

    public boolean deleteAccount(String username) {

        boolean result = false;

        String query = "SELECT * FROM " + TABLE_ACCOUNTS + " WHERE "
                + COLUMN_USERNAME + " = \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        UserData userData = new UserData();

        if (cursor.moveToFirst()) {
            userData.setMyUserName(cursor.getString(0));
            db.delete(TABLE_ACCOUNTS, COLUMN_USERNAME + " =? ",
                    new String[]{userData.getMyUserName()});
            cursor.close();
            result = true;
            Log.v("Whack-A-Mole 3.0", ": Database delete user: " + query);
        }
        db.close();
        return result;
    }
        /* HINT:
            This finds and delete the user data in the database.
            This is not reversible.
            Log.v(TAG, FILENAME + ": Database delete user: " + query);
         */

}
