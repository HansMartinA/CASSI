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

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;

/**
 * A fragment for the main screen.
 *
 * @author Martin Armbruster
 */
public class MainScreenFragment extends Fragment {
    /**
     * ID used for requesting permissions.
     */
    public final static int PERMISSION_REQUEST_ID = 5;
    /**
     * ID used for requesting enabling bluetooth.
     */
    public final static int ENABLE_BLUETOOTH = 7;

    /**
     * Creates  a new instance.
     */
    public MainScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View resultingView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        ((Button) resultingView.findViewById(R.id.cassi_start)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean perms = checkPermissions();
                        if(perms) {
                            startClicked(view);
                        }
                    }
                });
        ((Button) resultingView.findViewById(R.id.cassi_stop)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopClicked(view);
                    }
                });
        return resultingView;
    }

    /**
     * Checks if all needed permissions are granted and if not, asks the user to grant them.
     *
     * @return true when all permissions are granted. false otherwise.
     */
    private boolean checkPermissions() {
        boolean result = true;
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        result = result&&checkSinglePermission(Manifest.permission.BLUETOOTH, notGrantedPermissions);
        result = result&&checkSinglePermission(Manifest.permission.BLUETOOTH_ADMIN, notGrantedPermissions);
        result = result&&checkSinglePermission(Manifest.permission.READ_PHONE_STATE, notGrantedPermissions);
        result = result&&checkSinglePermission(Manifest.permission.RECEIVE_SMS, notGrantedPermissions);
        result = result&&checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION, notGrantedPermissions);
        if(!result) {
            String[] permissionsToRequest =
                    notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissionsToRequest, 0);
        }
        return result;
    }

    /**
     * Checks for a single permission.
     *
     * @param permission the permission.
     * @param notGrantedPermissions list of all not-granted permissions. If this permission is not
     *                              granted, it will be added to the list.
     * @return true when the permission is granted. false otherwise.
     */
    private boolean checkSinglePermission(String permission, ArrayList<String> notGrantedPermissions) {
        if(ActivityCompat.checkSelfPermission(getContext(), permission)
                !=PackageManager.PERMISSION_GRANTED) {
            notGrantedPermissions.add(permission);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Called when the start button is clicked.
     *
     * @param view view.
     */
    private void startClicked(View view) {
        // Ensure BLE is enabled.
        BluetoothAdapter bleAdapter = ((BluetoothManager)
                getContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(bleAdapter==null||!bleAdapter.isEnabled()) {
            Intent enableBLEIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBLEIntent, ENABLE_BLUETOOTH);
            return;
        }
        Intent intent = new Intent(getContext(), CASSIService.class);
        getContext().startService(intent);
    }

    /**
     * Called when the stop button is clicked.
     *
     * @param view view.
     */
    private void stopClicked(View view) {
        Intent intent = new Intent(getContext(), CASSIService.class);
        getContext().stopService(intent);
    }
}