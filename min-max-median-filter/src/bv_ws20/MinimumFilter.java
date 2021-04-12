package bv_ws20;

public class MinimumFilter implements Filter{
    private RasterImage destinationImage;
    private RasterImage sourceImage;
    private int kernelWidth;
    private int kernelHeight;

    @Override
    public void setSourceImage(RasterImage sourceImage) {
        this.sourceImage = sourceImage;
    }

    @Override
    public void setDestinationImage(RasterImage destinationImage) {
        this.destinationImage = destinationImage;
    }

    @Override
    public void setKernelWidth(int kernelWidth) {
        this.kernelWidth = kernelWidth;
    }

    @Override
    public void setKernelHeight(int kernelHeight) {
        this.kernelHeight = kernelHeight;
    }

    @Override
    public void apply() {
        //array mit Werten des Kernel zum späteren vergleichen
        int[] kernel = new int[kernelWidth * kernelHeight];
        //kernelmittelpunkt, je nach eingegebenem wert
        int kernelRange = (kernelHeight / 2); // int damit -> (1) 0.5 = 0; (3) 1.5 = 1....
        //System.out.println(kernelHeight+" * "+kernelWidth+" = "+kernelMiddle);


        //zwei schleifen zum ansprechen jedes pixels des bildes
        for (int y = 0; y < sourceImage.height; y++) {
            for (int x = 0; x < sourceImage.width; x++) {
                int pos = y * sourceImage.width + x;
                int index = 0;

                // kernel wird zeilenweise von oben nach unten und zeilenintern von links nach rechts ausgelesen
                for (int nachbarY  = -kernelRange; nachbarY  <= kernelRange; nachbarY ++) {
                    for (int nachbarX = -kernelRange; nachbarX <= kernelRange; nachbarX++) {

                        //begrenzung
                        int resetValueX = 0;
                        int resetValueY = 0;
                        //resetValue nimmt den wert an um den die border überschritten wurde
                        if (nachbarX+x<0) {
                            resetValueX = nachbarX;
                        }
                        if (nachbarX+x>= sourceImage.width) {
                            resetValueX = nachbarX;
                        }
                        if (nachbarY+y<0){
                            resetValueY = nachbarY;
                        }
                        if (y + nachbarY >= sourceImage.height){
                            resetValueY = nachbarY;
                        }

                        int currentY = y + nachbarY - resetValueY;
                        int currentX = x + nachbarX - resetValueX;

                        // aktuelle pixelfarbbe = argb[y * width + x]
                        int currentPixelColor = (sourceImage.argb[currentY * sourceImage.width + currentX] >> 16) & 0xff;

                        kernel[index] = currentPixelColor;
                        index++;
                    }
                }
                index = 0; // zurücksetzen nach fertigem durchlauf

                //Werte vergleichen und den kleinsten in minimum speichern
                int minimum = kernel[0];
                for (int color : kernel) {
                    if (color < minimum) {
                        minimum = color;
                    }
                }
                destinationImage.argb[pos] = (0xff << 24) | (minimum << 16) | (minimum << 8) | (minimum);
            }

        }

    }
}

