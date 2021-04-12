// BV Ue4 SS2020 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws20;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToneCurve {
	
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    
    private int brightness = 0;
    private double gamma = 1.0;
    
    private int[] grayTable = new int[grayLevels];

	public ToneCurve(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void setBrightness(int brightness) {
		this.brightness = brightness;
		updateTable();
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
		updateTable();
	}

	private void updateTable() {
		// TODO: Fill the grayTable[] array to map gray input values to gray output values.
		// It will be used as follows: grayOut = grayTable[grayIn].
		//
		// Use brightness and gamma values.
		// See "Gammakorrektur" at slide no. 18 of 
		// http://home.htw-berlin.de/~barthel/veranstaltungen/GLDM/vorlesungen/04_GLDM_Bildmanipulation1_Bildpunktoperatoren.pdf
		//
		// First apply the brightness change, afterwards the gamma correction.

		//gamma correction: f(x) = (255*x^1/Y)/(255*1/Y) -> (255*Math.pow(newBrightness, 1/gamma))/(Math.pow(255,1/gamma))

		//iteration over all gray levels
		for(int grayIn = 0; grayIn < grayLevels; grayIn++){
			//applying the brightness change
			int newBrightness = grayIn+brightness;
			//limit the values
			if(newBrightness > 255) newBrightness = 255;
			else if(newBrightness < 0) newBrightness = 0;
			//gamma correction according to the formula
			int grayOut = (int) ((255*Math.pow(newBrightness, 1/gamma))/(Math.pow(255,1/gamma)));
			//assign the values
			grayTable[grayIn] = grayOut;
		}
		
	}
	
	public int mappedGray(int inputGray) {
		return grayTable[inputGray];
	}
	
	public void draw() {
		gc.clearRect(0, 0, grayLevels, grayLevels);
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(3);

		// TODO: draw the tone curve into the gc graphic context

		// Remark: This is some dummy code to give you an idea for graphics drawing with pathes
		//lineTo(x-value of ending point, y-value of ending point)
		gc.beginPath();
		//iteration over all possible gray values
		for(int grayValue = 0; grayValue < grayLevels; grayValue++){
			gc.lineTo(grayValue, 255-grayTable[grayValue]);
			gc.stroke();
		}
	}

	
}
