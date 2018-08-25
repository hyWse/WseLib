package eu.hywse.lib.config;

import eu.hywse.lib.config._class.WseConfigClass;
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
 * @ver: 1.3
 */
public class WseConfig {

    public File getFile() {
        return file;
    }

    private File file;
    private String filePath;
    private String fileName;

    /**
     * @since 1.0
     * @return Should the file create if not exists on "getConfig()"
     */
    public boolean createNew = true;
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
     * @since 1.0
     * @return saves the config
     */
    public void saveConfig() {
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @since 1.3
     * @return saves the config
     */
    public void loadConfig() {
        try {
            getConfig().load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @since 1.0
     * @return returns a string from the config
     */
    public String getString(String str) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(str));
    }

    public String getString(String str, String... find) {
        String tmp = getString(str);
        for (int i = 0; i < find.length; i += 2) {
            tmp = tmp.replace(find[i], find[i + 1]);
        }
        return tmp;
    }

    /**
     * @since 1.0
     * @return returns an integer from the config
     */
    public int getInt(String str) {
        return getConfig().getInt(str);
    }

    /**
     * @since 1.0
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
     * @since 1.1
     * @return adds a string to a stringlist
     */
    public void addString(String stringlist, String string) {
        List<String> tmpList;
        if (getConfig().isSet(stringlist)) {
            tmpList = getConfig().getStringList(stringlist);
        } else {
            tmpList = new ArrayList<String>();
        }
        if (!tmpList.contains(string)) {
            tmpList.add(string);
        }
        getConfig().set(stringlist, tmpList);
    }

    /**
     * @since 1.1
     * @return removes a string from a stringlist
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
     * @since 1.2
     * @return sets the fileconfiguration
     */
    private void setfConf(FileConfiguration fConf) {
        this.fConf = fConf;
    }

    /**
     * @since 1.2
     * @return gets all keys from a configuration section
     */
    public List<String> getKeys(String key) {
        List<String> list = new ArrayList<String>();

        ConfigurationSection section = getConfig().getConfigurationSection(key);
        if (section == null) {
            return list;
        }

        for (String sectionstr : section.getKeys(false)) {
            list.add(sectionstr);
        }
        return list;
    }

    /**
     * @since 1.2
     * @return Returns an ItemStack
     */
    public ItemStack getItemStack(String s) {
        return getConfig().getItemStack(s);
    }

    /**
     * Deletes config
     * @since 1.3
     */
    public void delete() {
        System.out.println("Deleting " + filePath + fileName);
        getFile().delete();
    }

    public void loadClass(WseConfigClass configClass) {
        configClass.setConfig(this);
        configClass.load();
    }

    public void saveClass(WseConfigClass configClass) {
        configClass.setConfig(this);
        configClass.save();
    }
}