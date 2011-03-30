package android.unibo.swtlc.risto;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ristoMenu extends Activity {
    /** Called when the activity is first created. */
	// Oggetti Globali non istanziati
	ListView listView;
	Spinner selCatSpin;
	SimpleCursorAdapter adapterList;
	Cursor dati;
	sqliteRistoDb menu;
	String category="table";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        listView = (ListView) findViewById(R.id.cocktailList);
        selCatSpin = (Spinner) findViewById(R.id.selectCategory);
		menu = new sqliteRistoDb(getApplicationContext());
		menu.open();
		dati = menu.fetchTable(category);
		String[] A = TableIndex(dati,1);
		dati.close();
		// Lista dello Spinner 
		ArrayAdapter<String> adapterMenu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, A){
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView pippo = (TextView) super.getView(position, convertView, parent);
				String C = getItem(position);
				pippo.setText(C);
				return pippo;				
			}
		};
		adapterMenu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selCatSpin.setAdapter(adapterMenu);
		selCatSpin.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,int position, long arg3) {
				// If the category change this method is called, so refresh the cursor and tell it to the Adapter (it will refresh the view)
				category = parent.getItemAtPosition(position).toString();
				dati.close();
				dati = menu.fetchTable(category);
				adapterList.changeCursor(dati);
				}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}			
		});
		// Scelta iniziale altrimenti da un errore di inizializzazione del cursore (non serve al programma in se)
		dati = menu.fetchTable("Secondi");
		startManagingCursor(dati); dati.moveToFirst();
		// Lista dei piatti -- si può creare anche una vista custom con il metodo bindView come da DOC
		adapterList = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, dati, new String[] {"nome"}, new int[] {android.R.id.text1});
		listView.setAdapter(adapterList);
		//Chiudo il Database del menu
		//menu.close();
    }

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
}