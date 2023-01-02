/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.utils.Commander;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class AndroidDeviceResolver {

    private static final Map<String, DevicePresenter> INFO_ANDROID_DEVICES = new LinkedHashMap<>();
    private static File adb;

    private static <HANDLER> void updateAndroidDevices(boolean waitfor) {
        if (adb == null || !adb.isFile())
            return;

        Commander cmd = new Commander(adb.getAbsolutePath(), "devices");
        final Map<String, DevicePresenter> adev = new TreeMap<>();
        adev.put("- No Device Found -", new DevicePresenter("- No Device Found -", "- No Device Found -", "- No Device Found -"));
        cmd.setOutListener(new Consumer<String>() {
            private boolean stillGarbage = true;

            @Override
            public void accept(String data) {
                if (data.toLowerCase().contains("list of devices"))
                    stillGarbage = false;
                else if (!stillGarbage) {
                    String[] dev = data.split("\\s");
                    if (dev.length == 2) {
                        String device = dev[0];
                        String name = dev[1];
                        adev.remove("- No Device Found -");
                        adev.put(device, new DevicePresenter(device, name + " : " + device, "Android device named \"" + name + "\" with ID \"" + device + "\""));
                    }
                }
            }
        });
        cmd.setEndListener(result -> {
            synchronized (INFO_ANDROID_DEVICES) {
                INFO_ANDROID_DEVICES.clear();
                INFO_ANDROID_DEVICES.putAll(adev);
            }
        });
        cmd.exec();
        if (waitfor)
            cmd.waitFor();
    }

    static Collection<DevicePresenter> getAndroidDevices() {
        updateAndroidDevices(true);
        return INFO_ANDROID_DEVICES.values();
    }

    static String getAndroidDeviceNameFromInfo(String info) {
        updateAndroidDevices(true);
        return INFO_ANDROID_DEVICES.get(info).name;
    }

    static String[] getAndroidDeviceInfo() {
        updateAndroidDevices(true);
        return INFO_ANDROID_DEVICES.keySet().toArray(new String[INFO_ANDROID_DEVICES.size()]);
    }

    static int findAndroidDeviceNameIndex(String name) {
        updateAndroidDevices(true);
        return findNameIndex(INFO_ANDROID_DEVICES.values(), name);
    }

    private static int findNameIndex(Collection<DevicePresenter> names, String seek) {
        int index = 0;
        for (DevicePresenter prop : names) {
            if (prop.name.equals(seek))
                return index;
            index++;
        }
        return 0;
    }

    public static final class DevicePresenter {

        public final String name;
        public final String info;
        public final String descr;

        private DevicePresenter(String name, String info, String descr) {
            this.name = name;
            this.info = info;
            this.descr = descr;
        }

        @Override
        public String toString() {
            return info;
        }

    }
}
