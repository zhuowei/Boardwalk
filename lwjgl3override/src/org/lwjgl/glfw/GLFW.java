/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * Boardwalk: Modified version of GLFW.java to stub all methods.
 */
package org.lwjgl.glfw;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Native bindings to the <a target="_blank" href="http://www.glfw.org/docs/latest/">GLFW</a> library.
 * 
 * <p>GLFW is a free, Open Source, multi-platform library for opening a window, creating an OpenGL context and managing input. It is easy to integrate into
 * existing applications and does not lay claim to the main loop.</p>
 */
public class GLFW {

    /** The major version number of the GLFW library. This is incremented when the API is changed in non-compatible ways. */
    public static final int GLFW_VERSION_MAJOR = 3;

    /** The minor version number of the GLFW library. This is incremented when features are added to the API but it remains backward-compatible. */
    public static final int GLFW_VERSION_MINOR = 3;

    /** The revision number of the GLFW library. This is incremented when a bug fix release is made that does not contain any API changes. */
    public static final int GLFW_VERSION_REVISION = 0;

    /** Boolean values. */
    public static final int
        GLFW_TRUE  = 1,
        GLFW_FALSE = 0;

    /** The key or button was released. */
    public static final int GLFW_RELEASE = 0;

    /** The key or button was pressed. */
    public static final int GLFW_PRESS = 1;

    /** The key was held down until it repeated. */
    public static final int GLFW_REPEAT = 2;

    /** Joystick hat states. */
    public static final int
        GLFW_HAT_CENTERED   = 0,
        GLFW_HAT_UP         = 1,
        GLFW_HAT_RIGHT      = 2,
        GLFW_HAT_DOWN       = 4,
        GLFW_HAT_LEFT       = 8,
        GLFW_HAT_RIGHT_UP   = (GLFW_HAT_RIGHT | GLFW_HAT_UP),
        GLFW_HAT_RIGHT_DOWN = (GLFW_HAT_RIGHT | GLFW_HAT_DOWN),
        GLFW_HAT_LEFT_UP    = (GLFW_HAT_LEFT  | GLFW_HAT_UP),
        GLFW_HAT_LEFT_DOWN  = (GLFW_HAT_LEFT  | GLFW_HAT_DOWN);

    /** The unknown key. */
    public static final int GLFW_KEY_UNKNOWN = -1;

    /** Printable keys. */
    public static final int
        GLFW_KEY_SPACE         = 32,
        GLFW_KEY_APOSTROPHE    = 39,
        GLFW_KEY_COMMA         = 44,
        GLFW_KEY_MINUS         = 45,
        GLFW_KEY_PERIOD        = 46,
        GLFW_KEY_SLASH         = 47,
        GLFW_KEY_0             = 48,
        GLFW_KEY_1             = 49,
        GLFW_KEY_2             = 50,
        GLFW_KEY_3             = 51,
        GLFW_KEY_4             = 52,
        GLFW_KEY_5             = 53,
        GLFW_KEY_6             = 54,
        GLFW_KEY_7             = 55,
        GLFW_KEY_8             = 56,
        GLFW_KEY_9             = 57,
        GLFW_KEY_SEMICOLON     = 59,
        GLFW_KEY_EQUAL         = 61,
        GLFW_KEY_A             = 65,
        GLFW_KEY_B             = 66,
        GLFW_KEY_C             = 67,
        GLFW_KEY_D             = 68,
        GLFW_KEY_E             = 69,
        GLFW_KEY_F             = 70,
        GLFW_KEY_G             = 71,
        GLFW_KEY_H             = 72,
        GLFW_KEY_I             = 73,
        GLFW_KEY_J             = 74,
        GLFW_KEY_K             = 75,
        GLFW_KEY_L             = 76,
        GLFW_KEY_M             = 77,
        GLFW_KEY_N             = 78,
        GLFW_KEY_O             = 79,
        GLFW_KEY_P             = 80,
        GLFW_KEY_Q             = 81,
        GLFW_KEY_R             = 82,
        GLFW_KEY_S             = 83,
        GLFW_KEY_T             = 84,
        GLFW_KEY_U             = 85,
        GLFW_KEY_V             = 86,
        GLFW_KEY_W             = 87,
        GLFW_KEY_X             = 88,
        GLFW_KEY_Y             = 89,
        GLFW_KEY_Z             = 90,
        GLFW_KEY_LEFT_BRACKET  = 91,
        GLFW_KEY_BACKSLASH     = 92,
        GLFW_KEY_RIGHT_BRACKET = 93,
        GLFW_KEY_GRAVE_ACCENT  = 96,
        GLFW_KEY_WORLD_1       = 161,
        GLFW_KEY_WORLD_2       = 162;

    /** Function keys. */
    public static final int
        GLFW_KEY_ESCAPE        = 256,
        GLFW_KEY_ENTER         = 257,
        GLFW_KEY_TAB           = 258,
        GLFW_KEY_BACKSPACE     = 259,
        GLFW_KEY_INSERT        = 260,
        GLFW_KEY_DELETE        = 261,
        GLFW_KEY_RIGHT         = 262,
        GLFW_KEY_LEFT          = 263,
        GLFW_KEY_DOWN          = 264,
        GLFW_KEY_UP            = 265,
        GLFW_KEY_PAGE_UP       = 266,
        GLFW_KEY_PAGE_DOWN     = 267,
        GLFW_KEY_HOME          = 268,
        GLFW_KEY_END           = 269,
        GLFW_KEY_CAPS_LOCK     = 280,
        GLFW_KEY_SCROLL_LOCK   = 281,
        GLFW_KEY_NUM_LOCK      = 282,
        GLFW_KEY_PRINT_SCREEN  = 283,
        GLFW_KEY_PAUSE         = 284,
        GLFW_KEY_F1            = 290,
        GLFW_KEY_F2            = 291,
        GLFW_KEY_F3            = 292,
        GLFW_KEY_F4            = 293,
        GLFW_KEY_F5            = 294,
        GLFW_KEY_F6            = 295,
        GLFW_KEY_F7            = 296,
        GLFW_KEY_F8            = 297,
        GLFW_KEY_F9            = 298,
        GLFW_KEY_F10           = 299,
        GLFW_KEY_F11           = 300,
        GLFW_KEY_F12           = 301,
        GLFW_KEY_F13           = 302,
        GLFW_KEY_F14           = 303,
        GLFW_KEY_F15           = 304,
        GLFW_KEY_F16           = 305,
        GLFW_KEY_F17           = 306,
        GLFW_KEY_F18           = 307,
        GLFW_KEY_F19           = 308,
        GLFW_KEY_F20           = 309,
        GLFW_KEY_F21           = 310,
        GLFW_KEY_F22           = 311,
        GLFW_KEY_F23           = 312,
        GLFW_KEY_F24           = 313,
        GLFW_KEY_F25           = 314,
        GLFW_KEY_KP_0          = 320,
        GLFW_KEY_KP_1          = 321,
        GLFW_KEY_KP_2          = 322,
        GLFW_KEY_KP_3          = 323,
        GLFW_KEY_KP_4          = 324,
        GLFW_KEY_KP_5          = 325,
        GLFW_KEY_KP_6          = 326,
        GLFW_KEY_KP_7          = 327,
        GLFW_KEY_KP_8          = 328,
        GLFW_KEY_KP_9          = 329,
        GLFW_KEY_KP_DECIMAL    = 330,
        GLFW_KEY_KP_DIVIDE     = 331,
        GLFW_KEY_KP_MULTIPLY   = 332,
        GLFW_KEY_KP_SUBTRACT   = 333,
        GLFW_KEY_KP_ADD        = 334,
        GLFW_KEY_KP_ENTER      = 335,
        GLFW_KEY_KP_EQUAL      = 336,
        GLFW_KEY_LEFT_SHIFT    = 340,
        GLFW_KEY_LEFT_CONTROL  = 341,
        GLFW_KEY_LEFT_ALT      = 342,
        GLFW_KEY_LEFT_SUPER    = 343,
        GLFW_KEY_RIGHT_SHIFT   = 344,
        GLFW_KEY_RIGHT_CONTROL = 345,
        GLFW_KEY_RIGHT_ALT     = 346,
        GLFW_KEY_RIGHT_SUPER   = 347,
        GLFW_KEY_MENU          = 348,
        GLFW_KEY_LAST          = GLFW_KEY_MENU;

