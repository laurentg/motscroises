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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.EvictingQueue;

public class WordCrosser {

	private Dictionnary dict;
	private LetterGrid grid;
	private CmdLineParams params;
	private int wordsToScoreCount = 100;
	private int niters = 0;
	private Random rand;
	private Queue<WordOnGrid> lastRemoved = EvictingQueue.create(20);

	private static final double LEN_SCORE[] = { 1, 0.1, 2, 8, 16, 32, 48, 64,
			80, 88, 92, 96, 100 };

	public WordCrosser(Dictionnary dict, LetterGrid grid,
			CmdLineParams params) {
		this.dict = dict;
		this.grid = grid;
		this.params = params;
	}

	public boolean solve(long seed) {
		if (seed == 0)
			seed = System.currentTimeMillis();
		rand = new Random(seed);
		Logger.error("Solving, seed is " + seed);

		while (true) {
			niters++;
			if (Logger.level < Logger.WARN) {
				if (niters % 20 == 0)
					Logger.errorNoLn(".");
				if (niters % 1000 == 0)
					Logger.error(niters + " iterations");
			} else {
				Logger.warn("================={ step %d }=================",
						niters);
			}
			if (niters >= params.maxIterations) {
				Logger.error("Bailing out, too much iterations (max %d).",
						niters, params.maxIterations);
				return false;
			}
			if (loop()) {
				GridStats stats = grid.getStats();
				Logger.error("");
				Logger.error("Grid found in %d iterations (seed is %d)", niters,
						seed);
				Logger.error(grid.toString(false, true));
				Logger.error("%d black cells (%d%%)", stats.nBlacks,
						stats.nBlacks * 100 / grid.cellCount());
				return true;
			}
		}
	}

	private boolean loop() {

		GridStats stats = grid.getStats();
		if (stats.nEmpty == 0) {
			return true;
		}

		if (stats.nLetters == 0) {
			// Must seed the grid
			seedGrid();
			Logger.warn(grid.toString());
			return false;
		}

		boolean tooBlack = stats.nBlacks * 100
				/ grid.cellCount() > params.maxBlackPerc;
		if (tooBlack && stats.nFreeBlacks > 0 && !params.fixedBlacks) {
			removeBlack();
			Logger.warn(grid.toString(Logger.level >= Logger.INFO, false));
			return false;
		}

		List<ScoredWord> tentatives = findNextWord();

		// Sort tentatives on score
		Collections.sort(tentatives);

		Logger.info("20 best tentatives:");
		for (int i = 0; i < 20 && i < tentatives.size(); i++) {
			Logger.info(tentatives.get(i).toString());
		}

		boolean remove = false;
		if (tentatives.isEmpty()) {
			remove = true;
		} else {
			ScoredWord best = tentatives.get(0);
			if (best.score <= 0.01) {
				remove = true;
			}
		}

		if (remove) {
			removeWord();
			Logger.warn(grid.toString(Logger.level >= Logger.INFO, false));
			return false;
		}

		ScoredWord tentative = selectTentative(tentatives);
		Logger.warn("Writing word: %s", tentative);
		grid.writeWord(tentative.word);
		Logger.warn(grid.toString(Logger.level >= Logger.INFO, false));

		return false;
	}

	private void seedGrid() {
		LetterCell cell = grid.findRandomEmptyCell(rand);
		// char letter = (char)('A' + rand.nextInt(26));
		char letter = 'E';
		cell.letter = letter;
		Logger.warn("Seeding empty grid with %s", cell);
		lastRemoved.clear();
	}

	private void removeBlack() {
		LetterCell blackCell = grid.findMaxConstraintedBlackCell();
		if (blackCell == null) {
			Logger.warn("Can't find black to remove!");
			return;
		}
		Logger.warn("Too black, removing black %s", blackCell);
		grid.removeBlack(blackCell);
	}

	private void removeWord() {
		while (true) {
			WordOnGrid maxConstrainedWord = grid.findMaxConstrainedOpenWord();
			if (maxConstrainedWord == null) {
				Logger.warn("Can't find constrained word to remove!");
				break;
			}
			Logger.warn("Removing word %s", maxConstrainedWord);
			grid.removeWord(maxConstrainedWord, !params.fixedBlacks);
			boolean finished = !lastRemoved.contains(maxConstrainedWord);
			lastRemoved.add(maxConstrainedWord);
			if (finished)
				break;
		}
	}

