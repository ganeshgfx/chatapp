package ganesh.gfx.chatapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.main.chatPage.Chat;

public class Sms extends SQLiteOpenHelper {
    ////
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "smsManager";

    private static String SMS;

    private static final String KEY_ID = "id";//AI

    private static final String CHAT = "chat";
    private static final String SBY = "sendBy";
    private static final String TIME = "time";

    private static final String TAG = "appgfx";

    public Sms(Context context,String friendID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SMS = "sms_"+friendID;
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SMS_TABLE = "CREATE TABLE "+SMS+"("+KEY_ID+" INTEGER PRIMARY KEY " +
                "AUTOINCREMENT," + CHAT + " TEXT," + SBY+ " TEXT,"+TIME+" TEXT)";
        db.execSQL(CREATE_SMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SMS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public boolean addSms(Chat chat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CHAT, chat.getMessage());
        values.put(SBY, chat.getUserId());
        values.put(TIME, chat.getDbTime());

        boolean success = true;

        // Inserting Row
        try {
            db.insertOrThrow(SMS, null, values);

        }catch (Exception err){
            Log.d(TAG, "addChat Err : "+err.getMessage());
            success = false;
        }

        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
        return success;
    }

    // code to get the single contact
    public Chat getSms(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                SMS,
                new String[] { KEY_ID, CHAT, SBY, TIME},
                KEY_ID + "=?",
                new String[] {id},
                null,
                null,
                null,
                null);

        if( cursor.getCount()==0){
            return new Chat("","","","");
        }

        if (cursor != null)
            cursor.moveToFirst();

        Chat chat = new Chat(
                cursor.getString(0)+"",
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
        );
        // return contact
        return chat;

    }

    // code to get all contacts in a list view
    public List<Chat> getAllChats() {
        List<Chat> chatList = new ArrayList<Chat>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                chatList.add(new Chat(
                        cursor.getString(0)+"",
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        // return contact list
        return chatList;

    }

    // Deleting single contact
//    public void deleteChat(Chat chat) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(SMS, KEY_ID + " = ?",
//                new String[] { chat.getUserId() });
//        db.close();
//    }

    // Getting contacts Count
    public int getChatCount() {
        String countQuery = "SELECT  * FROM " + SMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }
    ////

}
