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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LetterGrid implements Cloneable {

	LetterCell[][] cells;
	int nCols, nRows;

	public LetterGrid(int nCols, int nRows) {
		this(nCols, nRows, true);
	}

	private LetterGrid(int nCols, int nRows, boolean initCells) {
		this.nCols = nCols;
		this.nRows = nRows;
		cells = new LetterCell[nCols][nRows];
		if (initCells) {
			for (int icol = 0; icol < nCols; icol++) {
				for (int irow = 0; irow < nRows; irow++) {
					cells[icol][irow] = new LetterCell(icol, irow);
				}
			}
		}
	}

	public Collection<LetterCell> openLetters() {
		return filterCells(false, false, true, false);
	}

	/*
	 * TODO Make an iterable to make sure we return latest values for the cells
	 * (otherwise the caller need to ensure the cells grid are not modified
	 * during the loop, as any modification will not be seen).
	 */
	public Collection<LetterCell> filterCells(boolean includeEmpty,
			boolean includeBlack, boolean includeOpenLetter,
			boolean includeClosedLetter) {
		List<LetterCell> ret = new ArrayList<>(nCols * nRows);
		for (int icol = 0; icol < cells.length; icol++) {
			LetterCell[] col = cells[icol];
			for (int irow = 0; irow < col.length; irow++) {
				LetterCell cell = col[irow];
				boolean empty = cell.letter == LetterCell.EMPTY;
				if (empty && !includeEmpty)
					continue;
				boolean black = cell.letter == LetterCell.BLACK;
				if (black && !includeBlack)
					continue;
				if (!empty && !black) {
					// Letter
					if (cell.h != null && cell.v != null
							&& !includeClosedLetter)
						continue;
					if (!(cell.h != null && cell.v != null)
							&& !includeOpenLetter)
						continue;
				}
				ret.add(cell);
			}
		}
		return ret;
	}

	public List<LetterCell> cellSequence(WordOnGrid word) {
		return cellSequence(word.icol, word.irow, word.h, true, word.length());
	}

	public List<LetterCell> cellSequence(int icol, int irow, boolean h,
			boolean fwd) {
		return cellSequence(icol, irow, h, fwd, -1);
	}

	public List<LetterCell> cellSequence(int icol, int irow, boolean h,
			boolean fwd, int len) {
		List<LetterCell> ret = new ArrayList<>(
				len > 0 ? len : Math.max(nCols, nRows));
		int dcol = h ? fwd ? 1 : -1 : 0;
		int drow = h ? 0 : fwd ? 1 : -1;

		int i = 0;
		while (true) {
			LetterCell cell = cells[icol][irow];
			if (cell.letter == LetterCell.BLACK)
				break;
			ret.add(cell);
			icol += dcol;
			irow += drow;
			if (icol < 0 || icol >= nCols || irow < 0 || irow >= nRows)
				break;
			i++;
			if (len >= 0 && i >= len)
				break;
		}
		return ret;
	}

	public LetterCell nextBeforeBlack(int icol, int irow, boolean h,
			boolean fwd) {
		int dcol = h ? fwd ? 1 : -1 : 0;
		int drow = h ? 0 : fwd ? 1 : -1;
		LetterCell curr = cells[icol][irow];
		while (true) {
			icol += dcol;
			irow += drow;
			if (icol < 0 || icol >= nCols || irow < 0 || irow >= nRows) {
				// Out of grid
				return curr;
			}
			LetterCell next = cells[icol][irow];
			if (next.letter == LetterCell.BLACK) {
				// Black cell
				return curr;
			}
			// OK, move to next
			curr = next;
		}
	}

	public LetterCell nextLetter(int icol, int irow, boolean h, boolean fwd,
			int maxDelta) {
		int dcol = h ? fwd ? 1 : -1 : 0;
		int drow = h ? 0 : fwd ? 1 : -1;
		int delta = 0;
		while (true) {
			icol += dcol;
			irow += drow;
			delta++;
			if (delta > maxDelta)
				return null;
			if (icol < 0 || icol >= nCols || irow < 0 || irow >= nRows) {
				// Out of grid
				return null;
			}
			LetterCell cell = cells[icol][irow];
			if (cell.letter == LetterCell.BLACK) {
				// Black cell
				return null;
			}
			if (cell.letter != LetterCell.EMPTY) {
				// Found
				return cell;
			}
		}
	}

	public LetterCell offsetFrom(int icol, int irow, boolean h, int offset) {
		int dcol = h ? 1 : 0;
		int drow = h ? 0 : 1;
		int icol2 = icol + dcol * offset;
		int irow2 = irow + drow * offset;
		if (icol2 >= 0 && icol2 < nCols && irow2 >= 0 && irow2 < nRows)
			return cells[icol2][irow2];
		return null;
	}

	public WordOnGrid extractPattern(int icol, int irow, boolean h) {
		LetterCell start = nextBeforeBlack(icol, irow, h, false);
		StringBuffer sb = new StringBuffer();
		for (LetterCell cell : cellSequence(start.icol, start.irow, h, true)) {
			sb.append(cell.letter);
		}
		int len = sb.length();
		WordOnGrid pattern = len == 1 ? null
				: new WordOnGrid(start.icol, start.irow, h,
						sb.toString().toCharArray());
		return pattern;
	}

	/**
	 * @param word
	 *            The word to write on the grid.
	 * @return The list of written cell before writing.
	 */
	public Collection<LetterCell> writeWord(WordOnGrid word) {
		List<LetterCell> ret = new ArrayList<>(word.length() + 2);
		char[] letters = word.letters;
		int i = 0;
		for (LetterCell cell : cellSequence(word.icol, word.irow, word.h, true,
				word.length())) {
			ret.add(cell);
			LetterCell newCell = cell.clone();
			cells[newCell.icol][newCell.irow] = newCell;
			newCell.letter = letters[i];
			if (word.length() > 1) {
				// Only set word for real words, not 1-letter
				if (word.h)
					newCell.h = word;
				else
					newCell.v = word;
				i++;
			}
		}
		if (word.h) {
			int icol2 = word.icol - 1;
			if (icol2 >= 0) {
				// Black before
				LetterCell cell = cells[icol2][word.irow];
				if (cell.letter != LetterCell.EMPTY
						&& cell.letter != LetterCell.BLACK)
					throw new IllegalArgumentException(
							"Cell must be black or empty before word " + word);
				ret.add(cell);
				LetterCell newCell = cell.clone();
				cells[newCell.icol][newCell.irow] = newCell;
				newCell.letter = LetterCell.BLACK;
			}
			icol2 = word.icol + word.length();
			if (icol2 < nCols) {
				// Black after
				LetterCell cell = cells[icol2][word.irow];
				if (cell.letter != LetterCell.EMPTY
						&& cell.letter != LetterCell.BLACK)
					throw new IllegalArgumentException(
							"Cell must be black or empty after word " + word);
				ret.add(cell);
				LetterCell newCell = cell.clone();
				cells[newCell.icol][newCell.irow] = newCell;
				newCell.letter = LetterCell.BLACK;
			}
		} else {
			int irow2 = word.irow - 1;
			if (irow2 >= 0) {
				// Black before
				LetterCell cell = cells[word.icol][irow2];
				if (cell.letter != LetterCell.EMPTY
						&& cell.letter != LetterCell.BLACK)
					throw new IllegalArgumentException(
							"Cell must be black or empty before word " + word);
				ret.add(cell);
				LetterCell newCell = cell.clone();
				cells[newCell.icol][newCell.irow] = newCell;
				newCell.letter = LetterCell.BLACK;
			}
			irow2 = word.irow + word.length();
			if (irow2 < nRows) {
				// Black after
				LetterCell cell = cells[word.icol][irow2];
				if (cell.letter != LetterCell.EMPTY
						&& cell.letter != LetterCell.BLACK)
					throw new IllegalArgumentException(
							"Cell must be black or empty before word " + word);
				ret.add(cell);
				LetterCell newCell = cell.clone();
				cells[newCell.icol][newCell.irow] = newCell;
				newCell.letter = LetterCell.BLACK;
			}
		}
		return ret;
	}

	public void undoCells(Iterable<LetterCell> oldCells) {
		for (LetterCell oldCell : oldCells) {
			cells[oldCell.icol][oldCell.irow] = oldCell;
		}
	}

	public WordOnGrid findMaxConstrainedOpenWord() {
		double max = -Double.MAX_VALUE;
		WordOnGrid retval = null;
		for (int open = 0; open < 2; open++) {
			for (int icol = 0; icol < nCols; icol++) {
				for (int irow = 0; irow < nRows; irow++) {
					LetterCell cell = cells[icol][irow];
					if (open == 0) {
						if (cell.isBlack() || cell.isEmpty()
								|| cell.h != null && cell.v != null)
							continue;
					} else {
						if (cell.isBlack() || cell.isEmpty())
							continue;
					}
					double constraint = cell.h == null ? -1 : cell.h.constraint;
					if (constraint > max) {
						max = constraint;
						retval = cell.h;
					}
					constraint = cell.v == null ? -1 : cell.v.constraint;
					if (constraint > max) {
						max = constraint;
						retval = cell.v;
					}
				}
			}
			if (retval != null)
				return retval;
		}
		return retval;
	}

	public LetterCell findRandomEmptyCell(Random rand) {
		while (true) {
			int icol = rand.nextInt(nCols);
			int irow = rand.nextInt(nRows);
			LetterCell cell = cells[icol][irow];
			if (cell.isEmpty())
				return cell;
			// TODO We may loop forever here
		}
	}

	public LetterCell findMaxConstraintedBlackCell() {
		double max = -Double.MAX_VALUE;
		LetterCell retval = null;
		for (int icol = 0; icol < nCols; icol++) {
			for (int irow = 0; irow < nRows; irow++) {
				LetterCell cell = cells[icol][irow];
				if (!cell.isBlack())
					continue;
				if (cell.fixed)
					continue;
				int blackNeighbors = blackNeighbors(cell);
				double score1 = blackNeighbors * blackNeighbors;
				double score2 = 0;
				LetterCell up = offsetFrom(cell.icol, cell.irow, false, -1);
				if (up != null && up.v != null) {
					score2 += up.v.constraint;
				}
				LetterCell down = offsetFrom(cell.icol, cell.irow, false, 1);
				if (down != null && down.v != null) {
					score2 += down.v.constraint;
				}
				LetterCell left = offsetFrom(cell.icol, cell.irow, true, -1);
				if (left != null && left.h != null) {
					score2 += left.h.constraint;
				}
				LetterCell right = offsetFrom(cell.icol, cell.irow, true, 1);
				if (right != null && right.h != null) {
					score2 += right.h.constraint;
				}
				double score = score1 * score2;
				if (score > max) {
					max = score;
					retval = cell;
				}
			}
		}
		return retval;
	}

	public void removeBlack(LetterCell blackCell) {
		LetterCell up = offsetFrom(blackCell.icol, blackCell.irow, false, -1);
		if (up != null && up.v != null) {
			removeWord(up.v, true);
		}
		LetterCell down = offsetFrom(blackCell.icol, blackCell.irow, false, 1);
		if (down != null && down.v != null) {
			removeWord(down.v, true);
		}
		LetterCell left = offsetFrom(blackCell.icol, blackCell.irow, true, -1);
		if (left != null && left.h != null) {
			removeWord(left.h, true);
		}
		LetterCell right = offsetFrom(blackCell.icol, blackCell.irow, true, 1);
		if (right != null && right.h != null) {
			removeWord(right.h, true);
		}
		// Remove black, some times we do not have it removed by removeWord.
		blackCell.clear();
	}

	public void relaxConstraint() {
		Set<WordOnGrid> processedWords = new HashSet<>(nCols * nRows);
		for (int icol = 0; icol < nCols; icol++) {
			for (int irow = 0; irow < nRows; irow++) {
				LetterCell cell = cells[icol][irow];
				if (cell.h != null && processedWords.add(cell.h))
					cell.h.constraint /= 2;
				if (cell.v != null && processedWords.add(cell.v))
					cell.v.constraint /= 2;
			}
		}
	}

	public void constrain(WordOnGrid pattern, double constraint) {
		char[] letters = pattern.letters;
		int i = 0;
		for (LetterCell cell : cellSequence(pattern.icol, pattern.irow,
				pattern.h, true, pattern.length())) {
			if (letters[i] != LetterCell.EMPTY) {
				if (cell.h != null) {
					int len = cell.h.length();
					cell.h.constraint += constraint / (len * len * len);
				}
				if (cell.v != null) {
					int len = cell.v.length();
					cell.v.constraint += constraint / (len * len * len);
				}
			}
			i++;
		}
	}

	public void removeWord(WordOnGrid word, boolean removeBlack) {
		// Find optional black at the begining of word
		LetterCell blackStart = offsetFrom(word.icol, word.irow, word.h, -1);
		// Find optional black at the end of word
		LetterCell blackEnd = offsetFrom(word.icol, word.irow, word.h,
				word.length());
		for (LetterCell letter : cellSequence(word.icol, word.irow, word.h,
				true, word.length())) {
			if (word.h) {
				letter.h = null;
			} else {
				letter.v = null;
			}
			if ((word.h && letter.v != null) || (!word.h && letter.h != null))
				continue;
			if (letter.fixed)
				continue;
			letter.letter = LetterCell.EMPTY;
		}
		if (removeBlack) {
			if (blackStart != null)
				removeBlackIfPossible(blackStart);
			if (blackEnd != null)
				removeBlackIfPossible(blackEnd);
		}
	}

	public boolean removeBlackIfPossible(LetterCell blackCell) {
		if (blackCell.fixed)
			return false;
		LetterCell up = offsetFrom(blackCell.icol, blackCell.irow, false, -1);
		if (up != null && up.v != null && up.v.length() > 1)
			return false;
		LetterCell down = offsetFrom(blackCell.icol, blackCell.irow, false, 1);
		if (down != null && down.v != null && down.v.length() > 1)
			return false;
		LetterCell left = offsetFrom(blackCell.icol, blackCell.irow, true, -1);
		if (left != null && left.h != null && left.h.length() > 1)
			return false;
		LetterCell right = offsetFrom(blackCell.icol, blackCell.irow, true, 1);
		if (right != null && right.h != null && right.h.length() > 1)
			return false;
		Logger.warn("Removing black at %s", blackCell);
		blackCell.clear();
		// Clear potential 1 letter words
		if (up != null && up.v != null) {
			Logger.warn("Removing up 1 letter word %s", up.v);
			up.v = null;
		}
		if (down != null && down.v != null) {
			Logger.warn("Removing down 1 letter word %s", down.v);
			down.v = null;
		}
		if (left != null && left.h != null) {
			Logger.warn("Removing left 1 letter word %s", left.h);
			left.h = null;
		}
		if (right != null && right.h != null) {
			Logger.warn("Removing right 1 letter word %s", right.h);
			right.h = null;
		}
		return true;
	}

	public int cellCount() {
		return nCols * nRows;
	}

	public GridStats getStats() {
		GridStats stats = new GridStats();
		for (int icol = 0; icol < nCols; icol++) {
			for (int irow = 0; irow < nRows; irow++) {
				LetterCell cell = cells[icol][irow];
				if (cell.isBlack()) {
					stats.nBlacks++;
					if (!cell.fixed) {
						stats.nFreeBlacks++;
					}
				} else if (cell.isEmpty()) {
					stats.nEmpty++;
				} else {
					stats.nLetters++;
				}
			}
		}
		return stats;
	}

	public int blackNeighbors(LetterCell cell) {
		int n = 0;
		for (int dcol = -1; dcol <= 1; dcol++) {
			int icol2 = cell.icol + dcol;
			if (icol2 < 0 || icol2 >= nCols) {
				n += 3; // 3 borders
				continue;
			}
			for (int drow = -1; drow <= 1; drow++) {
				int irow2 = cell.irow + drow;
				if (irow2 < 0 || irow2 >= nRows) {
					n++; // 1 border
					continue;
				}
				LetterCell cell2 = cells[icol2][irow2];
				if (cell2.isBlack())
					n++; // 1 black
			}
		}
		return n;
	}

	@Override
	public String toString() {
		return toString(true, false);
	}

	public String toString(boolean includeConstraint, boolean pretty) {
		StringBuffer sb = new StringBuffer();

		sb.append("    ");
		for (int icol = 0; icol < nCols; icol++) {
			sb.append(' ').append((char) ('A' + icol)).append(' ');
		}
		if (includeConstraint) {
			sb.append("    ");
			for (int icol = 0; icol < nCols; icol++) {
				sb.append("     ").append((char) ('A' + icol)).append("   ");
			}
		}
		sb.append("\n");

		sb.append("   +");
		for (int icol = 0; icol < nCols; icol++) {
			sb.append("===");
		}
		sb.append("+");
		if (includeConstraint) {
			sb.append("   +");
			for (int icol = 0; icol < nCols; icol++) {
				sb.append("=========");
			}
			sb.append("+");
		}
		sb.append("\n");

		for (int irow = 0; irow < nRows; irow++) {
			sb.append(String.format("%2d |", irow + 1));
			for (int i = 0; i < nCols; i++) {
				LetterCell cell = cells[i][irow];
				sb.append(cell.toCellString(pretty));
			}
			sb.append("|");
			if (includeConstraint) {
				sb.append(String.format("%2d |", irow + 1));
				for (int i = 0; i < nCols; i++) {
					LetterCell cell = cells[i][irow];
					sb.append(String.format("%8.2f ",
							(cell.h == null ? 0 : cell.h.constraint)
									+ (cell.v == null ? 0
											: cell.v.constraint)));
				}
				sb.append("|");
			}
			sb.append("\n");
		}

		sb.append("   +");
		for (int icol = 0; icol < nCols; icol++) {
			sb.append("===");
		}
		sb.append("+");
		if (includeConstraint) {
			sb.append("   +");
			for (int icol = 0; icol < nCols; icol++) {
				sb.append("=========");
			}
			sb.append("+");
		}
		sb.append("\n");

		return sb.toString();
	}
}