    /** If this bit is set one or more Shift keys were held down. */
    public static final int GLFW_MOD_SHIFT = 0x1;

    /** If this bit is set one or more Control keys were held down. */
    public static final int GLFW_MOD_CONTROL = 0x2;

    /** If this bit is set one or more Alt keys were held down. */
    public static final int GLFW_MOD_ALT = 0x4;

    /** If this bit is set one or more Super keys were held down. */
    public static final int GLFW_MOD_SUPER = 0x8;

    /** If this bit is set the Caps Lock key is enabled and the {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int GLFW_MOD_CAPS_LOCK = 0x10;

    /** If this bit is set the Num Lock key is enabled and the {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int GLFW_MOD_NUM_LOCK = 0x20;

    /** Mouse buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#input_mouse_button">mouse button input</a> for how these are used. */
    public static final int
        GLFW_MOUSE_BUTTON_1      = 0,
        GLFW_MOUSE_BUTTON_2      = 1,
        GLFW_MOUSE_BUTTON_3      = 2,
        GLFW_MOUSE_BUTTON_4      = 3,
        GLFW_MOUSE_BUTTON_5      = 4,
        GLFW_MOUSE_BUTTON_6      = 5,
        GLFW_MOUSE_BUTTON_7      = 6,
        GLFW_MOUSE_BUTTON_8      = 7,
        GLFW_MOUSE_BUTTON_LAST   = GLFW_MOUSE_BUTTON_8,
        GLFW_MOUSE_BUTTON_LEFT   = GLFW_MOUSE_BUTTON_1,
        GLFW_MOUSE_BUTTON_RIGHT  = GLFW_MOUSE_BUTTON_2,
        GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;

    /** Joysticks. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#joystick">joystick input</a> for how these are used. */
    public static final int
        GLFW_JOYSTICK_1    = 0,
        GLFW_JOYSTICK_2    = 1,
        GLFW_JOYSTICK_3    = 2,
        GLFW_JOYSTICK_4    = 3,
        GLFW_JOYSTICK_5    = 4,
        GLFW_JOYSTICK_6    = 5,
        GLFW_JOYSTICK_7    = 6,
        GLFW_JOYSTICK_8    = 7,
        GLFW_JOYSTICK_9    = 8,
        GLFW_JOYSTICK_10   = 9,
        GLFW_JOYSTICK_11   = 10,
        GLFW_JOYSTICK_12   = 11,
        GLFW_JOYSTICK_13   = 12,
        GLFW_JOYSTICK_14   = 13,
        GLFW_JOYSTICK_15   = 14,
        GLFW_JOYSTICK_16   = 15,
        GLFW_JOYSTICK_LAST = GLFW_JOYSTICK_16;

