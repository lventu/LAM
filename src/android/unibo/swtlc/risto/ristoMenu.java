package android.unibo.swtlc.risto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ristoMenu extends Activity {
    /** Called when the activity is first created. */
	// Global Objects Definition
	private ListView listView;
	private Spinner selCatSpin;
	private SimpleCursorAdapter adapterList;
	private Cursor dati;
	private sqliteRistoDb menu;
	private String category="table";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        listView = (ListView) findViewById(R.id.cocktailList);
        selCatSpin = new Spinner(this);
		menu = new sqliteRistoDb(getApplicationContext(),this.getClass().getName().substring(26));
		// se c'è stato un errore nel programma termina l'applicazione
		if(menu.TRUE_DATA_PROC){
		menu.open();
		//dati = menu.fetchTable(category);
		//String[] A = TableIndex(dati,1);
		String[] A = menu.fetchTable("table");
		//dati.close();
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
		selCatSpin.setOnItemSelectedListener(new SpinnSel());
		// Scelta iniziale altrimenti da un errore di inizializzazione del cursore (non serve al programma in se)
		dati = menu.fetchPiatti("Secondi");
		startManagingCursor(dati); dati.moveToFirst();
		// Lista dei piatti -- si puo' creare anche una vista custom con il metodo bindView come da DOC, per semplicit� usiamo un adapter gi� costruito per l'uso con cursori
		adapterList = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, dati, new String[] {"nome", "prezzo"}, new int[] {android.R.id.text1,android.R.id.text2});
		listView.addHeaderView(selCatSpin);
		listView.setAdapter(adapterList);
		listView.setOnItemClickListener(new ShowDetail());
		listView.setOnItemLongClickListener(new AddItem());
		}else{
			
		}
    }
	
	private class SpinnSel implements AdapterView.OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// If the category change this method is called, so refresh the cursor and tell it to the Adapter (it will refresh the view)
			category = parent.getItemAtPosition(position).toString();
			dati.close();
			dati = menu.fetchPiatti(category);
			adapterList.changeCursor(dati);
		}
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub			
		}
	}
	private class ShowDetail implements AdapterView.OnItemClickListener{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(getApplicationContext(), dati.getString(1), Toast.LENGTH_LONG).show();
			// TODO add intent to the show activity (reuse the old one created before)						
			Intent intent = new Intent().setClass(getApplicationContext(), ShowPiattoChar.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("nomePiatto", dati.getString(0));
			intent.putExtra("categoriaPiatto", category);
			//intent.putExtra("stringheChar", value);
			startActivity(intent);
		}		
	}
	private class AddItem implements AdapterView.OnItemLongClickListener{
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO aggiungere funzione aggiungi all'ordine del tavolo
			//Toast.makeText(getApplicationContext(), "long touch of "+dati.getString(1), Toast.LENGTH_SHORT).show();
			Intent data = new Intent();
			data.putExtra("cat", category);
			data.putExtra("nome", dati.getString(1));
			data.putExtra("prezzo", dati.getString(2));
			setResult(RESULT_OK, data);
			finish();
			return true;
		}
	}
	@Override
	public void onDestroy(){
		super.onPause();
		Log.i("DEB", "esegue onDestroy");
		dati.close();
		menu.close();
	}
}