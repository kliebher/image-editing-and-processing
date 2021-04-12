// BV Ue4 SS2020 Vorgabe
//
// Copyright (C) 2019 by Klaus Jung
// All rights reserved.
// Date: 2019-05-12

package bv_ws20;

import bv_ws20.ImageAnalysisAppController.StatsProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Histogram {

	private static final int grayLevels = 256;

	private final GraphicsContext gc;
	private final int maxHeight;
	private int allPixels;
	private int selectedPixels;



	private int[] histogram = new int[grayLevels];

	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}

	public void update(RasterImage image, Point2D ellipseCenter, Dimension2D ellipseSize, int selectionMax, ObservableList<StatsProperty> statsData) {
		// TODO: calculate histogram[] out of the gray values of the image for pixels inside the given ellipse
		for(int z = 0; z < grayLevels; z++) {
			histogram[z] = 0; // remove this line when implementing the proper calculations
		}
		selectedPixels = 0;
		// Remark: Please ignore selectionMax and statsData in Exercise 4. It will be used in Exercise 5.
		// ellipse formula: (((x-x0)^2)/a^2)+(((y-y0)^2)/b^2) = 1
		double x0 = ellipseCenter.getX();
		double y0 = ellipseCenter.getY();
		// 2a = ellipseSize.getWidth() -> a = width/2
		double a = ellipseSize.getWidth()/2;
		// 2b = ellipseSize.getHeight() -> b = height/2
		double b = ellipseSize.getHeight()/2;
		// ellipse formula in java: ((Math.pow(x-x0, 2)/Math.pow(a,2))+(Math.pow(y-y0, 2)/Math.pow(b, 2)))

		allPixels = image.height*image.width;

		for(int x = 0; x < image.width;x++) {
			for(int y = 0; y < image.height;y++) {
				double ellipseRadius = ((Math.pow(x-x0, 2)/Math.pow(a,2))+(Math.pow(y-y0, 2)/Math.pow(b, 2)));
				if(ellipseRadius <= 1) {
					int pos = y * image.width + x;
					int color = (image.argb[pos]) & 0xff;
					histogram[color]++;
					selectedPixels++;
				}
			}
		}
		//test the amounts
		/**
		 * int counter = 0;
		 * 		for(int amount : histogram){
		 *
		 * 				System.out.println(counter + " : " + amount);
		 * 				counter++;
		 *
		 *                }
		 */
		printStatistics(image, selectionMax, statsData);
	}

	public void printStatistics(RasterImage image, int selectionMax, ObservableList<StatsProperty> statsData){
		//minimum
		//maximum
		//mean (= average)
		//median (middle value) -> even -> sum/2 ; odd -> just the middle one
		double mean = 0;
		int median = 0;
		double level;
		int summe = 0;
		double variance = 0;
		double information = 0;
		double entropy = 0;

		for(int i = 0; i < selectionMax;i++){
			summe += histogram[i];
			//pixelInformation = -1*Math.log(histogram[i]);
		}

		for(StatsProperty property : statsData){
			switch (property){
				case Level:
					level = (summe/(double)selectedPixels);
					property.setValueInPercent(level);
				break;
				case Maximum:
					int max;
					for(int i = grayLevels-1; i >= 0; i--){
						if(histogram[i] != 0){
							max = i;
							property.setValue(max);
							break;
						}
					}
					break;
				case Minimum:
					int min;
					for(int i = 0; i < grayLevels; i++){
						if(histogram[i] != 0){
							min = i;
							property.setValue(min);
							break;
						}
					}
				break;
				case Mean:
					int pixel = 0;
					for(int j = 0; j < grayLevels; j++){
						pixel += histogram[j];
					}
					for(int i = 0; i <grayLevels; i++){
						mean += (double)histogram[i] * i; //p(j)*j
					}
					mean = mean/pixel;
					property.setValue(mean);
				break;
				case Median:
					int[] sortedArray = new int[image.argb.length];
					for(int i = 0; i < image.argb.length; i++){
					    sortedArray[i] = image.argb[i] & 0xff;
                    }
					//System.out.println(Arrays.toString(sortedArray));
					Arrays.sort(sortedArray);
                    //System.out.println(Arrays.toString(sortedArray));
					int length = sortedArray.length;
					if(length % 2 == 0) {
						median = ((sortedArray[length/2-1])+(sortedArray[length/2]))/2;
					}
					else median = sortedArray[length/2];

					//median = sortedArray[sortedArray.length/2];
					property.setValue(median);
				break;
				case Variance: 	// ((histogramValue-mean)^2)*histogram[i]
								// /allPixels because the value was insane high, so is just tried out
					for(int i = 0; i < grayLevels; i++){
						variance += Math.pow((i-mean),2)*histogram[i]/allPixels;
					}
					property.setValue(variance);
				break;
				case Entropy: 	// H = H + (-p(j) * log_2(p(j)) (from slides)
								// p = property of certain value
								// log_2 = log(x)/log(2)
					for(int j = 0; j < grayLevels; j++){
						if(histogram[j] != 0) {
							double proberty = (double)histogram[j]/allPixels;
							entropy += -1.0 * (proberty) * log2(proberty);
						}
					}
					property.setValue(entropy);
				break;
			}
		}
	}

	private double log2(double x){
		return (Math.log(x)/Math.log(2));
	}

	public void draw() {
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setLineWidth(1);

		// TODO: draw histogram[] into the gc graphic context

		// Remark: This is some dummy code to give you an idea for graphics drawing
		double shift = 0.5;
		int maxValue = getMaxHeight(histogram);
		//System.out.println(maxHeight); (200)
		// note that we need to add 0.5 to all coordinates to get a one pixel thin line

		gc.setStroke(Color.BLACK);
		for(int i = 0; i < grayLevels;i++){
			if(maxValue != 0) {
				gc.strokeLine(i + shift, 255, i + shift, (maxHeight - (histogram[i] * (double) maxHeight / maxValue)));
				// changed y1 to 255 so that the background is white
			}
		}
		//gc.strokeLine(128 + shift, shift, 128 + shift, maxHeight + shift);
		//gc.strokeLine(shift, maxHeight/2 + shift, grayLevels + shift, maxHeight/2 + shift);
		//gc.setStroke(Color.ORANGE);
		//gc.strokeLine(shift, shift, grayLevels + shift, maxHeight + shift);
		//gc.strokeLine(grayLevels + shift, shift, shift, maxHeight + shift);

	}
	public int getMaxHeight(int[] histogram){
		int max = 0;
		for(int values : histogram){
			if(values > max) max = values;
		}
		return max;
	}

	public void autoContrast(){
		//System.out.println("Button worked");
		//1% of the values
		double onePercent = allPixels*0.01;
		double ninetyNine = onePercent*99;
		int minimum = 0;
		int pixelSumme = 0;
		for(int i = 0; i < grayLevels; i++){
			if(pixelSumme <= onePercent) {
				pixelSumme += histogram[i];
				minimum = i; // i -> number of different gray levels from left
			}
		}
		pixelSumme = 0; //reset for maximum
		int maximum = 0;
		for(int i = grayLevels-1; i >= 0; i--){
			if(pixelSumme <= onePercent ) {
				pixelSumme += histogram[i];
				maximum = i;
			}
		}
		double gap = maximum-minimum;
		double sections = 255/gap; //e.g for a gap of 25, each section would be 25.5 long

		System.out.println("minimum : "+minimum);
		System.out.println("maximum : "+maximum);
		System.out.println("gap : "+gap);
		//System.out.println("1% to 99% of all Pixels are between the "+ onePercent+" pixel and the "+ninetyNine+" Pixel of the image");
	}


}
