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

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

public class CmdLineParams {

	@Parameter(names = { "-h",
			"--help" }, help = true, description = "Print help and exit.")
	public boolean help;

	@Parameter(names = {
			"--version" }, help = true, description = "Show program version and exit.")
	public boolean version;

	@Parameter(names = { "-v",
			"--verbose" }, description = "Level of verbosity")
	public int verbose = 1;

	@Parameter(names = { "-c",
			"--cols" }, description = "Grid number of columns", validateWith = GridSizeValidator.class)
	public int cols = 7;

	@Parameter(names = { "-r",
			"--rows" }, description = "Grid number of rows", validateWith = GridSizeValidator.class)
	public int rows = 7;

	@Parameter(names = { "-s",
			"--wordSize" }, description = "Max word size", validateWith = WordSizeValidator.class)
	public int maxWordSize = 7;

	@Parameter(names = { "-d",
			"--dictionnary" }, description = "Dictionnary file")
	public String dictionnary = "fr.txt";

	@Parameter(names = {
			"--seed" }, description = "Random seed. Default to 0 (use a random seed)")
	public long seed = 0L;

	@Parameter(names = {
			"--init" }, description = "Grid initialization file. Should contain <rows> lines of <cols> characters. Dot: empty cell - Underscore: empty cell, forbid black - Letter: fixed letter - #/@: black cell. Any line starting with ; is ignored.")
	public String init = null;

	@Parameter(names = {
			"--fixed" }, description = "Do not add new black cells")
	public boolean fixedBlacks = false;

	@Parameter(names = {
			"--borderFactor" }, description = "Factor to prevent black on borders.")
	public int borderFactor = 10;

	@Parameter(names = {
			"--twinsFactor" }, description = "Factor to prevent black twins.")
	public int twinsFactor = 5;

	@Parameter(names = {
			"--maxFill" }, description = "Cap on this percentage of black fill")
	public int maxBlackPerc = 20;

	@Parameter(names = { "--maxIterations" }, description = "Max iterations")
	public int maxIterations = 10000;

	public static class GridSizeValidator implements IParameterValidator {

		@Override
		public void validate(String name, String value)
				throws ParameterException {
			new PositiveInteger().validate(name, value);
			int size = Integer.parseInt(value);
			if (size < 3)
				throw new ParameterException("Grid size too small: " + size);
			if (size > 100)
				throw new ParameterException("Grid size too large: " + size);
		}
	}

	public static class WordSizeValidator implements IParameterValidator {

		@Override
		public void validate(String name, String value)
				throws ParameterException {
			new PositiveInteger().validate(name, value);
			int size = Integer.parseInt(value);
			if (size < 3)
				throw new ParameterException("Word size too small: " + size);
		}
	}
}
