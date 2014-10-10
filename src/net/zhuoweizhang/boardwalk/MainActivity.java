package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.net.URL;
import java.lang.reflect.*;
import java.util.*;
import java.security.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;

import dalvik.system.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.AndroidContextImplementation;
import org.lwjgl.opengl.AndroidDisplay;
import org.lwjgl.opengl.AndroidKeyCodes;
import android.opengl.*;
import javax.microedition.khronos.opengles.GL10;

import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.model.*;
import net.zhuoweizhang.boardwalk.util.*;

public class MainActivity extends Activity implements View.OnTouchListener
{
	public static final String VERSION_TO_LAUNCH = "1.7.10";
	public static final String initText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
					"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ";

	private static int[] hotbarKeys = {
		Keyboard.KEY_1, Keyboard.KEY_2,	Keyboard.KEY_3,
		Keyboard.KEY_4, Keyboard.KEY_5,	Keyboard.KEY_6,
		Keyboard.KEY_7, Keyboard.KEY_8, Keyboard.KEY_9};
	private GLSurfaceView glSurfaceView;
	private DisplayMetrics displayMetrics;
	private static String[] libsToRename = {"vecmath", "testcases"};
	private Button forwardButton, jumpButton, primaryButton, secondaryButton;
	private Button debugButton, shiftButton;
	private Button keyboardButton;
	private int scaleFactor = 1;
	private PopupWindow hiddenTextWindow;
	private TextView hiddenTextView;
	private String hiddenTextContents = initText;
	private boolean hiddenTextIgnoreUpdate = true;
	private int guiScale = 1;
	public static final int KEY_BACKSPACE = 14; //WTF lwjgl?

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		/* Preconditions: 
		 * The Launcher Activity should have built the finished dex from the libraries and the Minecraft jar.
		 * The final dex path should be passed in as an Intent.
		 */
		super.onCreate(savedInstanceState);
		LaunchMinecraftTask.setupWorkingDir(this);
		initEnvs();
		if (DalvikTweaks.isDalvik()) {
			DalvikTweaks.setDefaultStackSize(512 * 1024);
		}
		System.loadLibrary("glshim");

		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		//AndroidDisplay.windowWidth = displayMetrics.widthPixels;
		//AndroidDisplay.windowHeight = displayMetrics.heightPixels;

		//glSurfaceView = new GLSurfaceView(this);
		setContentView(R.layout.main);

		forwardButton = findButton(R.id.control_forward);
		jumpButton = findButton(R.id.control_jump);
		primaryButton = findButton(R.id.control_primary);
		secondaryButton = findButton(R.id.control_secondary);
		debugButton = findButton(R.id.control_debug);
		shiftButton = findButton(R.id.control_shift);
		keyboardButton = findButton(R.id.control_keyboard);