	private ScoredWord selectTentative(List<ScoredWord> tentatives) {
		double scoreSum = 0.;
		// TODO Optimize this, this is O(n)
		for (int i = 0; i < tentatives.size() && i < 20; i++) {
			scoreSum += tentatives.get(i).score;
		}
		double x = rand.nextDouble() * scoreSum;
		scoreSum = 0.;
		for (ScoredWord tentative : tentatives) {
			scoreSum += tentative.score;
			if (x <= scoreSum) {
				return tentative;
			}
		}
		// Cannot happen
		return null;
	}

	/**
	 * Return a list of tentative moves, with for each a score. To do this, scan
	 * all open cells, for each look for the longest pattern and fit words.
	 */
	private List<ScoredWord> findNextWord() {
		List<ScoredWord> tentatives = new ArrayList<ScoredWord>();
		Set<WordOnGrid> alreadySeen = new HashSet<>();
		for (LetterCell cell : grid.openLetters()) {

			WordOnGrid fullPattern = findLongestPattern(grid, cell);
			if (fullPattern == null) {
				// Single letter between two blacks
				Logger.debug("### Single-letter cell ", cell);
				continue;
			}
			Logger.debug("### Open cell %s fullPattern %s", cell, fullPattern);

			int cellIndexInPattern = fullPattern.h
					? cell.icol - fullPattern.icol
					: cell.irow - fullPattern.irow;
			List<ScoredWord> cellTentatives = fitWords(fullPattern,
					cellIndexInPattern, alreadySeen);

			tentatives.addAll(cellTentatives);
		}
		return tentatives;
	}

	private WordOnGrid findLongestPattern(LetterGrid grid, LetterCell cell) {
		boolean h = cell.v != null;
		return grid.extractPattern(cell.icol, cell.irow, h);
	}

	private List<ScoredWord> fitWords(WordOnGrid fullPattern,
			int cellIndexInPattern, Set<WordOnGrid> alreadySeen) {
		List<ScoredWord> retval = new ArrayList<>(wordsToScoreCount);
		List<WordOnGrid> patterns = Arrays.asList(fullPattern);
		int delta = 0;
		do {
			for (WordOnGrid subPattern : patterns) {
				double patternScore = scorePattern(subPattern);
				if (patternScore == 0)
					continue;
				List<String> words;
				if (subPattern.word().length() == 1) {
					words = Arrays.asList(subPattern.word());
				} else {
					words = dict.getWords(subPattern.word());
				}
				Logger.debug("Found %d words with sub-pattern %s: %s",
						words.size(), subPattern,
						Arrays.toString(words.toArray()));
				if (subPattern.word().length() > 1
				/* && patternIsSingleSequence(subPattern) */) {
					double constraint = 100.0 / Math.sqrt(words.size() + 1);
					grid.constrain(subPattern, constraint);
				}

				if (words.isEmpty())
					continue;

				int nWords = words.size();
				int offset = rand.nextInt(nWords);
				for (int i = 0; i < nWords; i++) {
					String word = words.get((i + offset) % nWords);
					WordOnGrid filledWord = subPattern.fillWithWord(word);
					retval.add(scoreWord(subPattern, filledWord, patternScore));
					if (retval.size() > wordsToScoreCount)
						return retval;
				}
			}
			delta++;
			if (!params.fixedBlacks && delta >= fullPattern.length()
					|| params.fixedBlacks && delta > 0)
				return retval;
			patterns = fullPattern.subPatterns(delta, cellIndexInPattern);
		} while (retval.size() < wordsToScoreCount);
		return retval;
	}

