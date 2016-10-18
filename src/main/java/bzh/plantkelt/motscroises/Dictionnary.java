/*
 *  This file is part of MotsCroisés.
 *
 *  MotsCroisés is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MotsCroisés is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MotsCroisés. If not, see <http://www.gnu.org/licenses/>.
 */
package bzh.plantkelt.motscroises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class Dictionnary {

	private Set<String> words;
	private long totalLetterCount = 0;
	private ListMultimap<String, String> patterns;
	private AlphabetFreq alphabetFrequencies;
	private long[] lengthFrequencies;

	private int[] MAX_LETTER_COUNT_IN_PATTERN = new int[] { 0, 1, 2, 3, 4, 4, 4,
			3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1 };

	public Dictionnary(String filename, int maxWordLen) {
		if (maxWordLen >= MAX_LETTER_COUNT_IN_PATTERN.length)
			throw new IllegalArgumentException(
					"maxWordLen is too large, maximum is "
							+ MAX_LETTER_COUNT_IN_PATTERN.length);

		// Load words
		Logger.error("Loading dictionnary '" + filename + "'...");
		words = new HashSet<>(500000);
		try {
			Files.lines(Paths.get(filename)).forEach((line) -> {
				String word = stripAccents(line).toUpperCase().replaceAll("-",
						"");
				int len = word.length();
				if (isAlpha(word) && len > 1 && len <= maxWordLen) {
					words.add(word);
				}
			});
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		Logger.error("Loaded " + words.size() + " words.");

		// Index words
		Logger.error("Indexing words...");
		patterns = ArrayListMultimap.create(100000, 10);
		alphabetFrequencies = new AlphabetFreq();
		lengthFrequencies = new long[maxWordLen + 1];
		for (int i = 0; i < maxWordLen + 1; i++) {
			lengthFrequencies[i] = 0;
		}
		int n = 0;
		for (String word : words) {
			int len = word.length();
			totalLetterCount += len;
			int maxLetters = MAX_LETTER_COUNT_IN_PATTERN[len];
			lengthFrequencies[len]++;
			char[] wordLetters = word.toCharArray();
			char[] pattern = new char[len];
			for (int i = 0; i < len; i++) {
				pattern[i] = '_';
			}

			// 1-char pattern & letter frequencies
			for (int i = 0; i < len; i++) {
				char letter = wordLetters[i];
				alphabetFrequencies.inc(letter);
				pattern[i] = letter;
				patterns.put(new String(pattern), word);
				pattern[i] = '_';
			}

			if (maxLetters >= 2) {
				// 2-chars pattern
				for (int i = 0; i < len; i++) {
					pattern[i] = wordLetters[i];
					for (int j = i + 1; j < len; j++) {
						pattern[j] = wordLetters[j];
						patterns.put(new String(pattern), word);
						pattern[j] = '_';
					}
					pattern[i] = '_';
				}
			}

			if (maxLetters >= 3) {
				// 3-chars pattern
				for (int i = 0; i < len; i++) {
					pattern[i] = wordLetters[i];
					for (int j = i + 1; j < len; j++) {
						pattern[j] = wordLetters[j];
						for (int k = j + 1; k < len; k++) {
							pattern[k] = wordLetters[k];
							patterns.put(new String(pattern), word);
							pattern[k] = '_';
						}
						pattern[j] = '_';
					}
					pattern[i] = '_';
				}
			}

			if (maxLetters >= 4) {
				// 4-chars pattern
				for (int i = 0; i < len; i++) {
					pattern[i] = wordLetters[i];
					for (int j = i + 1; j < len; j++) {
						pattern[j] = wordLetters[j];
						for (int k = j + 1; k < len; k++) {
							pattern[k] = wordLetters[k];
							for (int l = k + 1; l < len; l++) {
								pattern[l] = wordLetters[l];
								patterns.put(new String(pattern), word);
								pattern[l] = '_';
							}
							pattern[k] = '_';
						}
						pattern[j] = '_';
					}
					pattern[i] = '_';
				}
			}

			if (maxLetters >= 5)
				throw new IllegalArgumentException(
						"Too many max letters in pattern, max 4.");

			n++;
			if (n % 100000 == 0)
				Logger.warn(n + " words indexed");
		}

	}

	public void dump(int maxPatterns) {

		// Sort patterns by frequency
		List<Map.Entry<String, List<String>>> entries = new ArrayList<>(
				Multimaps.asMap(patterns).entrySet());
		Collections.sort(entries,
				new Comparator<Map.Entry<String, List<String>>>() {
					@Override
					public int compare(Map.Entry<String, List<String>> e1,
							Map.Entry<String, List<String>> e2) {
						return Integer.compare(e2.getValue().size(),
								e1.getValue().size());
					}
				});
		System.out.println(maxPatterns + " most common patterns:");
		int n = 0;
		for (Map.Entry<String, List<String>> entry : entries) {
			System.out.println(String.format("%20s : %8d", entry.getKey(),
					entry.getValue().size()));
			n++;
			if (n > maxPatterns)
				break;
		}

		System.out.println("Letters frequencies:");
		for (char letter = 'A'; letter <= 'Z'; letter++) {
			System.out
					.println(letter + ": " + alphabetFrequencies.freq(letter));
		}

		System.out.println("Word length frequencies:");
		for (int i = 1; i < lengthFrequencies.length; i++) {
			System.out.println(i + ": " + lengthFrequencies[i]);
		}

		System.out.println("Number of patterns: " + patterns.keySet().size());
		System.out.println("Number of patterns entries: " + patterns.size());
	}

	public List<String> getWords(String pattern) {
		// Count number of letters in pattern
		final char[] patternChars = pattern.toCharArray();
		int patternLen = patternChars.length;
		List<Integer> letterIndexes = new ArrayList<>(patternLen);
		for (int i = 0; i < patternLen; i++) {
			if (patternChars[i] != '_') {
				letterIndexes.add(i);
			}
		}
		int maxLetters = patternLen < MAX_LETTER_COUNT_IN_PATTERN.length
				? MAX_LETTER_COUNT_IN_PATTERN[patternLen] : 1;
		if (letterIndexes.size() <= maxLetters) {
			// The pattern is OK, use it right away
			return patterns.get(pattern);
		} else {
			// Too much letters, strip the most common ones
			Collections.sort(letterIndexes, new Comparator<Integer>() {
				@Override
				public int compare(Integer i1, Integer i2) {
					long freq1 = alphabetFrequencies.freq(patternChars[i1]);
					long freq2 = alphabetFrequencies.freq(patternChars[i2]);
					return Long.compare(freq2, freq1);
				}
			});
			int nLettersToRemove = letterIndexes.size() - maxLetters;
			int[] indexesToCheck = new int[nLettersToRemove];
			for (int i = 0; i < nLettersToRemove; i++) {
				int letterIndex = letterIndexes.get(i);
				patternChars[letterIndex] = '_';
				indexesToCheck[i] = letterIndex;
			}
			String reducedPattern = new String(patternChars);

			// Linear scan potential words,
			// stripping away the one that does not fit the pattern
			List<String> potentials = patterns.get(reducedPattern);
			List<String> retval = new ArrayList<>(potentials.size());
			char[] patternChars2 = pattern.toCharArray();
			for (String potential : potentials) {
				char[] letters = potential.toCharArray();
				boolean fits = true;
				for (int i : indexesToCheck) {
					if (letters[i] != patternChars2[i]) {
						fits = false;
						break;
					}
				}
				if (fits) {
					retval.add(potential);
				}
			}
			return retval;
		}
	}

	public double letterFrequency(char letter) {
		return 1. * alphabetFrequencies.freq(letter) / totalLetterCount;
	}

	private boolean isAlpha(String name) {
		for (char c : name.toCharArray()) {
			if (!Character.isLetter(c)) {
				return false;
			}
		}
		return true;
	}

	private String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}
}
