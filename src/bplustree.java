import java.io.FileOutputStream;

public class bplustree {
	
	
	private int maxKeys;
	private Node root;
	
	public int getMaxKey()
	{
		return maxKeys;
	}
	
	public Node getroot()
	{
		return root;
	}
	
	public void setRoot(Node root)
	{
		this.root = root;
	}
	
	public bplustree()
	{
		maxKeys = constants.MAX_NUM_KEYS; 
		root = new leafNode();
	}
	
	public void insert(String key, String value) {
		root.insert(key, value);
	}
	
	// search for key or part of key
		public void search(String key) {
			root.search(key);
		}

}
