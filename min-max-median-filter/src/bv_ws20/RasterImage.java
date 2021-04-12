// BV Ue1 WS2020/21 Vorgabe
//
// Copyright (C) 2019-2020 by Klaus Jung
// All rights reserved.
// Date: 2020-10-08

package bv_ws20;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {

	private static final int gray = 0xffa0a0a0; // (160, 160, 160)

	public int[] argb;    // pixels represented as ARGB values in scanline order
	public int width;    // image width in pixels
	public int height;    // image height in pixels

	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}

	public RasterImage(RasterImage src) {
		// copy constructor
		this.width = src.width;
		this.height = src.height;
		argb = src.argb.clone();
	}

	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if (file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if (image != null && image.getPixelReader() != null) {
			width = (int) image.getWidth();
			height = (int) image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}

	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int) image.getWidth();
		height = (int) image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
	}

	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if (argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}


	// image point operations to be added here

	public void convertToGray() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int pos = y * width + x;
				int pixels = argb[pos];

				int r = (pixels >> 16) & 0xff;
				int g = (pixels >> 8) & 0xff;
				int b = pixels & 0xff;

				int gray = (r + g + b) / 3;

				argb[pos] = (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
			}
		}
	}

	/**
	 * @param quantity The fraction of pixels that need to be modified
	 * @param strength The brightness to be added or subtracted from a pixel's gray level
	 */
	public void addNoise(double quantity, int strength) {
		// TODO: add noise with the given quantity and strength
		Random bd = new Random();
		int anzPixel = argb.length; // works
		double anzWidth = 0;
		double anz = (anzPixel * quantity);
		if (anz != 0) {
			anzWidth = anz / width;
		}
		int pPerWidthRounded = (int)Math.ceil(anzWidth); 	// kein halben Pixel möglich
														// = anzahl pixel per width die verändert werden müssen

		//zusatz infos
		/**
		 System.out.println("gesamt: " + anzPixel);
		System.out.println("quantity: " + quantity);
		System.out.println("anz: " + anz);
		System.out.println("seqR: "+pPerWidthRounded);
		System.out.println("width: " + width);
		 */

		for (int y = 0; y < height; y++) {

			ArrayList<Integer> randomXPixelNumbers = genRandomArray(pPerWidthRounded);

			for (int x = 0; x < width; x++) {

				if(randomXPixelNumbers.contains(x)){
					int pos = y * width + x;
					int pixels = argb[pos];

					int r = (pixels >> 16) & 0xff;
					int g = (pixels >> 8) & 0xff;
					int b = pixels & 0xff;

					int c = bd.nextInt(2);
					int rn, gn, bn;

					if(c == 1){
						rn = ((pixels >> 16) & 0xff)+strength;
						gn = ((pixels >> 8) & 0xff)+strength;
						bn = ((pixels ) & 0xff)+strength;
					}
					else{
						rn = ((pixels >> 16) & 0xff)-strength;
						gn = ((pixels >> 8) & 0xff)-strength;
						bn = ((pixels ) & 0xff)-strength;
					}

					if (rn > 255) rn = 255;
					if (gn > 255) gn = 255;
					if (bn > 255) bn = 255;
					if (rn < 0) rn = 0;
					if (gn < 0) gn = 0;
					if (bn < 0) bn = 0;

					argb[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}

			}
		}


	}
	private ArrayList<Integer> genRandomArray(double anzahl) {
		ArrayList<Integer> rNumberOutput = new ArrayList<>();
		while (rNumberOutput.size() != anzahl && !(dubes(rNumberOutput))) {
			for(int i = 0; i < anzahl;i++){
				Random rand = new Random();
				rNumberOutput.add(rand.nextInt(351));
			}
		}
		return rNumberOutput;
	}

	private boolean dubes(ArrayList<Integer> rNumberOutput) {
		boolean status = false;
		for(int i = 0; i<rNumberOutput.size();i++){
			for(int k = 0; k<rNumberOutput.size();k++){
				int a = rNumberOutput.get(i);
				int b = rNumberOutput.get(k);
				if (a == b) {
					status = true;
					break;
				}
			}
		}
		return status;
	}
}