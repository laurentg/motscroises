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

public class Logger {

	public static int ERROR = 0;
	public static int WARN = 1;
	public static int INFO = 2;
	public static int DEBUG = 3;

	public static int level = WARN;

	public static void errorNoLn(String fmt, Object... args) {
		if (level >= ERROR) {
			System.out.print(String.format(fmt, args));
		}
	}

	public static void error(String fmt, Object... args) {
		if (level >= ERROR) {
			System.out.println(String.format(fmt, args));
		}
	}

	public static void warn(String fmt, Object... args) {
		if (level >= WARN) {
			System.out.println(String.format(fmt, args));
		}
	}

	public static void info(String fmt, Object... args) {
		if (level >= INFO) {
			System.out.println(String.format(fmt, args));
		}
	}

	public static void debug(String fmt, Object... args) {
		if (level >= DEBUG) {
			System.out.println(String.format(fmt, args));
		}
	}
}
