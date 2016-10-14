package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author https://en.wikibooks.org/wiki/Algorithm_Implementation/Statistics/Cohen%27s_kappa.
 *         Licensed by CC BY-SA 3.0
 */
public class CohensKappa {

	public static double computeKappa(List<List<Integer>> scores) {
		if (scores.size() != 2) {
			throw new IllegalArgumentException("Only scores from two annotators are valid for Cohen's Kappa");
		}

		if (scores.get(0).size() != scores.get(1).size()) {
			throw new IllegalArgumentException("The scores from both annotators must have the same length");
		}

		Map<Integer, Map<Integer, Integer>> table = new HashMap<Integer, Map<Integer, Integer>>();

		int tableSize = 0;

		for (int i = 0; i < scores.get(0).size(); i++) {
			int first = scores.get(0).get(i);
			int second = scores.get(1).get(i);

			if (!table.containsKey(first)) {
				table.put(first, new HashMap<Integer, Integer>());
			}

			if (!table.get(first).containsKey(second)) {
				table.get(first).put(second, 0);
			}

			table.get(first).put(second, table.get(first).get(second) + 1);

			if (first > tableSize) {
				tableSize = first + 1;
			}

			if (second > tableSize) {
				tableSize = second + 1;
			}
		}

		boolean DEBUG = false;
		if (DEBUG) {
			System.out.println(table);
		}

		// sum the rows and cols
		Map<Integer, Integer> sumRows = new HashMap<Integer, Integer>();
		Map<Integer, Integer> sumCols = new HashMap<Integer, Integer>();

		for (Map.Entry<Integer, Map<Integer, Integer>> entry : table.entrySet()) {
			int rowNumber = entry.getKey();
			int sumRow = 0;

			for (Map.Entry<Integer, Integer> rowEntry : entry.getValue().entrySet()) {
				int colNumber = rowEntry.getKey();
				int value = rowEntry.getValue();

				sumRow += value;

				if (!sumCols.containsKey(colNumber)) {
					sumCols.put(colNumber, 0);
				}

				sumCols.put(colNumber, sumCols.get(colNumber) + value);
			}

			sumRows.put(rowNumber, sumRow);
		}

		// sum the total
		int sumTotal = 0;
		for (Integer rowSum : sumRows.values()) {
			sumTotal += rowSum;
		}

		if (DEBUG) {
			System.out.println("Row sums: " + sumRows);
			System.out.println("Col sums: " + sumCols);
			System.out.println("Total: " + sumTotal);
			System.out.println("table size: " + tableSize);
		}

		// sum diagonal
		int sumDiagonal = 0;
		for (int i = 1; i <= tableSize; i++) {
			int value = 0;
			if (table.containsKey(i) && table.get(i).containsKey(i)) {
				value = table.get(i).get(i);
			}

			sumDiagonal += value;
		}

		if (DEBUG) {
			System.out.println("Sum diagonal: " + sumDiagonal);
		}

		double p = (double) sumDiagonal / (double) sumTotal;

		if (DEBUG) {
			System.out.println("p: " + p);
		}

		double peSum = 0;
		for (int i = 1; i <= tableSize; i++) {
			if (sumRows.containsKey(i) && sumCols.containsKey(i)) {
				peSum += (double) sumRows.get(i) * (double) sumCols.get(i);
			}
		}

		if (DEBUG) {
			System.out.println("pesum: " + peSum);
		}

		double pe = peSum / (sumTotal * sumTotal);

		if (DEBUG) {
			System.out.println("pe: " + pe);
		}

		return (p - pe) / (1.0d - pe);
	}

	public static void main(String[] args) {
		exampleWikipediaOne();
		exampleWikipediaTwo();
		examplePaper1();
		examplePaper2();
	}

