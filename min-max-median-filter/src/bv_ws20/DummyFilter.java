// BV Ue1 WS2020/21 Vorgabe
//
// Copyright (C) 2019-2020 by Klaus Jung
// All rights reserved.
// Date: 2020-10-08

package bv_ws20;

public class DummyFilter implements Filter {

	@Override
	public void setSourceImage(RasterImage sourceImage) {
	}

	@Override
	public void setDestinationImage(RasterImage destinationImage) {
	}

	@Override
	public void setKernelWidth(int kernelWidth) {
	}

	@Override
	public void setKernelHeight(int kernelHeight) {
	}

	@Override
	public void apply() {
		// this is a dummy filter that does nothing
	}

}
