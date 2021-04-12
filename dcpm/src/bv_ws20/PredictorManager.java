package bv_ws20;

public class PredictorManager {

    private float MSE = 0; // dont know how to calculate it

    public enum FilterType {
        A("A (horizontal)"),
        B("B (vertical)"),
        C("C (diagonal)"),
        ABC("A+B-C"),
        adaptive("adaptive");

        private final String name;
        FilterType(String s) { name = s; }
        public String toString() { return this.name; }
    }

    public void horizontal (RasterImage originalImage, RasterImage predictionImage, RasterImage reconstructedImage, float qValue) {
        //loops over width and height to access all Pixels
        for (int x = 0; x < originalImage.width; x++) {
            for(int y = 0; y < originalImage.height; y++) {

                //two positions -> the current and the one before => last 2
                int position = y * originalImage.width + x;
                int positionA = position - 1; //pixel left next to the current

                //if outside the image -> Prädiktor = 128
                int colorA = 128;

                //check whether the values are inside the borders
                if(pixelIsInBorders(position, positionA, originalImage.argb.length)){
                    colorA = originalImage.argb[positionA] & 0xff;
                }

                int colorX = originalImage.argb[position] & 0xff;

                //prediction error
                int e = colorX - colorA;

                //quantization (?)
                int qIndex = Math.round(e/qValue); //quantization index
                e = Math.round(qIndex*qValue); //quantized error

                //predicted Image
                int preOutput = colorIsInBorders(e + 128);
                predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;

                //reconstructed Image
                int recOutput = colorIsInBorders(((colorA + e) & 0xff));
                reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

            }
        }

    }

    public void vertical (RasterImage originalImage, RasterImage predictionImage, RasterImage reconstructedImage, float qValue) {

        //loops over width and height to access all Pixels
        for (int x = 0; x < originalImage.width; x++) {
            for(int y = 0; y < originalImage.height; y++) {

                //two positions -> the current and the one before => last 2
                int position = y * originalImage.width + x;
                int positionB = position - originalImage.width; //pixel above the current

                //if outside the image -> Prädiktor = 128
                int colorB = 128;

                //check whether the values are inside the borders
                if(pixelIsInBorders(position, positionB, originalImage.argb.length)){
                    colorB = originalImage.argb[positionB] & 0xff;
                }

                int colorX = originalImage.argb[position] & 0xff;

                //prediction error
                int e = colorX - colorB;

                //quantization (?)
                int qIndex = Math.round(e/qValue); //quantization index
                e = Math.round(qIndex*qValue); //quantized error

                //predicted Image
                int preOutput = colorIsInBorders(e + 128);
                predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;

                //reconstructed Image
                int recOutput = colorIsInBorders(((colorB + e) & 0xff));
                reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

            }
        }
    }

    public void diagonal (RasterImage originalImage, RasterImage predictionImage, RasterImage reconstructedImage, float qValue) {

        //loops over width and height to access all Pixels
        for (int x = 0; x < originalImage.width; x++) {
            for(int y = 0; y < originalImage.height; y++) {

                //two positions -> the current and the one before => last 2
                int position = y * originalImage.width + x;
                int positionC = position - originalImage.width-1; //pixel left above the current

                //if outside the image -> Prädiktor = 128
                int colorC = 128;

                //check whether the values are inside the borders
                if(pixelIsInBorders(position, positionC, originalImage.argb.length)){
                    colorC = originalImage.argb[positionC] & 0xff;
                }

                int colorX = originalImage.argb[position] & 0xff;

                //prediction error
                int e = colorX - colorC;

                //quantization (?)
                int qIndex = Math.round(e/qValue); //quantization index
                e = Math.round(qIndex*qValue); //quantized error

                //predicted Image
                int preOutput = colorIsInBorders(e + 128);
                predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;

                //reconstructed Image
                int recOutput = colorIsInBorders((colorC + e) & 0xff);
                reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

            }
        }
    }

