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
 * Class that defines callbacks to the CASSI service.
 *
 * @author Martin Armbruster
 */
public final class CASSIServiceCallback {
    /**
     * Constant representing the scanning for BLE devices.
     */
    public final static int BLE_STATE_SCANNING = 0;
    /**
     * Constant for representing the connecting to a BLE device.
     */
    public final static int BLE_STATE_CONNECTING = 1;
    /**
     * Constant for representing connection to a BLE device.
     */
    public final static int BLE_STATE_CONNECTED = 2;
    /**
     * Constant for representing an unsuccessful scanning.
     */
    public final static int BLE_STATE_SCAN_DEVICE_NOT_FOUND = 3;
    /**
     * Constant for representing the disconnecting from the BLE device.
     */
    public final static int BLE_STATE_DISCONNECTING = 4;
    /**
     * Constant for representing a disconnection.
     */
    public final static int BLE_STATE_DISCONNECTED = 5;

    /**
     * Private constructor to avoid instantiation.
     */
    private CASSIServiceCallback() {
    }

    /**
     * This interface provides a callback to the CASSI service for Bluetooth Low Energy connection
     * handlers.
     *
     * @author Martin Armbruster
     */
    public interface OnBLEStateChanged {
        /**
         * Called when the state of the BLE connection changes.
         *
         * @param newState the new state of the connection.
         */
        void onBLEStateChanged(int newState);
    }

    /**
     * Interface for notifying the CASSI service that a message is delivered.
     *
     * @author Martin Armbruster
     */
    public interface OnMessageDelivery {
        /**
         * Called when a call starts.
         */
        void onCallStart();

        /**
         * Called when a call stops.
         */
        void onCallStop();

        /**
         * Called when a SMS/MMS is received.
         */
        void onSMSMMSReceived();
    }
}