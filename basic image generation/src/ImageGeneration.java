
import com.sun.jdi.DoubleType;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import javax.swing.*;
import java.util.RandomAccess;
class ImageGeneration implements PlugIn {

	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild",
		"Belgische Fahne",
		"Schwarz/Weiss Verlauf",
		"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
		"USA Fahne",
		"Japanische Fahne"
	};

	private String choice;

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		ImageGeneration imageGeneration = new ImageGeneration();
		imageGeneration.run("");
	}

	public void run(String arg) {

		int width  = 566;  // Breite
		int height = 400;  // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();

		dialog();

		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen

		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}

		if ( choice.equals("Gelbes Bild") ) {
			generateYellowImage(width, height, pixels);
		}

		if ( choice.equals("Belgische Fahne") ) {
			generateBelgFlag(width, height, pixels);
		}

		if ( choice.equals("Schwarz/Weiss Verlauf") ) {
			generateBlackWhiteCourse(width, height, pixels);
		}

		if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf") ) {
			generateHBlackRedVBlackBlue(width, height, pixels);
		}

		if ( choice.equals("USA Fahne") ) {
			generateUSAFlag(width, height, pixels);
		}

		if ( choice.equals("Japanische Fahne") ) {
			generateJapFlag(width, height, pixels);
		}

		//zusätzliche Quelle/Hilfe (hauptsächlich Japanische Flagge)
		//https://books.google.de/books?id=_xV4DwAAQBAJ&printsec=frontcover&dq=book+java+praktikum&hl=de&sa=X&ved=0ahUKEwjNiYi6lYvpAhVGfZoKHUjuB0wQ6AEIKDAA#v=onepage&q=japanische%20flagge&f=false
		////////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateJapFlag(int width, int height, int[] pixels) {
		int radius = 3*height/10; // der radius beträgt 3/10 der höhe
		int r, g, b;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				//hypot = sqrt(x^2, y^2)
				if (Math.hypot(x - width/2, y - height / 2) < radius) {
					r = 255;
					g = 0;
					b = 0;
				} else {
					r = 255;
					g = 255;
					b = 255;
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateUSAFlag(int width, int height, int[] pixels) {

		for (int a = 0; a<13; a++) { // zählt a von 0 -> 13 (13 Streifen)

			for (int y = a*(height/13); y < (a+1)*(height / 13); y++) { // y=0 -> max Höhe
				//13 Streifen, jeweils von y = a*(height/13) - y < (a+1)*(height/13)
				//Bsp:0 --> y = 0*(heigth/13) = 0 ; y < (0+1)*(height/13) = heigth/13 // Erster Streifen

				for (int x = 0; x < width; x++) { // x=0 -> max. Breite
					int pos = y*width+x;
					if(a % 2 ==0){			// wenn a gerade ist, ist der abschnitt (Streifen) rot

						int r = 190;
						int b = 0;
						int g = 30;

						pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
					}
					else {				// wenn a ungerade ist, ist der abschnitt (Streifen) weiß

						int r = 255;
						int b = 255;
						int g = 255;

						pixels[pos] = 0xFFFFFFFF;
					}

				}
			}
		}		// blaues kästchen
				for(int y = 0; y < 7*(height/13);y++){ // endet bei y = 7*(height/13) // nach 7 Streifen
					for(int x = 0; x <= width/2.5;x++){ // endet bei 40% der Breite -> width/2.5
						int pos = y*width+x;
						int r = 0;
						int b = 255;					// blau
						int g = 0;

						pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
					}
				}
				// Problem : unten am Rand entsteht ein kleiner schwarzer Streifen
	}

	private void generateHBlackRedVBlackBlue(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte

			int b = y * 255 / height; // /height um nicht über 255 zu kommen -> Fehler

			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = x * 255 / width; // /width
				// ebenfalls um nicht über 255 zu kommen
				int g = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateBlackWhiteCourse(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y<height;y++){
			// Schleife ueber die x-Werte
			for(int x=0;x<width;x++){
				int pos = y*width+x;

				int r = (255 * x) / (width-1); // von 0 ((255*0)/565) -> 255 ((255*255)/565)
				int b = (255 * x) / (width-1);// schwarz			  -> weiß
				int g = (255 * x) / (width-1);

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;

			}


		}
	}

	private void generateBelgFlag(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width/3; x++) { // von 0 - zum Ende des 1. Drittels
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=width/3; x<width-width/3; x++) { // vom ende des 1. Drittels zum Ende des 2.
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=width-width/3; x<width; x++) { // Vom ende des 2. zum ende des dritten
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 0;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}


	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");

		gd.addChoice("Bildtyp", choices, choices[0]);


		gd.showDialog();	// generiere Eingabefenster

		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}
}
