import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
class Main implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ","Graustufen", "Binärbild(S/W)","Binärbild(5 greys)",
						"Binärbild(10 greys)","Binärbild(FD)", "Sepia", "Sechs Farben"};


	public static void main(String args[]) {

		IJ.open("src/Bear.jpg");

		Main pw = new Main();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null)
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			}

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Graustufen")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int grey = (int) (r+g+b)/3;
						int rn = grey;
						int gn = grey;
						int bn = grey;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Negativ")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}

			if (method.equals("Sepia")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						int grey = (((argb >> 16) & 0xff)+((argb >>  8) & 0xff)+(argb        & 0xff))/3;
						// to make it grey
						int r = grey;
						int g = grey;
						int b = grey;

						int rn = (int) (0.393*r+0.769*g+0.189*b);
						int gn = (int) (0.349*r+0.686*g+0.168*b); 
						int bn = (int) (0.272*r+0.534*g+0.131*b);

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}

			if (method.equals("Binärbild(S/W)")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						int grey = (((argb >> 16) & 0xff)+((argb >>  8) & 0xff)+(argb        & 0xff))/3;
						// to make it grey
						int r = grey;
						int g = grey;
						int b = grey;

						int binValue;

						if(r <= 100){ // ich habe einfach ein paar werte ausprobiert bis der bär okay aussah
							binValue = 0;
						}
						else binValue = 255;

						int rn = binValue;
						int gn = binValue;
						int bn = binValue;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Binärbild(5 greys)")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						int grey = (((argb >> 16) & 0xff)+((argb >>  8) & 0xff)+(argb        & 0xff))/3;
						// to make it grey
						int r = grey;
						int g = grey;
						int b = grey;

						int binValue;

						int d = 255/5;

						if(r < d){ // ich habe einfach ein paar werte ausprobiert bis der bär okay aussah
							binValue = d;
						}
						else if(r<2*d){
							binValue = 2*d;
						}
						else if(r<3*d){
							binValue = 3*d;
						}
						else if(r<4*d){
							binValue = 4*d;
						}
						else binValue = 5+d;

						int rn = binValue;
						int gn = binValue;
						int bn = binValue;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Binärbild(10 greys)")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						int grey = (((argb >> 16) & 0xff)+((argb >>  8) & 0xff)+(argb        & 0xff))/3;
						// to make it grey
						int r = grey;
						int g = grey;
						int b = grey;

						int a = grey/10;
						int d = 255/10;

						int binValue;

						if(r < d){ // ich habe einfach ein paar werte ausprobiert bis der bär okay aussah
							binValue = d;
						}
						else if(r<2*d){
							binValue = 2*d;
						}
						else if(r<3*d){
							binValue = 3*d;
						}
						else if(r<4*d){
							binValue = 4*d;
						}
						else if(r < 5*d){
							binValue = 5*d;
						}
						else if(r<6*d){
							binValue = 6*d;
						}
						else if(r<7*d){
							binValue = 7*d;
						}
						else if(r<8*d){
							binValue = 8*d;
						}
						else if(r<9*d){
							binValue = 9*d;
						}
						else binValue = 10*d;

						int rn = binValue;
						int gn = binValue;
						int bn = binValue;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Binärbild(FD)")) {
				int a = 0;	//hilfsvariable zum speichern der differenz
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						// to make it grey
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int grey = (r+g+b)/3;

						if((grey+a)> 128){ // 128 = Schwellenwert, wenn die werte darüber liegen
							r = 255;      // sollen weiße Pixel erzeugt werden
							g = 255;
							b = 255;
							a += (grey -255);
						}else{			// wenn die werte drunter iegen sollen schwarzen pixel
							r = 0;		// erzeugt werden
							g = 0;
							b = 0;
							a += grey;
						}
						int rn = r;
						int gn = g;
						int bn = b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Sechs Farben")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte
						int grey = (((argb >> 16) & 0xff)+((argb >>  8) & 0xff)+(argb        & 0xff))/3;
						// to make it grey
						int r = grey;
						int g = grey;
						int b = grey;


						// took the rgb values from the internet and just picked 6

						if(r <= 50){
							r = 51;
							g = 25;
							b = 0;
						}
						else if(r<=100){
							r = 102;
							g = 51;
							b = 0;
						}
						else if(r<=150){
							r = 255;
							g = 178;
							b = 102;
						}
						else if(r<=200){
							r = 102;
							g = 178;
							b = 255;
						}
						else if(r<=250){
							r = 153;
							g = 204;
							b = 255;
						}
						else {
							r = 204;
							g = 229;
							b = 255;
						};

						int rn = r;
						int gn = g;
						int bn = b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						if(bn > 255) bn = 255;

						if(rn < 0) rn = 0;
						if(gn < 0) gn = 0;
						if(bn < 0) bn = 0;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}

		}


	} // CustomWindow inner class
}
