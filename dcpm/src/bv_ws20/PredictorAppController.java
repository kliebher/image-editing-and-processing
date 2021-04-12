// BV Ue1 WS2020/21 Vorgabe
//
// Copyright (C) 2019-2020 by Klaus Jung
// All rights reserved.
// Date: 2020-10-08

package bv_ws20;

import java.io.File;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class PredictorAppController {
	
	private static final String initialFileName = "test1.jpg";
	private static File fileOpenPath = new File(".");

	//Image Views

	@FXML
	private ImageView originalImage;

	@FXML
	private ImageView predictionImage;

	@FXML
	private ImageView reconstructedImage;

	//ComboBox

	@FXML
	private ComboBox<PredictorManager.FilterType> predictorSelection;

	//Slider

    @FXML
    private Slider quantizationSlider;

    //Labels

    @FXML
    private Label quantizationLabel;

	@FXML
	private Label originalEntropy;

	@FXML
	private Label predictedEntropy;

	@FXML
	private Label reconstructedEntropy;

	@FXML
	private Label MSE;

    @FXML
    private Label messageLabel;

    @FXML
    void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			RasterImage img = new RasterImage(selectedFile);
			img.convertToGray();
			img.setToView(originalImage);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void predicatorChanged() {
    	processImages();
    }

    
    @FXML
    void quantizationChanged() {
		double quantizationValue = quantizationSlider.getValue();
		quantizationLabel.setText(String.format("%.2f", quantizationValue));
    	processImages();
    }
    

    
	@FXML
	public void initialize() {
		// set combo boxes items
		predictorSelection.getItems().addAll(PredictorManager.FilterType.values());
		predictorSelection.setValue(PredictorManager.FilterType.A);
		
		// initialize parameters
		//noiseQuantityChanged();
		quantizationChanged();
		predicatorChanged();
		
		// load and process default image
		RasterImage img = new RasterImage(new File(initialFileName));
		img.convertToGray();
		img.setToView(originalImage);
		processImages();
	}
	
	private void processImages() {
		if(originalImage.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImage);
		RasterImage predImg = new RasterImage(origImg.width, origImg.height);
		RasterImage recImg = new RasterImage(origImg.width, origImg.height);


		// TODO: add classes to project for minimum/maximum/median filters that implement the interface Filter
		PredictorManager predictor = new PredictorManager();
		switch(predictorSelection.getValue()) {
			case A:
				predictor.horizontal(origImg, predImg, recImg, (float)quantizationSlider.getValue());
				break;
			case B:
				predictor.vertical(origImg, predImg, recImg, (float)quantizationSlider.getValue());
				break;
			case C:
				predictor.diagonal(origImg, predImg, recImg, (float)quantizationSlider.getValue());
				break;
			case ABC:
				predictor.abc(origImg, predImg, recImg, (float)quantizationSlider.getValue());
				break;
			case adaptive:
				predictor.adaptive(origImg, predImg, recImg, (float)quantizationSlider.getValue());
				break;
		}

		predImg.setToView(predictionImage);
		recImg.setToView(reconstructedImage);

		originalEntropy.setText(String.format("%.3f", getEntropy(origImg)));
		predictedEntropy.setText(String.format("%.3f", getEntropy(predImg)));
		reconstructedEntropy.setText(String.format("%.3f", getEntropy(recImg)));
		MSE.setText((" "+ predictor.MSE()));
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}

	public double getEntropy(RasterImage image){
    	int grayLevels = 256;
    	double entropy = 0;
    	int[] histo = new int[grayLevels];
    	int allPixels = image.width*image.height;
    	for(int y = 0; y < image.height; y++){
    		for(int x = 0; x < image.width; x++){
    			int pos = y * image.width + x;
    			int color = (image.argb[pos]) & 0xff;
    			histo[color]++;
			}
		}
    	for(int j = 0; j < grayLevels; j++){
    		if(histo[j] != 0){
    			double p = (double)histo[j]/allPixels;
    			entropy += -1.0 * (p) * log2(p);
			}
		}
    	return entropy;
	}

	private double log2(double x){
    	return (Math.log(x)/Math.log(2));
	}


	

	



}
