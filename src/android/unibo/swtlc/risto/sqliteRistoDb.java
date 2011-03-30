/* The database object
*  
* Author: Luca Venturini
*/

package android.unibo.swtlc.risto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class sqliteRistoDb {

		SQLiteDatabase mDb;
		menuDbHelper mDbHelper;
		Context mContext; // ha bisogno di un contesto
		
		public sqliteRistoDb(Context ctx){
			mContext=ctx; // the context is passed as constructor's parameter
			mDbHelper=new menuDbHelper(ctx);	  //create also the helper instance (see the other class file)	
		}
		
		public void open(){  //open the R/W database
			mDb=mDbHelper.getWritableDatabase();		
		}
		
		public void close(){ //Closing the Database
			mDb.close();
		}
		
		// Function that return the rows whit a string Name on input (note that the cursor could contains many entries)
		public Cursor fetchTable(String CKName){
			Cursor C=null;
			Log.i("prova", CKName);
			if(CKName.equalsIgnoreCase("table")){
				C = mDb.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'",null);
			}else{
				C = mDb.rawQuery("SELECT "+MenuMetaData.ID+","+MenuMetaData.MENU_NOME_KEY+","+MenuMetaData.MENU_PREZZO_KEY+" FROM "+CKName,null);
			}
			//Log.i("prova", "ok"+C.getColumnCount()+C.getCount());
			return C;
		}
		
		public static class MenuMetaData {  
			static final String ID = "_id";
			static final String MENU_NOME_KEY = "nome";
			static final String MENU_INGR_KEY = "ingredienti";
			static final String MENU_PREZZO_KEY = "prezzo";
			static final String MENU_IMG_KEY = "img_ref";
		}
		public static class MenuMetaTable{
			static final String BEVANDE = "Bevande";
			static final String PRIMI = "Primi";
			static final String SECONDI = "Secondi";
			static final String CONTORNI = "Contorni";
			static final String DOLCI = "Dolci";
		}
}
