/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet;

import java.util.Collection;

import org.code_house.bacnet4j.wrapper.api.Type;
import org.openhab.binding.bacnet.internal.BacNetBindingConfig;
import org.openhab.core.binding.BindingProvider;

public interface BacNetBindingProvider extends BindingProvider {
    public BacNetBindingConfig configForItemName(String itemName);

    public BacNetBindingConfig configForProperty(int deviceId, Type type, int id);

    public Collection<BacNetBindingConfig> allConfigs();

}
