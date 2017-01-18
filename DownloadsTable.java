import java.util.*;

import javax.swing.JProgressBar;
import javax.swing.table.*;

//manages download table's data
public class DownloadsTable 
extends AbstractTableModel implements Observer{

	//names for table's columns
	private static final String[] columnNames = {"URL", 
	"Size", "Progress", "Status"};
	
	//classes for each column value
	private static final Class[] columnClasses = {String.class,
	String.class, JProgressBar.class, String.class};
	
	//ArrayList of table's downloads
	private ArrayList<DownloadMethods> downloadList = new ArrayList<DownloadMethods>();
	
	//add new download to table
	public void addDownload(DownloadMethods download){
		//notifies if download changes;
		download.addObserver(this);
		downloadList.add(download);

		//table row insertion notification to table
		fireTableRowsInserted(getRowCount() - 1, getRowCount() -1);
	}

	//get download for specified row
	public DownloadMethods getDownload(int row){
		return downloadList.get(row);
	}
	
	//Remove download from list
	public void clearDownload(int row){
		downloadList.remove(row);
		
		//fire table row deletion notification to table
		fireTableRowsDeleted(row, row);
	}
	
	//getter for table's column count
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	//getter column name
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	//getter column's class
	public Class getColumnClass(int col){
		return columnClasses[col];
	}
	
	//getter table's row count
	@Override
	public int getRowCount(){
		return downloadList.size();
	}
	
	@Override
	//get value for index
	public Object getValueAt(int row, int col) {
		DownloadMethods download = downloadList.get(row);
		switch (col) {
		case 0: //called stored URL from ArrayList
			return download.getUrl();
		case 1: //size
			int size = download.getSize();
		case 2: //progress
			return new Float(download.getProgress());
			case 3: //status
				return DownloadMethods.STATUS[download.getStatus()];
			
		} return "";
	
}
//update when DownloadMethods notifies observer of change
	public void update(Observable o, Object arg){
		int index = downloadList.indexOf(o);
	//run table row update notification to table
	fireTableRowsUpdated(index, index);
	}
}
