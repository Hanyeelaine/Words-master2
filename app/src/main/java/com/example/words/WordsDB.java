package com.example.words;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.words.wordcontract.Words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class WordsDB {
    private static final String TAG = "myTag";

    private static WordsDBHelper mDbHelper;

    //采用单例模式
    private static WordsDB instance = new WordsDB();
    public static WordsDB getWordsDB(){
        return WordsDB.instance;
    }

    private WordsDB() {
        if (mDbHelper == null) {
            mDbHelper = new WordsDBHelper(WordsApplication.getContext());
        }
    }

    public void close() {
        if (mDbHelper != null)
            mDbHelper.close();  //关闭游标，释放资源
    }

    //获得单个单词的全部信息
    public Words.WordDescription getSingleWord(String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();//得到可读数据库

        String sql = "select * from words where _ID=?";
        Cursor cursor = db.rawQuery(sql, new String[]{id});

        // rawQuery是直接使用SQL语句进行查询的
        // Cursor 是每行的集合。使用 moveToFirst() 定位第一行.

        if (cursor.moveToNext()) {
            ;
            Words.WordDescription item = new Words.WordDescription
                    (cursor.getString(cursor.getColumnIndex(Words.Word._ID)),
                    cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_WORD)),
                    cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_MEANING)),
                    cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_SAMPLE)));
                // getColumnIndex(String columnName)  返回指定列的名称，如果不存在返回-1
            return item;

        }
        return null;

    }

    //得到全部单词列表
    public ArrayList<Map<String, String>> getAllWords() {
        if (mDbHelper == null) {
            Log.v(TAG, "WordsDB::getAllWords()");
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Words.Word._ID,
                Words.Word.COLUMN_NAME_WORD
        };

        //排序
        String sortOrder =
                Words.Word.COLUMN_NAME_WORD + " ASC"; //ASC 升序  DESC 降序

        Cursor c = db.query(
                Words.Word.TABLE_NAME,  // The table to query
                projection,             // The columns to return
                null,                   // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        return ConvertCursor2WordList(c);
    }

    //将游标转化为单词列表
    private ArrayList<Map<String, String>> ConvertCursor2WordList(Cursor cursor) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put(Words.Word._ID, String.valueOf(cursor.getString(cursor.getColumnIndex(Words.Word._ID))));
            map.put(Words.Word.COLUMN_NAME_WORD, cursor.getString(cursor.getColumnIndex(Words.Word.COLUMN_NAME_WORD)));
            result.add(map);
        }
        return result;
    }

    //使用insert方法增加单词
    public void Insert(String strWord, String strMeaning, String strSample) {

        //Gets the data repository in write mode*/
        // 数据库写入模式
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        //创建一个新的map 列名为主键
        ContentValues values = new ContentValues();
        values.put(Words.Word._ID, GUID.getGUID());
        values.put(Words.Word.COLUMN_NAME_WORD, strWord);
        values.put(Words.Word.COLUMN_NAME_MEANING, strMeaning);
        values.put(Words.Word.COLUMN_NAME_SAMPLE, strSample);

        // 插入新行，返回新行的主键值
        long newRowId;
        newRowId = db.insert(
                Words.Word.TABLE_NAME,
                null,
                values);
    }

    //使用Sql语句删除单词
    public void DeleteUseSql(String strId) {
        String sql = "delete from words where _id='" + strId + "'";

        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(sql);
    }

    //使用Sql语句更新单词
    public void UpdateUseSql(String strId, String strWord, String strMeaning, String strSample) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update words set word=?,meaning=?,sample=? where _id=?";
        db.execSQL(sql, new String[]{strWord,strMeaning, strSample, strId});
    }

    //使用Sql语句查找
    public ArrayList<Map<String, String>> SearchUseSql(String strWordSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "select * from words where word like ? order by word desc";
        Cursor c = db.rawQuery(sql, new String[]{"%" + strWordSearch + "%"});
        // 支持模糊查询  SQL中 匹配任意字符用“%”   rawQuery是直接使用SQL语句进行查询的

        return ConvertCursor2WordList(c);
    }

}