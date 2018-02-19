/* ########################################################
 * #####    CASSI, Call Assistant - The MIT-License    ####
 * ########################################################
 *
 * Copyright (C) 2018, Martin Armbruster
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

package marm.mobile.cassi;

import android.content.Context;
import java.io.File;
import marm.mobile.cassi.model.FileManager;

/**
 * Implementation of the PlatformFileManager for the Android platform.
 *
 * @author Martin Armbruster
 */
public class AndroidFileManager implements FileManager.PlatformFileManager {
    /**
     * Stores the current context in which this object is used.
     */
    private Context curContext;

    /**
     * Creates a new instance.
     *
     * @param context context in which the instance is created.
     */
    public AndroidFileManager(Context context) {
        this.curContext = context;
    }

    @Override
    public File getInternalMainDirectory() {
        return curContext.getFilesDir();
    }

    @Override
    public File getExternalMainDirectory() {
        return curContext.getFilesDir();
    }
}