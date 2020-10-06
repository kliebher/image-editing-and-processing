
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

import javax.swing.BorderFactory;
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


    public static void main(String args[]) {
        //new ImageJ();
        IJ.open("src/orchid.jpg");

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


    class CustomWindow extends ImageWindow implements ChangeListener {

        private JSlider jSliderBrightness;
        private JSlider jSliderSaturation;
        private JSlider jSliderContrast;
        private JSlider jSliderHue;
        private double brightness;
        private double contrast = 1.0;
        private double saturation = 1.0;
        private double hue;
        // given on moddle
        double[] contrastIntervall = {0, 0.2, 0.4, 0.6, 0.8, 1, 2, 4, 6, 8, 10}; // 11, index 0-10
        double[] saturationIntervall = {0, 0.25, 0.5, 0.75, 1, 2, 3, 4, 5}; // 9, index 0-8

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", -28, 228, 100);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 10, 5);
            jSliderSaturation = makeTitledSilder("Sättigung", 0, 8, 4);
            jSliderHue = makeTitledSilder("Hue", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);

            add(panel);

            pack();
        }

        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {

            JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
            Dimension preferredSize = new Dimension(width, 50);
            slider.setPreferredSize(preferredSize);
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
            slider.setMajorTickSpacing((maxVal - minVal)/10 );
            slider.setPaintTicks(true);
            slider.addChangeListener(this);

            return slider;
        }

        private void setSliderTitle(JSlider slider, String str) {
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
        }

        public void stateChanged( ChangeEvent e ){
            JSlider slider = (JSlider)e.getSource();

            if (slider == jSliderBrightness) {
                brightness = (slider.getValue()-100);
                String str = "Helligkeit " + brightness;
                setSliderTitle(jSliderBrightness, str);
            }
            if (slider == jSliderContrast) {
                int value = slider.getValue();
                contrast = contrastIntervall[value]; // to relate on the index
                String str = "Kontrast : " + contrastIntervall[value];
                setSliderTitle(jSliderContrast, str);
            }
            if (slider == jSliderSaturation) {
                int value = slider.getValue();
                saturation = saturationIntervall[value]; // to relate on the index
                String str = "Sättigung : " + saturationIntervall[value];
                setSliderTitle(jSliderSaturation, str);
            }
            if (slider == jSliderHue) {
                hue = slider.getValue(); // da die slider values von 0-360° gehen
                String str = "Hue :  " + hue;
                setSliderTitle(jSliderHue, str);
            }

            changePixelValues(imp.getProcessor());

            imp.updateAndDraw();
        }


        private void changePixelValues(ImageProcessor ip) {

            // Array fuer den Zugriff auf die Pixelwerte
            int[] pixels = (int[])ip.getPixels();

            for (int y=0; y<height; y++) {
                for (int x=0; x<width; x++) {
                    int pos = y*width + x;
                    int argb = origPixels[pos];  // Lesen der Originalwerte

                    int r = (argb >> 16) & 0xff;
                    int g = (argb >>  8) & 0xff;
                    int b =  argb        & 0xff;


                    // anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
                    // die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
                    //U = (B - Y) * 0.493
                    //V = (R - Y) * 0.877
                    // [x']  =  [cos q  -sin q] * [x] = (Math.cos(Math.radians(angle)))+(-Math.sin(Math.radians(angle))) // hue
                    // [y']  =  [sin q  cos q ]   [y]
                    // x = U ; y = V ; z = Y
                    //double Y = 0.299 * r + 0.587 * g + 0.114 * b;
                    // RGB -> YUV
                    // = saturation, it is defined through the way from the middle (Y) to the edge (a,b)
                    double Y = (int) (0.299 * r + 0.587 * g + 0.114 * b) + brightness;

                    //Formel mit Threshold halte ich für sinnvoll
                    if(Y>=127){
                        double t =  Y-127;
                        double compressed = t*contrast;
                        Y = 127+compressed;
                    }
                    if(Y<127){
                        double t = 127-Y;
                        double kompressed = t*contrast;
                        Y = 127-kompressed;
                    }
                    // = contrast, depending on the Y-Value -> (0 <- low contrast <- 127(middle) -> high contrast-> 255)
                    //YUV -> RGB *neu

                    double U =  saturation * (Math.cos(Math.toRadians(hue))+(-Math.sin(Math.toRadians(hue)))) * (-0.168736 * r - 0.331264 * g + 0.5 * b);
                    double V =  saturation * (Math.sin(Math.toRadians(hue))+( Math.cos(Math.toRadians(hue)))) *  (0.5 * r - 0.418688 * g - 0.081312 * b);

                    //Umwandlung von YUV zu RGB
                    int rn = (int) Math.round(Y + 1.402 * V);
                    int gn = (int) Math.round(Y - 0.3441 * U - 0.7141 * V);
                    int bn = (int) Math.round(Y + 1.772 * U);

                    //int rn = (int) (r + brightness);
                    //int gn = (int) (g + brightness); // old
                    //int bn = (int) (b + brightness);

                    // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                    if(rn>255){rn=255;}
                    if(gn>255){gn=255;}
                    if(bn>255){bn=255;}

                    if(rn<0){rn=0;}
                    if(gn<0){gn=0;}
                    if(bn<0){bn=0;}

                    pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                }
            }
        }

    } // CustomWindow inner class
}

    