package logic;

import java.util.Random;

public class RandomUtilities {
	
	private static Random randomGenerator = new Random(System.currentTimeMillis());

	/**
	 * Provides a random number between 0 (inclusive) and a given bound (exclusive).
	 * @param bound The upper bound.
	 * @return The random number.
	 */
	public static int getRandomInteger(int bound) {
		return (int) (bound * randomGenerator.nextDouble());
	}
	
}
