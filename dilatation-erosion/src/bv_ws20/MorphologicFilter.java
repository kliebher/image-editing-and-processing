// BV Ue3 WS2019/20 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws20;

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
		System.arraycopy(src.argb, 0, dst.argb, 0, src.argb.length);

		/*
		for(int i = 0; i < src.argb.length; i++){
			dst.argb[i] = src.argb[i];
		}
		 */
		/* test
		dst.argb = src.argb.clone();
		dst.invert();
		 */
	}
	
	public void dilation(RasterImage src, RasterImage dst, double radius) {
		dst.argb = src.argb.clone(); // dst = src
		int black = 0xff000000;
		// loop over the image
		for(int y= 0; y < dst.height; y++) {
			for (int x = 0; x < dst.width; x++) {
				// loop to access the pixels in the radius
				for (int i = (int) -radius; i < radius; i++) {
					for (int j = (int) -radius; j < radius; j++) {
						// formula for distance between two points:
						// Math.sqrt(Math.pow((xP2 - xP1), 2) + Math.pow((yP2-yP1), 2))
						// range describes the distance between the original x, y and the ones including the range
						double range = Math.sqrt(Math.pow(((x + i) - x), 2) + Math.pow(((y + j) - y), 2));
						// new position including the range
						int posWithRadius = (y + j) * src.width + (x + i);
						if (posWithRadius > 0 && posWithRadius < src.argb.length) {
							int dstPos = y * dst.width + x;
							//if a pixel around the originally one within the radius is black
							if (src.argb[posWithRadius] == black && range <= radius) {
								//they become colored in the dst image
								dst.argb[dstPos] = black;
							}
						}

					}

				}
			}
		}
	}
	
	public void erosion(RasterImage src, RasterImage dst, double radius) {
		// TODO: erode the image using a structure element that is a neighborhood with the given radius
		dst.argb = src.argb.clone(); // dst = src
		int white = 0xffffffff;
		// loop over the image
		for(int y= 0; y < dst.height; y++) {
			for (int x = 0; x < dst.width; x++) {
				// loop to access the pixels in the radius
				for (int i = (int) -radius; i < radius; i++) {
					for (int j = (int) -radius; j < radius; j++) {
						// formula for distance between two points:
						// Math.sqrt(Math.pow((xP2 - xP1), 2) + Math.pow((yP2-yP1), 2))
						// range describes the distance between the original x, y and the ones including the range
						double range = Math.sqrt(Math.pow(((x + i) - x), 2) + Math.pow(((y + j) - y), 2));
						// new position including the range
						int posWithRadius = (y + j) * src.width + (x + i);
						if (posWithRadius > 0 && posWithRadius < src.argb.length) {
							int dstPos = y * dst.width + x;
							//if a pixel around the originally one within the radius is white
							if (src.argb[posWithRadius] == white && range <= radius) {
								//they become colored in the dst image
								dst.argb[dstPos] = white;
							}
						}

					}

				}
			}
		}

	}
}
