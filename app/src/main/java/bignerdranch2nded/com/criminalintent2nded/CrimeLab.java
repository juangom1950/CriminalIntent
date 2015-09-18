package bignerdranch2nded.com.criminalintent2nded;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bignerdranch2nded.com.criminalintent2nded.database.CrimeBaseHelper;
import bignerdranch2nded.com.criminalintent2nded.database.CrimeCursorWrapper;
import bignerdranch2nded.com.criminalintent2nded.database.CrimeDbSchema;
import bignerdranch2nded.com.criminalintent2nded.database.CrimeDbSchema.CrimeTable;

/**
 * Created by Juan on 9/1/2015.
 * This is a singleton
 * It return a list of crimes
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    //private ArrayList<Crime> mCrimes;
    //private CriminalIntentJSONSerializer mSerializer;



    //private ArrayList<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        //mCrimes = new ArrayList<>();

        //Add 100 crimes
       /* for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }*/

        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Crime crime) {

        ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    public void addCrime(Crime c) {
        //mCrimes.add(c);

        ContentValues values = getContentValues(c);
        //The first argument is the table you want to insert into here. The last argument is the data you want to put in.
        //The second argument will allow to pass a null value when you try to insert an empty value
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime crime) {
        //mCrimes.remove(c);

        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public boolean saveCrimes() {
        try {
            //mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        //The 3rd parameter the symbol ? will treat that value like a string always.
        //The 4rth parameter is the value that you are going to insert in the 3rd parameter
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    //Instead of writing this code each time you need to read data from a Cursor, you can create your own Cursor subclass that takes
    // care of this in one place.
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes() {
        //return mCrimes;
        //return new ArrayList<>();

        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            crimes.add(cursor.getCrime());
            cursor.moveToNext();
        }
        //It is necessary otherwise it will cause errors
        cursor.close();

        return crimes;

    }

    public Crime getCrime(UUID id) {
        /*for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }*/

        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    //This method only return File objects that point to the right locations.
    public File getPhotoFile(Crime crime) {

        /*File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_DIRECTORY_DCIM);*/

        File externalFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        //create the file inside this directory
        return new File(externalFilesDir, crime.getPhotoFilename());
    }
}
