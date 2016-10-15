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
import java.util.List;

public class WordOnGrid {

	int icol, irow;
	boolean h;
	char[] letters;
	double constraint;

	public WordOnGrid(int icol, int irow, boolean h, char[] letters) {
		this.icol = icol;
		this.irow = irow;
		this.h = h;
		this.letters = letters;
	}

	public WordOnGrid fillWithWord(String word) {
		if (letters.length != word.length())
			throw new IllegalArgumentException("Word size mismatch: "
					+ letters.length + " != " + word.length());
		return new WordOnGrid(this.icol, this.irow, this.h, word.toCharArray());
	}

	public int length() {
		return this.letters.length;
	}

	public List<WordOnGrid> subPatterns(int delta, int offsetInPattern) {
		List<WordOnGrid> retval = new ArrayList<>(delta + 1);
		int len = letters.length;
		int subPatternLen = len - delta;
		int start = 0;
		if (start + subPatternLen < offsetInPattern)
			start = offsetInPattern - subPatternLen;
		int end = len - subPatternLen + 1;
		if (end > offsetInPattern + 1)
			end = offsetInPattern + 1;
		for (int offset = start; offset < end; offset++) {
			if (offset > 0) {
				if (letters[offset - 1] != LetterCell.EMPTY) {
					// Invalid sub-pattern: can't have a letter at left
					continue;
				}
			}
			if (offset - delta < 0) {
				if (letters[len - delta + offset] != LetterCell.EMPTY) {
					// Invalid sub-pattern: can't have a letter at right
					continue;
				}
			}
			boolean emptyPattern = true;
			for (int i = offset; i < offset + len - delta; i++) {
				if (letters[i] != LetterCell.EMPTY) {
					emptyPattern = false;
					break;
				}
			}
			if (emptyPattern) {
				// Empty pattern (only ___), skip it
				continue;
			}
			int icol2 = icol;
			int irow2 = irow;
			if (h)
				icol2 += offset;
			else
				irow2 += offset;
			WordOnGrid subPattern = new WordOnGrid(icol2, irow2, h,
					Arrays.copyOfRange(letters, offset, len - delta + offset));
			retval.add(subPattern);
		}
		return retval;
	}

	public String word() {
		return new String(letters);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(LetterCell.toCoordinateString(icol, irow));
		sb.append(h ? "⇨" : "⇩");
		sb.append("[").append(letters).append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int retval = icol;
		retval *= 97;
		retval += irow;
		retval *= 97;
		for (char letter : letters) {
			retval += (int) letter;
			retval *= 97;
		}
		return retval;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof WordOnGrid))
			return false;
		WordOnGrid anotherWog = (WordOnGrid) another;
		if (anotherWog.icol != icol || anotherWog.irow != irow
				|| anotherWog.letters.length != letters.length)
			return false;
		for (int i = 0; i < letters.length; i++)
			if (letters[i] != anotherWog.letters[i])
				return false;
		return true;
	}
}