    /** Gamepad buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
    public static final int
        GLFW_GAMEPAD_BUTTON_A            = 0,
        GLFW_GAMEPAD_BUTTON_B            = 1,
        GLFW_GAMEPAD_BUTTON_X            = 2,
        GLFW_GAMEPAD_BUTTON_Y            = 3,
        GLFW_GAMEPAD_BUTTON_LEFT_BUMPER  = 4,
        GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5,
        GLFW_GAMEPAD_BUTTON_BACK         = 6,
        GLFW_GAMEPAD_BUTTON_START        = 7,
        GLFW_GAMEPAD_BUTTON_GUIDE        = 8,
        GLFW_GAMEPAD_BUTTON_LEFT_THUMB   = 9,
        GLFW_GAMEPAD_BUTTON_RIGHT_THUMB  = 10,
        GLFW_GAMEPAD_BUTTON_DPAD_UP      = 11,
        GLFW_GAMEPAD_BUTTON_DPAD_RIGHT   = 12,
        GLFW_GAMEPAD_BUTTON_DPAD_DOWN    = 13,
        GLFW_GAMEPAD_BUTTON_DPAD_LEFT    = 14,
        GLFW_GAMEPAD_BUTTON_LAST         = GLFW_GAMEPAD_BUTTON_DPAD_LEFT,
        GLFW_GAMEPAD_BUTTON_CROSS        = GLFW_GAMEPAD_BUTTON_A,
        GLFW_GAMEPAD_BUTTON_CIRCLE       = GLFW_GAMEPAD_BUTTON_B,
        GLFW_GAMEPAD_BUTTON_SQUARE       = GLFW_GAMEPAD_BUTTON_X,
        GLFW_GAMEPAD_BUTTON_TRIANGLE     = GLFW_GAMEPAD_BUTTON_Y;

    /** Gamepad axes. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
    public static final int
        GLFW_GAMEPAD_AXIS_LEFT_X        = 0,
        GLFW_GAMEPAD_AXIS_LEFT_Y        = 1,
        GLFW_GAMEPAD_AXIS_RIGHT_X       = 2,
        GLFW_GAMEPAD_AXIS_RIGHT_Y       = 3,
        GLFW_GAMEPAD_AXIS_LEFT_TRIGGER  = 4,
        GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER = 5,
        GLFW_GAMEPAD_AXIS_LAST          = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

    /**
     * Error codes.
     * 
     * <h5>Enum values:</h5>
     * 
     * <ul>
     * <li>{@link #GLFW_NO_ERROR NO_ERROR} - No error has occurred.</li>
     * <li>{@link #GLFW_NOT_INITIALIZED NOT_INITIALIZED} - 
     * GLFW has not been initialized.
     * 
     * <p>This occurs if a GLFW function was called that may not be called unless the library is initialized.</p>
     * </li>
     * <li>{@link #GLFW_NO_CURRENT_CONTEXT NO_CURRENT_CONTEXT} - 
     * No context is current for this thread.
     * 
     * <p>This occurs if a GLFW function was called that needs and operates on the current OpenGL or OpenGL ES context but no context is current on the
     * calling thread. One such function is {@link #glfwSwapInterval SwapInterval}.</p>
     * </li>
     * <li>{@link #GLFW_INVALID_ENUM INVALID_ENUM} - 
     * One of the arguments to the function was an invalid enum value.
     * 
     * <p>One of the arguments to the function was an invalid enum value, for example requesting {@link #GLFW_RED_BITS RED_BITS} with {@link #glfwGetWindowAttrib GetWindowAttrib}.</p>
     * </li>
     * <li>{@link #GLFW_INVALID_VALUE INVALID_VALUE} - 
     * One of the arguments to the function was an invalid value.
     * 
     * <p>One of the arguments to the function was an invalid value, for example requesting a non-existent OpenGL or OpenGL ES version like 2.7.</p>
     * 
     * <p>Requesting a valid but unavailable OpenGL or OpenGL ES version will instead result in a {@link #GLFW_VERSION_UNAVAILABLE VERSION_UNAVAILABLE} error.</p>
     * </li>
     * <li>{@link #GLFW_OUT_OF_MEMORY OUT_OF_MEMORY} - 
     * A memory allocation failed.
     * 
     * <p>A bug in GLFW or the underlying operating system. Report the bug to our <a target="_blank" href="https://github.com/glfw/glfw/issues">issue tracker</a>.</p>
     * </li>
     * <li>{@link #GLFW_API_UNAVAILABLE API_UNAVAILABLE} - 
     * GLFW could not find support for the requested API on the system.
     * 
     * <p>The installed graphics driver does not support the requested API, or does not support it via the chosen context creation backend. Below are a few
     * examples:</p>
     * 
     * <p>Some pre-installed Windows graphics drivers do not support OpenGL. AMD only supports OpenGL ES via EGL, while Nvidia and Intel only support it via
     * a WGL or GLX extension. macOS does not provide OpenGL ES at all. The Mesa EGL, OpenGL and OpenGL ES libraries do not interface with the Nvidia
     * binary driver. Older graphics drivers do not support Vulkan.</p>
     * </li>
     * <li>{@link #GLFW_VERSION_UNAVAILABLE VERSION_UNAVAILABLE} - 
     * The requested OpenGL or OpenGL ES version (including any requested context or framebuffer hints) is not available on this machine.
     * 
     * <p>The machine does not support your requirements. If your application is sufficiently flexible, downgrade your requirements and try again. Otherwise,
     * inform the user that their machine does not match your requirements.</p>
     * 
     * <p>Future invalid OpenGL and OpenGL ES versions, for example OpenGL 4.8 if 5.0 comes out before the 4.x series gets that far, also fail with this
     * error and not {@link #GLFW_INVALID_VALUE INVALID_VALUE}, because GLFW cannot know what future versions will exist.</p>
     * </li>
     * <li>{@link #GLFW_PLATFORM_ERROR PLATFORM_ERROR} - 
     * A platform-specific error occurred that does not match any of the more specific categories.
     * 
     * <p>A bug or configuration error in GLFW, the underlying operating system or its drivers, or a lack of required resources. Report the issue to our
     * <a target="_blank" href="https://github.com/glfw/glfw/issues">issue tracker</a>.</p>
     * </li>
     * <li>{@link #GLFW_FORMAT_UNAVAILABLE FORMAT_UNAVAILABLE} - 
     * The requested format is not supported or available.
     * 
     * <p>If emitted during window creation, one or more hard constraints did not match any of the available pixel formats. If your application is
     * sufficiently flexible, downgrade your requirements and try again. Otherwise, inform the user that their machine does not match your requirements.</p>
     * 
     * <p>If emitted when querying the clipboard, ignore the error or report it to the user, as appropriate.</p>
     * </li>
     * <li>{@link #GLFW_NO_WINDOW_CONTEXT NO_WINDOW_CONTEXT} - 
     * The specified window does not have an OpenGL or OpenGL ES context.
     * 
     * <p>A window that does not have an OpenGL or OpenGL ES context was passed to a function that requires it to have one.</p>
     * 
     * <p>Application programmer error. Fix the offending call.</p>
     * </li>
     * </ul>
     */
    public static final int
        GLFW_NO_ERROR            = 0,
        GLFW_NOT_INITIALIZED     = 0x10001,
        GLFW_NO_CURRENT_CONTEXT  = 0x10002,
        GLFW_INVALID_ENUM        = 0x10003,
        GLFW_INVALID_VALUE       = 0x10004,
        GLFW_OUT_OF_MEMORY       = 0x10005,
        GLFW_API_UNAVAILABLE     = 0x10006,
        GLFW_VERSION_UNAVAILABLE = 0x10007,
        GLFW_PLATFORM_ERROR      = 0x10008,
        GLFW_FORMAT_UNAVAILABLE  = 0x10009,
        GLFW_NO_WINDOW_CONTEXT   = 0x1000A;

