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

		private SQLiteDatabase mDb;
		private menuDbHelper mDbHelper;
		//private Context mContext; // ha bisogno di un contesto
		public boolean TRUE_DATA_PROC=false;
		
		public sqliteRistoDb(Context ctx, String className){
			//mContext=ctx; // the context is passed as constructor's parameter
			mDbHelper=new menuDbHelper(ctx);	  //create also the helper instance (see the other class file)	
			Log.i("DEB", "- "+className);
			// vedo la classe che chiama questo costruttore e se è la prima valuto se creare il DB
			// se è la classe di visualizzazione non lo chiama, viene solo costruito l'oggetto sfruttando il database corrente
			// !! il check sul db lo fa alla creazione della prima lista !!
			if (className.equalsIgnoreCase("ristoMenu")){
				mDbHelper.onCreate(mDb);
			}
			this.TRUE_DATA_PROC = mDbHelper.getProcessComplete();
		}
		
		public void open(){  //open the R/W database
			mDb=mDbHelper.getReadableDatabase();		
		}
		
		public void close(){ //Closing the Database
			mDb.close();
		}
		
		// Function that return the rows whit a string Name on input (note that the cursor could contains many entries)
		public String[] fetchTable(String _tabelle){
			Cursor C=null;
			String[] tabelle=null;
			Log.i("prova", _tabelle);
			if(_tabelle.equalsIgnoreCase("table")){
				C = mDb.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'",null);
				tabelle = TableIndex(C,1);
			}
			C.close();
			//Log.i("prova", "ok"+C.getColumnCount()+C.getCount());
			return tabelle;
		}
		
		public Cursor fetchPiatti(String tabName){
			Cursor C=null;
			if (tabName!=null)
				C = mDb.rawQuery("SELECT "+MenuMetaData.ID+","+MenuMetaData.MENU_NOME_KEY+","+MenuMetaData.MENU_PREZZO_KEY+" FROM "+tabName,null);
			return C;
		}
		
		public Cursor fetchPiattoXfromTableY (String piatto, String tab){
			Cursor C=null;
			C = mDb.rawQuery("SELECT * FROM "+tab+" WHERE "+MenuMetaData.ID+" = "+piatto,null);
			return C;
			
		}
		
		// from cursor to string array
		private String[] TableIndex(Cursor dati, int offset) {
			String[] tabelle = new String[dati.getCount()-offset];
			if(dati.moveToFirst()){
				if (offset==1) dati.moveToNext();
	        	do{
	        		tabelle[dati.getPosition()-offset] = new String();
	        		tabelle[dati.getPosition()-offset] = dati.getString(0);
	        	}while(dati.moveToNext());
			}
			return tabelle;
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
