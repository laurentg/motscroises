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

Of course you can make a large grid with short words (30x30 grid with word of 10 letters or less):

	$ motscroises -c 30 -r 30 -s 10 --maxFill 22 --dictionnary fr.txt

	Grid found in 5573 iterations (seed is 1476823947160)
	     A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z  [  \  ]  ^
	   +==========================================================================================+
	 1 | B  U  V  O  T  A  ■  M  ■  G  ■  A  ■  S  A  P  A  J  O  U  S  ■  M  I  L  S  ■  O  H  M |
	 2 | ■  N  ■  R  I  M  A  I  L  L  O  N  S  ■  X  ■  S  A  U  N  A  T  E  S  ■  A  N  S  E  E |
	 3 | T  A  C  ■  S  I  ■  R  O  U  P  E  T  T  E  S  ■  U  T  I  L  E  ■  B  ■  L  ■  E  U  S |
	 4 | R  U  C  H  E  R  ■  E  U  T  ■  R  U  ■  S  E  I  N  ■  ■  I  ■  L  A  I  E  S  ■  ■  ■ |
	 5 | I  ■  ■  ■  ■  A  M  E  R  E  ■  I  C  ■  ■  C  ■  I  R  A  S  ■  O  S  ■  R  E  L  I  S |
	 6 | A  C  C  A  B  L  E  ■  A  N  S  E  ■  P  O  U  R  R  A  I  ■  C  F  ■  C  A  L  I  N  A |
	 7 | ■  A  H  ■  A  ■  V  E  T  ■  A  S  T  U  ■  L  A  ■  M  E  G  O  T  E  E  S  ■  E  U  S |
	 8 | E  P  O  I  N  T  E  R  ■  D  L  ■  ■  L  I  A  S  S  E  S  ■  N  ■  M  S  ■  A  R  T  S |
	 9 | M  O  U  ■  ■  E  N  G  A  M  A  M  E  S  ■  R  H  E  ■  ■  E  C  H  U  S  ■  R  A  I  A |
	10 | U  T  ■  ■  E  S  T  ■  R  ■  S  A  M  E  D  I  ■  T  A  C  H  E  ■  S  E  R  A  I  L  S |
	11 | L  E  U  R  S  ■  E  U  R  O  ■  ■  ■  E  ■  S  E  S  ■  A  ■  D  E  ■  S  E  S  ■  I  ■ |
	12 | E  S  T  ■  S  E  ■  N  I  ■  E  T  E  ■  M  E  N  ■  A  P  L  A  T  I  ■  S  ■  O  T  E |
	13 | N  ■  ■  D  E  P  L  I  E  N  T  ■  ■  R  E  ■  T  I  S  S  E  ■  U  N  I  S  ■  P  E  U |
	14 | T  A  I  E  ■  E  ■  ■  R  E  E  N  G  A  G  E  R  ■  ■  ■  S  K  I  F  ■  E  T  E  ■  S |
	15 | ■  C  O  N  T  E  N  U  E  S  ■  E  ■  P  I  L  E  ■  C  L  E  ■  ■  E  ■  M  O  N  O  S |
	16 | R  E  N  I  A  S  ■  ■  R  ■  H  O  U  P  ■  I  ■  C  R  I  ■  S  U  R  H  A  U  S  S  E |
	17 | ■  R  ■  ■  L  ■  O  ■  ■  ■  A  ■  T  R  E  M  U  L  E  R  A  I  ■  T  ■  ■  R  ■  E  S |
	18 | G  E  R  E  E  ■  S  T  A  D  I  A  ■  E  U  E  ■  ■  V  E  R  ■  S  I  C  ■  O  S  ■  ■ |
	19 | ■  R  A  S  E  E  S  ■  ■  E  ■  V  ■  T  E  R  R  E  E  ■  E  T  A  L  O  N  N  A  I  T |
	20 | T  A  I  T  ■  M  U  S  A  R  D  A  M  E  S  ■  U  S  E  Z  ■  H  I  E  R  ■  ■  L  ■  ■ |
	21 | R  ■  N  ■  L  I  ■  ■  M  A  ■  L  I  ■  ■  R  A  S  ■  E  M  E  T  ■  N  U  M  I  D  E |
	22 | A  M  A  R  I  N  A  M  E  S  ■  I  M  P  I  E  ■  A  U  N  E  S  ■  P  E  N  A  T  E  S |
	23 | M  A  ■  ■  E  C  U  ■  N  A  S  S  E  ■  F  I  L  I  N  ■  T  E  N  U  E  ■  I  ■  C  C |
	24 | A  L  T  O  ■  A  ■  ■  D  ■  C  E  ■  C  ■  N  ■  M  E  U  R  S  ■  N  ■  S  T  R  I  A |
	25 | ■  M  U  ■  S  I  G  N  E  ■  A  R  M  A  ■  V  ■  A  ■  N  A  ■  L  A  S  E  R  ■  ■  L |
	26 | F  E  R  R  E  E  ■  ■  R  E  L  A  I  S  S  E  E  S  ■  E  S  ■  I  ■  O  ■  I  O  D  E |
	27 | I  N  C  A  R  N  E  R  A  ■  P  I  S  T  O  N  S  ■  A  S  ■  V  E  R  N  I  S  S  E  S |
	28 | A  I  ■  S  I  T  U  A  S  S  E  ■  ■  R  U  T  ■  O  H  ■  R  O  S  I  ■  D  E  ■  G  ■ |
	29 | S  E  M  E  E  ■  E  S  ■  A  R  R  I  E  R  E  ■  S  A  L  U  E  ■  E  ■  E  R  R  A  T |
	30 | ■  Z  ■  R  E  S  S  A  S  S  A  I  ■  R  I  S  B  A  N  ■  T  U  A  S  ■  S  A  U  T  A |
	   +==========================================================================================+
	207 black cells (23%)

For more information on available parameters:

	$ motscroises --help

Read the source code to learn how it works.

Enjoy.