		glSurfaceView = (GLSurfaceView) findViewById(R.id.main_gl_surface);
		glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				int x = ((int) e.getX()) / scaleFactor;
				int y = (glSurfaceView.getHeight() - (int) e.getY()) / scaleFactor;
				if (handleGuiBar(x, y, e)) return true;
				AndroidDisplay.mouseX = x;
				AndroidDisplay.mouseY = y;
				switch (e.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						AndroidDisplay.putMouseEventWithCoords((byte) 0, (byte) 1, x, y,
							0, System.nanoTime());
						AndroidDisplay.mouseLeft = true;
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL:
						AndroidDisplay.putMouseEventWithCoords((byte) 0, (byte) 0, x, y,
							0, System.nanoTime());
						AndroidDisplay.mouseLeft = false;
						break;
				}
				return true;
			}
		});
		glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
			public void onDrawFrame(GL10 gl) {
			}
			public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
				AndroidDisplay.windowWidth = glSurfaceView.getWidth() / scaleFactor;
				AndroidDisplay.windowHeight = glSurfaceView.getHeight() / scaleFactor;
				calculateMcScale();
				System.out.println("WidthHeight: " + AndroidDisplay.windowWidth + ":" + AndroidDisplay.windowHeight);
				AndroidContextImplementation.context = EGL14.eglGetCurrentContext();
				AndroidContextImplementation.display = EGL14.eglGetCurrentDisplay();
				AndroidContextImplementation.read = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);
				AndroidContextImplementation.draw = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
				EGL14.eglMakeCurrent(AndroidContextImplementation.display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
					EGL14.EGL_NO_CONTEXT);
				System.out.println("Gave up context: " + AndroidContextImplementation.context);
				//File dexOut = new File("/sdcard/boardwalk/testcases_final.jar");
				//runCraft(dexOut);
				runCraft2(MainActivity.this, VERSION_TO_LAUNCH);
				while (true) {
					try {
						Thread.sleep(0x7fffffff);
					} catch (Exception e) {}
				}
			}
			public void onSurfaceChanged(GL10 gl, int width, int height) {
			}
		});
		glSurfaceView.requestRender();
		/*File jarjarIn = new File("/sdcard/boardwalk/testcases.jar");
		File dexOut = new File("/sdcard/boardwalk/testcases_dexed.jar");
		if (new File("/sdcard/boardwalk/dexme").exists()) {
			long beginTime = System.currentTimeMillis();
			//runRename(new File("/sdcard/boardwalk/jarjarrules.txt"), jarjarIn, dexIn);
			//System.out.println("Rename done in " + (System.currentTimeMillis() - beginTime) + " ms");
			//System.gc();
			List<File> dexInFiles = new ArrayList<File>();
			dexInFiles.addAll(Arrays.asList(new File("/sdcard/boardwalk/dexin/").listFiles()));
			dexInFiles.add(jarjarIn);
			dexInFiles = runRenameLibs(new File("/sdcard/boardwalk/jarjarrules.txt"), dexInFiles);
			runDex(this, dexInFiles, dexOut);
			try {
				CleanZipUtil.process(dexOut, new File("/sdcard/boardwalk/testcases_final.jar"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("Dex done in " + (System.currentTimeMillis() - beginTime) + " ms");
			new File("/sdcard/boardwalk/dexme").delete();
		}
		//runCraft(dexOut);
		//runTest(dexOut, "ImageIOTest");*/
	}

	private Button findButton(int id) {
		Button button = (Button) findViewById(id);
		button.setOnTouchListener(this);
		return button;
	}

	public boolean onTouch(View v, MotionEvent e) {
		boolean isDown;
		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				isDown = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				isDown = false;
				break;
			default:
				return false;
		}
		System.out.println("Button: " + isDown + ":" + v);
		if (v == forwardButton) {
			sendKeyPress(Keyboard.KEY_W, isDown);
		} else if (v == jumpButton) {
			sendKeyPress(Keyboard.KEY_SPACE, isDown);
		} else if (v == primaryButton) {
			sendMouseButton(0, isDown);
		} else if (v == secondaryButton) {
			sendMouseButton(1, isDown);
		} else if (v == debugButton) {
			sendKeyPress(Keyboard.KEY_F3, isDown);
		} else if (v == shiftButton) {
			sendKeyPress(Keyboard.KEY_LSHIFT, isDown);
		} else if (v == keyboardButton) {
			showHiddenTextbox();
		} else {
			return false;
		}
		return false;
	}

	private void sendKeyPress(int keyCode, boolean status) {
		sendKeyPress(keyCode, (char) 0, status);
	}

	private void sendKeyPress(int keyCode, char keyChar, boolean status) {
		AndroidDisplay.setKey(keyCode, keyChar, status);
	}

	private void sendMouseButton(int button, boolean status) {
		AndroidDisplay.setMouseButtonInGrabMode((byte) button, status? (byte) 1: (byte) 0);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			sendKeyPress(Keyboard.KEY_ESCAPE, true);
			return true;
		}
		Integer lwjglCode = AndroidKeyCodes.keyCodeMap.get(keyCode);
		if (lwjglCode != null) {
			sendKeyPress(lwjglCode, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			sendKeyPress(Keyboard.KEY_ESCAPE, false);
			return true;
		}
		Integer lwjglCode = AndroidKeyCodes.keyCodeMap.get(keyCode);
		if (lwjglCode != null) {
			sendKeyPress(lwjglCode, false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void calculateMcScale() {
		// mojang's scaling algorithm:
		// keep increasing scale factor when
		// currentscale factor is below the maximum
		// next scale factor when applied gives an effective area greater than 320x240
		// if rendering unicode and on an odd scale factor
		// decrease scale factor
		int scale = 1;
		int screenWidth = AndroidDisplay.windowWidth;
		int screenHeight = AndroidDisplay.windowHeight;

		while(screenWidth / (scale + 1) >= 320 && screenHeight / (scale + 1) >= 240) {
			scale++;
		}
		// we can't deal with Unicode here.
		this.guiScale = scale;
	}

	public int mcscale(int input) {
		return input * guiScale;
	}

	public boolean handleGuiBar(int x, int y, MotionEvent e) {
		//if (!AndroidDisplay.grab) return false;
		boolean isDown;
		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				isDown = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				isDown = false;
				break;
			default:
				return false;
		}
		int screenWidth = AndroidDisplay.windowWidth;
		int screenHeight = AndroidDisplay.windowHeight;
		int barheight = mcscale(20);
		int barwidth = mcscale(180);
		int barx = (screenWidth / 2) - (barwidth / 2);
		int bary = 0;
		System.out.println("Gui bar: " + barx + ":" + bary + ": my " + x + ":" + y);
		if (x < barx || x >= barx + barwidth || y < bary || y >= bary + barheight) {
			return false;
		}
		int icon = ((x - barx) / mcscale(180 / 9)) % 9;
		sendKeyPress(hotbarKeys[icon], isDown);
		return true;
	}

	public void showHiddenTextbox() {
		int IME_FLAG_NO_FULLSCREEN = 0x02000000;
		hiddenTextIgnoreUpdate = true;
		if (hiddenTextWindow == null) {
			hiddenTextView = new EditText(this);
			PopupTextWatcher whoWatchesTheWatcher = new PopupTextWatcher();
			hiddenTextView.addTextChangedListener(whoWatchesTheWatcher);
			hiddenTextView.setOnEditorActionListener(whoWatchesTheWatcher);
			hiddenTextView.setSingleLine(true);
			hiddenTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT
					| EditorInfo.IME_FLAG_NO_EXTRACT_UI | IME_FLAG_NO_FULLSCREEN);
			hiddenTextView.setInputType(InputType.TYPE_CLASS_TEXT);
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.addView(hiddenTextView);
			hiddenTextWindow = new PopupWindow(linearLayout);
			hiddenTextWindow.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			hiddenTextWindow.setFocusable(true);
			hiddenTextWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			hiddenTextWindow.setBackgroundDrawable(new ColorDrawable());
			// To get back button handling for free
			hiddenTextWindow.setClippingEnabled(false);
			hiddenTextWindow.setTouchable(false);
			hiddenTextWindow.setOutsideTouchable(true);
			// These flags were taken from a dumpsys window output of Mojang's
			// window
			hiddenTextWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
				public void onDismiss() {
					//nativeBackPressed();
					hiddenTextIgnoreUpdate = true;
				}
			});
		}

		// yes, this is a kludge. Haters gonna hate.
		hiddenTextView.setText(initText);
		hiddenTextContents = hiddenTextView.getText().toString();
		Selection.setSelection((Spannable) hiddenTextView.getText(), hiddenTextContents.length());
		//this.hiddenTextDismissAfterOneLine = dismissAfterOneLine;

		int xLoc = -10000;

		hiddenTextWindow.showAtLocation(this.getWindow().getDecorView(),
				Gravity.LEFT | Gravity.TOP, xLoc, 0);
		hiddenTextView.requestFocus();
		showKeyboardView();
		hiddenTextIgnoreUpdate = false;
	}


	public void showKeyboardView() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(getWindow().getDecorView(), InputMethodManager.SHOW_FORCED);
	}

	public static List<File> runRenameLibs(File rulesFile, List<File> inFiles) {
		System.out.println("Before rename: " + inFiles);
		List<File> outFiles = new ArrayList<File>(inFiles.size());
		for (File f: inFiles) {
			String name = f.getName();
			if (name.endsWith("_renamed.jar") || name.endsWith("_dexed.jar")) {
				f.delete();
				continue;
			}
			boolean doRename = false;
			for (String s: libsToRename) {
				if (name.contains(s)) {
					// do the rename
					doRename = true;
					break;
				}
			}
			if (doRename) {
				String newPath = f.getAbsolutePath() + "_renamed.jar";
				File newFile = new File(newPath);
				MinecraftLaunch.runRename(rulesFile, f, newFile);
				outFiles.add(newFile);
			} else {
				outFiles.add(f);
			}
		}
		System.out.println("After rename: " + outFiles);
		return outFiles;
	}

	public static void runDex(Context context, List<File> dexInFiles, File dexOut) {
		System.out.println("Dexing " + dexInFiles);
		dexOut.delete();
		List<String> dexFragFiles = new ArrayList<String>();
		for (File f: dexInFiles) {
			if (f.getName().endsWith("_dexed.jar")) continue;
			String newName = f.getAbsolutePath() + "_dexed.jar";
			new File(newName).delete();
			dexFragFiles.add(newName);
			String[] dexArgs = {"--dex", "--no-optimize", "--num-threads=4", "--output=" + newName, f.getAbsolutePath()};
			try {
				runExt(context, "com.android.dx.command.Main", Arrays.asList(dexArgs));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		String[] dexArgs = {"--dex", "--no-optimize", "--num-threads=4", "--output=" + dexOut.getAbsolutePath()};
		List<String> dexNewArgs = new ArrayList<String>();
		dexNewArgs.addAll(Arrays.asList(dexArgs));
		dexNewArgs.addAll(dexFragFiles);
		try {
			runExt(context, "com.android.dx.command.Main", dexNewArgs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void runTest(File dexFile, String testName) {
		try {
			File optDir = new File("/data/data/net.zhuoweizhang.boardwalk/files/dalvik-cache");
			optDir.mkdirs();
			DexClassLoader classLoader = new DexClassLoader(dexFile.getAbsolutePath(), 
				optDir.getAbsolutePath(), "", MainActivity.class.getClassLoader());
			Class<?> clazz = classLoader.loadClass("net.zhuoweizhang.boardwalk.testcase." + testName);
			junit.framework.TestCase testCase = (junit.framework.TestCase) clazz.newInstance();
			testCase.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	public static void runExt(Context context, String className, List<String> args) throws IOException, InterruptedException {
		List<String> argsNew = new ArrayList<String>();
		argsNew.add("dalvikvm");
		argsNew.add("-Djava.library.path=" + System.getProperty("java.library.path"));
		argsNew.add("-classpath");
		argsNew.add(context.getPackageCodePath());
		argsNew.add("-Xms384M");
		argsNew.add("-Xmx768M");
		argsNew.add(className);
		argsNew.addAll(args);
		Process p = new ProcessBuilder(argsNew).redirectErrorStream(true).start();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while((line = in.readLine()) != null) {
			System.out.println(line);
		}
		p.waitFor();
	}

	public static void fixRSAPadding(File spongyFile, File optDir) throws Exception {
		// welcome to the territory of YOLO; I'll be your tour guide for today.
		System.out.println("Before: " + KeyFactory.getInstance("RSA"));
		DexClassLoader classLoader = new DexClassLoader(spongyFile.getAbsolutePath(),
			optDir.getAbsolutePath(), null, MainActivity.class.getClassLoader());
		Class<?> clazz = classLoader.loadClass("org.spongycastle.jce.provider.BouncyCastleProvider");
		Security.insertProviderAt((Provider) clazz.newInstance(), 1);
		System.out.println("After: " + KeyFactory.getInstance("RSA"));
/*		KeyFactory.getInstance("RSA");
		Class<?> clazz = Class.forName("org.apache.harmony.security.fortress.Services");
		Method method = clazz.getMethod("getServices", String.class);
		ArrayList<Provider.Service> rsaList = (ArrayList<Provider.Service>)
			method.invoke(null, "KeyFactory.RSA");
		System.out.println("Before: " + rsaList);
		ArrayList<Provider.Service> rsaPkcs1List = (ArrayList<Provider.Service>)
			method.invoke(null, "KeyFactory.RSA//PKCS1PADDING");
		rsaList.clear();
		rsaList.addAll(rsaPkcs1List);
		System.out.println("After: " + rsaList);

		Provider provider = KeyFactory.getInstance("RSA").getProvider();
		System.out.println("Before: " + provider.getService("KeyService", "RSA"));
		Provider.Service service = provider.getService("KeyService", "RSA/ECB/PKCS5Padding");
		System.out.println(service);
		provider.putService(service);
		System.out.println("After: " + provider.getService("KeyService", "RSA"));*/
	}

	public static void runCraft(File dexFile) {
		try {
			File optDir = new File("/data/data/net.zhuoweizhang.boardwalk/files/dalvik-cache");
			optDir.mkdirs();
			DexClassLoader classLoader = new DexClassLoader(dexFile.getAbsolutePath(), 
				optDir.getAbsolutePath(), "", MainActivity.class.getClassLoader());
			fixRSAPadding(new File("/sdcard/boardwalk/spongy.jar"), optDir);
			Class<?> clazz = classLoader.loadClass("net.minecraft.client.main.Main");
			Method mainMethod = clazz.getMethod("main", String[].class);
			File gameDir = new File("/sdcard/boardwalk/gamedir");
			gameDir.mkdirs();
			String[] myargs = {"--accessToken", "0", "--userProperties", "{}", "--version", "mcp",
				"--gameDir", gameDir.getAbsolutePath()};
			mainMethod.invoke(null, (Object) myargs);
			//junit.framework.TestCase testCase = (junit.framework.TestCase) clazz.newInstance();
			//testCase.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	public static void runCraft2(Context context, String versionName) {
		try {
			MinecraftVersion version = MinecraftDownloader.getVersionInfo(versionName);
			File optDir = context.getDir("dalvik-cache", 0);
			optDir.mkdirs();
			DexClassLoader classLoader = new DexClassLoader(MinecraftLaunch.getClassPath(version), 
				optDir.getAbsolutePath(), "", MainActivity.class.getClassLoader());
			Class<?> clazz = null;
			try {
				clazz = classLoader.loadClass("net.minecraft.client.main.Main");
			} catch (ClassNotFoundException ex) {
				clazz = classLoader.loadClass("net.minecraft.client.Minecraft");
			}
			Method mainMethod = clazz.getMethod("main", String[].class);
			File gameDir = new File("/sdcard/boardwalk/gamedir");
			gameDir.mkdirs();
			String[] myargs = {"--accessToken", "0", "--userProperties", "{}", "--version", "mcp",
				"--gameDir", gameDir.getAbsolutePath()};
			mainMethod.invoke(null, (Object) myargs);
			//junit.framework.TestCase testCase = (junit.framework.TestCase) clazz.newInstance();
			//testCase.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void forceUserHome(String s) throws Exception {
		Properties props = System.getProperties();
		Class clazz = props.getClass();
		Field f = null;
		while (clazz != null) {
			try {
				f = clazz.getDeclaredField("defaults");
				break;
			} catch (Exception e) {
				clazz = clazz.getSuperclass();
			}
		}
		if (f != null) {
			f.setAccessible(true);
			Properties defaultProps = (Properties) f.get(props);
			defaultProps.put("user.home", s);
		}
	}

	public static void initEnvs() {
		try {
			Class<?> libcoreClass = Class.forName("libcore.io.Libcore");
			Field osField = libcoreClass.getField("os");
			Object os = osField.get(null);
			Class osClass = os.getClass();
			Method setEnvMethod = osClass.getMethod("setenv", String.class, String.class, Boolean.TYPE);
			setEnvMethod.invoke(os, "LIBGL_MIPMAP", "3", true);
			System.setProperty("user.home", "/sdcard/boardwalk");
			if (!System.getProperty("user.home", "/").equals("/sdcard/boardwalk")) {
				forceUserHome("/sdcard/boardwalk");
			}
			System.setProperty("org.apache.logging.log4j.level", "INFO");
			// this one is for the API's built in logger; we only use this one,
			// but we set the one above also, just in case.
			System.setProperty("org.apache.logging.log4j.simplelog.level", "INFO");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class PopupTextWatcher implements TextWatcher, TextView.OnEditorActionListener {
		public void afterTextChanged(Editable e) {
//			nativeSetTextboxText(e.toString());
			if (hiddenTextIgnoreUpdate) return;
			String newText = e.toString();
			String oldText = hiddenTextContents;
			System.out.println("New: " + newText + " old: " + oldText);
			if (newText.length() < oldText.length()) {
				for (int i = 0; i < oldText.length() - newText.length(); i++) {
					sendKeyPress(KEY_BACKSPACE, true);
					sendKeyPress(KEY_BACKSPACE, false);
				}
			} else {
				for (int i = 0; i < newText.length() - oldText.length(); i++) {
					int index = oldText.length() + i;
					char keyChar = newText.charAt(index);
					sendKeyPress(0, keyChar, true);
					sendKeyPress(0, keyChar, false);
				}
			}
			hiddenTextContents = newText;
		}

		public void beforeTextChanged(CharSequence c, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence c, int start, int count, int after) {
		}

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
/*			if (BuildConfig.DEBUG)
				Log.i(TAG, "Editor action: " + actionId);
			if (hiddenTextDismissAfterOneLine) {
				hiddenTextWindow.dismiss();
			} else {
				nativeReturnKeyPressed();
			}
			return true;
*/
			return true;
		}
	}

}
