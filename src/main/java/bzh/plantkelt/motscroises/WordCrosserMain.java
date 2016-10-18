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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.beust.jcommander.JCommander;

public class WordCrosserMain {

	private static final String COPYRIGHT = "MotsCroisés - Copyright (c) 2016 by Laurent GRÉGOIRE <laurent.gregoire@protonmail.com>";
	private static final String VERSION = "0.1";

	public static void main(String[] args) throws Exception {

		// Parse command-line arguments
		CmdLineParams params = new CmdLineParams();
		JCommander jc = new JCommander(params, args);
		jc.setProgramName("java [-Xmx1G] -jar motscroises.jar");
		if (params.version || params.help) {
			System.out.println(COPYRIGHT);
			System.out.println("Version " + VERSION);
			if (params.help) {
				jc.usage();
			}
			System.exit(0);
		}

		// Set logger level based on verbosity
		switch (params.verbose) {
		default:
		case 1:
			Logger.level = Logger.ERROR;
			break;
		case 2:
			Logger.level = Logger.WARN;
			break;
		case 3:
			Logger.level = Logger.INFO;
			break;
		case 4:
			Logger.level = Logger.DEBUG;
			break;
		}

		LetterGrid grid = buildGridFromParams(params);

		Dictionnary dict = new Dictionnary(params.dictionnary,
				params.maxWordSize);
		WordCrosser wc = new WordCrosser(dict, grid, params);
		wc.solve(params.seed);
	}

	private static LetterGrid buildGridFromParams(CmdLineParams params)
			throws IOException {
		LetterGrid grid = new LetterGrid(params.cols, params.rows);

		if (params.init != null) {
			// Read a pre-filled grid from a text file
			try (Stream<String> stream = Files.lines(Paths.get(params.init))) {
				List<String> lines = stream.map(String::trim)
						.filter(line -> !line.startsWith(";"))
						.map(String::toUpperCase).collect(Collectors.toList());
				Logger.warn("Initializing grid with:");
				Logger.warn("----------------------------");
				for (String line : lines) {
					Logger.warn("[%s]", line);
				}
				Logger.warn("----------------------------");

				for (int irow = 0; irow < params.rows
						&& irow < lines.size(); irow++) {
					String line = lines.get(irow);
					for (int icol = 0; icol < params.cols
							&& icol < line.length(); icol++) {
						char c = line.charAt(icol);
						if (c == '.' || c == ' ') {
							// empty cell
						} else if (c == '_') {
							grid.cells[icol][irow].noBlack = true;
						} else if (c >= 'A' && c <= 'Z') {
							grid.cells[icol][irow].letter = c;
							grid.cells[icol][irow].fixed = true;
						} else {
							grid.cells[icol][irow].letter = LetterCell.BLACK;
							grid.cells[icol][irow].fixed = true;
						}
					}
				}
			}
		}
		return grid;
	}
}