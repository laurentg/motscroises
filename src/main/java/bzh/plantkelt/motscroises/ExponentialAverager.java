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

import java.util.Random;

/**
 * This class is not used. It was meant to test the feasability of a O(1)
 * exponential averager when the data follow the pattern below:
 * 
 * A) The total number of samples is high (for example 100k or 1M)
 * 
 * B) The number of time per round a sample is modified is low (for example a
 * few dozen samples are modified per round only)
 * 
 * C) the exponential factor alpha (with Avg=Avg*(1-alpha)+Val*alpha) is very
 * low (for example alpha=0.01)
 * 
 * The idea is to accumulate by using a slowly increasing scale factor that we
 * reset from time to time. When adding to or reading a sample, we add / read
 * the value ajusted by the scaling factor. At each round the scaling factor is
 * multiplied by k=1/(1-alpha). When the scaling factor become larger than a
 * certain value (depending on the needed precision), we rescale the whole of
 * the data set by dividing every sample by the scaling factor, and we reset the
 * scaling factor to 1.
 * 
 * See the demo code for the details.
 */
public class ExponentialAverager {

	public static void main(String[] args) {

		int N_ITER = 50;
		int N_VEC = 5;
		double alpha = 0.1;
		double k = 1 / (1 - alpha);

		Random rand = new Random(42);
		int[] indexes = new int[N_ITER];
		double[] xs = new double[N_ITER];
		for (int i = 0; i < N_ITER; i++) {
			indexes[i] = rand.nextInt(N_VEC);
			xs[i] = rand.nextDouble() * 3;
		}

		double s1[] = new double[N_VEC];
		double s2[] = new double[N_VEC];

		double scale = 1;
		double scale_max = 100;
		for (int i = 0; i < N_ITER; i++) {
			scale = scale * k;
			boolean rescale = false;
			if (scale > scale_max) {
				rescale = true;
				for (int j = 0; j < N_VEC; j++) {
					s2[j] /= scale;
				}
				scale = 1;
			}
			int index = indexes[i];
			double x = xs[i];
			for (int j = 0; j < N_VEC; j++) {
				s1[j] *= (1 - alpha);
			}
			s1[index] += x * alpha;
			s2[index] += x * alpha * scale;
			for (int j = 0; j < N_VEC; j++) {
				System.out.print(String.format("%c %7.6f/%7.6f %c ",
						j == index ? '>' : ' ', s1[j], s2[j] / scale,
						Math.abs(s1[j] - s2[j] / scale) / s1[j] > 1e-12 ? '!'
								: ' '));
			}
			System.out.println(String.format(" scale: %10.6f %s", scale,
					rescale ? "(rescaled)" : ""));
		}
	}
}
