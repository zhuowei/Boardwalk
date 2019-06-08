package net.zhuoweizhang.boardwalk;

import android.content.Context;
import android.opengl.*;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;

public class BoardwalkGLSurfaceView extends GLSurfaceView {
	/**
	 * Standard View constructor. In order to render something, you
	 * must call {@link #setRenderer} to register a renderer.
	 */
	public BoardwalkGLSurfaceView(Context context) {
		super(context);
	}

	/**
	 * Standard View constructor. In order to render something, you
	 * must call {@link #setRenderer} to register a renderer.
	 */
	public BoardwalkGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("Surface destroyed!");
	}
}
