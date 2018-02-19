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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is responsible for handling the mapping between services and patterns in the model.
 *
 * @author Martin Armbruster
 */
public class PSMapping {
    /**
     * Manager that holds all available patterns.
     */
    private PatternManager pManager;
    /**
     * Map storing the actual mapping between services (represented through string constants) and
     * patterns.
     */
    private HashMap<String, Pattern> mapping;
    /**
     * File in which the mapping is stored.
     */
    private File mappingFile;
    /**
     * String constant for call services.
     */
    public static final String CALL = "CALL";
    /**
     * String constant for sms or mms services.
     */
    public static final String SMS = "SMS";

    /**
     * Creates a new instance.
     */
    public PSMapping() {
        pManager = new PatternManager();
        pManager.load(FileManager.getInternalPatternDirectory());
        mapping = new HashMap<>();
        if(!FileManager.getInternalSettingsDirectory().exists()) {
            FileManager.getInternalSettingsDirectory().mkdirs();
        }
        mappingFile = new File(FileManager.getInternalSettingsDirectory(), "mapping.txt");
        load(mappingFile);
    }

    /**
     * Sets a mapping.
     *
     * @param service the service.
     * @param pattern the pattern.
     */
    public void setMapping(String service, Pattern pattern) {
        mapping.put(service, pattern);
        save(mappingFile);
    }

    /**
     * Returns the pattern mapped to a service.
     *
     * @param service the service.
     * @return the pattern mapping to the service or null if no mapping exists.
     */
    public Pattern getMapping(String service) {
        return mapping.get(service);
    }

    /**
     * Returns the used pattern manager.
     *
     * @return the pattern manager.
     */
    public PatternManager getPatternManager() {
        return pManager;
    }

    /**
     * Loads the mapping.
     *
     * @param f file in which the pattern is stored.
     */
    private void load(File f) {
        if(!f.exists()) {
            return;
        }
        try(FileReader is = new FileReader(f);
            BufferedReader reader = new BufferedReader(is)) {
            String line = reader.readLine();
            while(line!=null) {
                String[] map = line.split("=");
                mapping.put(map[0], resolvePattern(map[1]));
                line = reader.readLine();
            }
        } catch(IOException e) {
        }
    }

    /**
     * Resolves a pattern name to a pattern.
     *
     * @param patternName name of the requested pattern.
     * @return pattern whose name is equal to patternName or null if such a pattern doesn't exist.
     */
    private Pattern resolvePattern(String patternName) {
        for(int i=0; i<pManager.getPatterns().size(); i++) {
            Pattern p = pManager.getPatterns().get(i);
            if(patternName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Saves the mapping.
     *
     * @param f file in which the mapping will be stored.
     */
    private void save(File f) {
        try(FileWriter os = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(os)) {
            for(String key : mapping.keySet()) {
                writer.write(key+"="+mapping.get(key).getName()+"\n");
            }
        } catch(IOException e) {
        }
    }
}