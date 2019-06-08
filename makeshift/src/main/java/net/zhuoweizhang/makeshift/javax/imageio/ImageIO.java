package net.zhuoweizhang.makeshift.javax.imageio;

import java.io.*;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.zhuoweizhang.makeshift.java.awt.image.BufferedImage;
import net.zhuoweizhang.makeshift.java.awt.image.RenderedImage;

public class ImageIO {
	public static void setUseCache(boolean set) {
	}

	public static BufferedImage read(InputStream is) throws IOException {
		Bitmap bmp = BitmapFactory.decodeStream(is);
		return makeBufferedImage(bmp);
	}

	public static BufferedImage read(File input) throws IOException {
		Bitmap bmp = BitmapFactory.decodeFile(input.getAbsolutePath());
		return makeBufferedImage(bmp);
	}

	public static BufferedImage read(URL input) throws IOException {
		InputStream is = input.openStream();
		Bitmap bmp = BitmapFactory.decodeStream(is);
		is.close();
		return makeBufferedImage(bmp);
	}

	private static BufferedImage makeBufferedImage(Bitmap bmp) {
		// Every one of our BufferedImages is backed with an Android Bitmap
		return new BufferedImage(bmp);
	}

	public static boolean write(RenderedImage im, String formatName, File output) {
		System.out.println("ImageIO.write stub " + output);
		return true;
	}
}
