// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters;

public interface ParameterListener<P extends ProjectParameter> {

    public void updateParameter(P parameter);
}
