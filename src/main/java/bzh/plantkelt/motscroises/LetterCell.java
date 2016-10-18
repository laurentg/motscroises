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

public class LetterCell implements Cloneable {

	public static final char EMPTY = '_';
	public static final char BLACK = '■';

	char letter = EMPTY;
	int icol, irow;
	WordOnGrid h = null;
	WordOnGrid v = null;
	boolean fixed = false;
	boolean noBlack = false;

	public LetterCell(int icol, int irow) {
		this.icol = icol;
		this.irow = irow;
	}

	public LetterCell clone() {
		LetterCell retval = new LetterCell(icol, irow);
		retval.letter = letter;
		retval.h = h;
		retval.v = v;
		retval.fixed = fixed;
		retval.noBlack = noBlack;
		return retval;
	}

	public String toCellString(boolean pretty) {
		if (letter == LetterCell.EMPTY) {
			return " . ";
		} else if (letter == LetterCell.BLACK) {
			return " ■ ";
		} else {
			char border = '*';
			if (pretty)
				border = ' ';
			else if (v != null && h != null)
				border = ' ';
			else if (v != null)
				border = '|';
			else if (h != null)
				border = '-';
			return "" + border + letter + border;
		}
	}

	public boolean isBlack() {
		return letter == BLACK;
	}

	public boolean isEmpty() {
		return letter == EMPTY;
	}

	public boolean isLetter() {
		return letter != BLACK && letter != EMPTY;
	}

	public void clear() {
		letter = EMPTY;
		h = null;
		v = null;
	}

	public static String toCoordinateString(int icol, int irow) {
		return String.format("(%c,%d)", (char) ('A' + icol), irow + 1);
	}

	@Override
	public int hashCode() {
		return irow * 997 + icol;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LetterCell))
			return false;
		// Only take care of the position
		LetterCell cell2 = (LetterCell) obj;
		return cell2.icol == icol && cell2.irow == irow;
	}

	@Override
	public String toString() {
		return String.format("%s%s", toCellString(false),
				toCoordinateString(icol, irow));
	}
}