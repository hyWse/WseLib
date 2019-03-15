package eu.hywse.lib.bukkit.config;

import eu.hywse.lib.bukkit.config._class.WseConfigClass;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyWse
 * @version 1.3
 */
public class WseConfig {

    public File getFile() {
        return file;
    }

    private File file;
    private String filePath;
    private String fileName;

    @Getter @Setter
    private boolean createNew = true;

    private FileConfiguration fConf;

    public WseConfig(String path, String name) {
        this.filePath = path;
        this.fileName = name;

        file = new File(getFilePath(), getFileName());
        create();

        setfConf(YamlConfiguration.loadConfiguration(file));
    }

    public WseConfig(JavaPlugin pl, String name) {
        this.filePath = pl.getDataFolder().toString();
        this.fileName = name;

        file = new File(getFilePath(), getFileName());
        create();

        setfConf(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @since 1.0
     * @return creates a config
     */
    public boolean create() {
        if (!(file.exists())) {
            File tmp = new File(getFilePath());
            tmp.mkdirs();

            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * @since 1.0
     * @return returns the fileconfiguration
     */
    public FileConfiguration getConfig() {
        if (createNew) {
            create();
        }
        return getfConf();
    }

    /**
     * Saves the config to file
     * @since 1.0
     */
    public void saveConfig() {
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the config
     * @since 1.3
     */
    public void loadConfig() {
        try {
            getConfig().load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @since 2.0
     *
     * @param str Input String
     *
     * @return returns a string from the config
     */
    public String getString(String str) {
        return getString(str, null);
    }


    /**
     * @since 2.0
     *
     * @param str Input
     * @param def Default if not found in config
     *
     * @return returns a string from the config
     */
    public String getString(String str, String def) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(str, def));
    }

    /**
     * @since 1.0
     *
     * @param str Config-Path
     *
     * @return returns an integer from the config
     */
    public int getInt(String str) {
        return getConfig().getInt(str);
    }

    /**
     * @since 1.0
     *
     * @param str Config-Path
     *
     * @return returns a double from the config
     */
    public double getDouble(String str) {
        return getConfig().getDouble(str);
    }

    /**
     * @since 1.0
     * @return returns the file path
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * @since 1.0
     * @return returns the file name
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * adds a string to a stringlist
     *
     * @since 1.1
     *
     * @param stringlist Config-Path
     * @param string String to add
     */
    public void addString(String stringlist, String string) {
        List<String> tmpList;
        if (getConfig().isSet(stringlist)) {
            tmpList = getConfig().getStringList(stringlist);
        } else {
            tmpList = new ArrayList<>();
        }
        if (!tmpList.contains(string)) {
            tmpList.add(string);
        }
        getConfig().set(stringlist, tmpList);
    }

    /**
     * removes a string from a stringlist
     *
     * @since 1.1
     *
     * @param stringlist Config-Path
     * @param string String to remove
     */
    public void remString(String stringlist, String string) {
        List<String> tmpList;
        if (getConfig().isSet(stringlist)) {
            tmpList = getConfig().getStringList(stringlist);
            tmpList.remove(string);
            getConfig().set(stringlist, tmpList);
        }

    }

    /**
     * @since 1.2
     * @return Returns the fileconfiguration
     */
    public FileConfiguration getfConf() {
        return fConf;
    }

    /**
     * sets the fileconfiguration
     *
     * @since 1.2
     *
     * @param fConf FileConfiguration
     */
    private void setfConf(FileConfiguration fConf) {
        this.fConf = fConf;
        this.fConf.options().copyDefaults(true);
    }

    /**
     * @since 1.2
     *
     * @param key Config-Path
     *
     * @return gets all keys from a configuration section
     */
    public List<String> getKeys(String key) {
        List<String> list = new ArrayList<String>();

        ConfigurationSection section = getConfig().getConfigurationSection(key);
        if (section == null) {
            return list;
        }

        list.addAll(section.getKeys(false));
        return list;
    }

    /**
     * @since 1.2
     *
     * @param s Config-Path
     *
     * @return Returns an ItemStack
     */
    public ItemStack getItemStack(String s) {
        return getConfig().getItemStack(s);
    }

    /**
     * Deletes config
     *
     * @since 1.3
     */
    public void delete() {
        System.out.println("Deleting " + filePath + fileName);
        getFile().delete();
    }

    /**
     * Loads fields in a config class
     *
     * @param configClass Config Class
     */
    public void loadClass(WseConfigClass configClass) {
        configClass.setConfig(this);
        configClass.load();
    }

    /**
     * Saves all fields of a config class to config
     *
     * @param configClass Config Class
     */
    public void saveClass(WseConfigClass configClass) {
        configClass.setConfig(this);
        configClass.save();
    }

    /**
     * Adds default values
     *
     * @since 2.0
     * @param objects   Defaults
     */
    public void addDefaults(Object...objects) {
        for(int i = 0; i <  objects.length; i++) {
            if(objects.length <= (i+1)) {
                break;
            }

            String key = objects[i].toString();

            i++;
            Object val = objects[i];

            getConfig().addDefault(key, val);
        }
        saveConfig();
    }

}