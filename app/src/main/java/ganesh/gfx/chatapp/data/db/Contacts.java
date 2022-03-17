package ganesh.gfx.chatapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.main.contactPage.ListData;

public class Contacts  extends SQLiteOpenHelper {
    ////
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String FRIENDS = "friends";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String TAG = "appgfx";

    public Contacts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + FRIENDS + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FRIENDS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addContact(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, friend.getName()); // Contact Name
        values.put(KEY_ID, friend.getID());

        // Inserting Row
        try {
            db.insertOrThrow(FRIENDS, null, values);
        }catch (Exception err){
            Log.d(TAG, "addContact: "+err.getMessage());
        }

        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Friend getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                FRIENDS,
                new String[] { KEY_ID, KEY_NAME },
                KEY_ID + "=?",
                new String[] {id},
                null,
                null,
                null,
                null);

        if( cursor.getCount()==0){
            return new Friend("","");
        }

        if (cursor != null)
            cursor.moveToFirst();

        Friend friend = new Friend(cursor.getString(0), cursor.getString(1));
        // return contact
        return friend;

    }

    // code to get all contacts in a list view
    public List<Friend> getAllContacts() {
        List<Friend> friendList = new ArrayList<Friend>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FRIENDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                friend.setID(cursor.getString(0));
                friend.setName(cursor.getString(1));
                // Adding contact to list
                friendList.add(friend);
            } while (cursor.moveToNext());
        }

        // return contact list
        return friendList;

    }

    public  Friend[] getAllContactsList() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FRIENDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Friend[] myListData = new Friend[cursor.getCount()];

        int count = 0;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                friend.setID(cursor.getString(0));
                friend.setName(cursor.getString(1));
                // Adding contact to list

                myListData[count] = friend;
                count++;

            } while (cursor.moveToNext());
        }

        // return contact list

        return myListData;
    }

    // code to update the single contact
    public int updateContact(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, friend.getName());

        // updating row
        return db.update(FRIENDS, values, KEY_ID + " = ?",
                new String[] { friend.getID()});
    }

    // Deleting single contact
    public void deleteContact(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FRIENDS, KEY_ID + " = ?",
                new String[] { friend.getID() });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + FRIENDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    ////

}
