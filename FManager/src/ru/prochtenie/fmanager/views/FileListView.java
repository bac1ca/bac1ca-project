package ru.prochtenie.fmanager.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.prochtenie.fmanager.runnable.ReturnRes;
import ru.prochtenie.fmanager.runnable.SmartFilter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileListView {
	// **************************************************************************//
	// Enums //
	// **************************************************************************//

	// **************************************************************************//
	// Members //
	// **************************************************************************//
	private Activity myParent;

	private ListView myListView;

	private String myCurDir = ".";

	private String myCurFile = ".";

	private List<String> myHistory = new ArrayList<String>();
	
	private String myFilterTypes = "";

	// **************************************************************************//
	// Members for dynamic loading //
	// **************************************************************************//
	
	private ArrayAdapter<String> myAdapter;
	
	private List<String> myOrders = Collections.synchronizedList(new ArrayList<String>());
	
	private SmartFilter myFilter;
	
	private ReturnRes myReturnRes;
	
	private Thread myCurFilterThread;
	
	private ProgressDialog myProgressDialog;
	
	// **************************************************************************//
	// Constructors //
	// **************************************************************************//
	public FileListView(Activity parent, ListView listView) {
		myParent = parent;
		myListView = listView;

		// set parameters ProgressDialog
		myProgressDialog = new ProgressDialog(myParent);
		myProgressDialog.setTitle("Please wait...");
		myProgressDialog.setMessage("Retrieving data ...");
		
		myAdapter = new ArrayAdapter<String>(myParent, R.layout.list_item);
		myListView.setAdapter(myAdapter);
		myReturnRes = new ReturnRes(myOrders, myAdapter, myProgressDialog);
		myFilter = new SmartFilter(myParent, myOrders, myReturnRes);
		
		init(myCurDir);
		
		myListView.setTextFilterEnabled(true);
		myListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.setSelected(true);
				
				myCurFile = ((TextView) view).getText().toString();
				if (new File(myCurDir + "/" + myCurFile).isDirectory())
					myHistory.add(myCurFile);
				goAtDir(myCurDir + "/" + myCurFile);
			}
		});

	}

	// **************************************************************************//
	// Getters //
	// **************************************************************************//
	public ListView getListView() {
		return myListView;
	}

	public String getFilterTypes(){
		return myFilterTypes;
	}
	// **************************************************************************//
	// Setters //
	// **************************************************************************//
	
	// **************************************************************************//
	// Publics //
	// **************************************************************************//
	public String getPathToFile(){
		if (myCurDir.equals(myCurFile))
			return null;
		return myCurDir + "/" + myCurFile;
	}
	
	public void goAtBack(){
		back();
	}
	
	public void setFilter(String filterTypes){
		if (!myFilterTypes.equals(filterTypes)){
			myFilterTypes = filterTypes;
			goAtDir(myCurDir);
		}
	}

	// **************************************************************************//
	// Abstracts //
	// **************************************************************************//

	// **************************************************************************//
	// Protected //
	// **************************************************************************//

	// **************************************************************************//
	// Privates //
	// **************************************************************************//
	
	private void back(){
		if (myHistory.size() > 0){
			
		// TODO delete later 
			for (String s : myHistory){
				Log.v(FileManager.FILE_MANAGER_LOG_TAG, "histiry : " + s);
			}
			
			String dir = myHistory.remove(myHistory.size() - 1);
			myCurDir = myCurDir.substring(0, myCurDir.length() - dir.length() - 1);
			goAtDir(myCurDir);
		}
	}
	
	private void init(String path){
		myProgressDialog.show();
		File file = new File(path);
		myCurDir = path;
		myFilter.setPreferences(file, myFilterTypes);
		myCurFilterThread = new Thread(null, myFilter, "MagentoBackground");
		myCurFilterThread.start();
		
		for(String dir : START_DIR.split("[\\/]+")){
			myCurFile = dir;
			myCurDir += "/" + dir;
			
			Log.v(FileManager.FILE_MANAGER_LOG_TAG, "file: " + myCurFile + "\t dir: " + myCurDir);

			myHistory.add(myCurFile);
			goAtDir(myCurDir);
		}
	}
	
	public void goAtDir(String path) {
		if (new File(path).isDirectory()){

			myProgressDialog.show();
			File file = new File(path);
			myCurDir = path;
			myCurFilterThread.interrupt();
			myFilter.setPreferences(file, myFilterTypes);
			myCurFilterThread = new Thread(null, myFilter, "MagentoBackground");
			myCurFilterThread.start();
		}
	}
	
	// **************************************************************************//
	// Public Statics //
	// **************************************************************************//

	// **************************************************************************//
	// Private Statics //
	// **************************************************************************//
	private static final String START_DIR = "sdcard/Books";
	
	// **************************************************************************//
	// Internal Classes //
	// **************************************************************************//

}
