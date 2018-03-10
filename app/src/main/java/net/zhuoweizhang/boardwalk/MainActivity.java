package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.net.URL;
import java.lang.reflect.*;
import java.util.*;
import java.security.*;

import javax.crypto.Cipher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.lwjgl.*;
import net.zhuoweizhang.boardwalk.model.*;
import net.zhuoweizhang.boardwalk.potato.*;
import net.zhuoweizhang.boardwalk.util.*;

public class MainActivity extends Activity implements View.OnTouchListener
{
	public static final String VERSION_TO_LAUNCH = "1.8.7";
	public static final String initText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
					"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ";

	private static int[] hotbarKeys = {
		Keyboard.KEY_1, Keyboard.KEY_2,	Keyboard.KEY_3,
		Keyboard.KEY_4, Keyboard.KEY_5,	Keyboard.KEY_6,
		Keyboard.KEY_7, Keyboard.KEY_8, Keyboard.KEY_9};
	private GLSurfaceView glSurfaceView;
	private DisplayMetrics displayMetrics;
	private static String[] libsToRename = {"vecmath", "testcases"};
	private Button jumpButton, primaryButton, secondaryButton;
	private Button debugButton, shiftButton;
	private Button keyboardButton;
	private Button inventoryButton, talkButton;
	private Button upButton, downButton, leftButton, rightButton;
	private Button thirdPersonButton;
	private int scaleFactor = 1;
	private PopupWindow hiddenTextWindow;
	private TextView hiddenTextView;
	private String hiddenTextContents = initText;
	private boolean hiddenTextIgnoreUpdate = true;
	private int guiScale = 1;
	public static final int KEY_BACKSPACE = 14; //WTF lwjgl?
	private ViewGroup overlayView;
	private boolean triggeredLeftMouseButton = false;
	private int initialX, initialY;
	private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
	private int fingerStillThreshold = 8;
	private boolean rightOverride = false;
	private Drawable secondaryButtonDefaultBackground, secondaryButtonColorBackground;
	private File runtimeDir;
	private InputEventSender inputSender;
	private int mouseX, mouseY, windowWidth, windowHeight;

