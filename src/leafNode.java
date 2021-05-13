import java.io.FileNotFoundException; 
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class leafNode extends Node {

	bplustree _bt = new bplustree();
	leafNode next;
	Boolean visited = false;
		List<String> values;
		
		

		public leafNode() {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}

		
		@Override
		public void insert(String key, String value) {
			
			int location = Collections.binarySearch(keys, key);
			int valueIndex = 0;
			
			if (location >= 0) {
				valueIndex = location;
				values.set(valueIndex, value);
			} 
			else {
				valueIndex = -location - 1;
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}
			if (_bt.getroot().isOverflow()) {
				Node sibling = split();
				innerNode newRoot = new innerNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				_bt.setRoot(newRoot);
			}
		}

		@Override
		public String getFirstLeafKey() {
			return keys.get(0);
		}
		
		
		@Override
		public Node split() {
			leafNode sibling = new leafNode();
			int node_from = (keySize() + 1) / 2;
			int node_to = keySize();
			sibling.keys.addAll(keys.subList(node_from, node_to));
			sibling.values.addAll(values.subList(node_from, node_to));

			keys.subList(node_from, node_to).clear();
			values.subList(node_from, node_to).clear();

			sibling.next = next;
			next = sibling;
			return sibling;
		}

		
		@Override
		public boolean isOverflow() {
			return values.size() > _bt.getMaxKey() - 1;
		}
		
		@Override
		public void search(String key) {
			// find text in key or part of key
			visited = true;
			for (int i = 0; i < keys.size(); i++) {
				String s = keys.get(i); 
				String v = values.get(i);
				String k = key;
				if (s.toLowerCase().contains(k.toLowerCase()) || s.compareTo(k)==0) {
					System.out.println("Found in B+ Tree [" + k + "]: " + s + " - " + v);
				}
			}
		}


		@Override
		public boolean isVisited() {
			return visited;
		}

		

		
	}