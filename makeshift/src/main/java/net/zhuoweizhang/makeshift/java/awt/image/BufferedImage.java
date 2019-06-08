package net.zhuoweizhang.makeshift.java.awt.image;

import android.graphics.Bitmap;

import net.zhuoweizhang.makeshift.java.awt.Graphics;
import net.zhuoweizhang.makeshift.java.awt.Graphics2D;
import net.zhuoweizhang.makeshift.java.awt.Image;

public class BufferedImage extends Image implements RenderedImage {
	public static final int TYPE_INT_ARGB = 2;
	private Bitmap bitmap;
	public BufferedImage(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public BufferedImage(int width, int height, int imageType) {
		this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}

	public int getWidth() {
		return bitmap.getWidth();
	}

	public int getHeight() {
		return bitmap.getHeight();
	}

	public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
		if (rgbArray == null) {
			rgbArray = new int[offset + (scansize * h)];
		}
		bitmap.getPixels(rgbArray, offset, scansize, startX, startY, w, h);
		return rgbArray;
	}

	public Graphics getGraphics() {
		return new Graphics2D(this);
	}

	public Graphics2D createGraphics() {
		return new Graphics2D(this);
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
		System.out.println("Setting RGB stub");
	}

	public WritableRaster getRaster() {
		return new WritableRaster(this);
	}

	public Bitmap getAndroidBitmap() {
		return this.bitmap;
	}

	public int getType() {
		return TYPE_INT_ARGB;
	}
}
