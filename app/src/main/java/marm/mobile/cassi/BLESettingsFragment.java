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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A fragment for setting the BLE settings.
 *
 * @author Martin Armbruster
 */
public class BLESettingsFragment extends Fragment {
    /**
     * Object holding the BLE settings.
     */
    private AndroidBLEConnectionHandler bleConnector;
    /**
     * Saves the EditText in which the device name is typed in.
     */
    private EditText deviceEdit;
    /**
     * Saves the EditText in which the first part of the service name is typed in.
     */
    private EditText service1Edit;
    /**
     * Saves the EditText in which the second part of the service name is typed in.
     */
    private EditText service2Edit;

    /**
     * Creates a new instance.
     */
    public BLESettingsFragment() {
        bleConnector = new AndroidBLEConnectionHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View resultingView = inflater.inflate(R.layout.fragment_ble_settings, container, false);
        deviceEdit = resultingView.findViewById(R.id.cassi_settings_ble_device);
        deviceEdit.setText(bleConnector.getDeviceName());
        service1Edit = resultingView.findViewById(R.id.cassi_settings_ble_service1);
        service1Edit.setText(bleConnector.getServicePart1());
        service2Edit = resultingView.findViewById(R.id.cassi_settings_ble_service2);
        service2Edit.setText(bleConnector.getServicePart2());
        return resultingView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSettings();
    }

    /**
     * Saves the settings.
     */
    private void saveSettings() {
        bleConnector.setDeviceName(deviceEdit.getText().toString());
        bleConnector.setServicePart1(service1Edit.getText().toString());
        bleConnector.setServicePart2(service2Edit.getText().toString());
    }
}