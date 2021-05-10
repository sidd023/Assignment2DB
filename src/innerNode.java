import java.io.FileOutputStream; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class innerNode extends Node {
		List<Node> children;
		Boolean visited = false;
		bplustree _bt = new bplustree();
	
		public innerNode() {
			this.keys = new ArrayList<String>();
			this.children = new ArrayList<Node>();
		}

		@Override
		public void insert(String key, String value) {
			Node child = getChild(key);
			child.insert(key, value);
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
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

		public Node getChild(String key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			return children.get(childIndex);
		}

		public void insertChild(String key, Node child) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;

			if (loc >= 0) {
				children.set(childIndex, child);
			} 
			else {
				keys.add(childIndex, key);
				children.add(childIndex + 1, child);
			}
		}

		@Override
		public String getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}
	
		@Override
		public Node split() {
			int node_from = keySize() / 2 + 1;
			int node_to = keySize();
			innerNode sibling = new innerNode();
			sibling.keys.addAll(keys.subList(node_from, node_to));
			sibling.children.addAll(children.subList(node_from, node_to + 1));
			keys.subList(node_from - 1, node_to).clear();
			children.subList(node_from, node_to + 1).clear();
			return sibling;
		}

		@Override
		public boolean isOverflow() {
			return children.size() > _bt.getMaxKey();
		}
	}