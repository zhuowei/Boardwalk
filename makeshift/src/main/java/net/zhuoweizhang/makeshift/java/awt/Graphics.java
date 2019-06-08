package net.zhuoweizhang.makeshift.java.awt;
import net.zhuoweizhang.makeshift.java.awt.image.*;

import android.graphics.Canvas;

public class Graphics {

	private BufferedImage bufImage;
	private Canvas androidCanvas;

	public Graphics(BufferedImage bufImage) {
		this.bufImage = bufImage;
		this.androidCanvas = new Canvas(bufImage.getAndroidBitmap());
	}

	public void setColor(Color color) {
	}

	public void fillRect(int x, int y, int width, int height) {
	}

	public void drawString(String s, int x, int y) {
	}

	public void dispose() {
	}

	public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
		if (!(image instanceof BufferedImage)) return true;
		androidCanvas.drawBitmap(((BufferedImage) image).getAndroidBitmap(), x, y, null);
		return true;
	}
}
