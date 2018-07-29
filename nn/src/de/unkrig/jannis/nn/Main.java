
package de.unkrig.jannis.nn;

import java.util.Arrays;
import java.util.Random;

public class Main {

	static final int    NI = 2;
	static final int[]  NH = { 2, 2, 2, 2, 2, 2 };
	static final int    NO = 2;
	static final double EPSILON = 0.3;

	static boolean FAKE = false;
	
	public static void main(String[] args) {
		
		Random r = new Random();

		// Erstelle die Gewichte W. 
		double[][][] w = new double[NH.length + 1][][];
		for (int l = 0; l < w.length; l++) {
			w[l] = new double[l == 0 ? NI : NH[l - 1]][l == w.length - 1 ? NO : NH[l]];
		}
		for (int i1 = 0; i1 < w.length; i1++) {
			for (int i2 = 0; i2 < w[i1].length; i2++) {
				for (int i3 = 0; i3 < w[i1][i2].length; i3++) {
					w[i1][i2][i3] = FAKE ? 0.5 : 2 * r.nextDouble() - 1;
				}
			}
		}
//		o[0][0] = 0.25;
//		o[0][1] = 0.75;
		
		sysout(w, "w");
		
		// Erstelle die Bias-Gewichte.
		double[][] wb = new double[NH.length + 1][];
		for (int l = 0; l < wb.length; l++) {
			wb[l] = new double[l == wb.length - 1 ? NO : NH[l]];
		}
		for (int i1 = 0; i1 < wb.length; i1++) {
			for (int i2 = 0; i2 < wb[i1].length; i2++) {
				wb[i1][i2] = FAKE ? 0.5 : 2 * r.nextDouble() - 1;
			}
		}
		
		sysout(wb, "wb");

		// Trainiere mit 100 Datensätzen.
		for (int a = 0; a < 1000; a++) {
	
			System.out.printf("a=%d%n", a);
			
			double[][] o = new double[NH.length + 2][];
			for (int l = 0; l < o.length; l++) {
				o[l] = new double[l == 0 ? NI : l == NH.length + 1 ? NO : NH[l-1]];
			}
			// Wähle zufällige Eingabewerte.
			int bmin = 0;
			for (int b = 0; b < NI; b++) {
				o[0][b] = FAKE ? (b == 0 ? 0.25 : 0.75) : r.nextDouble();
				if (o[0][b] < o[0][bmin]) bmin = b;
			}
			double[][] i = new double[NH.length + 1][];
			for (int l = 0; l < i.length; l++) {
				i[l] = new double[l == NH.length ? NO : NH[l]];
			}
			
			// Berechne die Soll-Ausgangswerte.
			double[] so = new double[NO];
			so[bmin] = 1;
			sysout(so, "so");
			
			for (int l = 0; l < NH.length + 1; l++) {

				for (int i1 = 0; i1 < o[l].length; i1++) {
					for (int i2 = 0; i2 < i[l].length; i2++) {
						i[l][i2] += o[l][i1] * w[l][i1][i2];
					}
				}
				for (int i2 = 0; i2 < i[l].length; i2++) {
					i[l][i2] += wb[l][i2];
				}
				
				// Seismoid.
				for (int i1 = 0; i1 < i[l].length; i1++) {
					o[l + 1][i1] = 1 / (1 + Math.exp(-i[l][i1]));
				}
			}
			
			double[][] delta = new double[NH.length + 1][];
			for (int l = delta.length - 1; l >= 0; l--) {
				delta[l] = new double[l == NH.length ? NO : NH[l]];
				for (int i1 = 0; i1 < delta[l].length; i1++) {
					if (l == NH.length) {
						double x = i[NH.length][i1];
						delta[l][i1] = (1 / (1 + Math.exp(-x))) * (1 - 1 / (1 + Math.exp(-x))) * (so[i1] - o[NH.length + 1][i1]);
					} else {
						double x = i[NH.length][i1];
						double sum = 0;
						for (int i2 = l + 1; i2 < (l == NH.length - 1 ? NO : NH[l]); i2++) {
							sum += delta[l + 1][i2] * w[l + 1][i1][i2];
						}
						delta[l][i1] = (1 / (1 + Math.exp(-x))) * (1 - 1 / (1 + Math.exp(-x))) * sum;
					}
				}
			}

			for (int l = 0; l < w.length; l++) {
				for (int i1 = 0; i1 < w[l].length; i1++) {
					for (int i2 = 0; i2 < o[l + 1].length; i2++) {
						w[l][i1][i2] += EPSILON * delta[l][i2] * o[l][i1];
					}
				}

				for (int i2 = 0; i2 < o[l + 1].length; i2++) {
					wb[l][i2] += EPSILON * delta[l][i2];
				}
			}

			sysout(i, "i");
			sysout(o, "o");
			sysout(w, "w");
			sysout(wb, "wb");
		}
	}

	private static void sysout(double[][][] daaa, String name) {
		System.out.printf("%s=%s%n", name, Arrays.deepToString(daaa));
		// TODO Auto-generated method stub
		
	}

	private static void sysout(double[][] daa, String name) {
		System.out.printf("%s=%s%n", name, Arrays.deepToString(daa));
	}

	private static void sysout(double[] da, String name) {
		System.out.printf("%s=%s%n", name, Arrays.toString(da));
	}
}
