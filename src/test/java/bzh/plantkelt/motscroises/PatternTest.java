package bzh.plantkelt.motscroises;

import junit.framework.TestCase;

public class PatternTest extends TestCase {

	public void testPatterns() {
		testPattern("A", 0, new String[][] { { "A" } });
		testPattern("AB", 0, new String[][] { { "AB" } });
		testPattern("A_", 0, new String[][] { { "A" } });
		testPattern("A__", 0, new String[][] { { "A_" }, { "A" } });
		testPattern("_A", 1, new String[][] { { "A" } });
		testPattern("__A", 2, new String[][] { { "_A" }, { "A" } });
		testPattern("A___UW_", 0, new String[][] { { "A___UW" }, {}, {},
				{ "A__" }, { "A_" }, { "A" } });
		testPattern("A___UW_", 4, new String[][] { { "A___UW" }, { "__UW_" },
				{ "__UW", "_UW_" }, { "_UW", "UW_" }, { "UW" }, {} });
		testPattern("A___UW_", 5, new String[][] { { "A___UW" }, { "__UW_" },
				{ "__UW", "_UW_" }, { "_UW", "UW_" }, { "UW" }, {} });
		testPattern("__A__B__C__", 2,
				new String[][] { { "__A__B__C_", "_A__B__C__" },
						{ "__A__B__C", "_A__B__C_", "A__B__C__" },
						{ "_A__B__C", "A__B__C_" }, { "__A__B_", "A__B__C" },
						{ "__A__B", "_A__B_" }, { "_A__B", "A__B_" },
						{ "__A_", "A__B" }, { "__A", "_A_" }, { "_A", "A_" },
						{ "A" } });
		testPattern("__A__B__C__", 5,
				new String[][] { { "__A__B__C_", "_A__B__C__" },
						{ "__A__B__C", "_A__B__C_", "A__B__C__" },
						{ "_A__B__C", "A__B__C_" },
						{ "__A__B_", "A__B__C", "_B__C__" },
						{ "__A__B", "_A__B_", "_B__C_", "B__C__" },
						{ "_A__B", "A__B_", "_B__C", "B__C_" },
						{ "A__B", "B__C" }, { "_B_" }, { "_B", "B_" },
						{ "B" } });
	}

	private void testPattern(String pattern, int cellIndexInPattern,
			String[][] result) {
		WordOnGrid wog = new WordOnGrid(0, 0, true, pattern.toCharArray());
		System.out.println(String.format(
				"Pattern: [%s] cellIndexInPattern=%d (char=%c)", pattern,
				cellIndexInPattern, pattern.charAt(cellIndexInPattern)));
		for (int delta = 1; delta < pattern.length(); delta++) {
			String[] expected = result[delta - 1];
			int i = 0;
			for (WordOnGrid subPattern : wog.subPatterns(delta,
					cellIndexInPattern)) {
				assertEquals(subPattern.word(), expected[i]);
				i++;
			}
		}
	}
}
