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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import marm.mobile.cassi.model.BLEConnectionHandler;

/**
 * A Bluetooth Low Energy connection handler for Android.
 *
 * @author Martin Armbruster
 */
public class AndroidBLEConnectionHandler extends BLEConnectionHandler {
    /**
     * Context in which this connection happens.
     */
    private Context context;
    /**
     * Adapter for the ble connection.
     */
    private BluetoothAdapter bleAdapter;
    /**
     * Callback for the ble scan.
     */
    private BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if(bluetoothDevice.getName().equals(getDeviceName())) {
                bleAdapter.stopLeScan(this);
                bluetoothDevice.connectGatt(context, false, gattCallback);
            }
        }
    };
    /**
     * Stores the used gatt client.
     */
    private BluetoothGatt bleGatt;
    /**
     * Callback for gatt actions.
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState== BluetoothProfile.STATE_CONNECTED) {
                bleGatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for(BluetoothGattService s : gatt.getServices()) {
                if(s.getUuid().toString().equals(service)) {
                    for(BluetoothGattCharacteristic characteristic : s.getCharacteristics()) {
                        bleGatt.readCharacteristic(characteristic);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            String uuidString = characteristic.getUuid().toString();
            if(uuidString.equals(char1)) {
                motorAmount = characteristic.getValue()[0];
            } else if(uuidString.equals(char2)) {
                minPlayTime = characteristic.getValue()[0];
                minPlayTime = (1/minPlayTime)/1000;
            } else if(uuidString.equals(char3)) {
                receiver = characteristic;
                send(noPlay);
            }
        }
    };
    /**
     * Stores the characteristic that receives value updates.
     */
    private BluetoothGattCharacteristic receiver;
    /**
     * Name of the service.
     */
    private String service;
    /**
     * Name of the first characteristic.
     */
    private String char1;
    /**
     * Name of the second characteristic.
     */
    private String char2;
    /**
     * Name of the third characteristic.
     */
    private String char3;
    /**
     * Stores the minimum play time for a pattern part.
     */
    private int minPlayTime;
    /**
     * Stores the amount of motors.
     */
    private int motorAmount;

    /**
     * Creates a new instance.
     *
     * @param context context in which the connection takes place.
     */
    public AndroidBLEConnectionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void connect() {
        service = getServicePart1()+"0"+getServicePart2();
        char1 = getServicePart1()+"1"+getServicePart2();
        char2 = getServicePart1()+"2"+getServicePart2();
        char3 = getServicePart1()+"3"+getServicePart2();
        scanBLE();
    }

    /**
     * Scans for the ble device.
     */
    private void scanBLE() {
        Handler handler = new Handler();
        final BluetoothManager bManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bManager.getAdapter();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleAdapter.stopLeScan(callback);
            }
        }, 15000);
        bleAdapter.startLeScan(callback);
    }

    @Override
    public void send(byte[] values) {
        if(receiver==null) {
            return;
        }
        byte[] actualValues = new byte[motorAmount];
        for(int i=0; i<actualValues.length; i++) {
            actualValues[i] = values[i];
        }
        receiver.setValue(actualValues);
        bleGatt.writeCharacteristic(receiver);
    }

    @Override
    public int getMinPlayTime() {
        return minPlayTime;
    }

    @Override
    public void disconnect() {
        if(bleGatt!=null) {
            bleGatt.close();
            bleAdapter = null;
            bleGatt = null;
            receiver = null;
        }
    }
}