# Mots Croisés

MotsCroisés is an open-source java program to generate automatically
word-cross grids.

This software is open-source and released under the GPLv3;
in particular, there is no warranty.

## Usage

Find a word dictionnary (text file, UTF8, one word per line).

	$ motscroise --cols 14 -rows 14 --size 14 --dict fr.txt --seed 42 --maxFill 15

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

Or create an init.txt file containing:

	.■..■.....■..■.
	.......■.......
	■■■■■.....■■■■■
	.......■.......
	...■...■...■...
	...■■■■■■■■■...
	.......■.......
	■■■■.......■■■■
	.......■.......
	...■■■■■■■■■...
	.......■.......
	..■■...■...■■..
	.......■.......
	■■■■■.....■■■■■
	.......■.......
	...■■.....■■...

And run in fixed-black / initialized mode:

	$ motscroises --verbose 2 -c 15 -r 15 -s 8 --seed 42 --maxFill 30 --fixed --init init.txt

To get:

	Grid found in 301 iterations.
	     A  B  C  D  E  F  G  H  I  J  K  L  M  N  O
	   +=============================================+
	 1 | E  ■  A  I  ■  M  I  R  O  S  ■  L  E  ■  A |
	 2 | S  U  I  N  T  E  R  ■  S  A  V  A  N  T  S |
	 3 | ■  ■  ■  ■  ■  L  I  R  A  I  ■  ■  ■  ■  ■ |
	 4 | P  E  T  R  E  E  S  ■  I  G  N  O  R  A  S |
	 5 | E  R  E  ■  T  E  E  ■  S  A  I  ■  E  M  U |
	 6 | R  E  E  ■  ■  ■  ■  ■  ■  ■  ■  ■  P  I  E |
	 7 | E  S  S  O  R  A  I  ■  E  B  O  S  S  E  R |
	 8 | ■  ■  ■  ■  A  I  D  E  R  A  S  ■  ■  ■  ■ |
	 9 | P  R  O  F  I  L  E  ■  G  R  E  F  F  E  E |
	10 | R  I  T  ■  ■  ■  ■  ■  ■  ■  ■  ■  E  T  C |
	11 | A  V  A  L  I  S  E  ■  E  L  A  G  U  E  R |
	12 | M  E  ■  ■  D  U  T  ■  S  U  R  ■  ■  T  U |
	13 | E  R  A  I  E  R  A  ■  S  T  E  P  P  E  S |
	14 | ■  ■  ■  ■  ■  F  I  L  E  T  ■  ■  ■  ■  ■ |
	15 | A  M  P  U  T  A  S  ■  S  A  P  I  O  N  S |
	   +=============================================+
	65 black cells (28%)

Read the source code to learn how it works.

Enjoy.
