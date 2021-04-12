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

public class FilterAppController {
	
	private static final String initialFileName = "lena_klein.jpg";
	private static File fileOpenPath = new File(".");
	
	public enum FilterType { 
		MIN("Minimum Filter"), 
		MAX("Maximum Filter"),
		MEDIAN("Median Filter");
		
		private final String name;       
	    private FilterType(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	private double noiseQuantity;
	private int noiseStrength;
	private int kernelSize;
	
    @FXML
    private Slider noiseQuantitySlider;

    @FXML
    private Label noiseQuantityLabel;

    @FXML
    private Slider noiseStrengthSlider;

    @FXML
    private Label noiseStrengthLabel;

    @FXML
    private Slider kernelSizeSlider;

    @FXML
    private Label kernelSizeLabel;

    @FXML
    private Label kernelTitleLabel;

    @FXML
    private ComboBox<FilterType> filterSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView noisyImageView;

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
			RasterImage img = new RasterImage(selectedFile);
			img.convertToGray();
			img.setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void filterChanged() {
    	processImages();
    }

    @FXML
    void noiseQuantityChanged() {
    	noiseQuantity = noiseQuantitySlider.getValue();
    	noiseQuantityLabel.setText(String.format("%.0f %%", noiseQuantity * 100));
    	processImages();
    }
    
    @FXML
    void noiseStrengthChanged() {
    	noiseStrength = (int)noiseStrengthSlider.getValue();
    	noiseStrengthLabel.setText("" + noiseStrength);
    	processImages();
    }
    
    @FXML
    void kernelSizeChanged() {
    	kernelSize = (int)kernelSizeSlider.getValue() | 1; // ensure odd integer value
    	kernelSizeLabel.setText(kernelSize + " x " + kernelSize);
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		filterSelection.getItems().addAll(FilterType.values());
		filterSelection.setValue(FilterType.MIN);
		
		// initialize parameters
		noiseQuantityChanged();
		noiseStrengthChanged();
		kernelSizeChanged();
		filterChanged();
		
		// load and process default image
		RasterImage img = new RasterImage(new File(initialFileName));
		img.convertToGray();
		img.setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		
		RasterImage noisyImg = new RasterImage(origImg); 
		noisyImg.addNoise(noiseQuantity, noiseStrength);

		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		Filter filter = null;

		// TODO: add classes to project for minimum/maximum/median filters that implement the interface Filter

		switch(filterSelection.getValue()) {
		case MIN:
			filter = new MinimumFilter(); // TODO: replace by minimum filter
			break;
		case MAX:
			filter = new MaximumFilter(); // TODO: replace by maximum filter
			break;
		case MEDIAN:
			filter = new MedianFilter(); // TODO: replace by median filter
			break;
		}
		
		if(filter != null) {
			filter.setSourceImage(noisyImg);
			filter.setDestinationImage(filteredImg);
			filter.setKernelWidth(kernelSize);
			filter.setKernelHeight(kernelSize);
			filter.apply();
		}
		
		noisyImg.setToView(noisyImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
