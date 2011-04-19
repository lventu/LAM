/* This is my version of Database open Helper
 * it copy the database if doesn't exist inside device, it will implement the Update method
 * 
 * Author: Luca Venturini
 */
/* 29/03/11 - il database � locale e aggiornabile reinstallandolo sul dispositivo
 * 
 */
package android.unibo.swtlc.risto;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class menuDbHelper extends SQLiteOpenHelper {
	
    private static final String DB_NAME = "menu.db";
    private static final String DB_PATH = "/data/data/android.unibo.swtlc.risto/databases/";
    private static final Integer DB_VERSION = 1;
    private final Context context;
    //Credenziali di accesso al server ftp
    private class FTP_Server{
    	static final String UserName = "luca";
    	static final String Password = "luca";
    	static final String Server_ADDRESS = "192.168.0.13";
    	static final int Port_NUMBER = 21;
    }
    
    private long _newfiledate;
    private long _oldfiledate=0;
    // variabile che ci consente di vedere se le operazioni sono andate a buon fine (cioè il database è presente anche se non aggiornato)
    private boolean TRUE_DATA_PROC=false;
    
    public menuDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);      
        this.context = context;
    }
    
    public boolean getProcessComplete(){
    	return TRUE_DATA_PROC;
    }
    
    private boolean checkFolderPresence(){
    	File _folder = new File(DB_PATH);
    	boolean result=true;
    	//se la cartella non esiste la creo
    	if (!_folder.exists()){
    		 result = _folder.mkdir();
    	}
    	return result;
    }
    
    /* chiamato quando il database deve essere creato */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.i("DEB", "esegue onCreate");
        FTPClient client= new FTPClient();
        client.setDefaultTimeout(700);
        //Log.w("classe contesto", "�: "+context.);
        // serve in modo che se esiste un già un database, se si genera una eccezzione di collegamento,
        // possiamo utilizzarlo senza che l'applicazione si blocchi. Se questo non esiste invece questa genera un numero casuale
        // ma non è un problema perchè quando andremo a vedere se esiste il file questo valore lo sostituiremo con uno giusto
        _oldfiledate = new File(DB_PATH+DB_NAME).lastModified();
        try {
        	//li posiziono all'inizio altrimenti non vengono eseguiti a causa dell'eccezione lanciata dal collegamento
        	checkFolderPresence();
        	client.connect(FTP_Server.Server_ADDRESS, FTP_Server.Port_NUMBER);
    		Log.i("pippo", "ok: "+client.getReplyString());
        	//aperta la connessione al server procedo al login
        	boolean log_server = client.login(FTP_Server.UserName, FTP_Server.Password);
        	if (log_server) Log.i("ConnFTP", "...login ok");
        	else Log.e("ConnFTP", "...login fallito");
        	// creo la lista di file presenti sul server FTP
        	FTPFile[] ftpFiles = client.listFiles();
        	int targetfileposition=0;
        	for (FTPFile ftpFile : ftpFiles) {
        		targetfileposition++;
        		//if (ftpFile.getType() == FTPFile.FILE_TYPE) risposta.append("\nFTPFile: " + ftpFile.getName() +"; " + ftpFile.hashCode());
        		if (ftpFile.getName().equals(DB_NAME)){ targetfileposition--; break; }
        	}
        	// confronto le date di modifica dei file, se il nuovo file sul server ha una data più recente allora lo considero altrimenti no
          	_newfiledate = ftpFiles[targetfileposition].getTimestamp().getTimeInMillis();
        	File _oldfile = new File(DB_PATH+DB_NAME);
        	//se il file non esiste lo creo e la data è 0, se esiste carico la data corretta
        	if(!_oldfile.exists()){ 
    			_oldfile.createNewFile();
    			OutputStream fos = new BufferedOutputStream(new FileOutputStream(DB_PATH+DB_NAME));
        		client.retrieveFile(DB_NAME, fos);
        		fos.close();
        		Log.i("pippo", "file non esiste, lo creo");
        	}
        	_oldfiledate = _oldfile.lastModified();
        	
          	Log.e("DEB", "diff: "+_oldfiledate+" "+_newfiledate);
        	// TODO importante da sistemare la funzione update in data
        	if (_newfiledate > _oldfiledate){
        		OutputStream fos = new BufferedOutputStream(new FileOutputStream(DB_PATH+DB_NAME));
        		client.retrieveFile(DB_NAME, fos);
        		fos.close();
        		Log.i("pippo", "ok: "+fos.toString());
        		Log.i("ConnFTP", "Updated requested file");
        	}else{
        		Log.i("ConnFTP", "Requested file is up to date");
        	}
        	client.disconnect();
        	TRUE_DATA_PROC = true;
        } catch (SocketException e) {
        	e.printStackTrace();
        	Toast.makeText(this.context, "Errore di connessione, riprovare!", Toast.LENGTH_LONG).show();
        	TRUE_DATA_PROC = (_oldfiledate==0) ? false:true;
        	Log.i("ConnFTP", "test "+TRUE_DATA_PROC);
        } catch (IOException e) {
        	e.printStackTrace();
        	Toast.makeText(this.context, "Errore di connessione, riprovare!", Toast.LENGTH_LONG).show();
        	TRUE_DATA_PROC = (_oldfiledate==0) ? false:true;
        	Log.i("ConnFTP", "test "+TRUE_DATA_PROC);
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("DEB", "versione: "+db.getVersion());
	}
}