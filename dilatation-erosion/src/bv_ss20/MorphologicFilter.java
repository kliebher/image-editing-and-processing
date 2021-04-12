// BV Ue3 WS2019/20 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ss20;

public class MorphologicFilter {
	
	public enum FilterType { 
		DILATION("Dilation"),
		EROSION("Erosion");
		
		private final String name;       
	    private FilterType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	// filter implementations go here:
	
	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
	}
	
	public void dilation(RasterImage src, RasterImage dst, double radius) {
		// TODO: dilate the image using a structure element that is a neighborhood with the given radius
	}
	
	public void erosion(RasterImage src, RasterImage dst, double radius) {
		// TODO: erode the image using a structure element that is a neighborhood with the given radius
	}
	
	
	
	

}
