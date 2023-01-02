/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FilteredFileParameter;
import org.crossmobile.utils.ParamList;

import java.io.File;

import static org.crossmobile.utils.ParamsCommon.LAUNCH_STORYBOARD;

public class LaunchStoryboardParameter extends FilteredFileParameter {

    public LaunchStoryboardParameter(ParamList prop, File baseDir) {
        super(prop, LAUNCH_STORYBOARD.tag(), baseDir, f -> f.toLowerCase().endsWith(".storyboard"));
    }

    @Override
    public String getVisualTag() {
        return "Launch Storyboard";
    }
}
