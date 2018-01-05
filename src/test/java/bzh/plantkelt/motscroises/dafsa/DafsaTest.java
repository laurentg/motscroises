package bzh.plantkelt.motscroises.dafsa;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bzh.plantkelt.motscroises.dafsa.Dafsa;
import bzh.plantkelt.motscroises.dafsa.Scrabble.ScrabbleVisitor;
import junit.framework.TestCase;

public class DafsaTest extends TestCase {

	public void testDafsaContains() {
		Dafsa dafsa = new Dafsa();
		assertFalse(dafsa.contains(""));
		assertFalse(dafsa.contains("cat"));
		dafsa.insert("cat");
		assertFalse(dafsa.contains("ca"));
		assertFalse(dafsa.contains("can"));
		assertTrue(dafsa.contains("cat"));
		assertFalse(dafsa.contains("cats"));
		dafsa.insert("cats");
		assertTrue(dafsa.contains("cats"));
		assertFalse(dafsa.contains("catss"));
		dafsa.compact();
		assertTrue(dafsa.contains("cats"));
		assertFalse(dafsa.contains("catss"));
	}

	public void testDafsaGenerate() {
		Dafsa dafsa = new Dafsa();
		dafsa.insert("car");
		dafsa.insert("cars");
		dafsa.insert("cat");
		dafsa.insert("cats");
		dafsa.insert("do");
		dafsa.insert("dog");
		dafsa.insert("dogs");
		dafsa.insert("done");
		dafsa.insert("ear");
		dafsa.insert("ears");
		dafsa.insert("eat");
		dafsa.insert("eats");
		dafsa.insert("eons");
		dafsa.compact();

		List<String> words = dafsa.generate();
		assertEquals("car", words.get(0));
		assertEquals("cars", words.get(1));
		assertEquals("cat", words.get(2));
		assertEquals("eons", words.get(12));
		assertEquals(13, words.size());
	}

	public void testDafsaAccept() {
		Dafsa dafsa = new Dafsa();
		dafsa.insert("car");
		dafsa.insert("cars");
		dafsa.insert("cat");
		dafsa.insert("cats");
		dafsa.insert("do");
		dafsa.insert("dog");
		dafsa.insert("dogs");
		dafsa.insert("done");
		dafsa.insert("ear");
		dafsa.insert("ears");
		dafsa.insert("eat");
		dafsa.insert("eats");
		dafsa.insert("eons");

		dafsa.compact();
		dafsa.visit(new ScrabbleVisitor("aacr"));
	}

	/**
	 * Note: this test assume /usr/share/dict/ods7.txt exists (Officiel du
	 * Scrabble v7), file containing one word per line. If you do not have this
	 * file, the test is ignored.
	 */
	public void testDafsaDict() throws IOException {
		final String DICT_FILE = "/usr/share/dict/ods7.txt";
		if (!new File(DICT_FILE).exists()) {
			System.err.println("File " + DICT_FILE
					+ " does not exists, skipping dictionnary test.");
			return;
		}
		System.out.println("Loading dictionnary " + DICT_FILE + "...");
		Dafsa dafsa = new Dafsa();
		dafsa.readFile(DICT_FILE);
		System.out.println("Compacting...");
		System.out.println(dafsa.nodeCount() + " nodes before compact");
		dafsa.compact();
		System.out.println(dafsa.nodeCount() + " nodes after compact");
		System.out.println("Done");
		ScrabbleVisitor sv = new ScrabbleVisitor("zwqxonf");
		dafsa.visit(sv);
		Set<String> res = new HashSet<>(sv.result());
		assertEquals(
				new HashSet<>(Arrays.asList("fon", "fox", "won", "no", "on")),
				res);
	}
}
