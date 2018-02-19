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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A special handler for connections that send the values only. It provides a implementation
 * for the play and stopPlaying methods following the template pattern.
 *
 * @author Martin Armbruster
 */
public abstract class SendConnectionHandler implements ConnectionHandler {
    /**
     * Atomic boolean to indicate if the pattern should be played.
     */
    private AtomicBoolean play;
    /**
     * Values for no play.
     */
    protected byte[] noPlay;

    /**
     * Creates a new instance.
     */
    protected SendConnectionHandler() {
        play = new AtomicBoolean();
        noPlay = new byte[5];
    }

    @Override
    public void playPattern(Pattern p, boolean loop) {
        play.set(loop);
        do {
            int partsCount = p.getNumberOfPatternParts();
            for(int i=0; i<partsCount; i++) {
                Pattern.PatternPart pp = p.getPatternPart(i);
                int duration = pp.getDuration();
                if(duration==0) {
                    continue;
                } else if(duration<getMinPlayTime()) {
                    duration = getMinPlayTime();
                }
                send(pp.getValues());
                try {
                    Thread.sleep(duration);
                } catch(InterruptedException e) {
                }
            }
        } while(play.get());
        send(noPlay);
    }

    /**
     * Sends the values for the vibration.
     *
     * @param values the values.
     */
    protected abstract void send(byte[] values);

    @Override
    public void stopPlaying() {
        play.set(false);
    }
}