    /**
     * Window attributes.
     * 
     * <h5>Enum values:</h5>
     * 
     * <ul>
     * <li>{@link #GLFW_FOCUSED FOCUSED} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will be given input focus when created. This hint is ignored for full screen and
     * initially hidden windows.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window has input focus.</p>
     * </li>
     * <li>{@link #GLFW_ICONIFIED ICONIFIED} - {@code GetWindowAttrib}: Indicates whether the specified window is iconified, whether by the user or with {@link #glfwIconifyWindow IconifyWindow}.</li>
     * <li>{@link #GLFW_RESIZABLE RESIZABLE} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will be resizable <i>by the user</i>. The window will still be resizable using the
     * {@link #glfwSetWindowSize SetWindowSize} function. This hint is ignored for full screen windows.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window is resizable <i>by the user</i>.</p>
     * </li>
     * <li>{@link #GLFW_VISIBLE VISIBLE} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will be initially visible. This hint is ignored for full screen windows. Windows created
     * hidden are completely invisible to the user until shown. This can be useful if you need to set up your window further before showing it, for
     * example moving it to a specific location.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window is visible. Window visibility can be controlled with {@link #glfwShowWindow ShowWindow} and {@link #glfwHideWindow HideWindow}.</p>
     * </li>
     * <li>{@link #GLFW_DECORATED DECORATED} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will have window decorations such as a border, a close widget, etc. An undecorated window
     * may still allow the user to generate close events on some platforms. This hint is ignored for full screen windows.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window has decorations such as a border, a close widget, etc.</p>
     * </li>
     * <li>{@link #GLFW_AUTO_ICONIFY AUTO_ICONIFY} - 
     * {@code WindowHint}: Specifies whether the full screen window will automatically iconify and restore the previous video mode on input focus loss. This
     * hint is ignored for windowed mode windows.
     * </li>
     * <li>{@link #GLFW_FLOATING FLOATING} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will be floating above other regular windows, also called topmost or always-on-top. This
     * is intended primarily for debugging purposes and cannot be used to implement proper full screen windows. This hint is ignored for full screen
     * windows.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window is floating, also called topmost or always-on-top.</p>
     * </li>
     * <li>{@link #GLFW_MAXIMIZED MAXIMIZED} - 
     * {@code WindowHint}: Specifies whether the windowed mode window will be maximized when created. This hint is ignored for full screen windows.
     * 
     * <p>{@code GetWindowAttrib}: Indicates whether the specified window is maximized, whether by the user or {@link #glfwMaximizeWindow MaximizeWindow}.</p>
     * </li>
     * <li>{@link #GLFW_CENTER_CURSOR CENTER_CURSOR} - 
     * {@code WindowHint}: Specifies whether the cursor should be centered over newly created full screen windows. This hint is ignored for windowed mode
     * windows.
     * </li>
     * <li>{@link #GLFW_TRANSPARENT_FRAMEBUFFER TRANSPARENT_FRAMEBUFFER} - 
     * {@code WindowHint}: specifies whether the window framebuffer will be transparent. If enabled and supported by the system, the window framebuffer
     * alpha channel will be used to combine the framebuffer with the background. This does not affect window decorations.
     * </li>
     * <li>{@link #GLFW_HOVERED HOVERED} - {@code GetWindowAttrib}: indicates whether the cursor is currently directly over the client area of the window, with no other windows between.</li>
     * </ul>
     */
    public static final int
        GLFW_FOCUSED                 = 0x20001,
        GLFW_ICONIFIED               = 0x20002,
        GLFW_RESIZABLE               = 0x20003,
        GLFW_VISIBLE                 = 0x20004,
        GLFW_DECORATED               = 0x20005,
        GLFW_AUTO_ICONIFY            = 0x20006,
        GLFW_FLOATING                = 0x20007,
        GLFW_MAXIMIZED               = 0x20008,
        GLFW_CENTER_CURSOR           = 0x20009,
        GLFW_TRANSPARENT_FRAMEBUFFER = 0x2000A,
        GLFW_HOVERED                 = 0x2000A;

    /** Input options. */
    public static final int
        GLFW_CURSOR               = 0x33001,
        GLFW_STICKY_KEYS          = 0x33002,
        GLFW_STICKY_MOUSE_BUTTONS = 0x33003,
        GLFW_LOCK_KEY_MODS        = 0x33004;

    /** Cursor state. */
    public static final int
        GLFW_CURSOR_NORMAL   = 0x34001,
        GLFW_CURSOR_HIDDEN   = 0x34002,
        GLFW_CURSOR_DISABLED = 0x34003;

    /** Standard cursor shapes. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#cursor_standard">standard cursor creation</a> for how these are used. */
    public static final int
        GLFW_ARROW_CURSOR     = 0x36001,
        GLFW_IBEAM_CURSOR     = 0x36002,
        GLFW_CROSSHAIR_CURSOR = 0x36003,
        GLFW_HAND_CURSOR      = 0x36004,
        GLFW_HRESIZE_CURSOR   = 0x36005,
        GLFW_VRESIZE_CURSOR   = 0x36006;

    /** Monitor events. */
    public static final int
        GLFW_CONNECTED    = 0x40001,
        GLFW_DISCONNECTED = 0x40002;

    /** Init hints. */
    public static final int
        GLFW_JOYSTICK_HAT_BUTTONS  = 0x50001,
        GLFW_COCOA_CHDIR_RESOURCES = 0x51001,
        GLFW_COCOA_MENUBAR         = 0x51002;

    /** Don't care value. */
    public static final int GLFW_DONT_CARE = -1;

    /** PixelFormat hints. */
    public static final int
        GLFW_RED_BITS         = 0x21001,
        GLFW_GREEN_BITS       = 0x21002,
        GLFW_BLUE_BITS        = 0x21003,
        GLFW_ALPHA_BITS       = 0x21004,
        GLFW_DEPTH_BITS       = 0x21005,
        GLFW_STENCIL_BITS     = 0x21006,
        GLFW_ACCUM_RED_BITS   = 0x21007,
        GLFW_ACCUM_GREEN_BITS = 0x21008,
        GLFW_ACCUM_BLUE_BITS  = 0x21009,
        GLFW_ACCUM_ALPHA_BITS = 0x2100A,
        GLFW_AUX_BUFFERS      = 0x2100B,
        GLFW_STEREO           = 0x2100C,
        GLFW_SAMPLES          = 0x2100D,
        GLFW_SRGB_CAPABLE     = 0x2100E,
        GLFW_REFRESH_RATE     = 0x2100F,
        GLFW_DOUBLEBUFFER     = 0x21010;

