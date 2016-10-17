package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates a Plackett & Burman experimental design.
 * 
 * @author Tobias Angerstein
 *
 */
public class PlackettBurmanGenerator {
	/**
	 * Initial column.
	 */
	private static final int[] EIGHT_RUNS = new int[] { 1, 1, 1, 0, 1, 0, 0 };

	/**
	 * Returns the Placket-Burman matrix.
	 * 
	 * @param numberOfAttributes
	 *            the number of attributes
	 * @return the matrix.
	 */
	public static ArrayList<int[]> getMatrix(int numberOfAttributes) {
		ArrayList<int[]> matrix = new ArrayList<int[]>();
		matrix.add(Arrays.copyOf(EIGHT_RUNS, numberOfAttributes));
		int[] previousRow = Arrays.copyOf(EIGHT_RUNS, EIGHT_RUNS.length);
		int[] row = new int[numberOfAttributes];
		for (int i = 0; i < getNumberOfRuns(numberOfAttributes) - 1; i++) {
			row[0] = previousRow[previousRow.length - 1];
			for (int j = numberOfAttributes - 1; j >= 1; j--) {
				row[j] = previousRow[j - 1];
			}
			previousRow = Arrays.copyOf(row, numberOfAttributes);
			matrix.add(previousRow);
		}
		return matrix;
	}

	/**
	 * Returns the number of runs.
	 * 
	 * @param numberOfAttributes
	 *            the number of attributes which have to be diagnosed
	 * @return the needed number of runs
	 */
	public static int getNumberOfRuns(int numberOfAttributes) {
		return ((Double) (Math.ceil((numberOfAttributes + 1) / 4.0))).intValue() * 4;
	}

	/**
	 * Main class for testing issues.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {
		System.out.println(getNumberOfRuns(7));

		ArrayList<int[]> matrix = getMatrix(6);
		for (int[] row : matrix) {
			String rowString = "";
			for (int i = 0; i < row.length; i++) {
				rowString += row[i] + "; ";
			}
			System.out.println(rowString);

		}
	}

}
