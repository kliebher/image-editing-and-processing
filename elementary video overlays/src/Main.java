import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;


class Main implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende", "Überlagerung AB", "Überlagerung BA","Schiebe Blende",
										"Chroma Key", "Eigenes"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		IJ.open("src/videos/StackB.tif");

		Main sd = new Main();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();

		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();

		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		ImagePlus A = o.openImage("src/videos/StackA.tif");
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}

		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Überlagerung AB")) methode = 3;
		if (s.equals("Überlagerung BA")) methode = 4;
		if (s.equals("Schiebe Blende")) methode = 5;
		if (s.equals("Chroma Key")) methode = 6;
		if (s.equals("Eigenes")) methode = 7;



		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++) // für frames -> 95
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					if (methode == 1)
					{			// z=1 -> 0 & z = 95 bleibt die höhe übrig
					if (y+1 > (z-1)*(double)height/(length-1))
						pixels_Erg[pos] = pixels_B[pos];
					else
						pixels_Erg[pos] = pixels_A[pos];
					}


					if (methode == 2) // weiche Blende
					{
						// am anfang (z=1) -> alpha = 0 -> nur Bild A
						// am ende (z=length) -> alpha gleich 255 -> nur bild A
					double alpha = ((double)z-1)/((double)length-1)*255;
					int r = (int)(alpha*rA+(255-alpha)*rB)/255;
					int g = (int)(alpha*gA+(255-alpha)*gB)/255;
					int b = (int)(alpha*bA+(255-alpha)*bB)/255;

					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if(methode == 3){ // Überlagerung AB
						int r;
						int g;
						int b;
						if(rB <= 128){
							r = (int)((double)rB*(double)rA/128);
						} else{
							r = (int)(255-((255-(double)rA)*(255-(double)rB)/128));
						}
						if(gB <= 128){
							g = (int)((double)gB*(double)gA/128);
						}
						else{
							g = (int)(255-((255-(double)gA)*(255-(double)gB)/128));
						}
						if(bB <= 128){
							b = (int)((double)bB*(double)bA/128);
						}
						else{
							b = (int)(255-((255-(double)bA)*(255-(double)bB)/128));
						}
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + b;
					}
					if(methode == 4){ // Überlagerung BA
						int r;
						int g;
						int b;
						if(rA <= 128){
							r = (int)((double)rB*(double)rA/128);
						} else{
							r = (int)(255-((255-(double)rA)*(255-(double)rB)/128));
						}
						if(gA <= 128){
							g = (int)((double)gB*(double)gA/128);
						}
						else{
							g = (int)(255-((255-(double)gA)*(255-(double)gB)/128));
						}
						if(bA <= 128){
							b = (int)((double)bB*(double)bA/128);
						}
						else{
							b = (int)(255-((255-(double)bA)*(255-(double)bB)/128));
						}
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + b;

					}
					if(methode == 5) { // Schiebe Blende
						if (x+1 > ((z-1)*(double)width/(length-1))){
							pixels_Erg[pos] = pixels_B[(int) ((y * width + x) - ((z-1)*(double)width/(length-1)))];
						}else{
							// für z = 1 soll -> width -1 angezeigt werden, z = 2 -> width -2 usw.
							// und dann immer so weiter, quasi von hinten nach vorne
							int b = (int) (y*width-(((z-1)*(double)width/(length-1)))+x);
							if(b<0)b=0; // es kam sonst immer ein 'OutOfBounds' Fehler
							pixels_Erg[pos] = pixels_A[b];
						}
					}

					if(methode == 6){ //Chroma Keying
						// (alpha = binär, Kugel um Raumschiff) -> aber wie soll ich eine Kugel ohne tiefe erstellen
						// es ist anhand der Folien/Podcast für mich einfach nicht wirklich nachvollziehbar
						// besser als das hab ichs nicht hinbekommen (orange ausblenden)
						int r,g,b;
						int orange = pixels_A[1];
						int rO = (orange & 0xff0000) >> 16;
						int gO = (orange & 0x00ff00) >> 8;
						int bO = (orange & 0x0000ff);
						if(rA >= rO-10){
							r = rB;
						}else{
							r = rA;
						}
						if(gA >= gO){
							g = gB;
						}else{
							g = gA;
						}
						if(bA >= bO){
							b = bB;
						}else{
							b = bA;
						}
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + b;
					}
					if(methode == 7){ //eigene, Kreis
						int r;
						int g;
						int b;
						// py(pythagoras) entspricht dem abstand vom Mittelpukt zur EcKe
						// +3 hinter width & heigth damit es wirklich bis in die Ecken geht
						// mit Math.hypot hat es irgendwie nicht richtig geklappt
						double py = Math.sqrt((Math.pow((double)width+3,2))+(Math.pow((double)height+3,2)))/2;
						double radius = ((z-1)*py/(double)length-1); // radius wächst mit z, 0-150
						if(Math.hypot(x - (double)width/2, y - (double)height/2) < radius){
							r = rA;
							g = gA;
							b = bA;
						}
						else{
							r = rB;
							g = gB;
							b = bB;
						}
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + b;

					}

				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

