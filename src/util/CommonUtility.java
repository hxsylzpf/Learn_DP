package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class CommonUtility {

	public static void main(String[] args) throws Exception {

		// TEST testGetByProb();

		// double[][] realAns = new double[][] { { 0, 0, 0, 0 }, { 2, 1, 1, 3 }
		// };
		// double[][] noisyAns = new double[][] { { -0.1, 0.2, 0.2, 1 }, { 0.1,
		// -0.1, 0, 0.1 } };
		// System.out.println(computeKL2(noisyAns, realAns));
		// System.out.println(convertArrToTable("test",
		// normal_ratio_featureScaling_KL2(realAns)));
		// System.out.println(convertArrToTable("test",normal_ratio_KL2(normal_zero_KL2(noisyAns,
		// realAns))));

		// System.out.println(findTopKRatio_practical(realAns, noisyAns, 2));
		// System.out.println(findTopKRatio_practical(noisyAns, realAns, 2));

		// System.out.println(CommonUtility.convertArrToTable("test",
		// CommonUtility.transposeMatrix(realAns)));

		// TEST maxElemInArr
		// double[] ds = new double[] { 0, 0, 0, 0, -1 };
		// System.err.println(maxElemInArr(ds));

		// TEST getNormalizedProb_helper

	}
	
	public static double keepNdecimalDouble(double doubleVal, int decimalBit) {
		if (decimalBit <= 0) {
			return doubleVal;
		}

		if (!Double.isInfinite(doubleVal) && !Double.isNaN(doubleVal)) {

			BigDecimal b = new BigDecimal(doubleVal);
			doubleVal = b.setScale(decimalBit, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return doubleVal;

	}

	public static double[] calRatioOfDoubleArr(double[] row1, double[] row2) {
		double[] ratios = new double[row1.length];
		for (int i = 0; i < ratios.length; i++) {
			ratios[i] = row1[i] / row2[i];
		}
		return ratios;
	}

	public static long printInterval(long startTime) {
		long inv = 0;
		long endTime = System.currentTimeMillis();
		inv = endTime - startTime;
		System.out.println(inv);
		return endTime;
	}

	// ======random by prob=======
	// test method
	public static void testGetByProb() {
		double budget = 1;
		double[] score = new double[] { -97, -120, -150, -150, -100 };
		// double[] score = new double[]{3,-20,-50,-50,0};

		double[] data = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			data[i] = Math.exp(score[i]);
		}

		// data=CommonUtility.catDoubleArr(data, new double[] { 1,2,4,4});
		// data=CommonUtility.catDoubleArr(data, new double[] { 1,2,4,4});
		// data=CommonUtility.catDoubleArr(data, new double[] { 1,2,4,4});
		// data=CommonUtility.catDoubleArr(data, new double[] { 1,2,4,4});
		// data=CommonUtility.catDoubleArr(data, new double[] { 1,2,4,4});

		System.out.println("data.length=" + data.length);

		double[] normalizedProb = getNormalizedProb_helper(data);
		double[] proportionalProb = getprobProportional_helper(normalizedProb);

		int correctNum = 0;
		int testTimes = 1000000;
		for (int i = 0; i < testTimes; i++) {
			int rID = getRandomIdxByProb_helper(proportionalProb);
			// System.out.print(rID+",");
			if (rID == 0) {
				correctNum++;
			}
		}

		System.out.println("\ncorrect num=" + correctNum);
		System.out.println("correct rate=" + (double) correctNum / testTimes);
	}

	// main method
	public static int getRandomIdxByProbArr(double[] probWeight) {
		// 2 3 -> 0.4 0.6 (percentage)
		double[] normalizedProb = getNormalizedProb_helper(probWeight);
		// 0.4 0.6 -> 0.4 1.0 (proportion)
		double[] proportionalProb = getprobProportional_helper(normalizedProb);

		return getRandomIdxByProb_helper(proportionalProb);
	}

	public static int getRandomIdxByProb_helper(double[] probProportion) {
		int ranIdx = Integer.MIN_VALUE;

		Random r = new Random();
		double d = r.nextDouble();
		// System.out.println("random d="+d);
		for (int i = 0; i < probProportion.length; i++) {

			if (d < probProportion[i]) {
				ranIdx = i;
				// test
				// System.out.println("ranIdx="+i);
				break;
			}
		}

		return ranIdx;
	}

	public static double[] getNormalizedProb_helper(double[] probabilityList) {

		double sum = 0.0;
		for (int i = 0; i < probabilityList.length; i++) {
			sum = sum + probabilityList[i];
		}

		double[] normalizedProb = new double[probabilityList.length];
		for (int i = 0; i < normalizedProb.length; i++) {
			normalizedProb[i] = probabilityList[i] / sum;
		}

		// TEST
		// System.out.println(Arrays.toString(normalizedProb));

		return normalizedProb;

	}

	public static double[] getprobProportional_helper(double[] normalizedProb) {

		double[] proportionalProb = new double[normalizedProb.length];
		for (int i = 0; i < proportionalProb.length; i++) {
			if (i == 0) {
				proportionalProb[i] = normalizedProb[i];

			} else {
				proportionalProb[i] = proportionalProb[i - 1] + normalizedProb[i];
			}

			// test
			if (normalizedProb[i] == 0) {
				System.out.println("??");
			}

		}

		// TEST
		// System.out.println(Arrays.toString(proportionalProb));

		return proportionalProb;

	}

	// ======random by prob ~ end=======

	public static int getAppxiMMDIdx(double budget, double[][] noisyPrefixStream, int[] realAnsOnTS, int thisTimeIdx) {
		double b1 = budget / 2;
		double b2 = budget / 2;
		double[] adjData = noisyPrefixStream[thisTimeIdx - 1];

		int realMMDIdx = getMMDIdx(noisyPrefixStream, realAnsOnTS, thisTimeIdx);
		System.out.println("realMMDIdx=" + realMMDIdx);
		// input
		double MAE_realData_lstData = computeMAE(adjData, realAnsOnTS);
		double MAE_realData_tgtData = computeMAE(adjData, noisyPrefixStream[realMMDIdx]);
		double miniMAE = Math.abs(MAE_realData_lstData - MAE_realData_tgtData);
		double maxMAE = MAE_realData_lstData + MAE_realData_tgtData;
		double midMAEOfRange = (maxMAE - miniMAE) / 2;

		HashMap<Integer, Double> reIdxHM = new HashMap<Integer, Double>();
		double dis_to_midMAE = Double.MAX_VALUE;
		int minDisIdx = Integer.MIN_VALUE;

		for (int ithTS = 0; ithTS < thisTimeIdx - 1; ithTS++) {
			// compute MD btw last data ans each data on TS
			double mae = computeMAE(noisyPrefixStream[ithTS], adjData);

			if (mae >= miniMAE && mae <= maxMAE) {
				reIdxHM.put(ithTS, mae);
				if (dis_to_midMAE > Math.abs(mae - midMAEOfRange)) {
					dis_to_midMAE = Math.abs(mae - midMAEOfRange);

					minDisIdx = ithTS;

				}

			}

		}

		// maybe MMD is here-1
		if (MAE_realData_lstData == MAE_realData_tgtData) {
			minDisIdx = thisTimeIdx - 1;
			System.out.println("adj is best");
			reIdxHM.remove(minDisIdx);
		}

		// maybe the ans is not correct...return the nearst
		// one(getHashMapMinValueIndex)
		while (computeMAE(noisyPrefixStream[minDisIdx], realAnsOnTS) > MAE_realData_tgtData
				&& computeMAE(noisyPrefixStream[minDisIdx], adjData) > MAE_realData_lstData) {
			System.out.println("refind...");
			minDisIdx = getHashMapMinValueIndex(reIdxHM);
			reIdxHM.remove(minDisIdx);
		}

		System.out.println("minDisIdx:" + minDisIdx);
		System.out.println("reIdxHM.keySet():" + reIdxHM.keySet());
		System.out.println("reIdxHM.values():" + reIdxHM.values());

		return minDisIdx;

	}

	public static int getMMDIdx(double[][] NoisyPrefixStream, int[] realAnsOnTS, int thisTimeIdx) {
		double minMAE = Double.MAX_VALUE;
		int idx = Integer.MIN_VALUE;
		for (int i = 0; i < thisTimeIdx; i++) {
			double ithMAE = computeMAE(NoisyPrefixStream[i], realAnsOnTS);
			if (minMAE > ithMAE) {
				minMAE = ithMAE;
				idx = i;
			}
		}

		return idx;
	}

	public static int getHashMapMinValue(HashMap<Integer, Integer> data) {
		int min = Integer.MAX_VALUE;

		for (Integer vals : data.values()) {
			if (vals < min) {
				min = vals;
			}
		}

		return min;

	}

	public static int getHashMapMinValueIndex(HashMap<Integer, Double> data) {
		double minValue = Double.MAX_VALUE;
		int minKey = 0;

		for (Map.Entry<Integer, Double> en : data.entrySet()) {
			if (en.getValue() < minValue) {
				minKey = en.getKey();
			}
		}

		return minKey;

	}

	public static int getMMDIdx(int[] data, int nextData) {

		int minMD = Integer.MAX_VALUE;
		int minMDIdx = Integer.MIN_VALUE;
		for (int i = 0; i < data.length; i++) {
			int ithMD = Math.abs(data[i] - nextData);
			if (minMD > ithMD) {
				minMD = ithMD;
				minMDIdx = i;
			}
		}
		return minMDIdx;
	}

	public static int getMMDValue(int[] data, int nextData) {

		int minMD = Integer.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			int ithMD = Math.abs(data[i] - nextData);
			if (minMD > ithMD) {
				minMD = ithMD;
			}
		}
		return minMD;
	}

	

	

	

	// =============get xx array to string arr ~ satrt OVERLOAD
	public static String[] getStringArrOfArray(int[] locLengthArr) {
		String[] labelOfLength = new String[locLengthArr.length];
		for (int i = 0; i < locLengthArr.length; i++) {
			labelOfLength[i] = String.valueOf(locLengthArr[i]);
		}
		return labelOfLength;
	}

	public static String[] getStringArrOfArray(double[] locLengthArr) {
		String[] labelOfLength = new String[locLengthArr.length];
		for (int i = 0; i < locLengthArr.length; i++) {
			labelOfLength[i] = String.valueOf(locLengthArr[i]);
		}
		return labelOfLength;
	}

	// =============get xx array to string arr ~ END

	

	public static double doubleRoundDown(double d, int bitAfterZero) {

		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(bitAfterZero, BigDecimal.ROUND_HALF_DOWN);
		return bd.doubleValue();
	}

	public static double doubleRoundUp(double d, int bitAfterZero) {

		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(bitAfterZero, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static double[] catDoubleArr(double[] objectArr, double[] inputArr) {
		if (null == objectArr) {
			return inputArr;
		}
		int size1 = objectArr.length;
		int size2 = inputArr.length;
		double[] r = new double[size1 + size2];

		for (int i = 0; i < size1; i++) {
			r[i] = objectArr[i];
		}

		for (int i = 0; i < size2; i++) {
			r[i + size1] = inputArr[i];
		}

		return r;
	}

	public static double[] intArr2doubleArr(int[] intArr) {
		int size = intArr.length;
		double[] r = new double[size];

		for (int i = 0; i < size; i++) {
			r[i] = intArr[i];
		}
		return r;
	}

	public static int[] doubleArr2intArr(double[] doubleArr) {
		int size = doubleArr.length;
		int[] r = new int[size];

		for (int i = 0; i < size; i++) {
			r[i] = (int) doubleArr[i];
		}
		return r;
	}

	public static double[][] intArr2doubleArr(int[][] intArr) {
		int size = intArr.length;
		int size2 = intArr[0].length;
		double[][] r = new double[size][size2];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size2; j++) {
				r[i][j] = intArr[i][j];
			}

		}
		return r;
	}

	public static int[] ArrInteger2intArr(ArrayList<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static double[] ArrDouble2doubleArr(ArrayList<Double> doubles) {
		double[] ret = new double[doubles.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = doubles.get(i).doubleValue();
		}
		return ret;
	}

	public static int[][] cutArrArrInt(int[][] realAns, int previousKTS) throws Exception {

		int amountDim = realAns[0].length;
		int amountTS = realAns.length;
		if (previousKTS > realAns.length) {
			throw new Exception("k=" + previousKTS + " is more > than source length=" + amountTS);
		}
		int[][] newArrArr = new int[previousKTS][amountDim];

		for (int i = 0; i < previousKTS; i++) {
			newArrArr[i] = realAns[i];
		}
		return newArrArr;
	}

	public static int[][] parseTwoArrArr(int[][] realAns1, int[][] realAns2) throws Exception {
		int amountTS = realAns1.length + realAns2.length;
		int amountDim1 = realAns1[0].length;
		int amountDim2 = realAns2[0].length;

		if (amountDim1 != amountDim2) {
			throw new Exception("amountDim1 != amountDim2 " + amountDim1 + "!=" + amountDim2);
		}

		int[][] newArrArr = new int[amountTS][amountDim1];

		for (int i = 0; i < realAns1.length; i++) {
			newArrArr[i] = realAns1[i];
		}
		for (int i = 0; i < realAns2.length; i++) {
			newArrArr[i] = realAns2[i];
		}

		return newArrArr;

	}

	public static double[][] transposeMatrix(double[][] m) {
		double[][] temp = new double[m[0].length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				temp[j][i] = m[i][j];
		return temp;
	}

	// ===============utility metrics ================

	/*
	 * compute Kullback–Leibler divergence (relative entropy) only realAns non-0
	 * will be measured (realAns 0 implies NoisyAns 0) => noiseAns[ts][dim]
	 */

	public static double computeKL_tran(double[][] noisyAns, int[][] realAns) throws IOException {
		double[][] realDouble = intArr2doubleArr(realAns);

		return computeKL_tran(noisyAns, realDouble);
	}

	public static double computeKL_tran(double[][] noisyAns, double[][] realAnsDouble) throws IOException {
		noisyAns = CommonUtility.transposeMatrix(noisyAns);
		realAnsDouble = CommonUtility.transposeMatrix(realAnsDouble);

		return computeKL(noisyAns, realAnsDouble);
	}

	public static double computeKL(double[][] noisyAns, int[][] realAns) throws IOException {
		double[][] realDouble = intArr2doubleArr(realAns);
		return computeKL(noisyAns, realDouble);
	}

	public static double computeKL(double[][] noisyAns, double[][] realAnsDouble) throws IOException {

		double sumKL = 0.0;
		int amountTS = noisyAns.length;
		int num = 0;
		double[] kl = new double[amountTS];

		// normalize
		// only realAns non-0 will be measured (realAns 0 implies NoisyAns 0)
		double[][] realAns_normal = normalizeForKL_d(realAnsDouble);
		double[][] noisyAns_normal = normalizeForKL_d(noisyAns);

		// TEST
		// System.out.println(CommonUtility.convertArrToTable("noisyAns_normal",
		// noisyAns_normal));
		// System.out.println(CommonUtility.convertArrToTable("realAns_normal",
		// realAns_normal));

		for (int i = 0; i < realAnsDouble.length; i++) {
			num = noisyAns[0].length;

			for (int j = 0; j < realAnsDouble[0].length; j++) {
				if (realAns_normal[i][j] != 0) {
					if (noisyAns_normal[i][j] != 0) {
						kl[i] = kl[i] + realAns_normal[i][j] * (Math.log(realAns_normal[i][j] / noisyAns_normal[i][j]));
					} else {
						// if realAns_normal[i][j] != 0 && noisyAns_normal[i][j]
						// == 0, ignore
						num--;
					}

				} else {
					num--;
				}

			}

			if (num == 0) {
				kl[i] = 0;
			} else {
				kl[i] = kl[i] / num;
			}

			sumKL = sumKL + kl[i];

			// test
			// System.out.print("kl[i]="+kl[i]);
			// System.out.println(" num="+num);

		}

		return sumKL / amountTS;
	}

	// normalization for KL: first negative -> 0, then value/sum
	public static double[][] normalizeForKL_d(double[][] targetData) {
		double[][] normalized = new double[targetData.length][targetData[0].length];

		// negative -> 0
		double[] sumOnTS = new double[targetData.length];
		for (int ithTS = 0; ithTS < sumOnTS.length; ithTS++) {

			// find min on dim
			double minVal = Double.MAX_VALUE;
			for (int i = 0; i < targetData[0].length; i++) {
				if (minVal > targetData[ithTS][i]) {
					minVal = targetData[ithTS][i];
				}
			}

			// make it Positive on TS
			for (int i = 0; i < targetData[0].length; i++) {

				// scheme 1: only on the bit of realAns non-0
				// if (targetData[ithTS][i] == 0) {
				// normalized[ithTS][i] = 0;
				// continue;
				// }else {
				// normalized[ithTS][i] = normalized[ithTS][i]+minVal;
				// }

				// scheme 2: shift the curve (up)
				if (minVal < 0) {
					normalized[ithTS][i] = targetData[ithTS][i] - minVal;
				} else {
					normalized[ithTS][i] = targetData[ithTS][i];
				}

				if (targetData[ithTS][i] < 0) {
					normalized[ithTS][i] = 0;
				} else {
					normalized[ithTS][i] = targetData[ithTS][i];
				}

				sumOnTS[ithTS] = sumOnTS[ithTS] + normalized[ithTS][i];
			}

		}

		// value/sum
		for (int ithTS = 0; ithTS < sumOnTS.length; ithTS++) {
			if (sumOnTS[ithTS] == 0) {
				// System.err.println("sumOnTS[ithTS]==0");
				// System.out.println("please enter any key to continue ... ");
				// System.in.read();
			} else {
				for (int i = 0; i < targetData[0].length; i++) {
					normalized[ithTS][i] = normalized[ithTS][i] / sumOnTS[ithTS];
				}
			}

		}

		return normalized;
	}

	// =======COMPUTE KL version 2

	public static double computeKL2_tran(double[][] noisyAns, int[][] realAns) throws IOException {
		double[][] realDouble = intArr2doubleArr(realAns);

		return computeKL2_tran(noisyAns, realDouble);
	}

	public static double computeKL2_tran(double[][] noisyAns, double[][] realAnsDouble) throws IOException {
		noisyAns = CommonUtility.transposeMatrix(noisyAns);
		realAnsDouble = CommonUtility.transposeMatrix(realAnsDouble);

		return computeKL2(noisyAns, realAnsDouble);
	}

	public static double computeKL2(double[][] noisyAns, double[][] realAnsDouble) {
		// temporary variables
		double sumKL = 0.0;
		int amountTS = noisyAns.length;

		double[][] realAns_normalized = new double[noisyAns.length][noisyAns[0].length];
		double[][] noisyAns_normalized = new double[realAnsDouble.length][realAnsDouble[0].length];

		// compute each kl[i]: 1:normalization 2:kl equation

		noisyAns_normalized = normal_ratio_featureScaling_KL2(noisyAns);
		realAns_normalized = normal_ratio_featureScaling_KL2(realAnsDouble);

		noisyAns_normalized = normal_zero_KL2(noisyAns_normalized, realAns_normalized);

		realAns_normalized = normal_ratio_KL2(realAns_normalized);
		noisyAns_normalized = normal_ratio_KL2(noisyAns_normalized);

		// average KL
		for (int i = 0; i < amountTS; i++) {
			sumKL = sumKL + computeKL2_perTS(realAns_normalized[i], noisyAns_normalized[i]);
		}

		return sumKL / amountTS;
	}

	private static double computeKL2_perTS(double[] norRealPerTS, double[] norNoisePerTS) {
		double kl = 0;

		for (int i = 0; i < norRealPerTS.length; i++) {
			if (norRealPerTS[i] != 0 && norNoisePerTS[i] != 0) {
				double val = norRealPerTS[i] * Math.log(norRealPerTS[i] / norNoisePerTS[i]);
				kl = kl + val;
			}
		}

		return kl;
	}

	private static double[][] normal_zero_KL2(double[][] inputNoise) {
		double[][] noiseAnsNormal = new double[inputNoise.length][inputNoise[0].length];

		for (int i = 0; i < noiseAnsNormal.length; i++) {

		}

		return noiseAnsNormal;
	}

	private static double[][] normal_zero_KL2(double[][] input, double[][] tragetZeroData) {
		double[][] normalizedData = new double[input.length][input[0].length];

		for (int i = 0; i < tragetZeroData.length; i++) {
			for (int j = 0; j < tragetZeroData[0].length; j++) {
				// ★ <0 for noiseData
				if (tragetZeroData[i][j] == 0) {
					normalizedData[i][j] = 0;
				} else {
					normalizedData[i][j] = input[i][j];
				}

			}
		}

		return normalizedData;
	}

	private static double[][] normal_ratio_KL2(double[][] input) {
		double[][] normalizedData = new double[input.length][input[0].length];

		// sum per ts
		int amountTS = input.length;
		double[] sumArr = new double[amountTS];

		// compute sum per TS
		for (int i = 0; i < amountTS; i++) {
			for (int j = 0; j < input[0].length; j++) {
				sumArr[i] = sumArr[i] + input[i][j];
			}
		}

		// each value divides sum
		for (int i = 0; i < amountTS; i++) {
			for (int j = 0; j < input[0].length; j++) {
				if (sumArr[i] != 0) {
					normalizedData[i][j] = input[i][j] / sumArr[i];
				}
			}
		}
		return normalizedData;
	}

	private static double[][] normal_ratio_featureScaling_KL2(double[][] input) {
		double[][] normalizedData = new double[input.length][input[0].length];

		for (int ts = 0; ts < normalizedData.length; ts++) {

			// find max min each TS
			double max = Double.MIN_VALUE;
			double min = Double.MAX_VALUE;

			for (int i = 0; i < input[0].length; i++) {
				if (input[ts][i] > max) {
					max = input[ts][i];
				}

				if (input[ts][i] < min) {
					min = input[ts][i];
				}
			}

			// compute scaled val
			double diff = max - min;
			for (int i = 0; i < input[0].length; i++) {
				normalizedData[ts][i] = (input[ts][i] - min) / diff;
			}

		}

		return normalizedData;
	}

	// ==========compute MAE between 2 matrixes
	public static double computeMAE(double[][] noisedAns, int[][] realAns) {
		double[][] realAnsDouble = intArr2doubleArr(realAns);

		return computeMAE(noisedAns, realAnsDouble);
	}

	public static double computeMAE(double[][] noisedAns, double[][] realAns) {
		double r = 0.0;
		int dimension = realAns[0].length;
		// int dimension = amountDim;

		for (int i = 0; i < noisedAns.length; i++) {
			for (int j = 0; j < dimension; j++) {
				r = r + Math.abs(noisedAns[i][j] - realAns[i][j]);
			}
		}
		return r / (noisedAns.length * dimension);
	}

	public static double computeMAE(double[] noisedAns, int[] realAns) {
		double[] realAnsDouble = intArr2doubleArr(realAns);

		return computeMAE(noisedAns, realAnsDouble);

	}

	public static double computeMAE(double[] noisedAns, double[] realAns) {
		double r = 0;
		int dimension = realAns.length;
		// int dimension = amountDim;

		for (int j = 0; j < dimension; j++) {
			r = r + Math.abs(noisedAns[j] - realAns[j]);
		}

		return r / dimension;
	}

	public static double computeMAE_self(int[][] realAns) {
		double r = 0.0;
		int dimension = realAns[0].length;
		// int dimension = amountDim;

		for (int i = 0; i < realAns.length; i++) {
			for (int j = 0; j < dimension; j++) {
				r = r + Math.abs(realAns[i][j]);
			}
		}
		return r / (realAns.length * dimension);
	}

	public static double computeMAE_self(double[][] realAns) {
		double r = 0.0;
		int dimension = realAns[0].length;
		// int dimension = amountDim;

		for (int i = 0; i < realAns.length; i++) {
			for (int j = 0; j < dimension; j++) {
				r = r + Math.abs(realAns[i][j]);
			}
		}
		return r / (realAns.length * dimension);
	}

	public static double computeMAE_self(double[] realAns) {
		double r = 0.0;
		int dimension = realAns.length;
		// int dimension = amountDim;

		for (int j = 0; j < dimension; j++) {
			r = r + Math.abs(realAns[j]);

		}
		return r / (realAns.length);
	}

	public static double computeAE_self(double[] realAns) {
		double r = 0.0;
		int dimension = realAns.length;
		// int dimension = amountDim;

		for (int j = 0; j < dimension; j++) {
			r = r + (realAns[j]);

		}
		return r / (realAns.length);
	}

	public static double maxElemInArr(double[] doubleArr) {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < doubleArr.length; i++) {
			max = Math.max(max, doubleArr[i]);
		}
		return max;
	}

	public static double minElemInArr(double[] doubleArr) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < doubleArr.length; i++) {
			min = Math.min(min, doubleArr[i]);
		}
		return min;
	}

	public static int countOfDiffElemInArr(double[] doubleArr, double[] doubleArr2, boolean verbose) {
		int count = 0;
		for (int i = 0; i < doubleArr.length; i++) {
			if (doubleArr[i] != doubleArr2[i]) {
				count = count + 1;
				if (verbose) {
					System.out.println("i: " + i + " " + +doubleArr[i] + "!=" + doubleArr2[i] + " ");
				}
			}
		}
		return count;
	}

	public static double rateOfNonZeroElemInArr(double[] doubleArr) {
		double count = 0;
		for (int i = 0; i < doubleArr.length; i++) {
			if (doubleArr[i] != 0) {
				count = count + 1;
			}
		}
		return count / doubleArr.length;
	}

	public static double rateOfNegativeElemInArr(double[] doubleArr) {
		double count = 0;
		for (int i = 0; i < doubleArr.length; i++) {
			if (doubleArr[i] < 0) {
				count = count + 1;
			}
		}
		return count / doubleArr.length;
	}

	public static double rateOfPositiveElemInArr(double[] doubleArr) {
		double count = 0;
		for (int i = 0; i < doubleArr.length; i++) {
			if (doubleArr[i] > 0) {
				count = count + 1;
			}
		}
		return count / doubleArr.length;
	}

	// ========= MAE end ==========

	// compute Average value in a ArrayList NOT USED
	public static double[] computeAverageInArrList(ArrayList<double[]> noisedAns_m) {
		int size = noisedAns_m.size();
		int valSize = noisedAns_m.get(0).length;
		double[] r = new double[valSize];

		for (int i = 0; i < size; i++) {
			double[] temp = noisedAns_m.get(i);
			for (int k = 0; k < valSize; k++) {
				r[k] = r[k] + temp[k];
			}
		}

		for (int k = 0; k < valSize; k++) {
			r[k] = r[k] / size;
		}

		return r;
	}

	// compute Average value in a ArrayList NOT USED
	public static double[] computeSumDoubleArr(double[] d1, double[] d2) throws Exception {

		if (null == d1 && null != d2) {
			return d2;
		}

		double[] result = new double[d1.length];

		if (d1 == null || d2 == null || d1.length != d1.length) {
			throw new Exception("d1 == null || d1.length != d1.length");
		} else {
			for (int i = 0; i < d1.length; i++) {
				result[i] = d1[i] + d2[i];
			}

		}

		return result;
	}

	public static double[] computeDivideDoubleArr(double[] d1, int divide) {

		double[] result = new double[d1.length];

		for (int i = 0; i < d1.length; i++) {
			result[i] = d1[i] / divide;
		}

		return result;

	}

	// =========== MRE start~ ====================
	// compute MRE between two matrixes, MRE will be computer on each cell, then
	// average all them
	public static double computeMRE(double[][] noisedAns, int[][] realAns) {

		double[][] realAnsDouble = intArr2doubleArr(realAns);
		return computeMRE(noisedAns, realAnsDouble);
	}

	public static double computeMRE(double[][] noisedAns, double[][] realAns) {

		double re = 0;
		int dimension = realAns[0].length;
		// int dimension = amountDim;

		for (int i = 0; i < noisedAns.length; i++) {

			for (int j = 0; j < dimension; j++) {
				// NOTE here using sanity bound 1
				re = re + Math.abs(noisedAns[i][j] - realAns[i][j]) / Math.max(Math.abs(realAns[i][j]), 1.0);
				// re = re + Math.abs(noisedAns[i][j] - realAns[i][j]) /
				// Math.max(Math.abs(realAns[i][j]), 10);
			}

		}
		return re / (noisedAns.length * dimension);
	}

	public static double computeMRE(double[] noisedAnsOneTS, int[] realAnsOneTS) {

		double[] realAnsDouble = intArr2doubleArr(realAnsOneTS);
		return computeMRE(noisedAnsOneTS, realAnsDouble);
	}

	public static double computeMRE(double[] noisedAnsOneTS, double[] realAnsOneTS) {

		double re = 0;
		int dimension = realAnsOneTS.length;

		for (int j = 0; j < dimension; j++) {
			// NOTE here using sanity bound 1
			re = re + Math.abs(noisedAnsOneTS[j] - realAnsOneTS[j]) / Math.max(Math.abs(realAnsOneTS[j]), 1.0);
		}

		return re / dimension;
	}

	// =========== MRE end ~ ====================

	// =========== MSE start~ ====================
	public static double computeMSE(double[][] noisedAns, int[][] realAns) {
		double[][] realAnsDouble = intArr2doubleArr(realAns);
		return computeMSE(noisedAns, realAnsDouble);

	}

	public static double computeMSE_self(double[][] noisedAns) {
		int[][] realAns = new int[noisedAns.length][noisedAns[0].length];
		return computeMSE(noisedAns, realAns);
	}

	public static double computeMSE_self(double[] noisedAns) {
		int[] realAns = new int[noisedAns.length];
		return computeMSE(noisedAns, realAns);
	}

	public static double computeMSE(double[] noisedAns, int[] realAns) {
		double[] realAnsDouble = intArr2doubleArr(realAns);
		return computeMSE(noisedAns, realAnsDouble);
	}

	public static double computeRMSE(double[] noisedAns, int[] realAns) {

		return Math.sqrt(computeMSE(noisedAns, realAns));
	}

	public static double computeMSE(double[][] noisedAns, double[][] realAns) {

		double r = 0.0;
		int dimension = realAns[0].length;
		// int dimension = amountDim;

		for (int i = 0; i < realAns.length; i++) {
			for (int j = 0; j < dimension; j++) {
				r = r + (noisedAns[i][j] - realAns[i][j]) * (noisedAns[i][j] - realAns[i][j]);
			}
		}
		return r / (noisedAns.length * dimension);

	}

	public static double computeRMSE(double[][] noisedAns, double[][] realAns) {
		return Math.sqrt(computeMSE(noisedAns, realAns));
	}

	public static double computeMSE(double[] noisedAns, double[] realAns) {

		double r = 0.0;
		int dimension = realAns.length;
		// int dimension = amountDim;

		for (int j = 0; j < dimension; j++) {
			r = r + (noisedAns[j] - realAns[j]) * (noisedAns[j] - realAns[j]);
		}

		return r / (dimension);

	}

	public static double computeRMSE(double[] noisedAns, double[] realAns) {
		return Math.sqrt(computeMSE(noisedAns, realAns));
	}

	// =========== MSE end ~ ====================

	// ===============utility metrics end ================

	// ======statistical queries========

	public static int[] mode(int[] data) {
		ArrayList<Integer> maxCountVals = new ArrayList<Integer>();
		int maxNum = 0;
		int maxCount = 0;

		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();

		for (int i = 0; i < data.length; i++) {
			int count = 0;
			if (hm.containsKey(data[i])) {
				count = hm.get(data[i]);
			}
			count = count + 1;

			hm.put(data[i], count);

			if (count > maxCount) {
				maxCount = count;
				maxNum = 1;
				maxCountVals.add(data[i]);
			} else if (count == maxCount) {
				maxNum++;
				maxCountVals.add(data[i]);
			}

		}

		int[] re = new int[maxNum];
		for (int i = 0; i < maxNum; i++) {
			re[i] = maxCountVals.get(maxCountVals.size() - i - 1);
		}
		return re;

	}

	public static double[] mode(double[] data, int keptDecimal) {
		ArrayList<Double> maxCountVals = new ArrayList<Double>();
		int maxNum = 0;
		int maxCount = 0;

		HashMap<Double, Integer> hm = new HashMap<Double, Integer>();

		for (int i = 0; i < data.length; i++) {
			int count = 0;
			if (hm.containsKey(doubleRoundDown(data[i], keptDecimal))) {
				count = hm.get(doubleRoundDown(data[i], keptDecimal));
			}
			count = count + 1;

			hm.put(doubleRoundDown(data[i], keptDecimal), count);

			if (count > maxCount) {
				maxCount = count;
				maxNum = 1;
				maxCountVals.add(doubleRoundDown(data[i], keptDecimal));
			} else if (count == maxCount) {
				maxNum++;
				maxCountVals.add(doubleRoundDown(data[i], keptDecimal));
			}

		}

		double[] re = new double[maxNum];
		for (int i = 0; i < maxNum; i++) {
			re[i] = maxCountVals.get(maxCountVals.size() - i - 1);
		}
		return re;

	}

	public static double getStat_mean(double[] data) {
		DescriptiveStatistics d = new DescriptiveStatistics();
		for (int i = 0; i < data.length; i++) {
			d.addValue(data[i]);
		}

		return d.getMean();
	}

	// ★2-1
	public static StringBuffer getStat(double[] data) {
		DescriptiveStatistics d = new DescriptiveStatistics();
		for (int i = 0; i < data.length; i++) {
			d.addValue(data[i]);
		}

		StringBuffer sb = new StringBuffer();
		sb.append("============").append("\n");
		sb.append("N:\t" + d.getN()).append("\n");
		sb.append("max:\t" + d.getMax()).append("\n");
		sb.append("min:\t" + d.getMin()).append("\n");
		sb.append("mean:\t" + d.getMean()).append("\n");
		sb.append("median:\t" + d.getPercentile(50)).append("\n");
		sb.append("mode\t:" + Arrays.toString(CommonUtility.mode(data, 5))).append("\n");
		sb.append("std:\t" + d.getStandardDeviation()).append("\n");
		sb.append("variance:\t" + d.getVariance()).append("\n");
		sb.append("skewness:\t" + d.getSkewness()).append("\n");
		sb.append("============").append("\n");
		return sb;
	}

	// ★2-2
	public static StringBuffer getStat(int[] data) {
		DescriptiveStatistics d = new DescriptiveStatistics();
		for (int i = 0; i < data.length; i++) {
			d.addValue(data[i]);
		}

		StringBuffer sb = new StringBuffer();
		sb.append("============").append("\n");
		sb.append("N:\t" + d.getN()).append("\n");
		sb.append("max:\t" + d.getMax()).append("\n");
		sb.append("min:\t" + d.getMin()).append("\n");
		sb.append("mean:\t" + d.getMean()).append("\n");
		sb.append("median:\t" + d.getPercentile(50)).append("\n");
		sb.append("mode\t:" + Arrays.toString(CommonUtility.mode(data))).append("\n");
		sb.append("std:\t" + d.getStandardDeviation()).append("\n");
		sb.append("variance:\t" + d.getVariance()).append("\n");
		sb.append("skewness:\t" + d.getSkewness()).append("\n");
		sb.append("============").append("\n");
		return sb;
	}

	// ======statistics======== end

	public static void writeObjectToDiskInfo(String info, String filename, Object o) throws IOException {
		// dir string
		Matcher m = Pattern.compile("(.*\\w+/).*\\.").matcher(filename);
		m.find();
		String dir = m.group(1);
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}

		FileOutputStream fileOut = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
		out.writeObject(o);
		out.close();
		fileOut.close();
		System.out.println("Write object to " + filename + " OK \t" + info);
	}

	// write object to disk
	public static void writeObjectToDisk(String filename, Object o) throws IOException {

		// dir string
		Matcher m = Pattern.compile("(.*\\w+/).*\\.").matcher(filename);
		m.find();
		String dir = m.group(1);
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}

		FileOutputStream fileOut = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
		out.writeObject(o);
		out.close();
		fileOut.close();
		// System.out.println("Write object to " + filename + " OK");
	}

	// read object from fisk
	public static Object readObjectFromDisk(String filename) throws IOException, ClassNotFoundException {

		Object o = new Object();
		// read object from .data
		FileInputStream fileIn = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
		o = in.readObject();

		in.close();
		fileIn.close();

		// System.out.println("Read object from " + filename + " OK");

		return o;
	}

	public static void writeTxtToDisk(String s, String filename) throws IOException {
		// System.out.println("Write txt to " + filename);

		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		out.write(s);
		out.close();
	}

	// -----------convertHashMapToTable start ------------
	public static String convertHashMapToTable(String info, HashMap<String, Integer> h) {

		StringBuffer sb = new StringBuffer().append("#").append(info).append("\n");

		return sb.append(convertHashMapToTable(h)).toString();

	}

	public static String convertHashMapToTable(HashMap<String, Integer> h) {

		StringBuffer sb = new StringBuffer();

		if (null != h) {
			Object[] s1 = h.keySet().toArray();
			Object[] s2 = h.values().toArray();
			int size = h.size();
			// head
			sb.append("\n\tkey\tvalue\n");
			for (int i = 0; i < size; i++) {
				sb.append(i + 1).append(suffix).append("\t"); // column no.
				sb.append(s1[i].toString()).append("\t");
				sb.append(s2[i].toString()).append("\n");
			}

		} else {
			sb.append("NULL\n");
		}

		return sb.toString();
	}

	// -----------convertHashMapToTable end------------

	// -----------convertArrStrToTable ------------
	public static String suffix = ".";

	// for ArrayList<double[]>
	// NOTE: row_col means each Object[] is a column or row
	public static String convertArrListToTable(String info, ArrayList<double[]> oArr, String row_col) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(info).append(":\t");

		if (null != oArr && oArr.size() != 0) {
			int rowSize = oArr.size();
			int colSize = oArr.get(0).length; // align with size of first column

			// initial tb_side_legend
			String[] tb_side_legend = new String[colSize];
			for (int i = 0; i < colSize; i++) {
				tb_side_legend[i] = String.valueOf(i + 1);
			}

			// initial tb_head
			String[] tb_head = new String[rowSize];
			for (int i = 0; i < rowSize; i++) {
				tb_head[i] = String.valueOf(i + 1);
			}

			// basic info of oArr
			sb.append("h/w=").append(colSize).append("/").append(rowSize).append("\n");
			if (row_col.equals("col")) {
				sb.append(convertArrListToTable_helper(oArr, tb_head, tb_side_legend, row_col));
			} else {
				sb.append(convertArrListToTable_helper(oArr, tb_side_legend, tb_head, row_col));
			}

		} else {
			sb.append("NULL\n");
			System.err.println("convertArrColsToTable - Nullis output as info:" + info);
		}

		return sb.toString();

	}

	/**
	 * @param info
	 * @param doubleArrList
	 * @param row_col
	 * @param labelOfEleInList
	 * @param labelOfInsidElement
	 * @return
	 * @throws Exception
	 *             when labelOfEleInList/labelOfInsidElement is NULL, auto
	 *             increasing series No. will added.
	 */
	public static String convertArrListToTable(String info, ArrayList<double[]> doubleArrList, String row_col, Object[] labelOfEleInList,
			Object[] labelOfInsidElement) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(info).append(":\t");

		if (null != doubleArrList && doubleArrList.size() != 0) {
			int rowSize = doubleArrList.size();
			int colSize = doubleArrList.get(0).length; // align with size of
														// first column

			// basic info of oArr
			sb.append("h/w=").append(colSize).append("/").append(rowSize).append("\n");

			if (null == labelOfEleInList) {
				labelOfEleInList = new String[rowSize];
				for (int i = 0; i < rowSize; i++) {
					labelOfEleInList[i] = String.valueOf(i + 1);
				}
			}

			if (null == labelOfInsidElement) {
				labelOfInsidElement = new String[colSize];
				for (int i = 0; i < colSize; i++) {
					labelOfInsidElement[i] = String.valueOf(i + 1);
				}
			}

			// table content
			if (row_col.equals("col")) {

				sb.append(convertArrListToTable_helper(doubleArrList, labelOfEleInList, labelOfInsidElement, row_col));

			} else if (row_col.equals("row")) {

				sb.append(convertArrListToTable_helper(doubleArrList, labelOfInsidElement, labelOfEleInList, row_col));

			} else {
				throw new Exception("parameter is either COL or ROW ");
			}

		} else {
			sb.append("NULL\n");
			System.err.println("convertArrColsToTable - Nullis output as info:" + info);
		}

		return sb.toString();
	}

	// each Object[] is a column
	private static String convertArrListToTable_helper(ArrayList<double[]> oArr, Object[] tb_head, Object[] tb_side_legend, String row_col)
			throws Exception {
		StringBuffer sb = new StringBuffer();

		int rowSize = oArr.size();
		int colSize = oArr.get(0).length; // align with size of first column

		// tb_head
		// first column in first row is suffix
		sb.append(suffix).append("\t");
		for (int i = 0; i < tb_head.length; i++) {
			sb.append(tb_head[i].toString()).append(suffix).append("\t");
		}
		sb.append("\n");
		// content
		if (row_col.equals("col")) {

			if (colSize != tb_side_legend.length || rowSize != tb_head.length) {
				throw new Exception("helperConvertArrStrToTable size error");
			}

			for (int col = 0; col < colSize; col++) {
				sb.append(tb_side_legend[col].toString()).append(suffix).append("\t");
				for (int row = 0; row < rowSize; row++) {
					// first column is incremental 1.

					sb.append(oArr.get(row)[col]);
					sb.append("\t");
				}
				sb.append("\n");
			}
		} else if (row_col.equals("row")) {

			if (rowSize != tb_side_legend.length || colSize != tb_head.length) {
				throw new Exception("helperConvertArrStrToTable size error rowSize=" + rowSize + " tb_side_legend.length=" + tb_side_legend.length
						+ " colSize=" + colSize + " tb_head.length=" + tb_head.length);
			}

			for (int row = 0; row < rowSize; row++) {
				sb.append(tb_side_legend[row].toString()).append(suffix).append("\t");
				for (int col = 0; col < colSize; col++) {

					// first column is incremental 1.

					sb.append(oArr.get(row)[col]);
					sb.append("\t");
				}
				sb.append("\n");
			}
		} else {
			throw new Exception("helperConvertArrStrToTable row_col error");
		}

		return sb.toString();

	}

	public static String convertArrToTable(String info, int[][] o) {
		StringBuffer result = new StringBuffer(info).append(":\t");
		if (null != o) {
			result.append("h/w=").append(o.length).append("/").append(o[0].length).append("\n");

			// table heads
			for (int i = 0; i <= o[0].length; i++) {
				result.append(i).append(suffix).append("\t");
			}
			result.append("\n");

			for (int i = 0; i < o.length; i++) {

				// table side titles
				result.append(i + 1).append(suffix).append("\t");

				for (int j = 0; j < o[0].length; j++) {
					result.append(o[i][j]);
					result.append("\t");
				}
				result.append("\n");
			}
		} else {
			result.append("NULL\n");
		}
		return result.toString();

	}

	public static String convertArrToTable(String info, double[][] o) {

		StringBuffer result = new StringBuffer(info).append(":\t");
		if (null != o) {
			result.append("h/w=").append(o.length).append("/").append(o[0].length).append("\n");
			// table heads
			for (int i = 0; i <= o[0].length; i++) {
				result.append(i).append(suffix).append("\t");
			}
			result.append("\n");

			for (int i = 0; i < o.length; i++) {

				// table side titles
				result.append(i + 1).append(suffix).append("\t");

				for (int j = 0; j < o[0].length; j++) {
					result.append(o[i][j]);
					result.append("\t");
				}
				result.append("\n");
			}
		} else {
			result.append("NULL\n");
		}
		return result.toString();

	}

	public static void convertArrToTable_hugeLog(String info, double[][] o, String file) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		StringBuffer result = new StringBuffer(info).append(":\t");
		if (null != o) {
			result.append("h/w=").append(o.length).append("/").append(o[0].length).append("\n");

			// table heads
			for (int i = 0; i <= o[0].length; i++) {
				result.append(i).append(suffix).append("\t");
			}
			result.append("\n");

			bw.write(result.toString());
			bw.flush();
			result.setLength(0);

			for (int i = 0; i < o.length; i++) {

				// table side titles
				result.append(i + 1).append(suffix).append("\t");

				for (int j = 0; j < o[0].length; j++) {
					result.append(o[i][j]);
					result.append("\t");
				}
				result.append("\n");

				bw.write(result.toString());
				bw.flush();
				result.setLength(0);
			}
		} else {
			result.append("NULL\n");
			bw.write(result.toString());
		}

	}

	public static String convertArrToTable(String info, double[] o) {

		StringBuffer result = new StringBuffer(info + " ");
		if (null != o) {
			result.append("length=" + o.length + "\n");

			for (int i = 0; i < o.length; i++) {

				// table side titles
				result.append(i + 1).append(suffix).append("\t");

				result.append(o[i]);
				result.append("\n");
			}
			result.append("\n");
		} else {
			result.append("NULL\n");
		}
		return result.toString();

	}

	// -----------convertArrStrToTable end ------------

	// ---------queue in int[][]--------------
	public static void testQueArray1(int amountUser, int amountTS) throws Exception {

		int arraySize = 10;

		// initial 1
		PriorityQueue<Integer>[] pq = new PriorityQueue[amountUser];
		for (int i = 0; i < amountUser; i++) {
			pq[i] = new PriorityQueue();
		}

		// initial2
		int[][] cycleArrPerU = new int[amountUser][arraySize + 1];

		// TEST 1
		long startTime1 = System.currentTimeMillis();
		for (int uIdx = 0; uIdx < amountUser; uIdx++) {
			for (int tsIdx = 0; tsIdx < amountTS; tsIdx++) {
				pq[uIdx].offer(tsIdx);
				if (pq[uIdx].size() > arraySize) {
					pq[uIdx].poll();
				}
			}

		}
		long endTime1 = System.currentTimeMillis();

		// TEST 2
		long startTime2 = System.currentTimeMillis();
		for (int uIdx = 0; uIdx < amountUser; uIdx++) {
			for (int tsID = 0; tsID < amountTS; tsID++) {
				addIntToCycleQueue(cycleArrPerU[uIdx], tsID);
			}
		}
		long endTime2 = System.currentTimeMillis();

		// OUTPUT
		System.out.println("1=" + (endTime1 - startTime1) + "ms");
		System.out.println("2=" + (endTime2 - startTime2) + "ms");

		// print uIdx=0 TEST 1
		for (int i = 0; i < arraySize; i++) {
			System.out.print(pq[0].poll() + ",");
		}
		System.out.println();

		// print uIdx=0 TEST 2
		for (int i = 0; i < arraySize; i++) {
			System.out.print(cycleArrPerU[0][i] + ",");
		}
	}

	public static void testQueArray2() throws Exception {

		int[] cycleArr = new int[4 + 1];
		for (int i = 0; i < 10; i++) {

			addIntToCycleQueue(cycleArr, i + 10);
			for (int j = 0; j < cycleArr.length; j++) {
				System.out.print(cycleArr[j] + ",");
			}
			System.out.println();
			System.err.println("first idx:" + getFirstDataFromQueArray(cycleArr));
			System.err.println("last idx:" + getLastDataFromQueArray(cycleArr));
		}

	}

	// queueSize = cycleArr.length - 1
	public static int getFirstDataFromQueArray(int[] cycleArr) {

		return cycleArr[getFirstIdxFromQueArray(cycleArr)];

	}

	public static int getFirstIdxFromQueArray(int[] cycleArr) {

		int howManyData = cycleArr[cycleArr.length - 1];
		int queueSize = cycleArr.length - 1;
		if (howManyData < queueSize) {
			return 0;
		} else {
			return howManyData % queueSize;
		}

	}

	// queue not used
	public static int getLastDataFromQueArray(int[] cycleArr) {

		return cycleArr[getLastIdxFromQueArray(cycleArr)];

	}

	public static int getLastIdxFromQueArray(int[] cycleArr) {

		int howManyData = cycleArr[cycleArr.length - 1];
		int queueSize = cycleArr.length - 1;
		if (howManyData < queueSize) {
			return howManyData - 1;
		} else {
			return (howManyData - 1) % queueSize;
		}

	}

	// return the real data bits (the last bit is for idx of how many data has
	// been processed)
	public static int getDataLengthFromQueArray(int[] cycleArr) {
		return cycleArr.length - 1;
	}

	// add to queue
	// cycleArr: x+1 e.g.: 0000+0,
	// e.g., add 1 -> 10001 add 2-> 12002 add3->12303 add4->12344 add5->52345
	public static void addIntToCycleQueue(int[] cycleArr, int addVal) {
		int howManyData = cycleArr[cycleArr.length - 1];
		int queueSize = cycleArr.length - 1;

		// value of first <- addVal
		if (howManyData < queueSize) {
			cycleArr[howManyData] = addVal;
		} else {
			// pull out
			cycleArr[howManyData % queueSize] = addVal;
		}

		// howManyData++
		cycleArr[cycleArr.length - 1] = cycleArr[cycleArr.length - 1] + 1;

	}

	// ---------queue in int[][] end--------------

	public static String arr2String(ArrayList<Integer>[] tsOfAllTrajithUserArr) {
		StringBuffer sb = new StringBuffer();
		int userAmount = tsOfAllTrajithUserArr.length;
		for (int i = 0; i < userAmount; i++) {
			sb.append(i + ".\t").append(tsOfAllTrajithUserArr[i].toString()).append("\n");
		}
		return sb.toString();
	}

	private void testArr2String() {
		ArrayList<Integer>[] a = new ArrayList[3];
		a[0] = new ArrayList<Integer>();
		a[0].add(1);
		a[0].add(2);
		a[0].add(3);

		a[1] = new ArrayList<Integer>();
		a[1].add(2);
		a[1].add(3);

		a[2] = new ArrayList<Integer>();
		a[2].add(111);
		a[2].add(2222);
		a[2].add(333);
		a[2].add(444);

		System.out.println(arr2String(a));
	}

}
