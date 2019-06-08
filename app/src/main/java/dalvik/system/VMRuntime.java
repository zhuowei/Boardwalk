/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dalvik.system;

/**
 * Provides an interface to VM-global, Dalvik-specific features.
 * An application cannot create its own Runtime instance, and must obtain
 * one from the getRuntime method.
 *
 * @hide
 */
public final class VMRuntime {

    /**
     * Holds the VMRuntime singleton.
     */
    private static final VMRuntime THE_ONE = new VMRuntime();

    /**
     * Prevents this class from being instantiated.
     */
    private VMRuntime() {
    }

    /**
     * Returns the object that represents the VM instance's Dalvik-specific
     * runtime environment.
     *
     * @return the runtime object
     */
    public static VMRuntime getRuntime() {
        return THE_ONE;
    }

    /**
     * Gets the current ideal heap utilization, represented as a number
     * between zero and one.  After a GC happens, the Dalvik heap may
     * be resized so that (size of live objects) / (size of heap) is
     * equal to this number.
     *
     * @return the current ideal heap utilization
     */
    public native float getTargetHeapUtilization();

    /**
     * Sets the current ideal heap utilization, represented as a number
     * between zero and one.  After a GC happens, the Dalvik heap may
     * be resized so that (size of live objects) / (size of heap) is
     * equal to this number.
     *
     * <p>This is only a hint to the garbage collector and may be ignored.
     *
     * @param newTarget the new suggested ideal heap utilization.
     *                  This value may be adjusted internally.
     * @return the previous ideal heap utilization
     * @throws IllegalArgumentException if newTarget is &lt;= 0.0 or &gt;= 1.0
     */
    public float setTargetHeapUtilization(float newTarget) {
        if (newTarget <= 0.0 || newTarget >= 1.0) {
            throw new IllegalArgumentException(newTarget +
                    " out of range (0,1)");
        }
        /* Synchronize to make sure that only one thread gets
         * a given "old" value if both update at the same time.
         * Allows for reliable save-and-restore semantics.
         */
        synchronized (this) {
            float oldTarget = getTargetHeapUtilization();
            nativeSetTargetHeapUtilization(newTarget);
            return oldTarget;
        }
    }

    /**
     * Returns the minimum heap size, or zero if no minimum is in effect.
     *
     * @return the minimum heap size value
     */
    public long getMinimumHeapSize() {
        return nativeMinimumHeapSize(0, false);
    }

    /**
     * Sets the desired minimum heap size, and returns the
     * old minimum size.  If size is larger than the maximum
     * size, the maximum size will be used.  If size is zero
     * or negative, the minimum size constraint will be removed.
     *
     * <p>Synchronized to make the order of the exchange reliable.
     *
     * <p>This is only a hint to the garbage collector and may be ignored.
     *
     * @param size the new suggested minimum heap size, in bytes
     * @return the old minimum heap size value
     */
    public synchronized long setMinimumHeapSize(long size) {
        return nativeMinimumHeapSize(size, true);
    }

    /**
     * If set is true, sets the new minimum heap size to size; always
     * returns the current (or previous) size.
     *
     * @param size the new suggested minimum heap size, in bytes
     * @param set if true, set the size based on the size parameter,
     *            otherwise ignore it
     * @return the old or current minimum heap size value
     */
    private native long nativeMinimumHeapSize(long size, boolean set);

    /**
     * Requests that the virtual machine collect available memory,
     * and collects any SoftReferences that are not strongly-reachable.
     */
    public native void gcSoftReferences();

    /**
     * Does not return until any pending finalizers have been called.
     * This may or may not happen in the context of the calling thread.
     * No exceptions will escape.
     */
    public native void runFinalizationSync();

    /**
     * Implements setTargetHeapUtilization().
     *
     * @param newTarget the new suggested ideal heap utilization.
     *                  This value may be adjusted internally.
     */
    private native void nativeSetTargetHeapUtilization(float newTarget);

    /**
     * Asks the VM if &lt;size&gt; bytes can be allocated in an external heap.
     * This information may be used to limit the amount of memory available
     * to Dalvik threads.  Returns false if the VM would rather that the caller
     * did not allocate that much memory.  If the call returns false, the VM
     * will not update its internal counts.  May cause one or more GCs as a
     * side-effect.
     *
     * Called by JNI code.
     *
     * {@hide}
     *
     * @param size The number of bytes that have been allocated.
     * @return true if the VM thinks there's enough process memory
     *         to satisfy this request, or false if not.
     */
    @Deprecated
    public native boolean trackExternalAllocation(long size);

    /**
     * Tells the VM that &lt;size&gt; bytes have been freed in an external
     * heap.  This information may be used to control the amount of memory
     * available to Dalvik threads.
     *
     * Called by JNI code.
     *
     * {@hide}
     *
     * @param size The number of bytes that have been freed.  This same number
     *             should have been passed to trackExternalAlloc() when
     *             the underlying memory was originally allocated.
     */
    @Deprecated
    public native void trackExternalFree(long size);

    /**
     * Returns the number of externally-allocated bytes being tracked by
     * trackExternalAllocation/Free().
     *
     * @return the number of bytes
     */
    @Deprecated
    public native long getExternalBytesAllocated();

    /**
     * Tells the VM to enable the JIT compiler. If the VM does not have a JIT
     * implementation, calling this method should have no effect.
     *
     * {@hide}
     */
    public native void startJitCompilation();

    /**
     * Tells the VM to disable the JIT compiler. If the VM does not have a JIT
     * implementation, calling this method should have no effect.
     *
     * {@hide}
     */
    public native void disableJitCompilation();
}
