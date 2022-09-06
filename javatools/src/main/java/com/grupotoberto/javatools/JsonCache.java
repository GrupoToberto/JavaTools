package com.grupotoberto.javatools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonCache { // Grupo Toberto

    /* This class is used to facilitate the caching of important data to improve the user experience
    when there is not internet connection. It is intended to be used by creating a database on the screen (Activity, Fragment, etc.),
     for example, "dbActivityMain". It is recommended to name the database as the screen because these names are unique within a package.*/

    private final CacheSQLiteHelper mDb;

    private static final String SELECT_QUERY="SELECT data FROM cache;";
    private static final String SELECT_SPEC_QUERY="SELECT data FROM cache WHERE ref = 0";
    private static final String CREATE_QUERY="CREATE TABLE cache(ref INTEGER, data TEXT);";
    private static final String DROP_QUERY="DROP TABLE IF EXISTS cache;";
    private static final String UPDATE_SPEC_QUERY_PREFIX="UPDATE cache SET data = ";
    private static final String WHERE_QUERY_SUFFIX=" WHERE ref = ";
    private static final String INSERT_SPEC_QUERY_PREFIX="INSERT INTO cache (ref, data) ";

    public JsonCache(Context context, String dbName, int dbVersion){
        mDb = new CacheSQLiteHelper(context , null, dbName, dbVersion);
    }

    public JSONObject getData(int ref)
    {
        SQLiteDatabase db = mDb.getWritableDatabase();
        Cursor c = db.rawQuery(SELECT_SPEC_QUERY+ref+";", null);
        String crude;
        JSONObject data=null;

        try {
            if (c.moveToFirst()) {
                do {
                    crude = c.getString(0);
                    data = new JSONObject(crude);
                } while (c.moveToNext());

                c.close();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public JSONArray getData()
    {
        SQLiteDatabase db = mDb.getWritableDatabase();
        Cursor c = db.rawQuery(SELECT_QUERY, null);
        String crude;
        JSONArray data=null;
        int i=0;

        try {
            if (c.moveToFirst()) {
                data = new JSONArray();

                do {
                    crude = c.getString(0);
                    data.put(i, new JSONObject(crude));
                } while (c.moveToNext());

                c.close();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public void setData(JSONObject data, int ref)
    {
        SQLiteDatabase db = mDb.getWritableDatabase();
        Cursor c = db.rawQuery(SELECT_SPEC_QUERY+ref+";", null);
        boolean input=false;

        try {
            if (c.moveToFirst()) {
                do {
                    input = true;
                } while (c.moveToNext());

                c.close();
            }

            if(input)
            {
                db.execSQL(UPDATE_SPEC_QUERY_PREFIX+"'"+data.toString().replace("'", " ")+"'"+WHERE_QUERY_SUFFIX+ref);
            }
            else
            {
                db.execSQL(INSERT_SPEC_QUERY_PREFIX+"VALUES ("+ref+", '"+data.toString().replace("'", " ")+"') ;");
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class CacheSQLiteHelper extends SQLiteOpenHelper{

        public CacheSQLiteHelper(Context context, SQLiteDatabase.CursorFactory factory, String dbName, int dbVersion) {
            super(context, dbName, factory, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Deprecated table version is deleted
            db.execSQL(DROP_QUERY);

            //New table version is created
            db.execSQL(CREATE_QUERY);
        }
    }
}


