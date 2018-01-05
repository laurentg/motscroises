package bzh.plantkelt.motscroises.dafsa;

import java.util.Objects;

import com.google.common.collect.Multimap;

import bzh.plantkelt.motscroises.dafsa.Dafsa.InternalVisitor;
import bzh.plantkelt.motscroises.dafsa.Dafsa.Visitor;

/**
 */
class DafsaNode {

	private int hash = 0;
	private DafsaNode next[] = new DafsaNode[26];
	private boolean eow[] = new boolean[26];

	DafsaNode(Dafsa dafsa) {
		dafsa.nodeCount++;
	}

	void insert(Dafsa dafsa, String word, int wordIndex) {
		char ch = word.charAt(wordIndex);
		int index = (int) ch - (int) 'a';
		if (index < 0 || index >= 26) {
			throw new IllegalArgumentException(
					"Char out of bounds (" + index + "): '" + ch + "' in word '"
							+ word + "' at index " + wordIndex);
		}
		if (wordIndex == word.length() - 1) {
			eow[index] = true;
		} else {
			DafsaNode node = next[index];
			if (node == null) {
				node = new DafsaNode(dafsa);
				next[index] = node;
			}
			node.insert(dafsa, word, wordIndex + 1);
		}
		hash = 0;
	}

	boolean contains(String word, int wordIndex) {
		char ch = word.charAt(wordIndex);
		int index = (int) ch - (int) 'a';
		if (wordIndex == word.length() - 1) {
			return eow[index];
		} else {
			DafsaNode node = next[index];
			if (node == null) {
				return false;
			}
			return node.contains(word, wordIndex + 1);
		}
	}

	void visit(InternalVisitor visitor) {
		StringBuffer word = new StringBuffer(30);
		doVisit(visitor, 0, word);
	}

	public <T> void visit(Visitor<T> visitor, int level, StringBuffer word) {
		for (int i = 0; i < next.length; i++) {
			DafsaNode node = next[i];
			boolean eowd = eow[i];
			if (node != null || eowd) {
				char ch = (char) ((int) 'a' + i);
				T t = visitor.accept(ch, level);
				if (t != null) {
					word.append(ch);
					if (node != null) {
						node.visit(visitor, level + 1, word);
					}
					if (eowd)
						visitor.word(word.toString());
					word.setLength(word.length() - 1);
					visitor.back(t);
				}
			}
		}
	}

	private void doVisit(InternalVisitor visitor, int level,
			StringBuffer word) {
		for (int i = 0; i < next.length; i++) {
			DafsaNode node = next[i];
			boolean eowd = eow[i];
			if (node != null || eowd) {
				char ch = (char) ((int) 'a' + i);
				word.append(ch);
				if (visitor.visit(eowd, ch, word, node, level)) {
					if (node != null) {
						node.doVisit(visitor, level + 1, word);
					}
				}
				word.setLength(word.length() - 1);
			}
		}
	}

	void doCompact(Dafsa dafsa, Multimap<Integer, DafsaNode> nodesCache) {
		for (int i = 0; i < next.length; i++) {
			DafsaNode node = next[i];
			if (node == null)
				continue;
			int hash = node.hashCode();
			boolean replaced = false;
			cacheloop: for (DafsaNode onode : nodesCache.get(hash)) {
				if (node.equals(onode)) {
					node = onode;
					next[i] = onode;
					replaced = true;
					dafsa.nodeCount--;
					break cacheloop;
				}
			}
			if (!replaced) {
				nodesCache.put(hash, node);
			}
			/*
			 * Note: we compact *all* nodes to make sure nodeCount is properly
			 * computed.
			 */
			node.doCompact(dafsa, nodesCache);
		}
	}

	@Override
	public int hashCode() {
		if (hash != 0)
			return hash;
		hash = 1;
		for (int i = 0; i < next.length; i++) {
			DafsaNode node = next[i];
			int lhash = eow[i] ? 1 : 0;
			if (node != null) {
				lhash += node.hashCode();
			}
			hash = 31 * hash + lhash;
		}
		if (hash == 0)
			hash = -1;
		return hash;
	}

	@Override
	public boolean equals(Object another) {
		if (another == null)
			return false;
		if (another == this)
			return true;
		if (!(another instanceof DafsaNode))
			return false;
		DafsaNode other = (DafsaNode) another;
		for (int i = 0; i < next.length; i++) {
			if (eow[i] != other.eow[i])
				return false;
			if (!Objects.equals(next[i], other.next[i]))
				return false;
		}
		return true;
	}
}
