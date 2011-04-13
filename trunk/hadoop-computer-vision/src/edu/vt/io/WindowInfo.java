package edu.vt.io;

public class WindowInfo {
	
	// Size of parent image
	private int width;
	private int height;
	
	// Location of window in parent image
	private int xOffset;
	private int yOffset;
	
	public WindowInfo(){
		width = -1;
		height = -1;
		
		xOffset = -1;
		yOffset = -1;
	}
	
	public WindowInfo(int xOffset, int yOffset, int height, int width){
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public boolean isValid(){
		if (width < 0 || height < 0 || xOffset < 0 || yOffset < 0){
			return false;
		}
		
		return true;
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