    /**
     * Client API hints.
     * 
     * <h5>Enum values:</h5>
     * 
     * <ul>
     * <li>{@link #GLFW_CLIENT_API CLIENT_API} - 
     * {@code WindowHint}: Specifies which client API to create the context for. Possible values are {@link #GLFW_OPENGL_API OPENGL_API}, {@link #GLFW_OPENGL_ES_API OPENGL_ES_API} and {@link #GLFW_NO_API NO_API}. This is a hard
     * constraint.
     * 
     * <p>{@code GetWindowAttrib}: Indicates the client API provided by the window's context; either {@link #GLFW_OPENGL_API OPENGL_API}, {@link #GLFW_OPENGL_ES_API OPENGL_ES_API} or {@link #GLFW_NO_API NO_API}.</p>
     * </li>
     * <li>{@link #GLFW_CONTEXT_VERSION_MAJOR CONTEXT_VERSION_MAJOR} - 
     * {@code WindowHint}: Specifies the client API major version that the created context must be compatible with. The exact behavior of this hint depends on
     * the requested client API.
     * 
     * <div style="margin-left: 26px; border-left: 1px solid gray; padding-left: 14px;"><h5>Note</h5>
     * 
     * <ul>
     * <li>While there is no way to ask the driver for a context of the highest supported version, GLFW will attempt to provide this when you ask for a
     * version 1.0 context, which is the default for these hints.</li>
     * <li><b>OpenGL</b>: {@link #GLFW_CONTEXT_VERSION_MAJOR CONTEXT_VERSION_MAJOR} and {@link #GLFW_CONTEXT_VERSION_MINOR CONTEXT_VERSION_MINOR} are not hard constraints, but creation will fail if the OpenGL version of the
     * created context is less than the one requested. It is therefore perfectly safe to use the default of version 1.0 for legacy code and you will
     * still get backwards-compatible contexts of version 3.0 and above when available.</li>
     * <li><b>OpenGL ES</b>: {@link #GLFW_CONTEXT_VERSION_MAJOR CONTEXT_VERSION_MAJOR} and {@link #GLFW_CONTEXT_VERSION_MINOR CONTEXT_VERSION_MINOR} are not hard constraints, but creation will fail if the OpenGL ES version
     * of the created context is less than the one requested. Additionally, OpenGL ES 1.x cannot be returned if 2.0 or later was requested, and vice
     * versa. This is because OpenGL ES 3.x is backward compatible with 2.0, but OpenGL ES 2.0 is not backward compatible with 1.x.</li>
     * </ul></div>
     * 
     * <p>{@code GetWindowAttrib}: Indicate the client API major version of the window's context.</p>
     * </li>
     * <li>{@link #GLFW_CONTEXT_VERSION_MINOR CONTEXT_VERSION_MINOR} - 
     * {@code WindowHint}: Specifies the client API minor version that the created context must be compatible with. The exact behavior of this hint depends on
     * the requested client API.
     * 
     * <p>{@code GetWindowAttrib}: Indicate the client API minor version of the window's context.</p>
     * </li>
     * <li>{@link #GLFW_CONTEXT_REVISION CONTEXT_REVISION} - {@code GetWindowAttrib}: Indicates the client API version of the window's context.</li>
     * <li>{@link #GLFW_CONTEXT_ROBUSTNESS CONTEXT_ROBUSTNESS} - 
     * {@code WindowHint}: Specifies the robustness strategy to be used by the context. This can be one of {@link #GLFW_NO_RESET_NOTIFICATION NO_RESET_NOTIFICATION} or {@link #GLFW_LOSE_CONTEXT_ON_RESET LOSE_CONTEXT_ON_RESET}, or
     * {@link #GLFW_NO_ROBUSTNESS NO_ROBUSTNESS} to not request a robustness strategy.
     * 
     * <p>{@code GetWindowAttrib}: Indicates the robustness strategy used by the context. This is {@link #GLFW_LOSE_CONTEXT_ON_RESET LOSE_CONTEXT_ON_RESET} or {@link #GLFW_NO_RESET_NOTIFICATION NO_RESET_NOTIFICATION} if the window's
     * context supports robustness, or {@link #GLFW_NO_ROBUSTNESS NO_ROBUSTNESS} otherwise.</p>
     * </li>
     * <li>{@link #GLFW_OPENGL_FORWARD_COMPAT OPENGL_FORWARD_COMPAT} - 
     * {@code WindowHint}: Specifies whether the OpenGL context should be forward-compatible, i.e. one where all functionality deprecated in the requested
     * version of OpenGL is removed. This must only be used if the requested OpenGL version is 3.0 or above. If OpenGL ES is requested, this hint is
     * ignored.
     * 
     * <p>{@code GetWindowAttrib}: Indicates if the window's context is an OpenGL forward-compatible one.</p>
     * </li>
     * <li>{@link #GLFW_OPENGL_DEBUG_CONTEXT OPENGL_DEBUG_CONTEXT} - 
     * {@code WindowHint}: Specifies whether to create a debug OpenGL context, which may have additional error and performance issue reporting functionality.
     * If OpenGL ES is requested, this hint is ignored.
     * 
     * <p>{@code GetWindowAttrib}: Indicates if the window's context is an OpenGL debug context.</p>
     * </li>
     * <li>{@link #GLFW_OPENGL_PROFILE OPENGL_PROFILE} - 
     * {@code WindowHint}: Specifies which OpenGL profile to create the context for. Possible values are one of {@link #GLFW_OPENGL_CORE_PROFILE OPENGL_CORE_PROFILE} or {@link #GLFW_OPENGL_COMPAT_PROFILE OPENGL_COMPAT_PROFILE},
     * or {@link #GLFW_OPENGL_ANY_PROFILE OPENGL_ANY_PROFILE} to not request a specific profile. If requesting an OpenGL version below 3.2, {@link #GLFW_OPENGL_ANY_PROFILE OPENGL_ANY_PROFILE} must be used. If OpenGL ES
     * is requested, this hint is ignored.
     * 
     * <p>{@code GetWindowAttrib}: Indicates the OpenGL profile used by the context. This is {@link #GLFW_OPENGL_CORE_PROFILE OPENGL_CORE_PROFILE} or {@link #GLFW_OPENGL_COMPAT_PROFILE OPENGL_COMPAT_PROFILE} if the context uses a
     * known profile, or {@link #GLFW_OPENGL_ANY_PROFILE OPENGL_ANY_PROFILE} if the OpenGL profile is unknown or the context is an OpenGL ES context. Note that the returned profile may
     * not match the profile bits of the context flags, as GLFW will try other means of detecting the profile when no bits are set.</p>
     * </li>
     * <li>{@link #GLFW_CONTEXT_RELEASE_BEHAVIOR CONTEXT_RELEASE_BEHAVIOR} - 
     * {@code WindowHint}: Specifies the release behavior to be used by the context. If the behavior is {@link #GLFW_ANY_RELEASE_BEHAVIOR ANY_RELEASE_BEHAVIOR}, the default behavior of the
     * context creation API will be used. If the behavior is {@link #GLFW_RELEASE_BEHAVIOR_FLUSH RELEASE_BEHAVIOR_FLUSH}, the pipeline will be flushed whenever the context is released from
     * being the current one. If the behavior is {@link #GLFW_RELEASE_BEHAVIOR_NONE RELEASE_BEHAVIOR_NONE}, the pipeline will not be flushed on release.
     * </li>
     * <li>{@link #GLFW_CONTEXT_NO_ERROR CONTEXT_NO_ERROR} - 
     * {@code WindowHint}: Specifies whether errors should be generated by the context. If enabled, situations that would have generated errors instead cause
     * undefined behavior.
     * </li>
     * <li>{@link #GLFW_CONTEXT_CREATION_API CONTEXT_CREATION_API} - 
     * {@code WindowHint}: Specifies which context creation API to use to create the context. Possible values are {@link #GLFW_NATIVE_CONTEXT_API NATIVE_CONTEXT_API} and {@link #GLFW_EGL_CONTEXT_API EGL_CONTEXT_API}.
     * This is a hard constraint. If no client API is requested, this hint is ignored.
     * 
     * <div style="margin-left: 26px; border-left: 1px solid gray; padding-left: 14px;"><h5>Note</h5>
     * 
     * <ul>
     * <li><b>macOS</b>: The EGL API is not available on this platform and requests to use it will fail.</li>
     * <li><b>Wayland, Mir</b>: The EGL API <i>is</i> the native context creation API, so this hint will have no effect.</li>
     * <li>An OpenGL extension loader library that assumes it knows which context creation API is used on a given platform may fail if you change this
     * hint. This can be resolved by having it load via {@link #glfwGetProcAddress GetProcAddress}, which always uses the selected API.</li>
     * </ul></div>
     * 
     * <p>{@code GetWindowAttrib}: Indicates the context creation API used to create the window's context; either {@link #GLFW_NATIVE_CONTEXT_API NATIVE_CONTEXT_API} or {@link #GLFW_EGL_CONTEXT_API EGL_CONTEXT_API}.</p>
     * </li>
     * </ul>
     */
    public static final int
        GLFW_CLIENT_API               = 0x22001,
        GLFW_CONTEXT_VERSION_MAJOR    = 0x22002,
        GLFW_CONTEXT_VERSION_MINOR    = 0x22003,
        GLFW_CONTEXT_REVISION         = 0x22004,
        GLFW_CONTEXT_ROBUSTNESS       = 0x22005,
        GLFW_OPENGL_FORWARD_COMPAT    = 0x22006,
        GLFW_OPENGL_DEBUG_CONTEXT     = 0x22007,
        GLFW_OPENGL_PROFILE           = 0x22008,
        GLFW_CONTEXT_RELEASE_BEHAVIOR = 0x22009,
        GLFW_CONTEXT_NO_ERROR         = 0x2200A,
        GLFW_CONTEXT_CREATION_API     = 0x2200B;

