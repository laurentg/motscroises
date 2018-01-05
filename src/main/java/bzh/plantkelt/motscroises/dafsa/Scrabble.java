package bzh.plantkelt.motscroises.dafsa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bzh.plantkelt.motscroises.dafsa.Dafsa.Visitor;
import bzh.plantkelt.motscroises.dafsa.Scrabble.ScrabbleVisitor.ScrabbleState;

public class Scrabble {

	static class ScrabbleVisitor implements Visitor<ScrabbleState> {

		static class ScrabbleState {
			boolean anyOther;
			char ch;
		}

		private int board[] = new int[27];
		private boolean anyUsed = false;
		private boolean any[] = new boolean[26];

		private List<String> words = new ArrayList<>();

		public ScrabbleVisitor(String boardLetters) {
			this(boardLetters, "");
		}

		public ScrabbleVisitor(String boardLetters, String anyOtherLetters) {
			for (char ch : boardLetters.toLowerCase().toCharArray()) {
				int index = ch == '?' ? 0 : 1 + (int) ch - (int) 'a';
				board[index]++;
			}
			for (char ch : anyOtherLetters.toLowerCase().toCharArray()) {
				int index = ch == '?' ? 0 : (int) ch - (int) 'a';
				any[index] = true;
			}
		}

		@Override
		public ScrabbleState accept(char ch, int index) {
			ScrabbleState ret = null;
			int bndx = 1 + (int) ch - (int) 'a';
			if (board[bndx] > 0) {
				board[bndx]--;
				ret = new ScrabbleState();
				ret.ch = ch;
				return ret;
			} else if (!anyUsed && any[bndx - 1]) {
				anyUsed = true;
				ret = new ScrabbleState();
				ret.anyOther = true;
				return ret;
			} else if (board[0] > 0) {
				board[0]--;
				ret = new ScrabbleState();
				ret.ch = '?';
				return ret;
			} else {
				return null;
			}
		}

		@Override
		public void word(String word) {
			words.add(word);
		}

		@Override
		public void back(ScrabbleState scs) {
			if (scs.anyOther) {
				anyUsed = false;
			} else {
				int index = scs.ch == '?' ? 0 : 1 + (int) scs.ch - (int) 'a';
				board[index]++;
			}
		}

		public List<String> result() {
			Collections.sort(words, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					int l1 = s1.length();
					int l2 = s2.length();
					return (l1 == l2 ? s1.compareTo(s2)
							: Integer.compare(l2, l1));
				}
			});
			return words;
		}
	}

	public static void main(String[] args) throws IOException {

		Dafsa dict = new Dafsa();
		System.out.println("Reading file...");
		dict.readFile("/usr/share/dict/ods7.txt");
		System.out.println("Compacting...");
		System.out.println(dict.nodeCount() + " nodes before compact");
		dict.compact();
		System.out.println(dict.nodeCount() + " nodes after compact");
		System.out.println("Done");

		ScrabbleVisitor sv = new ScrabbleVisitor("PLITS", "RAETN");
		dict.visit(sv);
		for (String word : sv.result()) {
			System.out.println(word);
		}
	}
}
