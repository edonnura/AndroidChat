package com.fiek.ushtrime.chat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Edon Nura on 7/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG="DatabaseHelper";

    private static final String TABLE_NAME="chatappUsers";
    private static final String COL1="user";
    private static final String COL2="password";


    public DatabaseHelper(Context context){
        super(context,TABLE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+TABLE_NAME+"("+COL1+" TEXT, "+COL2+" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item1,String item2){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL1,item1);

        long result=db.insert(TABLE_NAME,null,contentValues);
        contentValues.put(COL2,item2);
        result+=db.insert(TABLE_NAME,null,contentValues);
        if (result<0){
            return false;
        }
        else {
            return true;
        }

    }

    public Cursor getData(){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM "+TABLE_NAME;
        Cursor data=db.rawQuery(query,null);
        return data;
    }
}
