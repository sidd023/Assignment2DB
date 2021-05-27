import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;


public class bplustree {

	private int maxKeys;
	private Node node;

	public bplustree() {
		maxKeys = constants.MAX_NUM_KEYS;
		node = new leafNode();
	}

	int getMaxKeys() {
		return maxKeys;
	}

	Node getNode() {
		return node;
	}

	void setNode(Node node) {
		this.node = node;
	}

	// search for a key in the tree
	public void searchTree(String key) {
		node.search_inTree(key);
	}

	// traverse the whole tree
	public void traverse_tree(FileOutputStream fos) {
		node.traverse(fos);
	}

	// sensor id or date range search within the tree
	public void rangeSearch(String k1, String k2, int searchType) {
		node.rangeSearch(k1, k2, searchType);
	}

	// insert the key and value into tree
	public void insert(String key, String value) {
		node.insert(key, value);
	}

	// abstract class for node implementation
	public abstract class Node {

		// list to store all keys
		List < String > keys; 

		// length of the key
		int keySize() {
			return keys.size();
		}

		// search for key within the tree
		abstract void search_inTree(String key); 

		// traverse the entire tree
		abstract void traverse(FileOutputStream fos); 

		// range search with range provided
		// searchType specifies to search within senosorId or Date
		abstract void rangeSearch(String k1, String k2, int searchType); 

		// to prevent double visitation of a node
		abstract boolean isVisited(); 

		// insert a key and value into tree
		abstract void insert(String key, String value);

		// to get first key on left
		abstract String getFirstLeafKey(); 

		// split the function while adding into tree
		abstract Node split(); 

		// check if the max number of keys is reached
		abstract boolean isOverflow(); 
	}

	
	
	public class leafNode extends Node {

		List < String > values;
		leafNode next;
		Boolean visited = false;
		innerNode node = new innerNode();

		public leafNode() {
			keys = new ArrayList < String > ();
			values = new ArrayList < String > ();
		}

		@Override
		public boolean isVisited() {
			return visited;
		}

		// search among the keys
		@Override
		public void search_inTree(String key) {
			// find the key in the tree
			visited = true;
			for (int i = 0; i < keys.size(); i++) {
				String stored_key = keys.get(i);
				String stored_value = values.get(i);
				if (stored_key.toLowerCase().contains(key.toLowerCase()) || stored_key.compareTo(key) == 0) {
					System.out.println("Found in B+ Tree [" + key + "]: " + stored_key + " - " + stored_value);
					// pass the values to seek into heap and find the record 
					dbquery.readSeek(stored_value, key);

				}
			}
		}

		// range search with specific range to be passed
		@Override
		public void rangeSearch(String key1, String key2, int searchType) {
			visited = true;
			for (int i = 0; i < keys.size(); i++) {
				String stored_key = keys.get(i);
				String stored_value = values.get(i);
				// check for the search type, id or date
				if (searchType == constants.RANGE_KEY_ID) {
					int range1 = Integer.parseInt(key1);
					int range2 = Integer.parseInt(key2);
					String[] search = stored_key.split("_");
					int searchKey = Integer.parseInt(search[0]);
					if (searchKey >= range1 && searchKey <= range2) {
						System.out.println("Found in B+ Tree Range [" + key1 + "] " + constants.RANGE_DELIMITER + " [" +
								key2 + "]: " + stored_key + " - " + stored_value);
						// pass the values to seek into the specific page
						dbquery.readSeek(stored_value, Integer.toString(searchKey));
					}
				} else if (searchType == constants.RANGE_KEY_DATE) {
					String space_split[] = stored_key.split(" ");
					String date_split[] = space_split[0].split("_");
					String date = date_split[1];
					Date search_date = null;
					Date range_date1 = null;
					Date range_date2 = null;

					try {
						search_date = new SimpleDateFormat("MM/dd/yyyy").parse(date);
						range_date1 = new SimpleDateFormat("MM/dd/yyyy").parse(key1);
						range_date2 = new SimpleDateFormat("MM/dd/yyyy").parse(key2);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (search_date.after(range_date1) && search_date.before(range_date2)) {
						System.out.println("Found in B+ Tree Range [" + key1 + "] " + constants.RANGE_DELIMITER + " [" +
								key2 + "]: " + stored_key + " - " + stored_value);

						// pass the found values to seek into the page
						dbquery.readSeek(stored_value, date);
					}
				}
			}
		}

		@Override
		public void traverse(FileOutputStream fos) {
			visited = true;
			byte[] record = new byte[constants.TREE_RECORD_SIZE];

			// iterate through the list
			for (int i = 0; i < keys.size(); i++) {
				String stored_key = keys.get(i);
				String stored_value = values.get(i);
				String output_string = stored_key + "," + stored_value + ",";
				record = output_string.getBytes();
				try {
					// write into the file
					fos.write(record);
				} catch (FileNotFoundException e) {
					System.out.println("File " + constants.BPLUS_TREE_FILE_NAME + " not found.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// insert a key into a tree
		@Override
		public void insert(String key, String value) {

			// find the key using binary search
			// If the key is present, add the new key next to it
			// else create a new key at index and add values

			int location = Collections.binarySearch(keys, key);
			int valueIndex = 0;
			if (location>0) {
				valueIndex = location+1;
				values.set(valueIndex, value);
			}
			else {
				valueIndex = -location-1;
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}

			// check for overflow
			if (node.isOverflow()) {
				Node sibling = split();
				innerNode newRoot = new innerNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				node = newRoot;
			}
		}

		@Override
		public String getFirstLeafKey() {
			return keys.get(0);
		}

		// split the node 
		@Override
		public Node split() {
			// create a new leafnode
			// the first and second half of the keys and values are assigned
			leafNode sibling = new leafNode();
			int from_key = (keySize() + 1) / 2;
			int to_key = keySize();
			sibling.keys.addAll(keys.subList(from_key, to_key));
			sibling.values.addAll(values.subList(from_key, to_key));

			keys.subList(from_key, to_key).clear();
			values.subList(from_key, to_key).clear();

			sibling.next = next;
			next = sibling;
			return sibling;
		}

		// check for overflow
		@Override
		public boolean isOverflow() {
			return values.size() > maxKeys - 1;
		}
	}
	
	
	public class innerNode extends Node {
		List < Node > children;
		Boolean visited = false;

		public innerNode() {
			this.keys = new ArrayList < String > ();
			this.children = new ArrayList < Node > ();
		}

		@Override
		public boolean isVisited() {
			return visited;
		}

		@Override
		public void search_inTree(String key) {
			// iterate through the child node to search for a key
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).isVisited()) {
					children.get(i).search_inTree(key);
					// set visited to prevent from double visitation
					visited = true;
				}
			}
		}

		@Override
		public void traverse(FileOutputStream fos) {
			// iterate through each child node
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).isVisited()) {
					children.get(i).traverse(fos);
					visited = true;
				}
			}
		}

