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

/**
 * A general connection handler for Bluetooth Low Energy connections.
 *
 * @author Martin Armbruster
 */
public abstract class BLEConnectionHandler extends SendConnectionHandler {
    /**
     * Stores the device name of the BLE device.
     */
    private String deviceName;
    /**
     * Stores the first part of the service.
     */
    private String servicePart1;
    /**
     * Stores the second part of the service.
     */
    private String servicePart2;
    /**
     * Stores the file in which the settings are stored.
     */
    private File settingsFile;

    /**
     * Creates a new instance.
     */
    protected BLEConnectionHandler() {
        if(!FileManager.getInternalSettingsDirectory().exists()) {
            FileManager.getInternalSettingsDirectory().mkdirs();
        }
        settingsFile = new File(FileManager.getInternalSettingsDirectory(), "ble_settings.txt");
        load();
    }

    /**
     * Returns the ble device name.
     *
     * @return the ble device name.
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the ble device name.
     *
     * @param deviceName the ble device name.
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        save();
    }

    /**
     * Returns the first part of the service name.
     *
     * @return the first part of the service name.
     */
    public String getServicePart1() {
        return servicePart1;
    }

    /**
     * Sets the first part of the service name.
     *
     * @param part1 the first part of the service name.
     */
    public void setServicePart1(String part1) {
        servicePart1 = part1;
        save();
    }

    /**
     * Returns the second part of the service name.
     *
     * @return the second part of the service name.
     */
    public String getServicePart2() {
        return servicePart2;
    }

    /**
     * Sets the second part of the service name.
     *
     * @param part2 the second part of the service name.
     */
    public void setServicePart2(String part2) {
        servicePart2 = part2;
        save();
    }

    /**
     * Loads the settings.
     */
    private void load() {
        if(settingsFile.exists()) {
            try(FileReader is = new FileReader(settingsFile);
                BufferedReader reader = new BufferedReader(is)) {
                String line = reader.readLine();
                while(line!=null) {
                    String[] parts = line.split("=");
                    switch(parts[0]) {
                        case "device-name": deviceName = parts[1];
                            break;
                        case "service-1": servicePart1 = parts[1];
                            break;
                        case "service-2": servicePart2 = parts[1];
                            break;
                    }
                    line = reader.readLine();
                }
            } catch(IOException e) {
            }
        }
    }

    /**
     * Saves the settings.
     */
    private void save() {
        try(FileWriter os = new FileWriter(settingsFile);
            BufferedWriter writer = new BufferedWriter(os)) {
            writer.write("device-name="+deviceName+"\n");
            writer.write("service-1="+servicePart1+"\n");
            writer.write("service-2="+servicePart2+"\n");
        } catch(IOException e) {
        }
    }
}