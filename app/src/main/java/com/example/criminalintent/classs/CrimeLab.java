package com.example.criminalintent.classs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.databases.CrimeBaseHelper;
import com.example.criminalintent.databases.CrimeCursorWrapper;
import com.example.criminalintent.databases.CrimeDBSchema;
import com.example.criminalintent.databases.CrimeDBSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 这个是一个单例类，用于管理Crime集合，用于提供数据库增删改查的方法
 */
public class CrimeLab {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //创建一个Crime的列表

    private static CrimeLab sCrimeLab;
    //构造方法
    private CrimeLab(Context context){

        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }

   //获取本类对象
    public static CrimeLab get(Context context){
        if (sCrimeLab==null){
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //用于获取本类中的集合对象
    public List<Crime> getCrimes(){
         List<Crime> crimes = new ArrayList<>();
         CrimeCursorWrapper cursor= queryCrimes(null,null);
         try {
             cursor.moveToFirst();
             while (!cursor.isAfterLast()){
                 crimes.add(cursor.getCrime());
                 cursor.moveToNext();
             }
         }finally {
             cursor.close();
         }
         return crimes;
    }

    //这个方法用于获取Crime集合中具体的某个Crime对象
    public Crime getCrime(UUID uuid){

        CrimeCursorWrapper cursor= queryCrimes(
                CrimeTable.Cols.UUID+"= ? ",
                new String[]{uuid.toString()}
        );
        try {
            if (cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

    //用于添加数据
    public void add(Crime c){
        ContentValues values =getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);

    }

    public void upDateCrime(Crime crime){
        String uuidString= crime.getId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + "=?" ,new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values=new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved()? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        return values;
    }

    //用于查询数据库数据
    private CrimeCursorWrapper queryCrimes(String whereClause , String [] whereArgs){
        Cursor cursor=mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }
    //用于删除数据
    public void deleteCrime(Crime c){
        mDatabase.delete(CrimeTable.NAME, "uuid = ?", new String[] {String.valueOf(c.getId())});
    }

    //指定照片文件路径已经对应照片的名称，返回某个具体位置的File对象
    public File getPhoneFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }
}
