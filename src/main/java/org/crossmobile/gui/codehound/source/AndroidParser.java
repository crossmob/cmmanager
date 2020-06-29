/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import org.crossmobile.utils.Dependency;
import org.crossmobile.utils.FileUtils;
import org.crossmobile.utils.Log;
import org.crossmobile.utils.func.Opt;
import org.jboss.forge.roaster.ParserException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.crossmobile.utils.Pom.SHADOW_ARTIFACT;

public class AndroidParser {

    public static Collection<Dependency> parseProject(File baseDir) {
        Collection<Dependency> found = new TreeSet<>();
        FileUtils.forAllFiles(new File(baseDir, "src/main/java"), (path, file) -> {
            try {
                if (file.getName().endsWith(".java"))
                    parse(file, found);
            } catch (ParserException ignored) {
            } catch (Exception e) {
                Log.error(e);
            }
        });
        return found;
    }

    private static void parse(File file, Collection<Dependency> found) throws Exception {
        JavaClassSource source = Roaster.parse(JavaClassSource.class, file);
        for (AnnotationSource<JavaClassSource> annotation : source.getAnnotations()) {
            if ("CMLib".equals(annotation.getName())) {
                Opt.of(annotation.getAnnotationArrayValue("depends")).ifExists(deps -> {
                    for (AnnotationSource<JavaClassSource> dep : deps) {
                        String pluginName = dep.getStringValue("pluginName");
                        String version = dep.getStringValue("version");
                        String type = dep.getStringValue("type");
                        String groupId = dep.getStringValue("groupId");
                        if ("aar".equals(type)) {
                            groupId = SHADOW_ARTIFACT + groupId;
                            type = "";
                        }
                        found.add(Dependency.find(groupId, pluginName, version, null, null, type));
                    }
                });
            }
        }
    }

    public static List<Dependency> filterShadow(List<Dependency> dependencies) {
        return dependencies.stream()
                .filter(d -> d.groupId.startsWith(SHADOW_ARTIFACT))
                .map(d -> Dependency.find(d.groupId.substring(SHADOW_ARTIFACT.length()), d.artifactId, d.version, d.classifier, d.scope, d.packaging))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
