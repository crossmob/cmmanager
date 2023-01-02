/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.utils.FileUtils;
import org.crossmobile.utils.Log;
import org.crossmobile.utils.images.ImageHound;
import org.crossmobile.utils.images.MetaImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ResourceInstaller {
    public static File createResourceDir(File projectDir, ImageHound hound, String target) {
        File resDir = new File(projectDir, "target" + File.separator + "installer-resources");
        FileUtils.delete(resDir);
        resDir.mkdirs();
        if (!resDir.isDirectory())
            return null;
        int[] size;
        switch (target) {
            case "win32":
            case "win64":
            case "linux64":
            case "linuxarm32":
            case "linuxarm64":
                size = new int[]{128, 64, 32};
                break;
            case "macos":
                size = new int[]{1024, 512, 256, 128};
                break;
            default:
                size = new int[]{};
                break;
        }

        if (size.length > 0) {
            for (int i = 0; i < size.length; i++) {
                MetaImage fore = hound.findFore(size[i], i >= size.length - 1);
                if (fore.isValid()) {
                    RenderedImage iconImage = hound.findFore(size[i], true).image;
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
