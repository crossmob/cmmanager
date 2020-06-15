/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import com.github.gino0631.icns.IcnsBuilder;
import com.github.gino0631.icns.IcnsIcons;
import com.github.gino0631.icns.IcnsType;
import org.crossmobile.utils.Log;
import org.crossmobile.utils.images.ImageHound;
import org.crossmobile.utils.images.MetaImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResourceInstaller {
    public static File createResourceDir(ImageHound hound, String target) {
        File resDir;
        try {
            resDir = File.createTempFile("res_", ".d");
        } catch (IOException e) {
            Log.error(e);
            return null;
        }
        //noinspection ResultOfMethodCallIgnored
        resDir.delete();
        //noinspection ResultOfMethodCallIgnored
        resDir.mkdirs();
        resDir.deleteOnExit();
        if (!resDir.isDirectory())
            return null;
        int[] size;
        switch (target) {
            case "win32":
            case "win64":
            case "lin64":
            case "linarm32":
            case "linarm64":
                size = new int[]{128, 64, 32};
                break;
            case "macos":
                size = new int[]{1024, 512, 256, 128, 64};
                break;
            default:
                size = new int[]{};
                break;
        }

        if (size.length > 0) {
            for (int i = 0; i < size.length; i++) {
                MetaImage fore = hound.findFore(size[i], i >= size.length - 1);
                if (fore.isValid()) {
                    RenderedImage iconImage = hound.requestImage(size[i]);
                    File iconFile = new File(resDir, "app.png");
                    try {
                        if (!ImageIO.write(iconImage, "PNG", iconFile))
                            return null;
                    } catch (IOException e) {
                        Log.error(e);
                    }
                    break;
                }
            }
        }
        return resDir;
    }
}