	public static void exampleWikipediaOne() {
		List<Integer> one = new ArrayList<Integer>();
		List<Integer> two = new ArrayList<Integer>();
		for (int i = 0; i < 45; i++) {
			one.add(1);
			two.add(1);
		}

		for (int i = 0; i < 15; i++) {
			one.add(1);
			two.add(2);
		}

		for (int i = 0; i < 25; i++) {
			one.add(2);
			two.add(1);
		}

		for (int i = 0; i < 15; i++) {
			one.add(2);
			two.add(2);
		}

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add(one);
		list.add(two);

		System.out.println(computeKappa(list));
	}

	public static void exampleWikipediaTwo() {
		List<Integer> one = new ArrayList<Integer>();
		List<Integer> two = new ArrayList<Integer>();
		for (int i = 0; i < 25; i++) {
			one.add(1);
			two.add(1);
		}

		for (int i = 0; i < 35; i++) {
			one.add(1);
			two.add(2);
		}

		for (int i = 0; i < 5; i++) {
			one.add(2);
			two.add(1);
		}

		for (int i = 0; i < 35; i++) {
			one.add(2);
			two.add(2);
		}

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add(one);
		list.add(two);

		System.out.println(computeKappa(list));
	}

	/*
	 * Example from
	 * http://www-users.york.ac.uk/~mb55/msc/clinimet/week4/kappash2.pdf Table
	 * 4, pg. 3
	 */
	public static void examplePaper1() {
		List<Integer> one = new ArrayList<Integer>();
		List<Integer> two = new ArrayList<Integer>();

		for (int i = 0; i < 12; i++) {
			one.add(1);
			two.add(1);
		}

		for (int i = 0; i < 4; i++) {
			one.add(1);
			two.add(2);
		}

		for (int i = 0; i < 2; i++) {
			one.add(1);
			two.add(3);
		}

		for (int i = 0; i < 12; i++) {
			one.add(2);
			two.add(1);
		}

		for (int i = 0; i < 56; i++) {
			one.add(2);
			two.add(2);
		}

		for (int i = 0; i < 3; i++) {
			one.add(3);
			two.add(1);
		}

		for (int i = 0; i < 4; i++) {
			one.add(3);
			two.add(2);
		}

		for (int i = 0; i < 1; i++) {
			one.add(3);
			two.add(3);
		}

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add(one);
		list.add(two);

		System.out.println(computeKappa(list));
	}

	/*
	 * Example from
	 * http://www-users.york.ac.uk/~mb55/msc/clinimet/week4/kappash2.pdf Table
	 * 6, pg. 4
	 */
	public static void examplePaper2() {
		List<Integer> one = new ArrayList<Integer>();
		List<Integer> two = new ArrayList<Integer>();

		for (int i = 0; i < 2; i++) {
			one.add(1);
			two.add(1);
		}

		for (int i = 0; i < 12; i++) {
			one.add(1);
			two.add(2);
		}

		for (int i = 0; i < 8; i++) {
			one.add(1);
			two.add(3);
		}

		for (int i = 0; i < 9; i++) {
			one.add(2);
			two.add(1);
		}

		for (int i = 0; i < 35; i++) {
			one.add(2);
			two.add(2);
		}

		for (int i = 0; i < 43; i++) {
			one.add(2);
			two.add(3);
		}

		for (int i = 0; i < 7; i++) {
			one.add(2);
			two.add(4);
		}

		for (int i = 0; i < 4; i++) {
			one.add(3);
			two.add(1);
		}

		for (int i = 0; i < 36; i++) {
			one.add(3);
			two.add(2);
		}

		for (int i = 0; i < 103; i++) {
			one.add(3);
			two.add(3);
		}

		for (int i = 0; i < 40; i++) {
			one.add(3);
			two.add(4);
		}

		for (int i = 0; i < 1; i++) {
			one.add(4);
			two.add(1);
		}

		for (int i = 0; i < 8; i++) {
			one.add(4);
			two.add(2);
		}

		for (int i = 0; i < 36; i++) {
			one.add(4);
			two.add(3);
		}

		for (int i = 0; i < 22; i++) {
			one.add(4);
			two.add(4);
		}

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add(one);
		list.add(two);

		System.out.println(computeKappa(list));
	}
}
