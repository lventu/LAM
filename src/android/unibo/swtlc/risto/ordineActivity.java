package android.unibo.swtlc.risto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ordineActivity extends Activity {
	private static int RES_CD_MN = 102;
	private static int COUNTER = 0;
	private static TableLayout tabella;
	private TextView pippo;
	private Button pluto;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordine);
		tabella = (TableLayout) findViewById(R.id.tabRiassOrdine);
		
		Button add = (Button) findViewById(R.id.aggiungi_dal_menu);
		add.setOnClickListener(new aggMen_1());
	}
	// listener to launch the menu activity
	private class aggMen_1 implements Button.OnClickListener{
		public void onClick(View v) {
			Intent a = new Intent(); 
			a.setClass(getApplicationContext(), ristoMenu.class);
			a.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(a, RES_CD_MN);
		}
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (requestCode==RES_CD_MN){
			if(resultCode==RESULT_OK){
				String ric = data.getStringExtra("nome");
				String ric2 = data.getStringExtra("prezzo");
				Toast.makeText(getApplicationContext(), "ricevuto "+ric, Toast.LENGTH_SHORT).show();
				pippo = new TextView(getApplicationContext());
				pippo.append(ric); pippo.append("\t\t\t"); pippo.append(ric2);
				pluto = new Button(getApplicationContext());
				pluto.setText(R.string.del);
				pluto.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				pluto.setOnClickListener(new deleteRow());
				TableRow rigaN = new TableRow(getBaseContext()); 
				rigaN.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));				
				rigaN.addView(pippo); rigaN.addView(pluto);
				tabella.addView(rigaN,COUNTER);
				COUNTER++;
			}
		}
	}
	
	private static class deleteRow implements Button.OnClickListener{
		private static int numOfDel = 0;
		public void onClick(View v) {
			Button A = (Button) v;
			TableRow B = (TableRow) A.getParent();
			tabella.removeView(B);
			numOfDel++;
			if(numOfDel>=COUNTER) 
				COUNTER = 0;
		}
	}
}
