package org.openhab.binding.bacnet.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.code_house.bacnet4j.wrapper.api.Type;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

public class BacNetBindingConfig implements BindingConfig {
    public final Class<? extends Item> itemType;
    public final String itemName;
    public final Integer deviceId;
    public final Type type;
    public final Integer id;
    public final long refreshInterval;

    private static final Pattern CONFIG_PATTERN = Pattern.compile("^([A-z]+=[^,]+(,|$))+");

    static BacNetBindingConfig parseBindingConfig(String itemName, Class<? extends Item> itemType, String configString)
            throws BindingConfigParseException {
        Matcher matcher = CONFIG_PATTERN.matcher(configString);
        if (!matcher.matches()) {
            throw new BindingConfigParseException(
                    "Invalid BacNet config: '" + configString + "'. Expected key1=value1,key2=value2");
        }

        Map<String, String> values = new HashMap<String, String>();
        for (String item : configString.split(",")) {
            String[] parts = item.split("=");
            if (parts.length != 2) {
                throw new BindingConfigParseException("Expected key=value in BacNet config");
            }
            values.put(parts[0].trim(), parts[1].trim());
        }
        return new BacNetBindingConfig(itemName, itemType, values);
    }

    public BacNetBindingConfig(String itemName, Class<? extends Item> itemType, Map<String, String> values)
            throws BindingConfigParseException {
        this.itemName = itemName;
        this.itemType = itemType;

        String deviceId = values.get("device");
        String type = values.get("type");
        String id = values.get("id");

        if (deviceId == null || id == null || type == null) {
            throw new BindingConfigParseException("Invalid BacNet config. Required properties are device, type, id");
        }

        this.deviceId = Integer.parseInt(deviceId);
        this.type = parseObjectTypeName(type);
        this.id = Integer.parseInt(id);

        if (values.containsKey("refreshInterval")) {
            this.refreshInterval = Long.parseLong(values.get("refreshInterval"));
        } else {
            this.refreshInterval = 0;
        }

    }

    private Type parseObjectTypeName(String name) {
        if (name.equals("binaryValue")) {
            return Type.BINARY_VALUE;
        } else if (name.equals("binaryInput")) {
            return Type.BINARY_INPUT;
        } else if (name.equals("binaryOutput")) {
            return Type.BINARY_OUTPUT;
        } else if (name.equals("analogValue")) {
            return Type.ANALOG_VALUE;
        } else if (name.equals("analogInput")) {
            return Type.ANALOG_INPUT;
        } else if (name.equals("analogOutput")) {
            return Type.ANALOG_OUTPUT;
        } else if (name.equals("multiStateInput")) {
            return Type.MULTISTATE_INPUT;
        } else if (name.equals("multiStateOutput")) {
            return Type.MULTISTATE_OUTPUT;
        } else if (name.equals("multiStateValue")) {
            return Type.MULTISTATE_VALUE;
        }
        return null;
    }

    @Override
    public int hashCode() {
        Object[] objects = { itemName, deviceId, type.name(), id };
        return hashCode(objects);
    }

    public int hashCode(Object[] composite) {
        int value = 0;
        for (Object object : composite) {
            value += object.hashCode();
        }
        return 97 * value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
