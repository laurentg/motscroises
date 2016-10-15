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

public class ScoredWord implements Comparable<ScoredWord> {

	public WordOnGrid word;
	public double score;
	public double invScore;
	public double lenScore;
	public double patternScore;
	public double[] letterScores;
	public int[] letterWordCounts;

	public ScoredWord(WordOnGrid word, double score, double invScore,
			double lenScore, double patternScore, double[] letterScores,
			int[] letterWordCounts) {
		if (letterScores.length != word.length()
				|| letterWordCounts.length != word.length())
			throw new IllegalArgumentException("Invalid letter score length");
		this.word = word;
		this.score = score;
		this.invScore = invScore;
		this.lenScore = lenScore;
		this.patternScore = patternScore;
		this.letterScores = letterScores;
		this.letterWordCounts = letterWordCounts;
	}

	@Override
	public int compareTo(ScoredWord o) {
		// Compare: high score first
		return -Double.compare(score, o.score);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(String.format(
				"%-20s (%6.1f = %6.1f * %6.1f * %6.1f) ", word, score,
				invScore, lenScore, patternScore));
		for (int i = 0; i < word.length(); i++) {
			int n = letterWordCounts[i];
			sb.append(word.letters[i]).append(" (").append(n == -2 ? "X" : n == -1 ? "_" : n)
					.append(")  ");
		}
		return sb.toString();
	}
}
