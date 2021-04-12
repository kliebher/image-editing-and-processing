// BV Ue2 WS20/21 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws20;


import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GeometricTransform {

	public enum InterpolationType {
		NEAREST("Nearest Neighbour"),
		BILINEAR("Bilinear");

		private final String name;

		private InterpolationType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	}

	;

	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch (interpolation) {
			case NEAREST:
				perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
				break;
			case BILINEAR:
				perspectiveBilinear(src, dst, angle, perspectiveDistortion);
				break;
			default:
				break;
		}

	}

	/**
	 * @param src                   source image
	 * @param dst                   destination Image
	 * @param angle                 rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using nearest neighbour image rendering
		double angleR = Math.toRadians(angle);
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		for (int ydst = 0; ydst < dst.height; ydst++) {
			for (int xdst = 0; xdst < dst.width; xdst++) {

				//distance of x,y-value to the middle, hint during the class
				double xDistance = xdst - ((double) dst.width / 2);
				double yDistance = ydst - ((double) dst.height / 2);

				// from the slides:
				// xdst = (cos(angle)*xsrc)/(perspectiveDistortion*sin(angle)*xsrc+1)
				// ydst = ysrc / (s*sin(angle)*xsrc+1)

				// change to xsrc, ysrc:
				double xsrc = (xDistance / (cos(angleR) - xDistance * perspectiveDistortion * sin(angleR)));
				double ysrc = yDistance * (sin(Math.toRadians(angle)) * xsrc * perspectiveDistortion + 1);

				//add distance again  -> but from src, because xsrc, and not xdst
				int xPos = (int) (xsrc + (double) src.width / 2);
				int yPos = (int) (ysrc + (double) src.height / 2);

				//new src position
				int srcPos = yPos * src.width + xPos;

				if (xPos < 0 || xPos >= src.width || yPos < 0 || yPos >= src.height) {
					dst.argb[ydst * dst.width + xdst] = 0xfff0f0f0; //light gray - color around the image
				} else {
					dst.argb[ydst * dst.width + xdst] = src.argb[srcPos];
				}
			}
		}
	}


	/**
	 * @param src                   source image
	 * @param dst                   destination Image
	 * @param angle                 rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation
		double angleR = Math.toRadians(angle);
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		for (int ydst = 0; ydst < dst.height; ydst++) {
			for (int xdst = 0; xdst < dst.width; xdst++) {

				//distance of x,y-value to the middle, hint during the class
				double xDistance = xdst - ((double) dst.width / 2);
				double yDistance = ydst - ((double) dst.height / 2);

				// from the slides:
				// xdst = (cos(angle)*xsrc)/(perspectiveDistortion*sin(angle)*xsrc+1)
				// ydst = ysrc / (s*sin(angle)*xsrc+1)

				// change to xsrc, ysrc:
				double xsrc = (xDistance / (cos(angleR) - xDistance * perspectiveDistortion * sin(angleR)));
				double ysrc = yDistance * (sin(Math.toRadians(angle)) * xsrc * perspectiveDistortion + 1);

				//add distance again  -> but from src, because xsrc, and not xdst
				double xPos = (xsrc + (double)src.width / 2);
				double yPos = (ysrc + (double)src.height / 2);

				int srcPos = (int)((yPos) * src.width + (xPos));

				// prozedure not nesessary if pixel already on the grid
				if(xPos == (int)xPos && yPos == (int)yPos){
						dst.argb[ydst * dst.width + xdst] = src.argb[srcPos];
				}else{
					// contains the next four surrounding pixels
					int[][] surroundingPixelPositions = surroundingPixelPositions(xPos,yPos);

					// the proportions of surrounding pixels depending on the distance
					double[] proportion = pixelProportion(surroundingPixelPositions, xPos, yPos);

					// exact pixel positions of surrounding pixels in the source image
					int[] pixelPos = pixelPositions(surroundingPixelPositions, src, xPos, yPos);

					// color array 4 (pixels), 3 (colors)
					int[][] argb = new int[4][3];

					argb[0] = new int[]{
							(pixelPos[0] >> 16) & 0xff, // int[0] = red of of surrounding pixel [0]
							(pixelPos[0] >> 8) & 0xff,  // int[1] = green of of surrounding pixel [0]
							(pixelPos[0]) & 0xff		// int[2] = blue of of surrounding pixel [0]
					};

					argb[1] = new int[]{
							(pixelPos[1] >> 16) & 0xff,	// int[0] = red of of surrounding pixel [1]
							(pixelPos[1] >> 8) & 0xff,	// int[1] = green of of surrounding pixel [1]
							(pixelPos[1]) & 0xff		// int[2] = blue of of surrounding pixel [1]
					};

					argb[2] = new int[]{
							(pixelPos[2] >> 16) & 0xff,	// int[0] = red of of surrounding pixel [2]
							(pixelPos[2] >> 8) & 0xff,	// int[1] = green of of surrounding pixel [2]
							(pixelPos[2]) & 0xff		// int[2] = blue of of surrounding pixel [2]
					};

					argb[3] = new int[]{
							(pixelPos[3] >> 16) & 0xff,	// int[0] = red of of surrounding pixel [3]
							(pixelPos[3] >> 8) & 0xff,	// int[1] = green of of surrounding pixel [3]
							(pixelPos[3]) & 0xff		// int[2] = blue of of surrounding pixel [3]
					};

					// red 		-> 	argb[0][0],[1][0],[2][0],[3][0]
					// green 	->	argb[0][1],[1][1],[2][1],[3][1]
					// blue 	->  argb[0][2],[1][2],[2][2],[3][2]

					double red 	 = (argb[0][0]*proportion[0])+(argb[1][0]*proportion[1])
									+(argb[2][0]*proportion[2])+(argb[3][0]*proportion[3]);
					double green = (argb[0][1]*proportion[0])+(argb[1][1]*proportion[1])
									+(argb[2][1]*proportion[2])+(argb[3][1]*proportion[3]);
					double blue  = (argb[0][2]*proportion[0])+(argb[1][2]*proportion[1])
									+(argb[2][2]*proportion[2]+argb[3][2]*proportion[3]);

					
					if (xPos < 0 || xPos >= src.width || yPos < 0 || yPos >= src.height) {
						dst.argb[ydst * dst.width + xdst] = 0xfff0f0f0; //light gray - color around the image
					} else {
						dst.argb[ydst * dst.width + xdst] = 0xff << 24 | (int) red << 16 | (int) green << 8 | (int) blue;
					}

				}


			}
		}
	}

	// outputs the four sourrounding pixels
	public int[][] surroundingPixelPositions(double x, double y){
		int evenX = (int)x; // int to put it on the grid
		int evenY = (int)y;
		return new int[][]{
				{evenX, evenY},
				{evenX+1, evenY},
				{evenX, evenY+1},
				{evenX+1, evenY+1}
		};
	}

	//outputs the proportions of the four sourrounding pixels depending in distance
	public double[] pixelProportion(int[][] surroundingPixelPositions, double x, double y){
		// difference between (double)x & (int)x -> range of values 0 - 1
		double xDifference = x - surroundingPixelPositions[0][0]; // [0][0] = x, [0][1] = y
		double yDifference = y - surroundingPixelPositions[0][1];

		//double value contains the proportions -> together 1, so it is like percentage
		// e.g. x-,yDifferece = 0.5 (x = 2.5, y = 2.5)
		return new double[]{
				(1-xDifference)*(1-yDifference), // (0.5)*(0.5)	= 0.25 = 25%
				xDifference*(1-yDifference),	// (0.5)*(0.5)	= 0.25 = 25%
				(1-xDifference)*yDifference,	// (0.5)*(0.5)	= 0.25 = 25%
				xDifference*yDifference			// (0.5)*(0.5)	= 0.25 = 25%
		};

	}

	public int[] pixelPositions(int[][] surroundingPixelPositions, RasterImage src, double x, double y) {
		int[] pixPos = new int[4]; // 4 pixels
			for (int i = 0; i < pixPos.length; i++) {

				if (!(surroundingPixelPositions[i][0] < 0 || surroundingPixelPositions[i][0] >= src.width
					|| surroundingPixelPositions[i][1] < 0 || surroundingPixelPositions[i][1] >= src.height)){

					pixPos[i] = src.argb[surroundingPixelPositions[i][1]*src.width+surroundingPixelPositions[i][0]];

				}
			}

		return pixPos;
	}

}



