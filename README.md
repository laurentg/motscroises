# Mots Croisés

MotsCroisés is an open-source java program to generate automatically
word-cross grids.

This software is open-source and released under the GPLv3;
in particular, there is no warranty.

## Usage

Find/create a word dictionnary (text file, UTF8, one word per line), or use `/usr/share/dict/words`:

	$ motscroise --cols 14 -rows 14 --size 14 --dictionnary fr.txt --seed 42 --maxFill 15

Will result in:

	Grid found in 265 iterations.
	     A  B  C  D  E  F  G  H  I  J  K  L 
	   +====================================+
	 1 | ■  B  E  ■  C  A  D  R  E  R  ■  G |
	 2 | S  A  C  C  A  G  E  ■  T  A  T  E |
	 3 | ■  L  A  ■  R  A  S  ■  A  T  O  N |
	 4 | P  A  R  T  I  C  I  P  I  A  L  E |
	 5 | E  N  T  R  A  I  N  A  N  T  E  S |
	 6 | ■  C  E  ■  S  E  T  ■  S  I  R  E |
	 7 | ■  O  L  E  ■  Z  E  N  ■  N  E  S |
	 8 | P  I  E  ■  R  ■  R  A  S  E  ■  ■ |
	 9 | A  R  R  I  E  R  E  G  A  R  D  E |
	10 | N  E  A  N  T  I  S  E  R  A  I  T |
	11 | S  ■  I  ■  E  ■  S  U  ■  I  ■  E |
	12 | E  R  E  I  N  T  A  S  S  E  N  T |
	13 | N  ■  N  ■  T  A  I  E  ■  N  U  A |
	14 | T  U  T  I  E  ■  S  ■  O  T  A  S |
	   +====================================+
	27 black cells (16%)

Or create an `init.txt` file containing for example:

	.......#.......
	.#...#.#.#...#.
	....#..#..#....
	...#MAGICAL#...
	..#....#....#..
	.#...#...#...#.
	.......#.......
	###.#.###.#.###
	.......#.......
	.#...#...#...#.
	..#....#....#..
	...#GHOSTLY#...
	....#..#..#....
	.#...#.#.#...#.
	.......#.......

And run in fixed-black / initialized mode (with a verbose mode to see the process going):

	$ motscroises --verbose 2 -c 15 -r 15 -s 7 --seed 43 --maxFill 30 --fixed --init init.txt --dictionnary /usr/share/dict/words
	
To get:

	Grid found in 169 iterations.
	     A  B  C  D  E  F  G  H  I  J  K  L  M  N  O
	   +=============================================+
	 1 | B  U  T  T  O  N  S  ■  D  E  P  A  R  T  S |
	 2 | E  ■  I  O  N  ■  L  ■  E  ■  D  O  E  ■  O |
	 3 | F  A  R  E  ■  J  O  ■  S  H  ■  L  I  M  B |
	 4 | A  G  E  ■  M  A  G  I  C  A  L  ■  S  U  E |
	 5 | L  E  ■  S  E  R  A  ■  A  M  I  E  ■  M  R |
	 6 | L  ■  R  T  E  ■  N  O  N  ■  M  A  W  ■  L |
	 7 | S  H  A  Y  K  H  S  ■  T  H  E  R  E  B  Y |
	 8 | ■  ■  ■  M  ■  O  ■  ■  ■  A  ■  L  ■  ■  ■ |
	 9 | M  E  L  I  S  S  A  ■  I  N  C  I  T  E  S |
	10 | I  ■  T  E  N  ■  B  E  N  ■  L  E  A  ■  O |
	11 | S  C  ■  S  A  W  S  ■  S  E  A  R  ■  D  I |
	12 | L  O  G  ■  G  H  O  S  T  L  Y  ■  W  A  R |
	13 | A  D  E  N  ■  O  R  ■  A  M  ■  D  I  N  E |
	14 | Y  ■  L  O  G  ■  B  ■  L  ■  C  O  N  ■  E |
	15 | S  E  T  T  E  E  S  ■  L  E  S  S  O  N  S |
	   +=============================================+
	49 black cells (21%)

You can forbid some cells to be black, just use an underscore in the initialization file:

	$ cat init2.txt
	_____________
	_#_#_#_#_#_#_
	____._._.____
	_#_......._#_
	__.........__
	_#_......._#_
	__.........__
	_#_......._#_
	__.........__
	_#_......._#_
	____._._.____
	_#_#_#_#_#_#_
	_____________

	$ motscroises -c 13 -r 13 -s 13 --maxFill 25 --init init2.txt --dictionnary fr.txt

	Grid found in 1178 iterations (seed is 1476815916472)
	     A  B  C  D  E  F  G  H  I  J  K  L  M 
	   +=======================================+
	 1 | C  O  N  T  R  E  T  I  R  E  R  A  I |
	 2 | A  ■  O  ■  E  ■  E  ■  E  ■  E  ■  R |
	 3 | T  R  I  S  M  E  ■  L  I  M  I  E  R |
	 4 | H  ■  R  I  E  N  ■  A  N  O  N  ■  E |
	 5 | E  X  ■  ■  R  ■  D  ■  S  I  ■  A  S |
	 6 | T  ■  R  ■  C  H  U  T  E  ■  G  ■  I |
	 7 | E  C  U  R  I  E  S  ■  R  O  U  E  S |
	 8 | R  ■  T  H  E  ■  ■  B  E  L  E  ■  T |
	 9 | I  N  ■  O  R  ■  D  O  R  E  R  A  I |
	10 | S  ■  C  ■  I  N  ■  R  I  ■  I  ■  B |
	11 | M  A  R  L  O  U  ■  D  O  R  S  A  L |
	12 | E  ■  U  ■  N  ■  N  ■  N  ■  O  ■  E |
	13 | S  A  T  I  S  F  A  I  S  A  N  T  S |
	   +=======================================+
	38 black cells (22%)