    /** Specifies whether to use full resolution framebuffers on Retina displays. This is ignored on other platforms. */
    public static final int GLFW_COCOA_RETINA_FRAMEBUFFER = 0x23001;

    /**
     * Specifies the UTF-8 encoded name to use for autosaving the window frame, or if empty disables frame autosaving for the window. This is ignored on other
     * platforms. This is set with {@link #glfwWindowHintString WindowHintString}.
     */
    public static final int GLFW_COCOA_FRAME_NAME = 0x23002;

    /**
     * Specifies whether to enable Automatic Graphics Switching, i.e. to allow the system to choose the integrated GPU for the OpenGL context and move it
     * between GPUs if necessary or whether to force it to always run on the discrete GPU. This only affects systems with both integrated and discrete GPUs.
     * This is ignored on other platforms.
     */
    public static final int GLFW_COCOA_GRAPHICS_SWITCHING = 0x23003;

    /** The desired ASCII encoded class and instance parts of the ICCCM {@code WM_CLASS} window property. These are set with {@link #glfwWindowHintString WindowHintString}. */
    public static final int
        GLFW_X11_CLASS_NAME    = 0x24001,
        GLFW_X11_INSTANCE_NAME = 0x24002;

    /** Values for the {@link #GLFW_CLIENT_API CLIENT_API} hint. */
    public static final int
        GLFW_NO_API        = 0,
        GLFW_OPENGL_API    = 0x30001,
        GLFW_OPENGL_ES_API = 0x30002;

    /** Values for the {@link #GLFW_CONTEXT_ROBUSTNESS CONTEXT_ROBUSTNESS} hint. */
    public static final int
        GLFW_NO_ROBUSTNESS         = 0,
        GLFW_NO_RESET_NOTIFICATION = 0x31001,
        GLFW_LOSE_CONTEXT_ON_RESET = 0x31002;

    /** Values for the {@link #GLFW_OPENGL_PROFILE OPENGL_PROFILE} hint. */
    public static final int
        GLFW_OPENGL_ANY_PROFILE    = 0,
        GLFW_OPENGL_CORE_PROFILE   = 0x32001,
        GLFW_OPENGL_COMPAT_PROFILE = 0x32002;

    /** Values for the {@link #GLFW_CONTEXT_RELEASE_BEHAVIOR CONTEXT_RELEASE_BEHAVIOR} hint. */
    public static final int
        GLFW_ANY_RELEASE_BEHAVIOR   = 0,
        GLFW_RELEASE_BEHAVIOR_FLUSH = 0x35001,
        GLFW_RELEASE_BEHAVIOR_NONE  = 0x35002;

    /** Values for the {@link #GLFW_CONTEXT_CREATION_API CONTEXT_CREATION_API} hint. */
    public static final int
        GLFW_NATIVE_CONTEXT_API = 0x36001,
        GLFW_EGL_CONTEXT_API    = 0x36002,
        GLFW_OSMESA_CONTEXT_API = 0x36003;

    /* Boardwalk */
    private static final long MONITOR_MAGIC = 0x33445566;
    private static final long WINDOW_MAGIC = 0x33445567;
    private static final long CURSOR_MAGIC = 0x33445568;
    private static final long CONTEXT_MAGIC = 0x33445569;

    protected GLFW() {
        throw new UnsupportedOperationException();
    }

