package org.openhab.binding.bacnet.internal;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.code_house.bacnet4j.wrapper.api.BacNetClient;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.api.JavaToBacNetConverter;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.openhab.binding.bacnet.BacNetBindingProvider;
import org.openhab.binding.bacnet.internal.queue.ReadPropertyTask;
import org.openhab.binding.bacnet.internal.queue.WritePropertyTask;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.UnDefType;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.type.Encodable;

public class BacNetBinding extends AbstractActiveBinding<BacNetBindingProvider>
        implements ManagedService, DeviceDiscoveryListener, PropertyValueReceiver<Encodable> {
    static final Logger logger = LoggerFactory.getLogger(BacNetBinding.class);

    private static final Long DEFAULT_REFRESH_INTERVAL = 30000L;
    private static final Integer DEFAULT_LOCAL_DEVICE_ID = 1339;
    private static final Long DEFAULT_DISCOVERY_TIMEOUT = 30000L;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "bacnet-binding-executor");
        }
    });

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private Map<Integer, Device> deviceMap = Collections.synchronizedMap(new HashMap<Integer, Device>());
    private IpNetworkBuilder networkConfigurationBuilder;
    private BacNetClient client;

    private Integer localDeviceId = DEFAULT_LOCAL_DEVICE_ID;
    private Long refreshInterval = DEFAULT_REFRESH_INTERVAL;

    @Override
    protected String getName() {
        return "BacNet Service";
    }

    @Override
    public void activate() {
        super.activate();
        logger.debug("Bacnet binding activated");
    }

    @Override
    public void deactivate() {
        logger.debug("Bacnet binding is going down");
        if (client != null) {
            scheduler.shutdown();
            client.stop();
            client = null;
            initialized.set(false);
        }
        super.deactivate();
    }

    @Override
    protected void internalReceiveUpdate(String itemName, State newState) {
        performUpdate(itemName, newState);
    }

    @Override
    public void internalReceiveCommand(String itemName, Command command) {
        performUpdate(itemName, command);
    }

    private void performUpdate(final String itemName, final Type newValue) {
        final BacNetBindingConfig config = configForItemName(itemName);
        if (config != null) {
            Property property = devicePropertyForConfig(config);
            if (property != null) {
                scheduler.execute(
                        new WritePropertyTask<Type>(client, property, newValue, new JavaToBacNetConverter<Type>() {
                            @Override
                            public Encodable toBacNet(Type java) {
                                return BacNetValueConverter.openHabTypeToBacNetValue(config.type.getBacNetType(),
                                        newValue);
                            }
                        }));
                logger.info("Submited task to write {} value {} for item {}", property, newValue, itemName);
            }
        }
    }

    public void addBindingProvider(BacNetBindingProvider provider) {
        super.addBindingProvider(provider);
    }

    public void removeBindingProvider(BacNetBindingProvider bindingProvider) {
        super.removeBindingProvider(bindingProvider);
    }

    @Override
    protected void execute() {
        if (!initialized.get()) {
            logger.trace("Creating scheduled tasks to read bacnet properties");
            for (BacNetBindingProvider provider : providers) {
                for (BacNetBindingConfig config : provider.allConfigs()) {
                    long refreshInterval = config.refreshInterval != 0 ? config.refreshInterval : getRefreshInterval();
                    Property property = devicePropertyForConfig(config);
                    if (property != null) {
                        scheduler.scheduleAtFixedRate(new ReadPropertyTask(client, property, this), refreshInterval,
                                refreshInterval, TimeUnit.MILLISECONDS);
                        logger.debug("Scheduled read property task to fetch item {} value from {} every {}ms",
                                config.itemName, property, refreshInterval);
                    }
                }
            }
            initialized.set(true);
        }
    }

    @Override
    protected long getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        deactivate();

        if (properties == null) {
            return;
        }

        this.networkConfigurationBuilder = new IpNetworkBuilder();
        if (properties.get("localBindAddress") != null) {
            networkConfigurationBuilder.localBindAddress((String) properties.get("localBindAddress"));
        }
        if (properties.get("broadcast") != null) {
            networkConfigurationBuilder.broadcastIp((String) properties.get("broadcast"));
        }
        if (properties.get("port") != null) {
            networkConfigurationBuilder.port(Integer.parseInt((String) properties.get("port")));
        }
        if (properties.get("localNetworkNumber") != null) {
            networkConfigurationBuilder
                    .localNetworkNumber(Integer.parseInt((String) properties.get("localNetworkNumber")));
        }

        if (properties.get("localDeviceId") != null) {
            this.localDeviceId = Integer.parseInt((String) properties.get("localDeviceId"));
        } else {
            if (this.localDeviceId != DEFAULT_LOCAL_DEVICE_ID) {
                this.localDeviceId = DEFAULT_LOCAL_DEVICE_ID; // reset to default from previous value
            }
        }

        if (properties.get("refreshInterval") != null) {
            this.refreshInterval = Long.parseLong((String) properties.get("refreshInterval"));
        } else {
            if (this.refreshInterval != DEFAULT_REFRESH_INTERVAL) {
                this.refreshInterval = DEFAULT_REFRESH_INTERVAL; // reset to default from previous value
            }
        }

        final long discoveryTimeout;
        if (properties.get("discoveryTimeout") != null) {
            discoveryTimeout = Long.parseLong((String) properties.get("discoveryTimeout"));
        } else {
            discoveryTimeout = DEFAULT_DISCOVERY_TIMEOUT;
        }

        client = new BacNetIpClient(networkConfigurationBuilder.build(), localDeviceId);
        client.start();

        // start discovery in new thread so it will not delay config admin thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.discoverDevices(BacNetBinding.this, discoveryTimeout);

                // upper call blocks thread for discoveryTimeout ms, thus we are safe to set
                // properly configured here - all known devices should be discovered already
                setProperlyConfigured(true);
            }
        }).start();
    }

    private State createState(Class<? extends Item> type, Encodable value) {
        try {
            return BacNetValueConverter.bacNetValueToOpenHabState(type, value);
        } catch (Exception e) {
            logger.debug("Couldn't create state of type '{}' for value '{}'", type, value);
            return StringType.valueOf(value.toString());
        }
    }

    private Property devicePropertyForConfig(BacNetBindingConfig config) {
        Device device = deviceMap.get(config.deviceId);
        if (device != null) {
            return new Property(device, config.id, config.type);
        } else {
            logger.warn("Could not find property {}.{}.{} for item {} cause device was not discovered", config.deviceId,
                    config.type.name(), config.id, config.itemName);
        }
        return null;
    }

    private BacNetBindingConfig configForItemName(String itemName) {
        for (BacNetBindingProvider provider : providers) {
            BacNetBindingConfig config = provider.configForItemName(itemName);
            if (config != null) {
                return config;
            }
        }
        return null;
    }

    private BacNetBindingConfig configForProperty(Property property) {
        for (BacNetBindingProvider provider : providers) {
            BacNetBindingConfig config = provider.configForProperty(property.getDevice().getInstanceNumber(),
                    property.getType(), property.getId());
            if (config != null) {
                return config;
            }
        }
        return null;
    }

    @Override
    public void deviceDiscovered(Device device) {
        logger.info("Discovered device " + device);
        deviceMap.put(device.getInstanceNumber(), device);
    }

    @Override
    public void receiveProperty(Property property, Encodable value) {
        State state = UnDefType.UNDEF;
        BacNetBindingConfig config = configForProperty(property);
        if (config == null || value == null) {
            return;
        }

        state = this.createState(config.itemType, value);
        eventPublisher.postUpdate(config.itemName, state);
        logger.debug("Updating item {} to value {} throught property {}", config.itemName, value, property);
    }

}
