package com.acadgild.balu.to_do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by BALU on 4/12/2016.
 */
public class ToDo_DBHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "DB_TODO";

    private static final String TABLE_TODO = "TODO";
    private static final String COL_ID = "KEY_ID";
    private static final String COL_TITLE = "KEY_TITLE";
    private static final String COL_DESCRIPTION = "KEY_DESCRIPTION";
    private static final String COL_DATE = "KEY_DATE";
    private static final String COL_STATUS = "KEY_STATUS";

    private static final String[] COLUMNS_TODO = {COL_ID, COL_TITLE, COL_DESCRIPTION, COL_DATE, COL_STATUS };

    public ToDo_DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String str_create_table = "CREATE TABLE " + TABLE_TODO + " ( "
                                 + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                 + COL_TITLE + " TEXT, "
                                 + COL_DESCRIPTION + " TEXT, "
                                 + COL_DATE + " TEXT, "
                                 + COL_STATUS + " INTEGER )";
        db.execSQL(str_create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_TODO);
        this.onCreate(db);
    }

    public void add_new_task(ToDo toDo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values_todo = new ContentValues();

        values_todo.put(COL_TITLE, toDo.getTitle());
        values_todo.put(COL_DESCRIPTION, toDo.getDescription());
        values_todo.put(COL_DATE, toDo.getDate());
        values_todo.put(COL_STATUS, 0);

        db.insert(TABLE_TODO, null, values_todo);

        db.close();
    }

    public ToDo get_task(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, COLUMNS_TODO, COL_ID + " =?",
                        new String[] {String.valueOf(id)}, null, null, null, null);
        ToDo toDo = null;
        if (cursor != null)
        {
            cursor.moveToFirst();
            toDo = new ToDo();
            toDo.setId(Integer.parseInt(cursor.getString(0)));
            toDo.setTitle(cursor.getString(1));
            toDo.setDescription(cursor.getString(2));
            toDo.setDate(cursor.getString(3));
            toDo.setStatus(Integer.parseInt(cursor.getString(4)));

        }
        cursor.close();
        return toDo;
    }

    public int update_task(ToDo toDo)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values_todo = new ContentValues();
        values_todo.put(COL_TITLE, toDo.getTitle());
        values_todo.put(COL_DESCRIPTION, toDo.getDescription());
        values_todo.put(COL_DATE, toDo.getDate());

        int i = db.update(TABLE_TODO, values_todo, COL_ID + "=?",
                new String[] {String.valueOf(toDo.getId())});
        db.close();

        return i;
    }

    public int mark_task_completed(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values_todo = new ContentValues();
        values_todo.put(COL_STATUS, 1);

        int i = db.update(TABLE_TODO, values_todo, COL_ID + "=?",
                new String[] {String.valueOf(id)});
        db.close();
        return i;
     }

    public int delete_task(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(TABLE_TODO, COL_ID + "=?", new String[] {String.valueOf(id)});

        db.close();
        return i;
    }

    public ArrayList<ToDo> get_completed_tasks()
    {
        ArrayList<ToDo> arrayList_todo = new ArrayList<>();

        String str_query = "SELECT * FROM " + TABLE_TODO + " WHERE " + COL_STATUS + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(str_query, null);

        ToDo toDo = null;

        if (cursor.moveToFirst())
        {
            do {
                toDo = new ToDo();

                toDo.setId(Integer.parseInt(cursor.getString(0)));
                toDo.setTitle(cursor.getString(1));
                toDo.setDescription(cursor.getString(2));
                toDo.setDate(cursor.getString(3));
                toDo.setStatus(Integer.parseInt(cursor.getString(4)));

                arrayList_todo.add(toDo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList_todo;
    }

    public ArrayList<ToDo> get_todo_tasks()
    {
        ArrayList<ToDo> arrayList_todo = new ArrayList<>();

        String str_query = "SELECT * FROM " + TABLE_TODO + " WHERE " + COL_STATUS + " = 0 " + " ORDER BY " + COL_DATE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(str_query, null);

        ToDo toDo = null;

        if (cursor.moveToFirst())
        {
            do {
                toDo = new ToDo();

                toDo.setId(Integer.parseInt(cursor.getString(0)));
                toDo.setTitle(cursor.getString(1));
                toDo.setDescription(cursor.getString(2));
                toDo.setDate(cursor.getString(3));
                toDo.setStatus(Integer.parseInt(cursor.getString(4)));

                arrayList_todo.add(toDo);
            }while (cursor.moveToNext());

        }
        cursor.close();
        return arrayList_todo;
    }

}
