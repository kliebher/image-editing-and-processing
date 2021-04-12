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

public class Histogram {

	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    private int maxHeight;
    
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
		// Remark: Please ignore selectionMax and statsData in Exercise 4. It will be used in Exercise 5.
		// ellipse formula: (((x-x0)^2)/a^2)+(((y-y0)^2)/b^2) = 1
		double x0 = ellipseCenter.getX();
		double y0 = ellipseCenter.getY();
		// 2a = ellipseSize.getWidth() -> a = width/2
		double a = ellipseSize.getWidth()/2;
		// 2b = ellipseSize.getHeight() -> b = height/2
		double b = ellipseSize.getHeight()/2;
		// ellipse formula in java: ((Math.pow(x-x0, 2)/Math.pow(a,2))+(Math.pow(y-y0, 2)/Math.pow(b, 2)))

		for(int y = 0; y < image.height; y++){
			for(int x = 0; x < image.width; x++){
				double ellipseRadius = ((Math.pow(x-x0, 2)/Math.pow(a,2))+(Math.pow(y-y0, 2)/Math.pow(b, 2)));
				if(ellipseRadius<=1) {
					int pos = y + image.width + x;
					int pixelColor = (image.argb[pos] >> 16) & 0xff;
					histogram[pixelColor]++;
				}
			}
		}
		//test the amounts

		 int counter = 0;
		 		for(int amount : histogram){
		 				System.out.println(counter + " : " + amount);
		  				counter++;

		                }


		draw();
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
				gc.strokeLine(i + shift, 0, i + shift, (maxHeight - (histogram[i] * (double) maxHeight / maxValue)));
				System.out.println(maxHeight - (histogram[i] * (double) maxHeight / maxValue));
			}
		}
		//gc.strokeLine(128 + shift, shift, 128 + shift, maxHeight + shift);
		//gc.strokeLine(shift, maxHeight/2 + shift, grayLevels + shift, maxHeight/2 + shift);
		//gc.setStroke(Color.ORANGE);
		//gc.strokeLine(shift, shift, grayLevels + shift, maxHeight + shift);
		//gc.strokeLine(grayLevels + shift, shift, shift, maxHeight + shift);

	}
    private int getMaxHeight(int[] histogram){
		int max = 0;
		for(int values : histogram){
			if(values > max) max = values;
		}
		return max;
	}
}
