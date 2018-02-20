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

import java.util.ArrayList;

/**
 * A class representing a vibration pattern.
 *
 * @author Martin Armbruster
 */
public class Pattern {
    /**
     * Stores the name of the pattern.
     */
    private String name;
    /**
     * Stores the parts of the pattern.
     */
    private ArrayList<PatternPart> patternParts;

    /**
     * Creates a new instance.
     *
     * @param name name of the pattern.
     */
    public Pattern(String name) {
        this.name = name;
        patternParts = new ArrayList<>();
    }

    /**
     * Returns the name of the pattern.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this pattern.
     *
     * @param newName the new name.
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Adds a new part for this pattern.
     *
     * @param duration duration of the part in milliseconds.
     * @param values values of the motors. The length has to be exactly five.
     */
    public void addPart(int duration, byte[] values) {
        patternParts.add(new PatternPart(duration, values));
    }

    /**
     * Returns a specified pattern part.
     *
     * @param number number of the requested pattern part.
     * @return the requested pattern part or null if number is out of bounds.
     */
    public PatternPart getPatternPart(int number) {
        if(number<0||number>=patternParts.size()) {
            return null;
        }
        return patternParts.get(number);
    }

    /**
     * Returns the number of current registered pattern parts.
     *
     * @return the current number of pattern parts.
     */
    public int getNumberOfPatternParts() {
        return patternParts.size();
    }

    /**
     * Clears all parts for this pattern.
     */
    public void clearAllParts() {
        patternParts.clear();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Part of a pattern.
     *
     * @author Martin Armbruster
     */
    public class PatternPart {
        /**
         * Stores the duration in milliseconds.
         */
        private int duration;
        /**
         * Stores the values of the motors.
         */
        private byte[] values;

        /**
         * Creates a  new instance.
         *
         * @param duration duration of this part in milliseconds.
         * @param values values for the five motors. The length has to be exactly five.
         */
        private PatternPart(int duration, byte[] values) {
            if(values.length!=5) {
                throw new IllegalArgumentException("values has to have a length of five.");
            }
            this.duration = duration;
            this.values = values;
        }

        /**
         * Returns the duration of this part.
         *
         * @return the duration in milliseconds.
         */
        public int getDuration() {
            return duration;
        }

        /**
         * Returns the values for the motors as a byte array.
         *
         * @return the values for the motors.
         */
        public byte[] getValues() {
            return values;
        }

        /**
         * Returns the values of the motors as a string.
         *
         * @return string representing the values of the motors.
         */
        public String getValuesAsString() {
            return ""+values[0]+values[1]+values[2]+values[3]+values[4];
        }
    }
}