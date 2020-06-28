/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import org.crossmobile.utils.FileUtils;
import org.crossmobile.utils.Log;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.IOException;

public class AndroidParser {

    public static void test(File baseDir) {
        FileUtils.forAllFiles(baseDir, (name, file) -> {
            try {
                if (name.endsWith(".java"))
                    parse(file);
            } catch (IOException e) {
                Log.error(e);
            }
        });
    }

    private static void parse(File file) throws IOException {
        JavaClassSource source = Roaster.parse(JavaClassSource.class, file);
        for (AnnotationSource<JavaClassSource> annotation : source.getAnnotations()) {
            System.out.println(annotation.getAnnotationValue("depends"));
        }

    }
}
