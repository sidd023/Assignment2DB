import java.io.FileOutputStream;
import java.util.List;


public abstract class Node {
		List<String> keys; 

		abstract void insert(String key, String value); 
		
		abstract boolean isOverflow();
		
		abstract void search(String key);
		
		abstract boolean isVisited();

		abstract  String getFirstLeafKey() ;

		abstract Node split();
		
		int keySize() {
			return keys.size();
		}
		
	
}
