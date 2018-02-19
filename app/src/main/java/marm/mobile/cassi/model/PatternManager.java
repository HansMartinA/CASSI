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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages patterns.
 *
 * @author Martin Armbruster
 */
public class PatternManager {
    /**
     * Stores all patterns.
     */
    private ArrayList<Pattern> patterns;

    /**
     * Creates a new instance.
     */
    public PatternManager() {
        patterns = new ArrayList<>();
    }

    /**
     * Returns a list of all current registered patterns.
     *
     * @return a list of all patterns.
     */
    public List<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Loads all patterns.
     *
     * @param dir directory path from which the patterns are read.
     */
    public void load(File dir) {
        patterns.clear();
        File[] files = dir.listFiles();
        byte[] valueBuffer = new byte[5];
        for(File f : files) {
            if(f.exists()&&f.getName().endsWith(".pattern")) {
                Pattern p = new Pattern(f.getName());
                try(FileInputStream is = new FileInputStream(f)) {
                    while(is.available()>0) {
                        int duration = is.read();
                        is.read(new byte[1]);
                        is.read(valueBuffer);
                        p.addPart(duration, valueBuffer);
                        is.read(new byte[1]);
                    }
                } catch(IOException e) {
                }
            }
        }
    }

    /**
     * Saves all patterns.
     *
     * @param dir directory path in which the patterns will be stored.
     */
    public void save(File dir) {
        for(Pattern p : patterns) {
            try(FileOutputStream os = new FileOutputStream(dir.getAbsolutePath()
                    +p.getName()+".pattern")) {
                int partCount = p.getNumberOfPatternParts();
                for(int i=0; i<partCount; i++) {
                    Pattern.PatternPart pp = p.getPatternPart(i);
                    os.write(pp.getDuration());
                    os.write('\n');
                    os.write(pp.getValues());
                    os.write('\n');
                }
            } catch(IOException e) {
            }
        }
    }
}