		@Override
		public void rangeSearch(String k1, String k2, int searchType) {
			// perform range search
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).isVisited()) {
					children.get(i).rangeSearch(k1, k2, searchType);
					visited = true;
				}
			}
		}

		@Override
		public void insert(String key, String value) {

			Node child = getChild(key);
			// insert key and value to the keys location stored
			child.insert(key, value); 

			// set the sibling as child if the child overflows
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			// create a new node after overflow
			if (node.isOverflow()) {
				Node sibling = split();
				innerNode newRoot = new innerNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				node = newRoot;
			}
		}

		// get the leaf on the left 
		@Override
		public String getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}

		// split the node
		@Override
		public Node split() {
			int from_key = keySize() / 2 + 1;
			int to_key = keySize();
			innerNode sibling = new innerNode();
			// copy keys and values to new innerNode
			sibling.keys.addAll(keys.subList(from_key, to_key));
			sibling.children.addAll(children.subList(from_key, to_key + 1));

			// clear list
			keys.subList(from_key - 1, to_key).clear();
			children.subList(from_key, to_key + 1).clear();

			return sibling;
		}

		// check for overflow
		@Override
		public boolean isOverflow() {
			return children.size() > maxKeys;
		}

		// get the location of the key using binary search and return the node
		public Node getChild(String key) {

			// get the location of the key among the keys list
			int location = Collections.binarySearch(keys, key);
			int childIndex = 0;
			if (location>0)
				childIndex = location+1;
			else
				childIndex = -location-1;
			return children.get(childIndex);
		}

		// insert the key and the child into the tree
		public void insertChild(String key, Node child) {
			// get the location of the key among the keys list
			int location = Collections.binarySearch(keys, key);
			int childIndex = 0;
			// if key in not empty, set the child
			if (location>0) {
				childIndex = location+1;
				children.set(childIndex, child);
			}
			// if key is empty, add keys and child
			else {
				childIndex = -location-1;
				keys.add(childIndex, key);
				children.add(childIndex + 1, child);
			}
		}


		public Node getChildLeftSibling(String key) {

			// get the location of the key among the keys list
			int location = Collections.binarySearch(keys, key);
			int childIndex = 0;
			if (location>0)
				childIndex = location+1;
			else
				childIndex = -location-1;

			if (childIndex > 0)
				return children.get(childIndex - 1);

			return null;
		}

		public Node getChildRightSibling(String key) {

			// get the location of the key among the keys list
			int location = Collections.binarySearch(keys, key);
			int childIndex = 0;
			if (location>0)
				childIndex = location+1;
			else
				childIndex = -location-1;

			// return only if the index is less than the max key size
			if (childIndex < keySize())
				return children.get(childIndex + 1);

			return null;
		}
	}

	
	
}