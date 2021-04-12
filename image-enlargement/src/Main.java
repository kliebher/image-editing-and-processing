import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


class Main implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}
        return DOES_RGB+NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }
    protected ImagePlus imp; // ImagePlus object

    //String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

    public static void main(String args[]) {
        ImageJ ij = new ImageJ();
        ij.exitWhenQuitting(true);
        IJ.open("src/component.jpg");
        Main sc = new Main();
        sc.imp = IJ.getImage();
        ImageProcessor ip = sc.imp.getProcessor();
        sc.run(ip);
    }


    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
        gd.addNumericField("Hoehe:",500,0);
        gd.addNumericField("Breite:",400,0);

        gd.showDialog();

        int methode = 0;
        String s = gd.getNextChoice();
        if (s.equals("Kopie")) methode = 1;
        if (s.equals("Pixelwiederholung")) methode = 2;
        if (s.equals("Bilinear")) methode = 3;


        int height_n = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n =  (int)gd.getNextNumber();

        int width  = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[])ip.getPixels();
        int[] pix_n = (int[])ip_n.getPixels();

        if(methode == 1){ //Kopie
            // Schleife ueber das neue Bild
            for (int y_n=0; y_n<height_n; y_n++) {
                for (int x_n=0; x_n<width_n; x_n++) {
                    int y = y_n;
                    int x = x_n;

                    if (y < height && x < width) {
                        int pos_n = y_n*width_n + x_n;
                        int pos  =  y  *width   + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }
            }
        }
        if(methode == 2){ //Pixelwiederholung

            // als erstes wird das verhältnis zwischen den alten und neuen werten gebildet
            double rX = (double)(width-1)/(double)(width_n-1);
            double rY = (double)(height-1)/(double)(height_n-1);
            // Schleife ueber das neue Bild
            for (int y_n=0; y_n<height_n; y_n++) {
                for (int x_n=0; x_n<width_n; x_n++) {
                    //rundung zum nächstgrößeren oder kleineren wert (nearest neighbor)
                    int origX = (int) Math.round(x_n*rX);
                    int origY = (int) Math.round(y_n*rY);
                    int posNew = y_n*width_n+x_n;
                    int nPos = origY*width+origX;
                    //zurückschreiben der werte
                    pix_n[posNew] = pix[nPos];
                }
            }
        }
        if(methode == 3){ //Bilinear
            
            double rX = (double)(width-1)/(double)(width_n-1);
            double rY = (double)(height-1)/(double)(height_n-1);
            int rgbA, rgbB, rgbC, rgbD;

            // Schleife ueber das neue Bild
            for (int y_n=0; y_n<height_n; y_n++) {
                for (int x_n=0; x_n<width_n; x_n++) {
                    int x = (int) Math.round(rX * x_n);
                    int y = (int) Math.round(rY * y_n);
                    int pos = y*width+x;
                    int posNew = y_n*width_n+x_n;
                    //P(x+h,y+v) , P = (x+hy+v)
                    double h = rX * x_n - x;
                    double v = rY * y_n - y;
                    // als erstes die einzelnen farbwerte auslesen
                    rgbA = pix[pos];
                    if (x == width-1) rgbB = pix[pos];
                    else rgbB = pix[y*width+x+1];
                    if (y == height-1) rgbC = pix[pos];
                    else rgbC = pix[(y+1)*width+x];
                    if (x == width-1 && y == height-1) rgbD = pix[pos];
                    else if (x == width-1 && y != height-1) rgbD = pix[(y+1)*width+x];
                    else if (x != width-1 && y == height-1) rgbD = pix[y*width+x+1];
                    else rgbD = pix[(y+1)*width+x+1];

                    // dann die farbwerte für A, B, C & D extrahieren
                    int rotA = (rgbA >> 16) & 0xff;
                    int gruenA = (rgbA >> 8) & 0xff;
                    int blauA = rgbA & 0xff;

                    int rotB = (rgbB >> 16) & 0xff;
                    int gruenB = (rgbB >> 8) & 0xff;
                    int blauB = rgbB & 0xff;

                    int rotC = (rgbC >> 16) & 0xff;
                    int gruenC = (rgbC >> 8) & 0xff;
                    int blauC = rgbC & 0xff;

                    int rotD = (rgbD >> 16) & 0xff;
                    int gruenD = (rgbD >> 8) & 0xff;
                    int blauD = rgbD & 0xff;

                    // und in Formel einsetzen
                    // P = A*(1-h)*(1-v)+B*h*(1-v)+C*(1-h)*v+D*h*v
                    int rN = (int) Math.round(rotA*(1-h)*(1-v) + rotB*h*(1-v) + rotC*(1-h)*v + rotD*h*v);
                    int gN = (int) Math.round(gruenA*(1-h)*(1-v) + gruenB*h*(1-v) + gruenC*(1-h)*v + gruenD*h*v);
                    int bN = (int) Math.round(blauA*(1-h)*(1-v) + blauB*h*(1-v) + blauC*(1-h)*v + blauD*h*v);

                    // werte begrenzen und zurückschreiben
                    if (rN > 255) rN = 255;
                    else if (rN < 0) rN = 0;
                    if (gN > 255)  gN = 255;
                    else if (gN < 0) gN = 0;
                    if (bN > 255) bN = 255;
                    else if (bN < 0) bN = 0;

                    pix_n[posNew] = (0xff<<24) | (rN<<16) | (gN<<8) | (bN);

                }
            }
        }


        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    void showAbout() {
        IJ.showMessage("");
    }
}

