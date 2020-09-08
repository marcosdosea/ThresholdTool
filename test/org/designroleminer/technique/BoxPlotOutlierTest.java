package org.designroleminer.technique;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.threshold.BoxPlotOutlier;

public class BoxPlotOutlierTest {

	double[] values = { 2, 5, 7, 11, 15 };
	double[] values2 = {1, 2, 5, 11, 15, 20};
	double[] values3 = {1, 2, 3, 4, 5, 6, 7, 10, 15, 16};
	double[] values4 = {1, 1, 2, 2, 3, 3};
	BoxPlotOutlier boxplot = new BoxPlotOutlier(values);
	BoxPlotOutlier boxplot2 = new BoxPlotOutlier(values2);
	BoxPlotOutlier boxplot3 = new BoxPlotOutlier(values3);
	BoxPlotOutlier boxplot4 = new BoxPlotOutlier(values4);
	
	@Test
	public void testGetMedCouple() {
		assertEquals(0.28, boxplot.getMedCouple(), 0.01);
		assertEquals(0.07, boxplot2.getMedCouple(), 0.01);
		assertEquals(0.35, boxplot3.getMedCouple(), 0.01);
		assertEquals(0.35, boxplot4.getMedCouple(), 0.01);
		
	}	

	@Test
	public void testGetMedian() {
		assertEquals(7, boxplot.getMedian(), 0);
		assertEquals(8, boxplot2.getMedian(), 0);
		assertEquals(2, boxplot4.getMedian(), 0);
	}

	@Test
	public void testGetValuePercentil() {
		assertEquals(2, boxplot.getValuePercentil(10), 1);
	}

	@Test
	public void testGetQ1() {
		assertEquals(5, boxplot.getQ1(), 1);
		assertEquals(2, boxplot2.getQ1(), 1);
		assertEquals(2, boxplot4.getQ1(), 1);
	}

	@Test
	public void testGetQ3() {
		assertEquals(11, boxplot.getQ3(), 0);
		assertEquals(15, boxplot2.getQ3(), 0);
		assertEquals(2, boxplot4.getQ3(), 0);
	}

	@Test
	public void testGetIQR() {
		assertEquals(6, boxplot.getIQR(), 0);
		assertEquals(13, boxplot2.getIQR(), 0);
		assertEquals(0, boxplot4.getIQR(), 0);
	}

	@Test
	public void testGetLowerThresholdTurkey() {
		assertEquals(-4, boxplot.getLowerThresholdTurkey(), 0);
		assertEquals(-17.5, boxplot2.getLowerThresholdTurkey(), 0);
		assertEquals(2, boxplot4.getLowerThresholdTurkey(), 0);
	}

	@Test
	public void testGetLowerThresholdAdjusted() {
		assertEquals(1.6, boxplot.getLowerThresholdAdjusted(), 0.1);
		assertEquals(-12.8, boxplot2.getLowerThresholdAdjusted(), 0.1);
		assertEquals(0, boxplot3.getLowerThresholdAdjusted(), 0.1);
		assertEquals(2, boxplot4.getLowerThresholdAdjusted(), 0.1);
	}

	@Test
	public void testGetUpperThresholdTurkey() {
		assertEquals(20, boxplot.getUpperThresholdTurkey(), 0);
		assertEquals(34.5, boxplot2.getUpperThresholdTurkey(), 0);
		assertEquals(2, boxplot4.getUpperThresholdTurkey(), 0);
	}
	
	@Test
	public void testGetUpperThresholdAdjusted() {
		assertEquals(38.8, boxplot.getUpperThresholdAdjusted(), 0.1);
		assertEquals(41.5, boxplot2.getUpperThresholdAdjusted(), 0.1);
		assertEquals(53.8, boxplot3.getUpperThresholdAdjusted(), 0.1);
		assertEquals(2, boxplot4.getUpperThresholdAdjusted(), 0.1);
	}

}