	private ScoredWord scoreWord(WordOnGrid pattern, WordOnGrid word,
			double patternScore) {
		Collection<LetterCell> oldCells = grid.writeWord(word);
		// Logger.debug("### Scoring word %s pattern %s", word, pattern);
		double totalScore = 0;
		double w = 0;
		int i = 0;
		int len = word.length();
		double[] letterScores = new double[word.length()];
		int[] letterWordCount = new int[word.length()];
		for (LetterCell cell : grid.cellSequence(word)) {
			if (pattern.letters[i] != LetterCell.EMPTY) {
				letterScores[i] = Double.NaN;
				letterWordCount[i] = -1;
				i++;
				continue;
			}
			WordOnGrid fullPattern = findLongestPattern(grid, cell);
			if (fullPattern == null) {
				// Single letter between two blacks
				letterScores[i] = Double.NaN;
				letterWordCount[i] = -2;
				Logger.debug("Skipping single-letter cell: %s", cell);
				i++;
				continue;
			}
			int cellOffsetInPattern = fullPattern.h
					? cell.icol - fullPattern.icol
					: cell.irow - fullPattern.irow;
			int nOptions = fitWords2(grid, fullPattern, cellOffsetInPattern);
			// This should work if nOptions = 0: score = +Inf
			double letterScore = 1 / Math.sqrt(nOptions);
			letterScores[i] = letterScore;
			letterWordCount[i] = nOptions;
			totalScore += letterScore;
			w += 1.;
			// Logger.debug(
			// "Scoring open letter %s nOptions=%d score=%.3f total=%.3f
			// w=%.1f",
			// cell, nOptions, letterScore, totalScore, w);
			// if (Double.isInfinite(totalScore))
			// break; // No need to continue
			i++;
		}
		// Undo
		grid.undoCells(oldCells);
		double lenScore = len - 1 < LEN_SCORE.length ? LEN_SCORE[len - 1]
				: LEN_SCORE[LEN_SCORE.length - 1];
		double totScoreInv;
		if (w == 0) {
			if (word.length() > 1) {
				totScoreInv = 1;
			} else {
				totScoreInv = 1;
			}
		} else {
			totScoreInv = w / totalScore;
		}
		Logger.debug(
				"Scored pattern %s word %s final score=%.3f len=%.3f (%d) pattern=%.3f",
				pattern.toString(), word.toString(), totScoreInv, lenScore, len,
				patternScore);
		return new ScoredWord(word, totScoreInv * lenScore * patternScore,
				totScoreInv, lenScore, patternScore, letterScores,
				letterWordCount);
	}

	private int fitWords2(LetterGrid grid, WordOnGrid fullPattern,
			int cellOffsetInPattern) {
		int retval = 0;
		List<WordOnGrid> patterns = Arrays.asList(fullPattern);
		int delta = 0;
		do {
			for (WordOnGrid subPattern : patterns) {
				int wordCount;
				if (subPattern.word().length() == 1) {
					wordCount = 1;
				} else {
					wordCount = dict.getWords(subPattern.word()).size();
				}
				retval += wordCount;
			}
			delta++;
			if (!params.fixedBlacks && delta >= fullPattern.length()
					|| params.fixedBlacks && delta > 0)
				return retval;
			patterns = fullPattern.subPatterns(delta, cellOffsetInPattern);
		} while (true); // Examine all sub-patterns
	}

	private double scorePattern(WordOnGrid pattern) {
		int nLetters = 0;
		for (char letter : pattern.letters) {
			if (letter != LetterCell.EMPTY) {
				nLetters++;
			}
		}
		// Favor patterns hard to find
		double patternScore = Math.exp(nLetters);
		// Prevent black letters cluster
		double blackScore = 1.0;
		// Prevent black on border
		double borderScore = 1.0;
		LetterCell blackBefore = grid.offsetFrom(pattern.icol, pattern.irow,
				pattern.h, -1);
		LetterCell blackAfter = grid.offsetFrom(pattern.icol, pattern.irow,
				pattern.h, pattern.length());
		if (blackBefore == null) {
			// Snap to border
			borderScore *= 2;
		} else {
			if (blackBefore.noBlack) {
				blackScore = 0;
			} else if (blackBefore.isBlack()) {
				blackScore *= 10;
			} else {
				int[] nn = grid.countNeighbors(blackBefore);
				int nBorder = nn[0];
				int nBlack = nn[1];
				borderScore /= (nBorder * nBorder * params.borderFactor + 1);
				blackScore /= (nBlack * nBlack * params.twinsFactor + 1);
			}
		}
		if (blackAfter == null) {
			// Snap to border
			borderScore *= 2;
		} else {
			if (blackAfter.noBlack) {
				blackScore = 0;
			} else if (blackAfter.isBlack()) {
				blackScore *= 10;
			} else {
				int[] nn = grid.countNeighbors(blackAfter);
				int nBorder = nn[0];
				int nBlack = nn[1];
				borderScore /= (nBorder * nBorder * params.borderFactor + 1);
				blackScore /= (nBlack * nBlack * params.twinsFactor + 1);
			}
		}
		double ret = blackScore * borderScore * patternScore;
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean patternIsSingleSequence(WordOnGrid pattern) {
		int nSequence = 0;
		int nLetters = 0;
		boolean inSequence = false;
		for (char letter : pattern.letters) {
			boolean isEmpty = letter == LetterCell.EMPTY;
			if (!isEmpty) {
				nLetters++;
			}
			if (!isEmpty && !inSequence) {
				inSequence = true;
				nSequence++;
				if (nSequence > 1) {
					return false;
				}
			} else if (isEmpty && inSequence) {
				inSequence = false;
				if (nLetters < 2) {
					return false;
				}
			}
		}
		return (nLetters >= 2);
	}
}