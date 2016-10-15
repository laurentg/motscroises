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

public class AlphabetFreq {

	public long freq[] = new long[26];
	public long sum = 0;

	public AlphabetFreq() {
	}

	public void inc(char letter) {
		freq[letter - 'A']++;
		sum++;
	}

	public long freq(char letter) {
		return freq[letter - 'A'];
	}
}