package com.flyadventure.flyadventure.VoiceRecognition.VoiceRecorder;

import java.util.Random;

//import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

public class MFCCData {
	public double[][] data;
	public int result;

	public double getDifference(MFCCData otherMFCCData) throws Exception {
		double[][] other = otherMFCCData.data;
		if (data[0].length != other[0].length) {
			throw new Exception("data[0].length != other.length");
		}

		double[][] longData = null;
		double[][] shortData = null;
		if (data.length > other.length) {
			longData = data;
			shortData = other;
		} else {
			longData = other;
			shortData = data;
		}

		int lengthDifference = longData.length - shortData.length;
		// divide longData into lengthDifference blocks
		// damp lengthDifference data from longData
		double sliceLength = 1.0 * longData.length / lengthDifference;

		Random random = new Random();
		double minDistance = -1;

		for (int j = 0; j < 10; j++) {
			double distance = -1;
			if (shortData.length != longData.length) {
				double[][] processedData = new double[shortData.length][];
				int processedDataIndex = 0;
				int usedLength = 0;
				for (int block = 0; block < lengthDifference; block++) {
					int finalLength = 0;
					double temp = ((block + 1) * sliceLength - usedLength);
					if (Math.abs(usedLength + temp - longData.length) < 1e-9) {
						finalLength = longData.length - usedLength;
					} else {
						finalLength = (int) (temp);
						if (usedLength + finalLength > longData.length) {
							finalLength = longData.length - usedLength;
						}
					}
					int missdataIndex = usedLength + random.nextInt(finalLength);
					for (int i = usedLength; i < usedLength + finalLength; i++) {
						if (i == missdataIndex) {
							continue;
						}
						processedData[processedDataIndex++] = longData[i];
					}
					usedLength += finalLength;
				}
				if (processedData[processedData.length - 1] == null) {
					throw new Exception("processedData[processedData.length - 1] == null");
				}
				distance = calculateDistance(processedData, shortData);
			} else {
				distance = calculateDistance(longData, shortData);
			}
			if (minDistance == -1) {
				minDistance = distance;
			} else if (minDistance > distance) {
				minDistance = distance;
			}
		}
		return minDistance;
	}

	static public double calculateDistance(double[][] data1, double[][] data2) throws Exception {
		if (data1.length != data2.length) {
			throw new Exception("data1.length != data2.length");
		}

		double sum = 0;
		for (int i = 0; i < data1.length; i++) {
			for (int j = 0; j < data1[i].length; j++) {
				double temp = data1[i][j] - data2[i][j];
				sum += temp * temp;
			}
		}
		return Math.sqrt(sum) / (data1.length * data1[0].length);
	}
}
