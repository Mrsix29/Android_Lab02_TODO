package ca.nait.zli.lab02todoornot;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    static final String TAG = "DBManager";

    static final String DB_NAME = "Lab02TodoOrNot.db";
    static final int DB_VERSION = 1;
    static final String TABLE_TODO_NAME = "TodoName";
    static final String TABLE_TODO_ITEM = "TodoItem";

    //TodoName fields
    static final String C_ID = BaseColumns._ID;
    static final String C_TODO_NAME = "todo_name";


    //TodoItem fields
    static final String C_TODO_ID = "todo_id";
    static final String C_CONTENT = "content";
    static final String C_DATE = "date";
    static final String C_COMPLETED_FLAG = "completed";

    public DBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        String sql = "create table " + TABLE_TODO_NAME + " (" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_TODO_NAME + " text)";
        Log.d(TAG, sql);
        database.execSQL(sql);


        sql = "create table " + TABLE_TODO_ITEM + " (" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_TODO_ID + " integer, " + C_CONTENT + " text, " + C_DATE + " text, " + C_COMPLETED_FLAG + " text)";
        Log.d(TAG, sql);
        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TODO_NAME);
        db.execSQL("drop table if exists " + TABLE_TODO_ITEM);
        Log.d(TAG, "onUpdated");
        onCreate(db);
    }


    public Cursor getItemID(String content){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT " + C_ID + " FROM " + TABLE_TODO_ITEM +
                " WHERE " + C_CONTENT + " = '" + content + "'";
        Cursor data = database.rawQuery(query,null);
        return data;
    }
    public Cursor getFlag(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT " + C_COMPLETED_FLAG + " FROM " + TABLE_TODO_ITEM +
                " WHERE " + C_ID + " = '" + id + "'";
        Cursor data = database.rawQuery(query,null);
        return data;
    }
    public void updateContent(String newContent,int id,String oldContent) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_TODO_ITEM + " SET " + C_CONTENT +
                " = '" + newContent + "' WHERE " + C_ID + " = '" + id + "'" +
                " AND " + C_CONTENT + " = '" + oldContent + "'";
        database.execSQL(query);
    }
    public void updateStatus(String flag,int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_TODO_ITEM + " SET " + C_COMPLETED_FLAG +
                " = '" + flag + "' WHERE " + C_ID + " = '" + id + "'";
        database.execSQL(query);
    }
    public void deleteContent(int id) {

        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TODO_ITEM + " WHERE _id =" + id;
        database.execSQL(query);
    }
}
