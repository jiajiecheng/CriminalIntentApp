package com.example.criminalintent.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.classs.Crime;
import com.example.criminalintent.databases.CrimeDBSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Crime getCrime(){
        String uuidString =getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title =getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date= getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int  isSolved =getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }
}
