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

package marm.mobile.cassi.model;

import java.io.File;

/**
 * Manages the actual file paths for the model.
 * With this abstraction, the paths can be provided platform-independent.
 *
 * @author Martin Armbruster
 */
public class FileManager {
    /**
     * Stores the manager responsible for the current platform.
     */
    private static PlatformFileManager platform;

    /**
     * Private constructor to avoid instantiation.
     */
    private FileManager() {
    }

    /**
     * Sets the manager for the current platform.
     *
     * @param manager the manager.
     */
    public static void setPlatformFileManager(PlatformFileManager manager) {
        platform = manager;
    }

    /**
     * Returns the path to directory for storing application-private data.
     *
     * @return directory path for application-private data.
     */
    public static File getInternalMainDirectory() {
        return platform.getInternalMainDirectory();
    }

    /**
     * Returns the path to a directory for storing patterns internally.
     *
     * @return directory path for storing patterns internally.
     */
    public static File getInternalPatternDirectory() {
        return new File(platform.getInternalMainDirectory(), "patterns");
    }

    /**
     * Returns the path to a directory for storing the settings.
     *
     * @return directory path for storing the settings.
     */
    public static File getInternalSettingsDirectory() {
        return new File(platform.getInternalMainDirectory(), "settings");
    }

    /**
     * Returns the path to a directory for storing exchangeable data.
     *
     * @return directory path for exchangeable data.
     */
    public static File getExternalMainDirectory() {
        return platform.getExternalMainDirectory();
    }

    /**
     * Returns the path to a directory for storing the patterns externally.
     *
     * @return directory path for storing patterns externally.
     */
    public static File getExternalPatternsDirectory() {
        return new File(platform.getExternalMainDirectory(), "patterns");
    }

    /**
     * Interface for the actual platform-dependent file paths.
     */
    public interface PlatformFileManager {
        /**
         * Gives the path to a directory for storing application-private data (like settings).
         *
         * @return directory path for application-private data.
         */
        File getInternalMainDirectory();

        /**
         * Returns the path to a directory for storing data that can be exchanged (like patterns).
         *
         * @return directory path for exchangeable data.
         */
        File getExternalMainDirectory();
    }
}