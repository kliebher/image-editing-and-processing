
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

	String[] items = {"Original", "Weichgezeichnetes Bild", "Hochpassgefiltertes Bild", "Bild mit verstärkten Kanten"};


	public static void main(String args[]) {

		IJ.open("src/sail.jpg");

		Main pw = new Main();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp == null)
			imp = WindowManager.getCurrentImage();
		if (imp == null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int[]) ip.getPixels()).clone();
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
			int[] pixels = (int[]) ip.getPixels();

			if (method.equals("Original")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}
			// aufgrund der addition und subtraktion mit den ergebnissen, ist es vom vorteil externe methoden zu verwenden
			// welche die veränderten/bearbeiteten Ergebnisse zurückliefern, um diese dann weiterzuverwenden

			if (method.equals("Weichgezeichnetes Bild")) {
				// das pixelarray speichert dass durch die externe methode veränderte array/bild und kann genutzt werden
				pixels = (weichzeichnen(pixels));
			}
			if (method.equals("Hochpassgefiltertes Bild")) {
				//hier kann nun das wichgezeichnete Bild verwendet werden um damit zu rechnen
				int[]weichgezeichnet = weichzeichnen(pixels);
				// die methode nutzt die orginal pixel und die weichgezeichneten als input
				pixels = hochPassFilter(pixels, weichgezeichnet);
			}
			if (method.equals("Bild mit verstärkten Kanten")) {
				//hier kann nun die hochpassgefilterte pixelveränderung genutzt werden
				int[]weichgezeichnet = weichzeichnen(pixels);
				int[]hochpassgefiltert = hochPassFilter(pixels, weichgezeichnet);
				pixels = verstaerkteKanten(pixels, hochpassgefiltert);
			}
		}

		private int[] weichzeichnen(int[] orginal) {
			int[] weich = orginal;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int ursprPos = y * width + x;
					//damit die werte nicht verloren gehen
					int ursprXWert = x, ursprYWert = y;
					//skalierung, damit die Helligkeit unverändert bleibt
					int skalierung = 0;
					//zum zwischenspeichern der veränderten werte
					int r = 0, g = 0, b = 0;
					//der erste Loop spricht die werte jeweils rechts und links vom kernel/mittelpunkt
					for (int nachbarVonX = -1; nachbarVonX <= 1; nachbarVonX++) {
						// der zweite loop um das 3x3 zu realisieren/addiert bzw zieht die width ab für den wert darüber
                        // bzw darunter
						for (int nachbarY = -width; nachbarY <= width; nachbarY += width) {
							// "<=" weil sonst eine Verschiebung des Bildes stattfinden würde
							skalierung++; // wird mit jedem durchlauf erhöht damit durch den entsprechenden wert später
							//dividiert werden kann, damit es zu keiner Helligkeitsänderung kommt
							int thresholdX = nachbarVonX;// wichtig um die werte nicht zu verlieren und zum rückführen
							int thresholdY = nachbarY;  // dieser innerhalb der Schleife
							//die Zustände repräsentieren die Szenarien wenn ein benachbarter Pixel außerhalb liegt
							// also rechts außerhalb, links außerhalb etc.
							var zustand1 = nachbarVonX == -1 && x == 0; // links außerhalb
							if (zustand1) {
								nachbarVonX = 0; // auf Kernel zurücksetzen
							}
							var zustand2 = nachbarVonX == 1 && x == width - 1; // rechts außerhalb
							if (zustand2) {
								nachbarVonX = 0; // auf kernel zurücksetzten
							}
							var zustand3 = nachbarY == -width && y == 0; // oben außerhalb
							if (zustand3) {
								nachbarY = (height-1)+(width-1); // auf den Kernel (Wert darunter) zurücksetzen
							}
							var zustand4 = nachbarY == width && y == height - 1; // unten außerhalb
							if (zustand4) {
								nachbarY = (height-1)-(width-1); // auf den Kernel (Wert darüber) zurücksetzen
							}
							int positionNachDurchlauf = nachbarY + ursprPos + nachbarVonX;
							nachbarVonX = thresholdX;
							nachbarY = thresholdY;
							r += (origPixels[positionNachDurchlauf] >> 16) & 0xFF;
							g += (origPixels[positionNachDurchlauf] >> 8) & 0xFF;
							b += (origPixels[positionNachDurchlauf]) & 0xFF;
						}
					}
					r /= skalierung;
					g /= skalierung;        // zum Schluss jeweils Berechnung des Mittelwertes
					b /= skalierung;
					// begrenzung nicht nötig, da "out of border" schon oben behandelt wird

					weich[ursprPos] = (0xFF) << 24 | r << 16 | g << 8 | b;

					y = ursprYWert;
					x = ursprXWert;
				}
			}
			return weich;
		}

		private int[] hochPassFilter(int[]orginal, int[] weich) {
			// der hochpassfilter entsteht wenn die werte der weichzeichnung von den orginalen abgezogen werden
			// folglich braucht man 2 x die rgb werte
			int[] weichzeichnung = weich;
			int[] outputarray = orginal; // zu beginn noch das orginal
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					//Zuerst die orginalen rbg werte aus dem orginalen Pixel Array extrahieren
					int argbOrginal = origPixels[pos];
					int rotOrginal = (argbOrginal >> 16) & 0xFF;
					int gruenOrginal = (argbOrginal >> 8) & 0xFF;
					int blauOrginal = (argbOrginal) & 0xFF;
					// dann die werte vom Weichgezeichneten pixelarray welches als input Variable der Methode vorliegt
					int argbWeich = weichzeichnung[pos];
					int rotWeich = (argbWeich >> 16) & 0xFF;
					int gruenWeich = (argbWeich >> 8) & 0xFF;
					int blauWeich = (argbWeich) & 0xFF;
					//nun muss nur noch die subtraktion durchgefürt werden
					int r = rotOrginal - rotWeich +128;
					int g = gruenOrginal - gruenWeich +128; // dadurch das beim HPF nur hohe Frequenzen enthalten sind
					int b = blauOrginal - blauWeich +128;	// erkennt man nicht viel -> +128 um es sichtlich zu machen


					outputarray[pos]= 0xFF << 24|r<<16|g<<8|b;
				}


			}
			return outputarray;
		}
		private int[] verstaerkteKanten(int[]orginal, int[] hochpass) {
			// verstärkte Kanten treten auf wenn man den hochpassfilter zum orginal Bild hinzu addiert
			// folglich werden auch hier wieder zwei arrays benötigt

			int[] hochpassfilter = hochpass;
			int[] outputarray = orginal; // zu beginn noch das orginal
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					//Zuerst die orginalen rbg werte, kann vom Hochpassfilter übernommen werden
					int argbOrginal = origPixels[pos];
					int rotOrginal = (argbOrginal >> 16) & 0xFF;
					int gruenOrginal = (argbOrginal >> 8) & 0xFF;
					int blauOrginal = (argbOrginal) & 0xFF;
					// dann die werte vom hochpassgefilterten pixel Array/Bild, dies ist hier der Input der Methode
					int argbHochpass = hochpassfilter[pos];
					int rotHochpass = (argbHochpass >> 16) & 0xFF;
					int gruenHochpass = (argbHochpass >> 8) & 0xFF;
					int blauHochpass = (argbHochpass) & 0xFF;
					//nun müssen die Werte nur noch addiert werden
					int r = rotOrginal + rotHochpass -128;		// da beim Hochpassfilter +128 addiert wurde damit es
					int g = gruenOrginal + gruenHochpass -128; 	// erkennbar bleibt und nicht zu dunkel ist, muss diese
					int b = blauOrginal + blauHochpass -128;	// helligkeitsänderung hier nun wieder rückgängig
																// gemacht werden

					// beim ersten mal ausführen traten Pixelfehler auf, darum hier noch die Begrenzung
					r = Math.max(0, Math.min(r, 255));
					g = Math.max(0, Math.min(g, 255));
					b = Math.max(0, Math.min(b, 255));

					outputarray[pos]= 0xFF << 24|r<<16|g<<8|b;
				}
			}
			return outputarray;
		}

	}// CustomWindow inner clas
}