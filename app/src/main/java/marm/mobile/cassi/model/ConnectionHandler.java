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

/**
 * Interface for handlers that manages a network connection.
 *
 * @author Martin Armbruster
 */
public interface ConnectionHandler {
    /**
     * Connects to the underlying network.
     */
    void connect();

    /**
     * Plays a pattern.
     *
     * @param p the pattern.
     * @param loop true when the pattern should play in a loop. false when the pattern should be
     *             played one time.
     */
    void playPattern(Pattern p, boolean loop);

    /**
     * Returns the minimum time for a pattern part to play.
     *
     * @return the minimum time for a pattern part to play in milliseconds.
     */
    int getMinPlayTime();

    /**
     * Stops playing the current pattern.
     */
    void stopPlaying();

    /**
     * Disconnets from the underlying network.
     */
    void disconnect();
}