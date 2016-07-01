package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.Arrays;

public class PlackettBurmanGenerator {
	private static final int[] EIGHT_RUNS = new int[] { 1, 1, 1, 0, 1, 0, 0 };

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

	public static int getNumberOfRuns(int numberOfAttributes) {
		return ((Double) (Math.ceil((numberOfAttributes + 1) / 4.0))).intValue() * 4;
	}

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
