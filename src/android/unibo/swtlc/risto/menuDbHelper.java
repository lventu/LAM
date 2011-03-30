/* This is my version of Database open Helper
 * it copy the database if doesn't exist inside device, it will implement the Update method
 * 
 * Author: Luca Venturini
 */
/* 29/03/11 - il database è locale e aggiornabile reinstallandolo sul dispositivo
 * 
 */
package android.unibo.swtlc.risto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.database.sqlite.*;
import android.content.Context;

public class menuDbHelper extends SQLiteOpenHelper {
	
    private static final String DB_NAME = "menu.db";
    private static final String DB_PATH = "/data/data/android.unibo.swtlc.risto/databases/";
    private static final Integer DB_VERSION = 1;
    private final Context context;
 
    public menuDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        // Check if the db already exist
        if (!isDataBaseExist()){
        	// if isn't, create folder
        	File f = new File(DB_PATH);
            if ( !f.exists() ) f.mkdirs();
            // DB copy
        	createNewDatabase();
        }
    }
    
	private boolean isDataBaseExist() {
		File dbFile = new File(DB_PATH+DB_NAME);
		return dbFile.exists();
	}
    
	public void createNewDatabase() {
        InputStream assetsDB = null;
        try {
            assetsDB = context.getAssets().open(DB_NAME);
            OutputStream dbOut = new FileOutputStream(DB_PATH + DB_NAME);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = assetsDB.read(buffer)) > 0) {
                dbOut.write(buffer, 0, length);
            }
            dbOut.flush();
            dbOut.close();
            assetsDB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}  
	
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    	
    }

}
