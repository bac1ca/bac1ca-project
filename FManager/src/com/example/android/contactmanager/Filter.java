package com.example.android.contactmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Filter {
	private String myCurDir = "";
	private String myFilterTypes = "";
	private List<String> myResultList = new ArrayList<String>();
	
	
	
	
	public String getFilterTypes() {
		return myFilterTypes;
	}
	
	public List<String> getFilteredList(File file, String filteredTypes){
		if (myCurDir.equals(file.getPath())){
			if (compareTypes(myFilterTypes, filteredTypes)){
				myResultList = getFiltredList(myResultList, myFilterTypes, filteredTypes);
				myFilterTypes = filteredTypes;
				return myResultList;
			}
		}
		
		myCurDir = file.getPath();
		myFilterTypes = filteredTypes;
		myResultList.clear();
		myResultList = getFiltredList(file.listFiles(), myFilterTypes);
		return myResultList;
	}
	
	
	private List<String> getFiltredList(File[] files, String types){
		List<String> resultList = new ArrayList<String>();
		for(File file : files){
			if (file.isDirectory())
				resultList.add(file.getName());
			else if (condition(file, types))
				resultList.add(file.getName());
		}
		return resultList;
	}

	
	private List<String> getFiltredList(List<String> curList, String curTypes, String newTypes){
		List<String> resList = new ArrayList<String>();

		String delTypes = "";
		for(String curType : curTypes.split("[\\s]+")){
			if ( newTypes.indexOf(curType.trim()) < 0 )
				delTypes += curType + " ";
		}
		
		
		ListIterator<String> it = curList.listIterator();
		while (it.hasNext()) {
			String file = it.next();
			if(condition(file, delTypes)){
				it.remove();
				it.previous();
			}
		}
		return curList;
		
//		for (String file : curList){
//			if (new File(file).isDirectory())
//				resList.add(file);
//			else if (condition(file, newTypes))
//				resList.add(file);
//		}
//		return resList;
	}
	
	
	private boolean condition(File file, String types) {
		return condition(file.getName(), types);
	}
	
	
	
	private boolean condition(String val, String types) {
		for (String type : types.split("[\\s]+")) {
			if (val.endsWith(type))
				return true;
		}
		return false;
	}
	
	
	private boolean compareTypes(String curTypes, String newTypes){
		if (newTypes.length() > curTypes.length())
			return false;
		for(String newType : newTypes.split("[\\s]+")){
			if ( curTypes.indexOf(newType.trim()) < 0 )
				return false;
		}
		return true;
	}
	
}