	private Handler theHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_LEFT_MOUSE_BUTTON_CHECK: {
					int x = mouseX;
					int y = mouseY;
					if (inputSender.grab &&
						Math.abs(initialX - x) < fingerStillThreshold &&
						Math.abs(initialY - y) < fingerStillThreshold) {
						triggeredLeftMouseButton = true;
						sendMouseButton(0, true);
					}
					break;
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		/* Preconditions: root FS extracted (by launcher activity) */
		super.onCreate(savedInstanceState);

		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		setContentView(R.layout.main);

		upButton = findButton(R.id.control_up);
		downButton = findButton(R.id.control_down);
		leftButton = findButton(R.id.control_left);
		rightButton = findButton(R.id.control_right);
		jumpButton = findButton(R.id.control_jump);
		primaryButton = findButton(R.id.control_primary);
		secondaryButton = findButton(R.id.control_secondary);
		debugButton = findButton(R.id.control_debug);
		shiftButton = findButton(R.id.control_shift);
		keyboardButton = findButton(R.id.control_keyboard);
		inventoryButton = findButton(R.id.control_inventory);
		talkButton = findButton(R.id.control_talk);
		thirdPersonButton = findButton(R.id.control_thirdperson);
		overlayView = (ViewGroup) findViewById(R.id.main_control_overlay);
		secondaryButtonDefaultBackground = secondaryButton.getBackground();
		secondaryButtonColorBackground = new ColorDrawable(0xffff0000);

		fingerStillThreshold = getResources().getDimensionPixelSize(R.dimen.finger_still_threshold);

		runtimeDir = getDir("runtime", 0);
		try {
			inputSender = new InputEventSender();
			int inputSenderPort = inputSender.runServer();
			LoadMe.setenv("INPUT_SENDER_PORT", Integer.toString(inputSenderPort));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		glSurfaceView = (GLSurfaceView) findViewById(R.id.main_gl_surface);
		glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				int x = ((int) e.getX()) / scaleFactor;
				int y = (glSurfaceView.getHeight() - (int) e.getY()) / scaleFactor;
				if (handleGuiBar(x, y, e)) return true;
				inputSender.setMouseCoords(x, y);
				mouseX = x;
				mouseY = y;
				switch (e.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						inputSender.putMouseEventWithCoords(rightOverride? (byte) 1: (byte) 0, (byte) 1, x, y,
							0, System.nanoTime());
						/*
						if (!rightOverride) {
							AndroidDisplay.mouseLeft = true;
						} else {
							//AndroidDisplay.mouseRight = true;
						}
						*/
						if (inputSender.grab) {
							initialX = x;
							initialY = y;
							theHandler.sendEmptyMessageDelayed(MSG_LEFT_MOUSE_BUTTON_CHECK, 500);
						}
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL:
						inputSender.putMouseEventWithCoords(rightOverride? (byte) 1: (byte) 0, (byte) 0, x, y,
							0, System.nanoTime());
						/*
						if (!rightOverride) {
							AndroidDisplay.mouseLeft = false;
						} else {
							//AndroidDisplay.mouseRight = false;
						}
						*/
						if (inputSender.grab) {
							if (!triggeredLeftMouseButton &&
								Math.abs(initialX - x) < fingerStillThreshold &&
								Math.abs(initialY - y) < fingerStillThreshold) {
								sendMouseButton(1, true);
								sendMouseButton(1, false);
							}
							if (triggeredLeftMouseButton) sendMouseButton(0, false);
							triggeredLeftMouseButton = false;
							theHandler.removeMessages(MSG_LEFT_MOUSE_BUTTON_CHECK);
						}
						break;
				}
				return true;
			}
		});

		glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
			public void onDrawFrame(GL10 gl) {
			}
			public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
				windowWidth = glSurfaceView.getWidth() / scaleFactor;
				windowHeight = glSurfaceView.getHeight() / scaleFactor;
				calculateMcScale();
				System.out.println("WidthHeight: " + windowWidth + ":" + windowHeight);

				LoadMe.setenv("POTATO_WINDOW_WIDTH", "" + windowWidth);
				LoadMe.setenv("POTATO_WINDOW_HEIGHT", "" + windowHeight);

				String selectedVersion = getSharedPreferences("launcher_prefs", 0).
					getString("selected_version", VERSION_TO_LAUNCH);

				EGL10 theEgl = (EGL10) EGLContext.getEGL();
				LoadMe.setupBridgeEGL();
				theEgl.eglMakeCurrent(theEgl.eglGetCurrentDisplay(), EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
					EGL10.EGL_NO_CONTEXT);
				LoadMe.runtimePath = runtimeDir.getAbsolutePath();
				PotatoRunner runner = new PotatoRunner();
				try {
					MinecraftVersion version = MinecraftDownloader.getVersionInfo(selectedVersion);
					runner.mcClassPath = LoadMe.runtimePath + "/clientname.jar:" +
						LoadMe.runtimePath + "/lwjgl.jar:" +
						LoadMe.runtimePath + "/lwjgl_util.jar:" +
						LoadMe.runtimePath + "/librarylwjglopenal-20100824.jar:" +
						MinecraftLaunch.getClassPath(version);
					runner.mcArgs = buildMCArgs(MainActivity.this, selectedVersion, version);
				} catch (IOException ie) {
					throw new RuntimeException(ie);
				}
				new Thread(runner).start();

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
		if (BuildConfig.DEBUG) System.out.println("Button: " + isDown + ":" + v);
		if (v == upButton) {
			sendKeyPress(Keyboard.KEY_W, isDown);
		} else if (v == downButton) {
			sendKeyPress(Keyboard.KEY_S, isDown);
		} else if (v == leftButton) {
			sendKeyPress(Keyboard.KEY_A, isDown);
		} else if (v == rightButton) {
			sendKeyPress(Keyboard.KEY_D, isDown);
		} else if (v == jumpButton) {
			sendKeyPress(Keyboard.KEY_SPACE, isDown);
		} else if (v == primaryButton) {
			sendMouseButton(0, isDown);
		} else if (v == secondaryButton) {
			if (inputSender.grab) {
				sendMouseButton(1, isDown);
			} else {
				setRightOverride(isDown);
			}
		} else if (v == debugButton) {
			sendKeyPress(Keyboard.KEY_F3, isDown);
		} else if (v == shiftButton) {
			sendKeyPress(Keyboard.KEY_LSHIFT, isDown);
		} else if (v == inventoryButton) {
			sendKeyPress(Keyboard.KEY_E, isDown);
		} else if (v == talkButton) {
			sendKeyPress(Keyboard.KEY_T, isDown);
		} else if (v == keyboardButton) {
			showHiddenTextbox();
		} else if (v == thirdPersonButton) {
			sendKeyPress(Keyboard.KEY_F5, isDown);
		} else {
			return false;
		}
		return false;
	}

	private void sendKeyPress(int keyCode, boolean status) {
		sendKeyPress(keyCode, (char) 0, status);
	}

	private void sendKeyPress(int keyCode, char keyChar, boolean status) {
		inputSender.setKey(keyCode, keyChar, status);
	}

	private void sendMouseButton(int button, boolean status) {
		inputSender.setMouseButtonInGrabMode((byte) button, status? (byte) 1: (byte) 0);
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
		int screenWidth = windowWidth;
		int screenHeight = windowHeight;

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
		if (!inputSender.grab) return false;
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
		int screenWidth = windowWidth;
		int screenHeight = windowHeight;
		int barheight = mcscale(20);
		int barwidth = mcscale(180);
		int barx = (screenWidth / 2) - (barwidth / 2);
		int bary = 0;
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

	private void setRightOverride(boolean val) {
		rightOverride = val;
		secondaryButton.setBackgroundDrawable(rightOverride? secondaryButtonColorBackground: secondaryButtonDefaultBackground);
	}

/*
	public static void runCraft2(Context context, String versionName) {
		try {
			MinecraftVersion version = MinecraftDownloader.getVersionInfo(versionName);
			File optDir = context.getDir("dalvik-cache", 0);
			optDir.mkdirs();
			String nativePath = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).
				applicationInfo.nativeLibraryDir;
			DexClassLoader classLoader = new DexClassLoader(MinecraftLaunch.getClassPath(version), 
				optDir.getAbsolutePath(), nativePath, MainActivity.class.getClassLoader());
			fixRSAPadding(null, null);

			Class<?> clazz = null;
			try {
				clazz = classLoader.loadClass("net.minecraft.client.main.Main");
			} catch (ClassNotFoundException ex) {
				clazz = classLoader.loadClass("net.minecraft.client.Minecraft");
			}
			Method mainMethod = clazz.getMethod("main", String[].class);
			String[] myargs = buildMCArgs(context, versionName);
			mainMethod.invoke(null, (Object) myargs);
			//junit.framework.TestCase testCase = (junit.framework.TestCase) clazz.newInstance();
			//testCase.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

*/

	private static String[] buildMCArgs(Context context, String versionName, MinecraftVersion version) {
		File gameDir = new File(Environment.getExternalStorageDirectory(), "boardwalk/gamedir");
		gameDir.mkdirs();
		File assetsDir = new File(gameDir, "assets");
		assetsDir.mkdirs();
		SharedPreferences prefs = context.getSharedPreferences("launcher_prefs", 0);
		String accessToken = prefs.getString("auth_accessToken", "0");
		String userUUID = prefs.getString("auth_profile_id", "00000000-0000-0000-0000-000000000000");
		boolean demo = userUUID.equals("00000000-0000-0000-0000-000000000000");
		String username = prefs.getString("auth_profile_name", "Player");
		String userType = demo? "legacy" : "mojang"; // Only demo uses non-Yggdrasil auth, it seems

		Map<String, String> subs = new HashMap<String, String>();
		subs.put("${auth_player_name}", username);
		subs.put("${version_name}", versionName);
		subs.put("${game_directory}", gameDir.getAbsolutePath());
		subs.put("${assets_root}", assetsDir.getAbsolutePath());
		subs.put("${assets_index_name}", version.assets);
		subs.put("${auth_uuid}", userUUID);
		subs.put("${auth_access_token}", accessToken);
		subs.put("${user_properties}", "{}");
		subs.put("${user_type}", userType);

		String[] mcArgs = version.minecraftArguments.split(" ");
		for (int i = 0; i < mcArgs.length; i++) {
			String sub = subs.get(mcArgs[i]);
			if (sub != null) mcArgs[i] = sub;
		}

		List<String> retval = new ArrayList(1 + mcArgs.length + (demo? 1: 0));
		retval.add(version.mainClass);
		retval.addAll(Arrays.asList(mcArgs));
		if (demo) retval.add("--demo");
		return retval.toArray(new String[0]);
	}

	/* MasterPotato start */

	public class PotatoRunner implements Runnable {
		public String mcClassPath;
		public String[] mcArgs;
		public void run() {
			try {
				LoadMe.exec(mcClassPath, mcArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* MasterPotato end */

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
			sendKeyPress(Keyboard.KEY_RETURN, '\n', true);
			sendKeyPress(Keyboard.KEY_RETURN, (char) 0, false);
			return true;
		}
	}

}
