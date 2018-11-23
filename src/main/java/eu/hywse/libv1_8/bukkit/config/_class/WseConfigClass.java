package eu.hywse.libv1_8.bukkit.config._class;

import eu.hywse.libv1_8.bukkit.WseLocation;
import eu.hywse.libv1_8.bukkit.WseSerialize;
import eu.hywse.libv1_8.bukkit.config.WseConfig;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class WseConfigClass {

    private WseConfig config;

    /* ================================================================================= */

    public WseConfigClass() {

    }

    public WseConfigClass(WseConfig config) {
        this.config = config;
    }

    /* ================================================================================= */

    public void load() {
        for (Field field : getClass().getDeclaredFields()) {
            // Check if field is variable
            if (!field.isAnnotationPresent(ConfigVariable.class)) continue;

            // Allow read / write
            field.setAccessible(true);

            ConfigVariable variable = field.getAnnotation(ConfigVariable.class);

            String path = variable.path().replace("{name}", field.getName());
            if (path.endsWith(".")) {
                path += field.getName();
            }

            try {
                Object obj = getConfig().getConfig().get(path, null);
                if(obj == null) continue;

                if(variable.serialize()) {
                    obj = WseSerialize.fromString(obj.toString());
                }

                // Bukkit location
                if (field.getType() == Location.class || field.getType() == WseLocation.class) {
                    obj = new WseLocation(obj.toString());
                }

                field.set(this, obj);
            } catch (IllegalAccessException e) {
                System.out.println("[!] Can't load " + field.getName() + ": " + e.getMessage());
            }
        }
    }

    public WseConfigClass load(WseConfig config) {
        setConfig(config);
        load();
        return this;
    }

    public void save() {

        System.out.println("Saving!");

        for (Field field : getClass().getDeclaredFields()) {
            System.out.println("[i] Field: " + field.getName());

            // Check if field is variable
            if (!field.isAnnotationPresent(ConfigVariable.class)) {
                continue;
            }

            // Allow read / write
            field.setAccessible(true);
            ConfigVariable variable = field.getAnnotation(ConfigVariable.class);

            String path = variable.path().replace("{name}", field.getName());
            if (path.endsWith(".")) {
                path += field.getName();
            }

            try {
                Object obj = field.get(this);

                if(variable.serialize()) {
                    obj = WseSerialize.toString(obj);
                }

                // Bukkit location
                if (obj != null && (field.getType() == Location.class || field.getType() == WseLocation.class)) {
                    obj = new WseLocation((Location) obj).toString();
                }

                System.out.println("[i] Saving \"" + field.getName() + "\" with val: \"" + (obj != null ? obj.toString() : "NULL") + "\"");

                getConfig().getConfig().set(path, obj);
            } catch (IllegalAccessException e) {
                System.out.println("[!] Can't save " + field.getName() + ": " + e.getMessage());
            }
        }

        getConfig().saveConfig();
    }

    public WseConfig getConfig() {
        return config;
    }

    public void setConfig(WseConfig config) {
        this.config = config;
    }
}
