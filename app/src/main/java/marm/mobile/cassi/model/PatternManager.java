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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
        if(files==null||files.length==0) {
            return;
        }
        byte[] valueBuffer = new byte[5];
        for(File f : files) {
            if(f.exists()&&f.getName().endsWith(".pattern")) {
                Pattern p = new Pattern(f.getName().substring(0,
                        f.getName().length()-8));
                try(FileReader is = new FileReader(f);
                BufferedReader reader = new BufferedReader(is)) {
                    String line = reader.readLine();
                    while(line!=null) {
                        int duration = Integer.parseInt(line);
                        line = reader.readLine();
                        String[] values = line.split(" ");
                        for(int j=0; j<valueBuffer.length; j++) {
                            valueBuffer[j] = Byte.parseByte(values[j]);
                        }
                        p.addPart(duration, valueBuffer);
                        line = reader.readLine();
                    }
                } catch(NumberFormatException | IOException e) {
                }
                patterns.add(p);
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
            save(dir, p);
        }
    }

    /**
     * Saves a single pattern.
     *
     * @param dir directory path in whicht the pattern will be stored.
     * @param p the pattern.
     */
    public void save(File dir, Pattern p) {
        if(!dir.exists()) {
            dir.mkdirs();
        }
        try(FileWriter os = new FileWriter(dir.getAbsolutePath()+File.separator
                +p.getName()+".pattern");
            BufferedWriter writer = new BufferedWriter(os)) {
            int partCount = p.getNumberOfPatternParts();
            for(int i=0; i<partCount; i++) {
                Pattern.PatternPart pp = p.getPatternPart(i);
                writer.write(pp.getDuration()+"\n");
                for(int j=0; j<pp.getValues().length; j++) {
                    writer.write(pp.getValues()[j]+" ");
                }
                writer.write("\n");
            }
        } catch(IOException e) {
        }
    }

    /**
     * Deletes a pattern from disk and from the collection.
     *
     * @param dir directory path in whicht the patterns are stored.
     * @param p the pattern.
     */
    public void delete(File dir, Pattern p) {
        File pFile = new File(dir, p.getName()+".pattern");
        if(pFile.exists()) {
            pFile.delete();
        }
        patterns.remove(p);
    }
}