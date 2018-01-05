package bzh.plantkelt.motscroises.dafsa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * DAFSA - Deterministic Acyclic Finite State Automaton.
 * 
 * This class is not synchronized.
 */
public class Dafsa {

	public interface Visitor<T> {
		public T accept(char ch, int index);

		public void word(String word);

		public void back(T t);
	}

	interface InternalVisitor {
		public boolean visit(boolean eow, char ch, StringBuffer word,
				DafsaNode node, int level);
	}

	int nodeCount = 0;
	private boolean compacted = false;

	private DafsaNode root = new DafsaNode(this);

	public Dafsa() {
	}

	public void insert(String word) {
		if (word.isEmpty())
			throw new IllegalArgumentException("Can't store an empty string.");
		if (compacted)
			throw new IllegalStateException(
					"Can't insert new word on a compacted Dawg.");
		root.insert(this, word, 0);
	}

	public boolean contains(String word) {
		if (word.isEmpty())
			return false;
		return root.contains(word, 0);
	}

	public <T> void visit(Visitor<T> visitor) {
		StringBuffer sb = new StringBuffer();
		root.visit(visitor, 0, sb);
	}

	public void print() {
		root.visit(new InternalVisitor() {
			@Override
			public boolean visit(boolean eow, char ch, StringBuffer word,
					DafsaNode node, int level) {
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < level; j++)
					sb.append("  ");
				sb.append(ch);
				if (eow)
					sb.append(" @");
				if (node != null) {
					sb.append(" hash=")
							.append(Integer.toHexString(node.hashCode()))
							.append(", id=").append(Integer.toHexString(
									System.identityHashCode(node)));
				} else {
					sb.append(" ]");
				}
				if (eow)
					sb.append(" (").append(word.toString()).append(")");
				System.out.println(sb);
				return true;
			}
		});
	}

	/** TODO Return an iterator */
	public List<String> generate() {
		final List<String> ret = new ArrayList<>();
		root.visit(new InternalVisitor() {
			@Override
			public boolean visit(boolean eow, char ch, StringBuffer word,
					DafsaNode node, int level) {
				if (eow) {
					ret.add(word.toString());
				}
				return true;
			}
		});
		return ret;
	}

	public void compact() {
		if (compacted)
			return;
		Multimap<Integer, DafsaNode> nodesCache = ArrayListMultimap.create();
		root.doCompact(this, nodesCache);
		compacted = true;
	}

	public int nodeCount() {
		return nodeCount;
	}

	public void readFile(String file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				insert(line.trim().toLowerCase());
			}
		}
	}
}