Another grid, made in fixed mode, for the fun:

	Grid found in 5184 iterations (seed is 1476816809553)
	     A  B  C  D  E  F  G  H  I  J  K  L  M
	   +=======================================+
	 1 | D  E  C  A  R  B  O  N  A  T  A  N  T |
	 2 | E  ■  E  ■  A  ■  U  ■  G  ■  V  ■  R |
	 3 | P  A  R  T  I  E  ■  S  E  R  I  N  A |
	 4 | R  ■  C  R  A  N  E  U  S  E  S  ■  N |
	 5 | A  L  E  A  ■  T  U  E  ■  G  A  R  S |
	 6 | V  ■  S  I  E  E  ■  E  M  I  T  ■  H |
	 7 | E  U  ■  T  U  ■  ■  ■  U  S  ■  R  U |
	 8 | R  ■  P  I  E  U  ■  F  A  T  S  ■  M |
	 9 | A  F  R  O  ■  P  S  I  ■  R  A  T  A |
	10 | I  ■  I  N  T  A  I  L  L  A  T  ■  I |
	11 | E  U  S  S  E  S  ■  M  Y  I  A  S  E |
	12 | N  ■  E  ■  T  ■  L  ■  R  ■  N  ■  N |
	13 | T  U  R  L  U  P  I  N  E  R  E  N  T |
	   +=======================================+
	33 black cells (19%)

Of course you can make a large grid with short words (20x20 grid with word of 8 letters or less):

	$ motscroises -c 20 -r 20 -s 8 --maxFill 22 --dictionnary fr.txt

	Grid found in 174 iterations (seed is 1476823582847)
	     A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T 
	   +============================================================+
	 1 | ■  R  A  G  E  ■  T  U  B  ■  B  ■  I  ■  O  ■  P  A  R  E |
	 2 | T  A  I  E  ■  R  E  L  I  G  I  O  N  ■  P  I  A  N  O  S |
	 3 | ■  S  ■  L  E  ■  ■  C  E  ■  A  ■  G  R  E  E  R  E  N  T |
	 4 | ■  ■  C  A  S  T  R  E  R  ■  I  D  E  E  S  ■  E  ■  F  ■ |
	 5 | E  M  ■  ■  S  E  ■  R  E  S  S  E  R  T  ■  D  U  A  L  E |
	 6 | R  I  S  S  O  L  E  E  ■  ■  E  ■  A  R  T  E  S  I  E  N |
	 7 | G  R  E  E  R  ■  N  ■  C  ■  R  O  S  A  I  ■  E  N  ■  R |
	 8 | ■  E  ■  ■  E  ■  T  R  A  M  ■  P  ■  C  E  ■  S  E  R  A |
	 9 | O  R  ■  P  R  ■  O  ■  D  E  L  E  T  E  R  E  ■  S  I  C |
	10 | ■  A  ■  R  A  L  L  I  E  R  A  ■  R  E  C  U  ■  S  A  I |
	11 | T  I  S  E  ■  ■  E  N  T  ■  S  U  E  ■  A  ■  ■  E  ■  N |
	12 | ■  ■  A  T  T  ■  R  A  S  E  ■  ■  M  A  N  I  A  S  S  E |
	13 | D  O  B  E  R  M  A  N  ■  ■  ■  R  ■  P  T  ■  L  ■  K  ■ |
	14 | ■  N  O  U  I  E  ■  I  S  O  C  E  L  E  ■  S  E  U  I  L |
	15 | ■  ■  R  S  E  ■  A  M  E  ■  ■  P  E  U  ■  ■  R  ■  A  I |
	16 | S  I  D  E  R  A  L  E  ■  C  R  E  E  R  O  N  T  ■  I  N |
	17 | A  N  A  ■  E  C  O  E  U  R  A  T  ■  E  M  I  A  S  S  E |
	18 | L  U  T  E  Z  ■  S  ■  R  A  T  I  E  R  E  S  ■  O  ■  A |
	19 | S  I  ■  M  ■  R  E  C  E  N  S  E  S  ■  T  E  R  R  I  L |
	20 | E  T  H  E  R  E  S  ■  E  S  ■  Z  ■  P  E  R  E  S  ■  E |
	   +============================================================+
	92 black cells (23%)

For more information on available parameters:

	$ motscroises --help

Read the source code to learn how it works.

Enjoy.