    public static boolean glfwInit() {
        return true;
    }
    public static void glfwTerminate() {}
    public static void glfwInitHint(int hint, int value) {}
    public static void glfwGetVersion(@Nullable @NativeType("int *") IntBuffer major, @Nullable @NativeType("int *") IntBuffer minor, @Nullable @NativeType("int *") IntBuffer rev) {}
    public static String glfwGetVersionString() {
        return "Boardwalk";
    }
    public static int glfwGetError(@Nullable @NativeType("const char **") PointerBuffer description) {
        return 0;
    }
    public static GLFWErrorCallback glfwSetErrorCallback(@Nullable @NativeType("GLFWerrorfun") GLFWErrorCallbackI cbfun) {
        return null;
    }
    public static PointerBuffer glfwGetMonitors() {
        PointerBuffer buffer = PointerBuffer.allocateDirect(1);
        buffer.put(MONITOR_MAGIC);
        return buffer;
    }
    public static long glfwGetPrimaryMonitor() {
        return MONITOR_MAGIC;
    }
    public static void glfwGetMonitorPos(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") IntBuffer xpos, @Nullable @NativeType("int *") IntBuffer ypos) {}
    public static void glfwGetMonitorPhysicalSize(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") IntBuffer widthMM, @Nullable @NativeType("int *") IntBuffer heightMM) {}
    public static void glfwGetMonitorContentScale(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("float *") FloatBuffer xscale, @Nullable @NativeType("float *") FloatBuffer yscale) {}
    public static String glfwGetMonitorName(@NativeType("GLFWmonitor *") long monitor) {
        return "Boardwalk";
    }
    public static void glfwSetMonitorUserPointer(@NativeType("GLFWmonitor *") long monitor, @NativeType("void *") long pointer) {}
    public static long glfwGetMonitorUserPointer(@NativeType("GLFWmonitor *") long monitor) {
        return 0;
    }
    public static GLFWMonitorCallback glfwSetMonitorCallback(@Nullable @NativeType("GLFWmonitorfun") GLFWMonitorCallbackI cbfun) {
        return null;
    }
    private static ByteBuffer getVideoModeBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(GLFWVidMode.SIZEOF);
        IntBuffer iBuffer = buffer.asIntBuffer();
        iBuffer.put(new int[] {Integer.parseInt(System.getProperty("boardwalk.windowWidth", "1024")),
            Integer.parseInt(System.getProperty("boardwalk.windowHeight", "768")),
            8, 8, 8, 60});
        return buffer;
    }
    public static GLFWVidMode.Buffer glfwGetVideoModes(@NativeType("GLFWmonitor *") long monitor) {
        return new GLFWVidMode.Buffer(getVideoModeBuffer());
    }
    public static GLFWVidMode glfwGetVideoMode(@NativeType("GLFWmonitor *") long monitor) {
        return new GLFWVidMode(getVideoModeBuffer());
    }
    public static void glfwSetGamma(@NativeType("GLFWmonitor *") long monitor, float gamma) {}
    public static GLFWGammaRamp glfwGetGammaRamp(@NativeType("GLFWmonitor *") long monitor) {
        throw new RuntimeException("why");
    }
    public static void glfwSetGammaRamp(@NativeType("GLFWmonitor *") long monitor, @NativeType("const GLFWgammaramp *") GLFWGammaRamp ramp) {}
    public static void glfwDefaultWindowHints() {}
    public static void glfwWindowHint(int hint, int value) {}
    public static void glfwWindowHintString(int hint, @NativeType("const char *") ByteBuffer value) {}
    public static void glfwWindowHintString(int hint, @NativeType("const char *") CharSequence value) {}
    public static long glfwCreateWindow(int width, int height, @NativeType("const char *") ByteBuffer title, @NativeType("GLFWmonitor *") long monitor, @NativeType("GLFWwindow *") long share) {
        return WINDOW_MAGIC;
    }
    public static long glfwCreateWindow(int width, int height, @NativeType("const char *") CharSequence title, @NativeType("GLFWmonitor *") long monitor, @NativeType("GLFWwindow *") long share) {
        return WINDOW_MAGIC;
    }
    public static void glfwDestroyWindow(@NativeType("GLFWwindow *") long window) {}
    public static boolean glfwWindowShouldClose(@NativeType("GLFWwindow *") long window) {
        return false;
    }
    public static void glfwSetWindowShouldClose(@NativeType("GLFWwindow *") long window, @NativeType("int") boolean value) {}
    public static void glfwSetWindowTitle(@NativeType("GLFWwindow *") long window, @NativeType("const char *") ByteBuffer title) {}
    public static void glfwSetWindowTitle(@NativeType("GLFWwindow *") long window, @NativeType("const char *") CharSequence title) {}
    public static void glfwSetWindowIcon(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("const GLFWimage *") GLFWImage.Buffer images) {}
    public static void glfwGetWindowPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") IntBuffer xpos, @Nullable @NativeType("int *") IntBuffer ypos) {}
    public static void glfwSetWindowPos(@NativeType("GLFWwindow *") long window, int xpos, int ypos) {}
    public static void glfwGetWindowSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") IntBuffer width, @Nullable @NativeType("int *") IntBuffer height) {}
    public static void glfwSetWindowSizeLimits(@NativeType("GLFWwindow *") long window, int minwidth, int minheight, int maxwidth, int maxheight) {}
    public static void glfwSetWindowAspectRatio(@NativeType("GLFWwindow *") long window, int numer, int denom) {}
    public static void glfwSetWindowSize(@NativeType("GLFWwindow *") long window, int width, int height) {}
    public static void glfwGetFramebufferSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") IntBuffer width, @Nullable @NativeType("int *") IntBuffer height) {}
    public static void glfwGetWindowFrameSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") IntBuffer left, @Nullable @NativeType("int *") IntBuffer top, @Nullable @NativeType("int *") IntBuffer right, @Nullable @NativeType("int *") IntBuffer bottom) {}
    public static void glfwGetWindowContentScale(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("float *") FloatBuffer xscale, @Nullable @NativeType("float *") FloatBuffer yscale) {}
    public static float glfwGetWindowOpacity(@NativeType("GLFWwindow *") long window) {
        return 1.0f;
    }
    public static void glfwSetWindowOpacity(@NativeType("GLFWwindow *") long window, float opacity) {}
    public static void glfwIconifyWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwRestoreWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwMaximizeWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwShowWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwHideWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwFocusWindow(@NativeType("GLFWwindow *") long window) {}
    public static void glfwRequestWindowAttention(@NativeType("GLFWwindow *") long window) {}
    public static long glfwGetWindowMonitor(@NativeType("GLFWwindow *") long window) {
        return MONITOR_MAGIC;
    }
    public static void glfwSetWindowMonitor(@NativeType("GLFWwindow *") long window, @NativeType("GLFWmonitor *") long monitor, int xpos, int ypos, int width, int height, int refreshRate) {}
    public static int glfwGetWindowAttrib(@NativeType("GLFWwindow *") long window, int attrib) {
        return 0;
    }
    public static void glfwSetWindowAttrib(@NativeType("GLFWwindow *") long window, int attrib, int value) {}
    public static void glfwSetWindowUserPointer(@NativeType("GLFWwindow *") long window, @NativeType("void *") long pointer) {}
    public static long glfwGetWindowUserPointer(@NativeType("GLFWwindow *") long window) {
        return 0;
    }
    public static GLFWWindowPosCallback glfwSetWindowPosCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowposfun") GLFWWindowPosCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowSizeCallback glfwSetWindowSizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowsizefun") GLFWWindowSizeCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowCloseCallback glfwSetWindowCloseCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowclosefun") GLFWWindowCloseCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowRefreshCallback glfwSetWindowRefreshCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowrefreshfun") GLFWWindowRefreshCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowFocusCallback glfwSetWindowFocusCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowfocusfun") GLFWWindowFocusCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowIconifyCallback glfwSetWindowIconifyCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowiconifyfun") GLFWWindowIconifyCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowMaximizeCallback glfwSetWindowMaximizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowmaximizefun") GLFWWindowMaximizeCallbackI cbfun) {
        return null;
    }
    public static GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWframebuffersizefun") GLFWFramebufferSizeCallbackI cbfun) {
        return null;
    }
    public static GLFWWindowContentScaleCallback glfwSetWindowContentScaleCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWwindowcontentscalefun") GLFWWindowContentScaleCallbackI cbfun) {
        return null;
    }
    public static void glfwPollEvents() {}
    public static void glfwWaitEvents() {}
    public static void glfwWaitEventsTimeout(double timeout) {}
    public static void glfwPostEmptyEvent() {}
    public static int glfwGetInputMode(@NativeType("GLFWwindow *") long window, int mode) {
        return 0;
    }
    public static void glfwSetInputMode(@NativeType("GLFWwindow *") long window, int mode, int value) {}
    public static String glfwGetKeyName(int key, int scancode) {
        return "Keyname todo";
    }
    public static int glfwGetKeyScancode(int key) {
        return 0;
    }
    public static int glfwGetKey(@NativeType("GLFWwindow *") long window, int key) {
        return 0;
    }
    public static int glfwGetMouseButton(@NativeType("GLFWwindow *") long window, int button) {
        return 0;
    }
    public static void glfwGetCursorPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") DoubleBuffer xpos, @Nullable @NativeType("double *") DoubleBuffer ypos) {}
    public static void glfwSetCursorPos(@NativeType("GLFWwindow *") long window, double xpos, double ypos) {}
    public static long glfwCreateCursor(@NativeType("const GLFWimage *") GLFWImage image, int xhot, int yhot) {
        return CURSOR_MAGIC;
    }
    public static long glfwCreateStandardCursor(int shape) {
        return CURSOR_MAGIC;
    }
    public static void glfwDestroyCursor(@NativeType("GLFWcursor *") long cursor) {}
    public static void glfwSetCursor(@NativeType("GLFWwindow *") long window, @NativeType("GLFWcursor *") long cursor) {}
    public static GLFWKeyCallback glfwSetKeyCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWkeyfun") GLFWKeyCallbackI cbfun) {
        return null;
    }
    public static GLFWCharCallback glfwSetCharCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcharfun") GLFWCharCallbackI cbfun) {
        return null;
    }
    public static GLFWCharModsCallback glfwSetCharModsCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcharmodsfun") GLFWCharModsCallbackI cbfun) {
        return null;
    }
    public static GLFWMouseButtonCallback glfwSetMouseButtonCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWmousebuttonfun") GLFWMouseButtonCallbackI cbfun) {
        return null;
    }
    public static GLFWCursorPosCallback glfwSetCursorPosCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcursorposfun") GLFWCursorPosCallbackI cbfun) {
        return null;
    }
    public static GLFWCursorEnterCallback glfwSetCursorEnterCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcursorenterfun") GLFWCursorEnterCallbackI cbfun) {
        return null;
    }
    public static GLFWScrollCallback glfwSetScrollCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWscrollfun") GLFWScrollCallbackI cbfun) {
        return null;
    }
    public static GLFWDropCallback glfwSetDropCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWdropfun") GLFWDropCallbackI cbfun) {
        return null;
    }
    public static boolean glfwJoystickPresent(int jid) {
        return false;
    }
    public static FloatBuffer glfwGetJoystickAxes(int jid) {
        return null;
    }
    public static ByteBuffer glfwGetJoystickButtons(int jid) {
        return null;
    }
    public static ByteBuffer glfwGetJoystickHats(int jid) {
        return null;
    }
    public static String glfwGetJoystickName(int jid) {
        return null;
    }
    public static String glfwGetJoystickGUID(int jid) {
        return null;
    }
    public static void glfwSetJoystickUserPointer(int jid, @NativeType("void *") long pointer) {}
    public static long glfwGetJoystickUserPointer(int jid) {
        return 0;
    }
    public static boolean glfwJoystickIsGamepad(int jid) {
        return false;
    }
    public static GLFWJoystickCallback glfwSetJoystickCallback(@Nullable @NativeType("GLFWjoystickfun") GLFWJoystickCallbackI cbfun) {
        return null;
    }
    public static boolean glfwUpdateGamepadMappings(@NativeType("const char *") ByteBuffer string) {
        return false;
    }
    public static boolean glfwUpdateGamepadMappings(@NativeType("const char *") CharSequence string) {
        return false;
    }
    public static String glfwGetGamepadName(int jid) {
        return null;
    }
    public static boolean glfwGetGamepadState(int jid, @NativeType("GLFWgamepadstate *") GLFWGamepadState state) {
        return false;
    }
    public static void glfwSetClipboardString(@NativeType("GLFWwindow *") long window, @NativeType("const char *") ByteBuffer string) {}
    public static void glfwSetClipboardString(@NativeType("GLFWwindow *") long window, @NativeType("const char *") CharSequence string) {}
    public static String glfwGetClipboardString(@NativeType("GLFWwindow *") long window) {
        return "Clipboard";
    }
    public static double glfwGetTime() {
        return 0;
    }
    public static void glfwSetTime(double time) {}
    public static long glfwGetTimerValue() {
        return 0;
    }
    public static long glfwGetTimerFrequency() {
        return 0;
    }
    public static void glfwMakeContextCurrent(@NativeType("GLFWwindow *") long window) {}
    public static long glfwGetCurrentContext() {
        return CONTEXT_MAGIC;
    }
    public static void glfwSwapBuffers(@NativeType("GLFWwindow *") long window) {}
    public static void glfwSwapInterval(int interval) {}
    public static boolean glfwExtensionSupported(@NativeType("const char *") ByteBuffer extension) {
        return false;
    }
    public static boolean glfwExtensionSupported(@NativeType("const char *") CharSequence extension) {
        return false;
    }
    public static long glfwGetProcAddress(@NativeType("const char *") ByteBuffer procname) {
        return 0;
    }
    public static long glfwGetProcAddress(@NativeType("const char *") CharSequence procname) {
        return 0;
    }
    public static void glfwGetVersion(@Nullable @NativeType("int *") int[] major, @Nullable @NativeType("int *") int[] minor, @Nullable @NativeType("int *") int[] rev) {}
    public static void glfwGetMonitorPos(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") int[] xpos, @Nullable @NativeType("int *") int[] ypos) {}
    public static void glfwGetMonitorPhysicalSize(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("int *") int[] widthMM, @Nullable @NativeType("int *") int[] heightMM) {}
    public static void glfwGetMonitorContentScale(@NativeType("GLFWmonitor *") long monitor, @Nullable @NativeType("float *") float[] xscale, @Nullable @NativeType("float *") float[] yscale) {}
    public static void glfwGetWindowPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] xpos, @Nullable @NativeType("int *") int[] ypos) {}
    public static void glfwGetWindowSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] width, @Nullable @NativeType("int *") int[] height) {}
    public static void glfwGetFramebufferSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] width, @Nullable @NativeType("int *") int[] height) {}
    public static void glfwGetWindowFrameSize(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("int *") int[] left, @Nullable @NativeType("int *") int[] top, @Nullable @NativeType("int *") int[] right, @Nullable @NativeType("int *") int[] bottom) {}
    public static void glfwGetWindowContentScale(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("float *") float[] xscale, @Nullable @NativeType("float *") float[] yscale) {}
    public static void glfwGetCursorPos(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("double *") double[] xpos, @Nullable @NativeType("double *") double[] ypos) {}

}
