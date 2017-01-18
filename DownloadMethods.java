import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class DownloadMethods extends Observable implements Runnable{
	
	//sets max size for buffer reader
	private static final int MAX_BUFFER_SIZE = 1024; 

	//status responses
	public static final String STATUS[] = {"Downloading", 
			"Complete", "Paused", "Cancelled", "Error"};
	
	//status response values
	public static final int DOWNLOADING = 0;
	public static final int COMPLETE = 1;
	public static final int PAUSED = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	
	private URL url; //holds Internet URL of file
	private int size; //bytes to download
	private int downloaded; //bytes downloaded
	private int status; //status of download
	
	/*constructor is passed a URL object to download
	  assigned to instance URL. The other instances
	  are assigned values while downloading starts
	 */
	public DownloadMethods(URL url) {
		this.url = url;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		
		//run download by creating new Thread
		download();
	}
	
	//getter for download URL
	public String getUrl(){
		return url.toString();
	}
	
	//getter for download size
	public int getSize(){
		return size;
	}
	//getter for progress
	public double getProgress(){
		return ((double) downloaded/size) * 100;
	}
	//getter for status
	public int getStatus(){
		return status;
	}
	
	//pause method for download
	public void pause(){
		status = PAUSED;
		stateChanged();
	}
	
	//resume method for download
	public void resume(){
		status = DOWNLOADING;
		stateChanged();
		download();
	}
	
	//cancel the download
	public void cancel(){
		status = CANCELLED;
		stateChanged();
	}
	
	//error exists in download process
	private void error(){
		status = ERROR;
		stateChanged();
	}
	
	//download method so that the DownloadMethods class is independent
	public void download(){
		//creates new thread
		Thread thread = new Thread(this);
		thread.start();
	}
	
	//retrieve name of download file
	private String getFileName(URL url){
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/')+1);
	}
	
	//run method to download the file
	@Override
	public void run() {
		//to be setup
		RandomAccessFile file = null;
		InputStream stream = null;
		
		try{
			//opens connection the URL (only supports HttpURL connection)
			HttpURLConnection connection =
			(HttpURLConnection) url.openConnection();
			
			//select portion of file bytes to download
			connection.setRequestProperty("Range", "bytes=" + downloaded +"-");
		    
			//connect to the server
			connection.connect();
			
			//retrieve response code, check for in range
			if(connection.getResponseCode() / 100 !=2){
				error();
			}
			
			//check content length is valid
			int contentLength = connection.getContentLength();
			if(contentLength < 1){
				error();
			}
			
			//set size for download if null
			if(size == -1){
				size = contentLength;
				stateChanged();
			}
		    
			//open the files and seek its end (RAF in "rw")
			file = new RandomAccessFile(getFileName(url), "rw");
			file.seek(downloaded);
			
			stream = connection.getInputStream();
			
			//runs as long as DOWNLOADING
			while (status == DOWNLOADING) {
				//size buffer by remainder of file to download
				byte buffer[];
				if(size - downloaded > MAX_BUFFER_SIZE){
					buffer = new byte[MAX_BUFFER_SIZE];
				}else{
					buffer = new byte[size - downloaded];
				}
				
				//reads from server to buffer from buffer[]
				int read = stream.read(buffer);
				if(read == -1)
					//exit loop, invalid
					break;
				
			    //write buffer to file
				file.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
				}
				
				/*change status to complete as download
				is complete */
				if(status == DOWNLOADING){
					status = COMPLETE;
					stateChanged();
				}
				
			}catch (Exception e){
				error();
			}finally{
				//closes file
				if(file != null){
					try{
						file.close();
					}catch(Exception e) {}
				}
			
			//closes connection
			if(stream != null){
				try{
					stream.close();
				}catch(Exception e){}
			}
		
		}
		
	}
	//keeps observer updated from Observable
	 private void stateChanged(){
		 setChanged();
		 notifyObservers();
	 }
	
}