    public void abc (RasterImage originalImage, RasterImage predictionImage, RasterImage reconstructedImage, float qValue) {

        //loops over width and height to access all Pixels
        for (int x = 0; x < originalImage.width; x++) {
            for(int y = 0; y < originalImage.height; y++) {

                //pixel positions
                int position = y * originalImage.width + x;
                int positionA = position - 1;
                int positionB = position - originalImage.width;
                int positionC = position - originalImage.width-1;

                //if outside the image -> Prädiktor = 128
                int colorA = 128;
                int colorB = 128;
                int colorC = 128;

                //check whether the values are inside the borders
                if(pixelIsInBorders(position, positionA, originalImage.argb.length)){
                    colorA = originalImage.argb[positionA] & 0xff;
                }
                if(pixelIsInBorders(position, positionB, originalImage.argb.length)){
                    colorB = originalImage.argb[positionB] & 0xff;
                }
                if(pixelIsInBorders(position, positionC, originalImage.argb.length)){
                    colorC = originalImage.argb[positionC] & 0xff;
                }

                int colorX = originalImage.argb[position] & 0xff;

                //prediction error
                int e = colorX - (colorA+colorB-colorC);

                //quantization (?)
                int qIndex = Math.round(e/qValue); //quantization index
                e = Math.round(qIndex*qValue); //quantized error

                //predicted Image
                int preOutput = colorIsInBorders(e + 128);
                predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;

                //reconstructed Image
                int recOutput = colorIsInBorders(((colorA+colorB-colorC) + e) & 0xff);
                reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

            }
        }

    }

    public void adaptive (RasterImage originalImage, RasterImage predictionImage, RasterImage reconstructedImage, float qValue) {

        //loops over width and height to access all Pixels
        for (int x = 0; x < originalImage.width; x++) {
            for(int y = 0; y < originalImage.height; y++) {

                //pixel positions
                int position = y * originalImage.width + x;
                int positionA = position - 1;
                int positionB = position - originalImage.width;
                int positionC = position - originalImage.width-1;

                //if outside the image -> Prädiktor = 128
                int colorA = 128;
                int colorB = 128;
                int colorC = 128;

                //check whether the values are inside the borders
                if(pixelIsInBorders(position, positionA, originalImage.argb.length)){
                    colorA = originalImage.argb[positionA] & 0xff;
                }
                if(pixelIsInBorders(position, positionB, originalImage.argb.length)){
                    colorB = originalImage.argb[positionB] & 0xff;
                }
                if(pixelIsInBorders(position, positionC, originalImage.argb.length)){
                    colorC = originalImage.argb[positionC] & 0xff;
                }

                int colorX = originalImage.argb[position] & 0xff;

                //prediction error
                int e;

                // |A-C| < |B-C| -> P = B
                if(Math.abs(colorA-colorC) < Math.abs(colorB-colorC)){

                    e = colorX-colorB;
                    int qIndex = Math.round(e/qValue); //quantization index
                    e = Math.round(qIndex*qValue); //quantized error
                    int preOutput = colorIsInBorders(e + 128);
                    predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;
                    int recOutput = colorIsInBorders((colorB + e) & 0xff);
                    reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

                }else{           // P = A

                    e = colorX-colorA;
                    int qIndex = Math.round(e/qValue); //quantization index
                    e = Math.round(qIndex*qValue); //quantized error
                    int preOutput = colorIsInBorders(e + 128);
                    predictionImage.argb[position] = 0xff << 24 | preOutput << 16 | preOutput << 8 | preOutput;
                    int recOutput = colorIsInBorders((colorA + e) & 0xff);
                    reconstructedImage.argb[position] = 0xff << 24 | recOutput << 16 | recOutput << 8 | recOutput;

                }

            }
        }
    }

    private int colorIsInBorders(int color) {
        if(color > 255) color = 255;
        if(color < 0) color = 0;
        return color;
    }

    private boolean pixelIsInBorders(int currentPosition, int otherPosition, int length) {
        return (currentPosition >= 0 && otherPosition >= 0) && (currentPosition < length && otherPosition < length);
    }

    public float MSE(){
        return MSE;
    }

}
