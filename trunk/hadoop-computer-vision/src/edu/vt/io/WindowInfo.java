package edu.vt.io;

public class WindowInfo {
	
	// Size of parent image
	private int width;
	private int height;
	
	// Location of window in parent image
	private int xOffset;
	private int yOffset;
	
	public WindowInfo(int width, int height, int xOffset, int yOffset){
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
}
