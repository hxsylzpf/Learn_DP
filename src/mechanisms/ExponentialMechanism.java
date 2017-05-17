package mechanisms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class ExponentialMechanism {

	public static void main(String[] args) {

		testGetByProb();

	}

	public static int run(double[] score, double budget, double sensitivity) {

		double[] data = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			data[i] = Math.exp(budget * score[i] *0.5 / sensitivity);
		}

		return getRandomIdxByProbArr(data);
	}
	
	public static double[] releaseDistr(double[] score, double budget, double sensitivity) {

		double[] data = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			data[i] = Math.exp(budget * score[i] *0.5  / sensitivity);
		}

		return getNormalizedProb_helper(data);
	}

	public static int r(float[] score, double budget, double sensitivity) {

		double[] weight = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			weight[i] = Math.exp(budget * score[i] *0.5 / sensitivity);
		}

		return getRandomIdxByProbArr(weight);
	}

	public static int r(int[] score, double budget, double sensitivity) {

		double[] data = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			data[i] = Math.exp(budget * score[i] *0.5 / sensitivity);
		}

		return getRandomIdxByProbArr(data);
	}

	// score is "value" (not "key") in Hashmap
	public static String r(HashMap<String, Integer> hm_score, double budget, double sensitivity) {

		int n = hm_score.size();
		String[] names = new String[n];
		int[] scores = new int[n];
		Iterator<String> it = hm_score.keySet().iterator();
		int ii = 0;
		while (it.hasNext()) {
			names[ii] = it.next();
			scores[ii] = hm_score.get(names[ii]);
			ii++;
		}

		double[] data = new double[scores.length];
		for (int i = 0; i < scores.length; i++) {
			data[i] = Math.exp(budget * scores[i] *0.5 / sensitivity);
		}

		return names[getRandomIdxByProbArr(data)];
	}

	public static HashMap<String, Double> getWeights_hm(HashMap<String, Integer> hm_score, double budget, double sensitivity) {

		int n = hm_score.size();

		String[] names = new String[n];
		int[] scores = new int[n];
		Iterator<String> it = hm_score.keySet().iterator();
		int ii = 0;
		while (it.hasNext()) {
			names[ii] = it.next();
			scores[ii] = hm_score.get(names[ii]);
			ii++;
		}

		double[] weight = new double[scores.length];
		for (int i = 0; i < n; i++) {
			weight[i] = Math.exp(budget * scores[i] *0.5 / sensitivity);
		}

		HashMap<String, Double> hm_weight = new HashMap<String, Double>();
		for (int i = 0; i < n; i++) {
			hm_weight.put(names[i], weight[i]);
		}

		return hm_weight;

	}

	// ======random by prob=======
	// test method
	public static void testGetByProb() {
		double budget = 1;
		double sensitivity = 1;
		double[] score = new double[] { 10, 10, 1, 1, 1 };
		// double[] score = new double[]{3,-20,-50,-50,0};

		double[] data = new double[score.length];
		for (int i = 0; i < score.length; i++) {
			data[i] = Math.exp(budget * score[i] *0.5  / sensitivity);
		}

		System.out.println("data.length=" + data.length);

		int correctNum = 0;
		int testTimes = 1000000;
		for (int i = 0; i < testTimes; i++) {
			int rID = getRandomIdxByProbArr(data);
			// System.out.print(rID+",");
			if (rID == 0) {
				correctNum++;
			}
		}

		System.out.println("\ncorrect num=" + correctNum);
		System.out.println("correct rate=" + (double) correctNum / testTimes);
	}

	// method
	public static int getRandomIdxByProbArr(double[] weights) {
		// 2 3 -> 0.4 0.6 (percentage)
		double[] normalizedProb = getNormalizedProb_helper(weights);
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

	public static HashMap<String, Double> getNormalizedProb_helper_hm(HashMap<String, Integer> hm_score) {

		int n = hm_score.size();

		String[] names = new String[n];
		double[] probabilityList = new double[n];
		Iterator<String> it = hm_score.keySet().iterator();
		int ii = 0;
		while (it.hasNext()) {
			names[ii] = it.next();
			probabilityList[ii] = hm_score.get(names[ii]);
			ii++;
		}

		double sum = 0.0;
		for (int i = 0; i < probabilityList.length; i++) {
			sum = sum + probabilityList[i];
		}

		double[] normalizedProb = new double[probabilityList.length];
		for (int i = 0; i < normalizedProb.length; i++) {
			normalizedProb[i] = probabilityList[i] / sum;
		}

		HashMap<String, Double> hm_score_prob = new HashMap<String, Double>();
		for (int i = 0; i < n; i++) {
			hm_score_prob.put(names[i], normalizedProb[i]);
		}

		return hm_score_prob;

	}
	
	public static HashMap<String, Double> getNormalizedProb_helper_hm_double(HashMap<String, Double> hm_score) {

		int n = hm_score.size();

		String[] names = new String[n];
		double[] probabilityList = new double[n];
		Iterator<String> it = hm_score.keySet().iterator();
		int ii = 0;
		while (it.hasNext()) {
			names[ii] = it.next();
			probabilityList[ii] = hm_score.get(names[ii]);
			ii++;
		}

		double sum = 0.0;
		for (int i = 0; i < probabilityList.length; i++) {
			sum = sum + probabilityList[i];
		}

		double[] normalizedProb = new double[probabilityList.length];
		for (int i = 0; i < normalizedProb.length; i++) {
			normalizedProb[i] = probabilityList[i] / sum;
		}

		HashMap<String, Double> hm_score_prob = new HashMap<String, Double>();
		for (int i = 0; i < n; i++) {
			hm_score_prob.put(names[i], normalizedProb[i]);
		}

		return hm_score_prob;

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

}
