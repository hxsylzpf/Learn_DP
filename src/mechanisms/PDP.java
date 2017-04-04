package mechanisms;

import Ylab.Correlation.Utility.UtilCommon;
import poc.utility.CommonUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PDP {

	public static void main(String[] args) throws Exception {

		// testPDPHist();

		// testPDP_RMSE();

		// testGeteArr_ASE_2();

		// test_count_score_binary2();

		// testVaryDatasize();

		testVaryThreshold();

	}

	// the same data set, the same privacy setting
	public static void testVaryThreshold() throws Exception {

		int testTimes = 3000;

		// get dataset
		int datasize = 1000;
		double p = 0.2;
		int[] data = getBinaryData(datasize, p);
		int realCount = calRawCountBinary(data);
		System.err.println("realCount="+realCount);

		// get privacy setting
		double e_cons = 0.001;
		double e_moderate = 0.05;
		double e_libral = 0.1;
		double frac_c = 0.5;
		double frac_m = 0.3;
		double[] eArrASE = geteArr_ASE(datasize, e_cons, e_moderate, e_libral, frac_c, frac_m);

		// results
		ArrayList<double[]> doubleArrList = new ArrayList<double[]>();

		int testThrNum = 30;
		String[] labelOfEleInList = new String[testThrNum];
		double t_inv = 0.01;
		for (int i = 1; i <= testThrNum; i++) {
			double t = 0.01 + t_inv * i;
			labelOfEleInList[i - 1] = String.valueOf(UtilCommon.keepNdecimalDouble(t, 2));
			doubleArrList.add(testThrehold(data, eArrASE, testTimes, t));

		}

		String[] labelOfInsidElement = { "PE", "Sample-Lap", "Sample-PE", "Lap" };

		//String oString = CommonUtility.convertArrListToTable("varying threshold", doubleArrList, "row", labelOfEleInList, labelOfInsidElement);

		//System.out.println(oString);

	}

	public static void testVaryDatasize() throws Exception {
		int datasize_start = 500;
		int datasize_inv = 500;
		double p = 0.15;

		// privacy setting
		double e_cons = 0.01;
		double e_moderate = 0.5;
		double e_libral = 1.0;
		double frac_c = 0.5;
		double frac_m = 0.3;

		// System.out.println(CommonUtility.getStat(eArrASE));
		// System.out.println(Arrays.toString(eArrASE));

		int testTimes = 1000;

		double Sample_threshold = 0;
		double[] cand_sample_t = { 0.1, 0.09, 0.08, 0.08, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01 };

		int testDataSizeNum = 10;
		String[] labelOfEleInList = new String[testDataSizeNum];
		ArrayList<double[]> doubleArrList = new ArrayList<double[]>();
		for (int i = 0; i < testDataSizeNum; i++) {
			int datasize = datasize_start + datasize_inv * i;
			// == generate data
			int[] data = getBinaryData(datasize, p);

			// == generate privacy preferences
			double[] eArrASE = geteArr_ASE(datasize, e_cons, e_moderate, e_libral, frac_c, frac_m);

			labelOfEleInList[i] = "size=" + datasize;
			Sample_threshold = 0.08;
			doubleArrList.add(testThrehold(data, eArrASE, testTimes, Sample_threshold));
		}

		String[] labelOfInsidElement = { "PE", "Sample-Lap", "Sample-PE", "Lap" };

		String oString = CommonUtility.convertArrListToTable("varying datasize", doubleArrList, "row", labelOfEleInList, labelOfInsidElement);

		System.out.println(oString);
	}

	// p: data density, how many "1"

	public static double[] testThrehold(int[] data, double[] eArrASE, int testTimes, double Sample_threshold) throws Exception {

		boolean log = false;
		double sensitivity = 1;

		// 4 methods: PE, Sample-Lap, Sample-PE, Laplace
		double[] re = new double[4];

		int realCount = calRawCountBinary(data);

		if (log) {
			System.err.println("realCount=" + realCount);
		}

		int[] realAns = new int[testTimes];
		for (int i = 0; i < testTimes; i++) {
			realAns[i] = realCount;
		}

		// ★★★ result of PE NOTE: budget & sensitivity are 1.
		double[] re_PE = new double[testTimes];
		double[] re_Sample_lap = new double[testTimes];
		double[] re_Sample_PE = new double[testTimes];
		double[] re_lap = new double[testTimes];

		for (int i = 0; i < testTimes; i++) {
			re_PE[i] = PE(data, eArrASE, sensitivity);
			re_Sample_lap[i] = Sample_count_lap(Sample_threshold, data, eArrASE, sensitivity);
			re_Sample_PE[i] = Sample_count_PE(Sample_threshold, data, eArrASE, sensitivity);
			re_lap[i] = LaplaceMechanism.addLaplaceNoise(realCount, CommonUtility.minElemInArr(eArrASE), sensitivity);
		}

		re[0] = CommonUtility.computeRMSE(re_PE, realAns);
		re[1] = CommonUtility.computeRMSE(re_Sample_lap, realAns);
		re[2] = CommonUtility.computeRMSE(re_Sample_PE, realAns);
		re[3] = CommonUtility.computeRMSE(re_lap, realAns);

		return re;
	}

	public static int calRawCountBinary(int[] data) {
		// == get real count
		int realCount = 0;
		// ★ for binary 0/1, count 1
		for (int i = 0; i < data.length; i++) {
			realCount = realCount + data[i];
		}
		return realCount;
	}

	public static double[] testEachPara(int size, double p, double e_min, double e_moderate, double e_libral, double frac_c, double frac_m,
			int testTimes, double Sample_threshold, double sensitivity) throws Exception {

		boolean log = false;

		int[] data = getBinaryData(size, p);
		double[] eArrASE = geteArr_ASE(size, e_min, e_moderate, e_libral, frac_c, frac_m);

		// 4 methods: PE, Sample-Lap, Sample-PE, Laplace
		double[] re = new double[4];

		int realCount = calRawCountBinary(data);

		if (log) {
			System.err.println("realCount=" + realCount);
		}

		int[] realAns = new int[testTimes];
		for (int i = 0; i < testTimes; i++) {
			realAns[i] = realCount;
		}

		long t0 = System.currentTimeMillis();

		// ★★★ result of PE NOTE: budget & sensitivity are 1.
		double[] re_PE = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			int returnedCount = PE(data, eArrASE, sensitivity);
			re_PE[i] = returnedCount;
		}

		long t1 = System.currentTimeMillis();

		re[0] = CommonUtility.computeRMSE(re_PE, realAns);

		if (log) {
			System.out.println("PE:" + re[0] + " time:" + (t1 - t0));
		}

		// ★★★ result of Sample-Lap

		// System.err.println("threshold=" + threshold);
		double[] re_Sample_lap = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			re_Sample_lap[i] = Sample_count_lap(Sample_threshold, data, eArrASE, sensitivity);
		}
		long t2 = System.currentTimeMillis();

		re[1] = CommonUtility.computeRMSE(re_Sample_lap, realAns);

		if (log) {
			System.out.println("Sample-Lap:" + re[1] + " time:" + (t2 - t1));
		}

		// ★★★ result of Sample-PE (the same threhold as Sample-Lap)

		// double threshold = CommonUtility.getStat_mean(eArrASE);
		// System.err.println("threshold=" + threshold);
		double[] re_Sample_PE = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			double returnedCount = Sample_count_PE(Sample_threshold, data, eArrASE, sensitivity);

			re_Sample_PE[i] = returnedCount;
		}
		long t3 = System.currentTimeMillis();

		re[2] = CommonUtility.computeRMSE(re_Sample_PE, realAns);
		if (log) {
			System.out.println("Sample-PE:" + re[2] + " time:" + (t3 - t2));
		}

		// ★★★ result of Laplace Mechanism
		double[] re_lap = new double[testTimes];

		double budget = eArrASE[0];
		for (int i = 0; i < testTimes; i++) {
			re_lap[i] = LaplaceMechanism.addLaplaceNoise(realCount, budget, sensitivity);
		}

		long t4 = System.currentTimeMillis();

		re[3] = CommonUtility.computeRMSE(re_lap, realAns);
		if (log) {
			System.out.println("Laplace:" + re[3] + " time:" + (t4 - t3));
		}

		return re;
	}

	public static void testPDP_RMSE() throws Exception {

		int datasize = 10000;
		double sensitivity = 1;

		// == generate data
		double p = 0.15;
		int[] data = getBinaryData(datasize, p);

		// == generate privacy preferences
		double e_cons = 0.01;
		double e_moderate = 0.5;
		double e_libral = 1.0;

		double frac_c = 0.2;
		double frac_m = 0.37;

		double[] eArrASE = geteArr_ASE(datasize, e_cons, e_moderate, e_libral, frac_c, frac_m);
		// System.out.println(CommonUtility.getStat(eArrASE));
		// System.out.println(Arrays.toString(eArrASE));

		int realCount = calRawCountBinary(data);
		System.err.println("realCount=" + realCount);

		int testTimes = 1000;
		int[] realAns = new int[testTimes];
		for (int i = 0; i < testTimes; i++) {
			realAns[i] = realCount;
		}

		long t0 = System.currentTimeMillis();

		// ★★★ result of PE NOTE: budget & sensitivity are 1.
		double[] re_PDP = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			int returnedCount = PE(data, eArrASE, sensitivity);
			re_PDP[i] = returnedCount;
		}

		long t1 = System.currentTimeMillis();
		System.out.println("PE:" + CommonUtility.computeRMSE(re_PDP, realAns) + " time:" + (t1 - t0));

		// ★★★ result of Sample-Lap
		double threshold = 0.1;
		// System.err.println("threshold=" + threshold);
		double[] re_Sample_lap = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			re_Sample_lap[i] = Sample_count_lap(threshold, data, eArrASE, sensitivity);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Sample-Lap:" + CommonUtility.computeRMSE(re_Sample_lap, realAns) + " time:" + (t2 - t1));

		// ★★★ result of Sample-PE (the same threhold as Sample-Lap)

		// double threshold = CommonUtility.getStat_mean(eArrASE);
		// System.err.println("threshold=" + threshold);
		double[] re_Sample_PE = new double[testTimes];
		for (int i = 0; i < testTimes; i++) {
			double returnedCount = Sample_count_PE(threshold, data, eArrASE, sensitivity);

			re_Sample_PE[i] = returnedCount;
		}
		long t3 = System.currentTimeMillis();
		System.out.println("Sample-PE:" + CommonUtility.computeRMSE(re_Sample_PE, realAns) + " time:" + (t3 - t2));

		// ★★★ result of Laplace Mechanism
		double[] re_lap = new double[testTimes];
		double budget = eArrASE[0];
		for (int i = 0; i < testTimes; i++) {
			re_lap[i] = LaplaceMechanism.addLaplaceNoise(realCount, budget, sensitivity);
		}

		long t4 = System.currentTimeMillis();

		System.out.println("Laplace:" + CommonUtility.computeRMSE(re_lap, realAns) + " time:" + (t4 - t3));

		// // ★★★ result of PE + lap
		// double thr_e = 0.5;
		// double[] re_PDP_Lap = new double[testTimes];
		// int[] data1 = PE_Lap_divideDB1(thr_e, data, eArrASE);
		// int[] data2 = PE_Lap_divideDB2(thr_e, data, eArrASE);
		// double[] eArr1 = PE_Lap_divideE1(thr_e, eArrASE);
		// double[] eArr2 = PE_Lap_divideE2(thr_e, eArrASE);
		//
		// for (int i = 0; i < testTimes; i++) {
		// double returnedCount = PE_Lap(data1, data2, eArr1, eArr2);
		// re_PDP_Lap[i] = returnedCount;
		// }
		//
		// long t4 = System.currentTimeMillis();
		// System.out.println("\n PE+Lap:" +
		// CommonUtility.computeRMSE(re_PDP_Lap, realAns) + " time:" + (t4 -
		// t3));

	}

	// out put into Mathematica to see the histogram
	public static void testPDPHist() throws IOException {

		int sensitivity = 1;

		// get data, E
		int datasize = 1000;
		double p = 0.5;
		int[] data = getBinaryData(datasize, p);

		// == generate privacy preferences
		double e_cons = 0.01;
		double e_moderate = 0.5;
		double e_libral = 1.0;
		double frac_c = 0.1;
		double frac_m = 0.37;
		double[] eArrASE = geteArr_ASE(datasize, e_cons, e_moderate, e_libral, frac_c, frac_m);
		// == generate over.

		int realCount = calRawCountBinary(data);

		System.out.println("realCount =" + realCount);

		// test PE
		StringBuffer o = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			int returnedID = PE(data, eArrASE, sensitivity);
			o.append(returnedID).append(",");
		}
		CommonUtility.writeTxtToDisk(o.toString(), "/Users/soyo/Downloads/1.txt");
	}

	// p is fraction of 1
	public static int[] getBinaryData(int size, double p) {
		int[] data = new int[size];

		for (int i = 0; i < size; i++) {
			data[i] = new Random().nextInt(100);
		}

		double threhold = p * 100;
		for (int i = 0; i < size; i++) {
			if (data[i] < threhold) {
				data[i] = 1;
			} else {
				data[i] = 0;
			}
		}

		return data;
	}

	// // frac of conservative user, ie small e
	// public static double[] geteArr_ASE(int size, double frac_conser) {
	// double[] eArr = new double[size];
	// for (int i = 0; i < size; i++) {
	// if (ThreadLocalRandom.current().nextDouble(e_min, e_moderate);
	// new Random().nextInt(100000) < frac_conser * 100000) {
	// eArr[i] = (1 + new Random().nextInt(50)) / 100.0;
	// } else {
	// eArr[i] = (50.0 + new Random().nextInt(50)) / 100.0;
	// }
	//
	// }
	//
	// Arrays.sort(eArr); // ASE
	// return eArr;
	//
	// }
	//
	// public static double[] geteArr_ASE(int size, double frac_conser, double
	// min) {
	// double[] eArr = new double[size];
	// for (int i = 0; i < size; i++) {
	// if (new Random().nextInt(100000) < frac_conser * 100000) {
	// eArr[i] = (min * 100 + new Random().nextInt(50)) / 100.0;
	// } else {
	// eArr[i] = (50.0 + new Random().nextInt(50)) / 100.0;
	// }
	//
	// }
	//
	// Arrays.sort(eArr); // ASE
	// return eArr;
	//
	// }
	//
	// // test geteArr_ASE_2
	// public static void testGeteArr_ASE_2() throws IOException {
	//
	// int datasize = 1000;
	//
	// double e_min = 0.01;
	// double e_moderate = 0.2;
	// double e_libral = 1.0;
	//
	// double frac_c = 0.333333333;
	// double frac_m = 0.333333333;
	//
	// double[] eArr2 = geteArr_ASE_2(datasize, e_min, e_moderate, e_libral,
	// frac_c, frac_m);
	//
	// StringBuffer o = new StringBuffer();
	// for (int i = 0; i < datasize; i++) {
	// o.append(eArr2[i]).append(",");
	// }
	// CommonUtility.writeTxtToDisk(o.toString(),
	// "/Users/soyo/Downloads/eArr2.txt");
	// System.out.println("see file.");
	//
	// }

	// new version, more control
	public static double[] geteArr_ASE(int size, double e_min, double e_moderate, double e_libral, double frac_c, double frac_m) {
		double[] eArr = new double[size];

		for (int i = 0; i < size; i++) {
			double cc = new Random().nextDouble();
			if (cc <= frac_c) {
				eArr[i] = ThreadLocalRandom.current().nextDouble(e_min, e_moderate);
			} else if (cc <= frac_c + frac_m) {
				eArr[i] = ThreadLocalRandom.current().nextDouble(e_moderate, e_libral);
			} else {
				eArr[i] = e_libral;

			}

		}

		Arrays.sort(eArr); // ASE
		return eArr;

	}

	public static double Sample_count_PE(double threshold, int[] rawData, double[] eArr, double sensitivity) throws Exception {

		double[][] newDataAndeArr = Sample_data(threshold, rawData, eArr);
		double[] newData = newDataAndeArr[0];
		double[] neweArr = newDataAndeArr[1];

		int realCount = 0;
		for (int i = 0; i < newData.length; i++) {
			realCount = realCount + (int) newData[i];
		}

		return PE(CommonUtility.doubleArr2intArr(newData), neweArr, sensitivity);

	}

	public static double Sample_count_lap(double threshold, int[] rawData, double[] eArr, double sensitivity) throws Exception {
		double[][] newDataAndeArr = Sample_data(threshold, rawData, eArr);
		double[] newData = newDataAndeArr[0];

		int realCount = 0;
		for (int i = 0; i < newData.length; i++) {
			realCount = realCount + (int) newData[i];
		}

		double e = threshold; // already ASE

		return LaplaceBinaryData(CommonUtility.doubleArr2intArr(newData), e, sensitivity);

	}

	public static double LaplaceBinaryData(int[] data, double budget, double sensitivity) throws Exception {

		int realCount = 0;
		for (int i = 0; i < data.length; i++) {
			realCount = realCount + (int) data[i];
		}

		return LaplaceMechanism.addLaplaceNoise(realCount, budget, sensitivity);

	}

	// sample mechanism return a new dataset
	public static double[][] Sample_data(double threshold, int[] rawData, double[] eArr) {
		int[] selectOrNot = new int[rawData.length];

		for (int i = 0; i < rawData.length; i++) {
			if (eArr[i] >= threshold) {
				selectOrNot[i] = 1;
			} else {
				double pr = (Math.exp(eArr[i]) - 1) / (Math.exp(threshold) - 1);
				if (new Random().nextInt(100000) < pr * 100000) {
					selectOrNot[i] = 1;
				} else {
					selectOrNot[i] = 0;
				}
			}
		}

		int sizeOfNewData = calRawCountBinary(selectOrNot);

		// new data & eArr
		double[][] newDataAndeArr = new double[2][sizeOfNewData];
		int idxOfNewData = 0;
		for (int i = 0; i < selectOrNot.length; i++) {
			if (selectOrNot[i] == 1) {
				newDataAndeArr[0][idxOfNewData] = rawData[i];
				newDataAndeArr[1][idxOfNewData] = eArr[i];
				idxOfNewData++;
			}
		}

		return newDataAndeArr;
	}

	// Exponential-like mechanism
	public static int PE(int[] data, double[] eArrASE, double sensitivity) {

		return ExponentialMechanism.r(count_score_binary2(data, eArrASE), 1, sensitivity);

	}

	// binary data, count "1"
	// output the score corresponding to each count r in [0,sizeOfData]
	public static double[] count_score_binary(int[] data, double[] eArrASE) {

		int maxR_count = data.length;

		double[] scoreArr = new double[maxR_count + 1];

		int realCount = 0;

		// ★ for binary 0/1, count 1
		for (int i = 0; i < eArrASE.length; i++) {
			realCount = realCount + data[i];
		}

		for (int r = 0; r <= maxR_count; r++) {

			if (realCount > r) {
				int numOfChange = realCount - r;
				for (int j = 0; j < data.length && numOfChange > 0; j++) {
					// ★ for binary 0/1, count 1
					if (data[j] == 1) {
						scoreArr[r] = scoreArr[r] - eArrASE[j];
						numOfChange = numOfChange - 1;
					}

				}
			} else if (r == realCount) {
				scoreArr[r] = 0;
			} else if (realCount < r) {

				int numOfChange = r - realCount;
				for (int j = 0; j < data.length && numOfChange > 0; j++) {
					// ★ for binary 0/1, count 1
					if (data[j] == 0) {
						scoreArr[r] = scoreArr[r] - eArrASE[j];
						numOfChange = numOfChange - 1;
					}

				}
			}
		}

		return scoreArr;

	}

	// version 2 improve computational efficiency
	// binary data, count "1"
	// output the score corresponding to each count r in [0,sizeOfData]
	public static double[] count_score_binary2(int[] data, double[] eArrASE) {

		int maxR_count = data.length;
		double[] scoreArr = new double[maxR_count + 1];

		int realCount = 0;

		// ★ for binary 0/1, count 1
		for (int i = 0; i < eArrASE.length; i++) {
			realCount = realCount + data[i];
		}

		// // need to change 0->1
		// ArrayList<Double> scoreArr_0_1 = new ArrayList<Double>();
		//
		// // need to change 1->0
		// ArrayList<Double> scoreArr_1_0 = new ArrayList<Double>();

		double acc_0_1 = 0.0;
		double acc_1_0 = 0.0;

		int count_0_1 = 0;
		int count_1_0 = 0;

		for (int i = 0; i < data.length; i++) {
			if (data[i] == 1) {

				acc_1_0 = acc_1_0 - eArrASE[i];
				// scoreArr_1_0.add(acc_1_0);

				scoreArr[realCount - 1 - count_1_0] = acc_1_0;

				count_1_0 = count_1_0 + 1;

			} else {

				acc_0_1 = acc_0_1 - eArrASE[i];
				// scoreArr_0_1.add(acc_0_1);
				scoreArr[realCount + 1 + count_0_1] = acc_0_1;

				count_0_1 = count_0_1 + 1;
			}
		}

		return scoreArr;

	}

	// TEST count_score_binary2
	public static void test_count_score_binary2() {
		int datasize = 1000;

		// == get data
		double p = 0.15;
		int[] data = getBinaryData(datasize, p);

		// == get eArr
		double e_cons = 0.01;
		double e_moderate = 0.5;
		double e_libral = 1.0;

		double frac_c = 0.54;
		double frac_m = 0.37;

		double[] eArrASE = geteArr_ASE(datasize, e_cons, e_moderate, e_libral, frac_c, frac_m);

		double[] scoreArr1 = count_score_binary(data, eArrASE);
		double[] scoreArr2 = count_score_binary2(data, eArrASE);

		System.out.println(Arrays.toString(scoreArr1));
		System.out.println(Arrays.toString(scoreArr2));
	}

	// hybrid
	// 1.1 divide to 2 parts
	public static int[] PE_Lap_divideDB1(double thr_e, int[] data, double[] eArrASE) {

		// divide data into 2 parts by privacy setting

		ArrayList<Integer> data1 = new ArrayList<Integer>();

		for (int i = 0; i < data.length; i++) {
			if (eArrASE[i] >= thr_e) {
				data1.add(data[i]);

			}
		}

		int[] data1_int = CommonUtility.ArrInteger2intArr(data1);

		return data1_int;
	}

	// 1.2 divide to 2 parts
	public static int[] PE_Lap_divideDB2(double thr_e, int[] data, double[] eArrASE) {

		// divide data into 2 parts by privacy setting

		ArrayList<Integer> data2 = new ArrayList<Integer>();

		for (int i = 0; i < data.length; i++) {
			if (eArrASE[i] < thr_e) {
				data2.add(data[i]);
			}
		}

		int[] data2_int = CommonUtility.ArrInteger2intArr(data2);

		return data2_int;
	}

	// 1.3 divide to 2 parts
	public static double[] PE_Lap_divideE1(double thr_e, double[] eArrASE) {

		// divide data into 2 parts by privacy setting

		ArrayList<Double> eArr1 = new ArrayList<Double>();

		for (int i = 0; i < eArrASE.length; i++) {
			if (eArrASE[i] >= thr_e) {
				eArr1.add(eArrASE[i]);

			}
		}

		double[] eArr1_double = CommonUtility.ArrDouble2doubleArr(eArr1);

		return eArr1_double;
	}

	// 1.4 divide to 2 parts
	public static double[] PE_Lap_divideE2(double thr_e, double[] eArrASE) {

		// divide data into 2 parts by privacy setting

		ArrayList<Double> eArr2 = new ArrayList<Double>();

		for (int i = 0; i < eArrASE.length; i++) {
			if (eArrASE[i] < thr_e) {
				eArr2.add(eArrASE[i]);

			}
		}

		double[] eArr2_double = CommonUtility.ArrDouble2doubleArr(eArr2);

		return eArr2_double;
	}

	public static double PE_Lap(int[] data1, int[] data2, double[] eArrASE1, double[] eArrASE2) throws Exception {

		double r1 = 0;
		double r2 = 0;

		if (data1.length != 0) {
			r1 = ExponentialMechanism.r(count_score_binary(data1, eArrASE1), 1, 1);

		}

		if (data2.length != 0) {
			r2 = LaplaceBinaryData(data2, eArrASE2[0], 1);
		}

		return r1 + r2;

	}

}
