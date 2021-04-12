// BV Ue3 WS2019/20 Vorgabe
//
// Copyright (C) 2019 by Klaus Jung
// All rights reserved.
// Date: 2019-04-26

package bv_ss20;

import java.io.File;

import bv_ss20.MorphologicFilter.FilterType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class MorphologicFilterAppController {
	
	private static final String initialFileName = "rhino_part.png";
	private static File fileOpenPath = new File(".");
	
	private static final MorphologicFilter filter = new MorphologicFilter();
	private int threshold;
	private double radius;
	
    @FXML
    private Slider thresholdSlider;

    @FXML
    private Label thresholdLabel;

    @FXML
    private ComboBox<FilterType> filterSelection;

    @FXML
    private Slider radiusSlider;

    @FXML
    private Label radiusLabel;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView binaryImageView;

    @FXML
    private ImageView filteredImageView;

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
			new RasterImage(selectedFile).setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void thresholdChanged() {
    	threshold = (int)thresholdSlider.getValue();
    	thresholdLabel.setText("" + threshold);
    	processImages();
    }
    
    @FXML
    void filterChanged() {
    	processImages();
    }
    
    @FXML
    void kernelChanged() {
    	radius = Math.floor(radiusSlider.getValue() * 10.0) / 10.0;
    	radiusLabel.setText(String.format("%.1f", radius));
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		filterSelection.getItems().addAll(FilterType.values());
		filterSelection.setValue(FilterType.DILATION);
		
		// initialize parameters
		thresholdChanged();
		kernelChanged();
		
		// load and process default image
		new RasterImage(new File(initialFileName)).setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage binaryImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		
		filter.copy(origImg, binaryImg);
		binaryImg.binarize(threshold);
		
		switch(filterSelection.getValue()) {
		case DILATION:
			filter.dilation(binaryImg, filteredImg, radius);
			break;
		case EROSION:
			filter.erosion(binaryImg, filteredImg, radius);
			break;
		default:
			break;
		}
		
		binaryImg.setToView(binaryImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
