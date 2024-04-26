/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright 2024 Synaptics Incorporated */

package org.tensorflow.lite.external;

import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.annotations.UsedByReflection;

/** {@link Delegate} for External inference. */
public class ExternalDelegate implements Delegate, AutoCloseable {

  private static final long INVALID_DELEGATE_HANDLE = 0;

  private long delegateHandle;

  /** Delegate options. */
  public static final class Options {
    public Options() {}

    /**
     * Configure external delegate library path.
     *
     */
    public Options setLibPath(String libPath) {
      this.libPath = libPath;
      return this;
    }

    /**
     * Configure the location to be used to store model compilation cache entries. If either {@code
     * cacheDir} or {@code modelToken} parameters are unset External delegate caching will be disabled.
     *
     */
    public Options setCacheDir(String cacheDir) {
      this.cacheDir = cacheDir;
      return this;
    }

    /** Returns the external delegate library path. */
    public String getLibPath() {
      return this.libPath;
    }

    /** Returns the location to be used to store model compilation cache entries. */
    public String getCacheDir() {
      return this.cacheDir;
    }

    private String libPath = null;
    private String cacheDir = null;
  }

  private Options options;

  @UsedByReflection("Interpreter")
  public ExternalDelegate(Options options) {
    // Ensure the native TensorFlow Lite libraries are available.
    TensorFlowLite.init();
    this.options = options;
    delegateHandle = createDelegate(options.getLibPath(),
                                    options.getCacheDir());
  }


  @UsedByReflection("Interpreter")
  public ExternalDelegate() {
    this.options = new Options();
    this.options.setLibPath("/data/local/tmp/libvx_delegate.so");
    delegateHandle = createDelegate(options.getLibPath(),
                                    options.getCacheDir());
  }


  @Override
  @UsedByReflection("Interpreter")
  public long getNativeHandle() {
    return delegateHandle;
  }

  @Override
  @UsedByReflection("Interpreter")
  public void close() {
    if (delegateHandle != INVALID_DELEGATE_HANDLE) {
      deleteDelegate(delegateHandle);
      delegateHandle = INVALID_DELEGATE_HANDLE;
    }
  }

  private static native long createDelegate(
      String libPath,
      String cacheDir);

  private static native void deleteDelegate(long delegateHandle);
}
