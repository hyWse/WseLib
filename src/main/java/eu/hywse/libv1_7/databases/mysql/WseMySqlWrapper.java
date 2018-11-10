package eu.hywse.libv1_7.databases.mysql;

import eu.hywse.libv1_7.misc.WseMap;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Suppress unused methods
@SuppressWarnings("ALL")

/*
 * @author hyWse
 * @version 1.1
 */
public abstract class WseMySqlWrapper {

    @Getter
    private String name;

    @Getter
    private String host;

    @Getter
    private String database;

    @Getter
    private String user;

    @Getter
    private int port;

    private String password;

    @Getter
    private Connection connection;

    @Getter @Setter
    private boolean debugMode;

    @Getter
    private boolean autoReconnect = true, useSsl = false;

    @Getter
    private boolean isReadOnly = false;

    @Getter
    private long lastQuery = System.currentTimeMillis();

    public void updateLastQuery() {

        if(System.currentTimeMillis() - getLastQuery() > 1000*60*60*3) {
            log("Checking connection if closed...");
            try {
                getConnection().prepareStatement("SHOW DATABASES;").executeQuery();
            } catch (SQLException e) {
                if(e.getMessage().toLowerCase().contains("allowed after connection closed")) {
                    log("Connection closed. Reconnecting...");
                    connect();
                }
            }
        }

        this.lastQuery = System.currentTimeMillis();
    }

    /* ==========================================================================================================================================
     * No SQL-File
     */
    public WseMySqlWrapper(String host, String database, String user, String password) {
        this(System.getProperty("program.name"), host, database, user, password, 3306);
    }

    public WseMySqlWrapper(String name, String host, String database, String user, String password) {
        this(name, host, database, user, password, 3306);
    }

    public WseMySqlWrapper(String name, String host, String database, String user, String password, int port) {
        this(name, host, database, user, password, port, true, false);
    }

    public WseMySqlWrapper(String name, String host, String database, String user, String password, boolean ar, boolean ssl) {
        this(name, host, database, user, password, 3306, ar, ssl);
    }

    public WseMySqlWrapper(String name, String host, String database, String user, String password, int port, boolean ar, boolean ssl) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        this.name = name;

        this.autoReconnect = ar;
        this.useSsl = ssl;

        connect();
    }

    /* ==========================================================================================================================================
     * With SQL-File
     */
    public WseMySqlWrapper() throws WseSqlInfoFileException {
        this(System.getProperty("program.name"));
    }

    public WseMySqlWrapper(String name) throws WseSqlInfoFileException {
        this(name, true, false);
    }

    public WseMySqlWrapper(String name, boolean ar, boolean ssl) throws WseSqlInfoFileException {
        this(name, "plugins//STZ//mysql.info", ar, ssl);
    }

    public WseMySqlWrapper(String name, String sqlInfoFile) throws WseSqlInfoFileException {
        this(name, sqlInfoFile, true, false);
    }

    public WseMySqlWrapper(String name, String sqlInfoFile, boolean ar, boolean ssl) throws WseSqlInfoFileException {
        this(name, sqlInfoFile, null, ar, ssl);
    }

    public WseMySqlWrapper(String name, String sqlInfoFile, String database) throws WseSqlInfoFileException {
        this(name, sqlInfoFile, database, true, false);
    }

    public WseMySqlWrapper(String name, String sqlInfoFile, String database, boolean ar, boolean ssl) throws WseSqlInfoFileException {
        this.name = name;

        log(" ");

        /* Get file */
        File file = new File(sqlInfoFile);
        if (!file.isFile()) {
            throw new WseSqlInfoFileException("sqlInfoFile is not a file");
        }

        Path path;
        try {
            path = Paths.get(file.getCanonicalPath());
        } catch (IOException e) {
            throw new WseSqlInfoFileException("IOException: " + e.getMessage());
        }

        /* Read lines */
        List<String> connData;
        try {
            connData = Files.readAllLines(path);
        } catch (IOException e) {
            throw new WseSqlInfoFileException("IOException: " + e.getMessage());
        }

        if (connData.size() == 0) {
            throw new WseSqlInfoFileException("File is empty");
        }

        boolean foundInfo = false;
        Map<String, String> info = new HashMap<>();

        /* Parse */
        for (String line : connData) {
            line = line.trim();

            // Comment
            if (line.startsWith("#")) continue;
            if (line.length() == 0) continue;

            // Info
            if (line.startsWith("!") && line.contains("<") && line.contains(">") && line.contains("|") && line.endsWith("#")) {
                String version = line.substring(line.indexOf("|") + 1, line.indexOf(">"));
                String sqliName = line.substring(line.indexOf("<") + 1, line.indexOf("|"));

                if (!sqliName.equalsIgnoreCase("wseSqlInfo")) {
                    throw new WseSqlInfoFileException("Invalid file.");
                }

                log("Loading infos from \"%s\" -- Version: %s", path.getFileName(), version);
                foundInfo = true;
            }

            if (!foundInfo) continue;

            // Declare
            if (line.contains("=") && !line.startsWith("#")) {
                String key = line.split("=")[0].trim().toLowerCase();
                String value = line.substring(line.indexOf("=") + 1).trim();

                if (key.equalsIgnoreCase("password")) key = "pass";
                if (key.equalsIgnoreCase("pwd")) key = "pass";

                if (key.equalsIgnoreCase("db")) key = "database";

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                info.put(key, value);

                // Censor password..
                if (key.contains("pass")) {
                    int valLen = value.length();
                    value = "";
                    for (int i = 0; i < valLen; i++) {
                        value += "*";
                    }
                }
                log("Loading info \"%s\": \"%s\"", key, value);
            }
        }

        /* Get from parsed */
        String host = info.getOrDefault("host", null);
        String user = info.getOrDefault("user", null);
        String pass = info.getOrDefault("pass", null);
        String port = info.getOrDefault("port", "3306");
        String db = database == null ? info.getOrDefault("database", null) : database;

        /* Check args */
        if (host == null) throw new WseSqlInfoFileException("Attention! Key \"host\" could not be read from the file!");
        if (user == null) throw new WseSqlInfoFileException("Attention! Key \"user\" could not be read from the file!");
        if (pass == null) throw new WseSqlInfoFileException("Attention! Key \"pass\" could not be read from the file!");
        if (port == null) throw new WseSqlInfoFileException("Attention! Key \"port\" could not be read from the file!");
        if (db == null) throw new WseSqlInfoFileException("Attention! Key \"db\" could not be read from the file!");

        /* parse port */
        int portInt;
        try {
            portInt = Integer.parseInt(port);
        } catch (NumberFormatException nfex) {
            portInt = 3306;
            log("Attention! The specified port could not be converted to numbers! Use default port: 3306");
        }

        /* set info */
        this.host = host;
        this.database = db;
        this.user = user;
        this.password = pass;
        this.port = portInt;

        this.autoReconnect = ar;
        this.useSsl = ssl;

        /* connect */
        connect();

        log(" ");
    }

    /* ========================================================================================================================================== */

    /**
     * Returns whether a column exists in a ResultSet
     *
     * @param set    ResultSet
     * @param column Column
     * @return boolean | false on error
     */
    public static boolean columnExists(ResultSet set, String column) {
        try {
            ResultSetMetaData resultSetMetaData = set.getMetaData();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                if (resultSetMetaData.getColumnName(i).equals(column)) return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the raw query from a statement
     *
     * @param statement Statement
     * @return String | never null
     */
    public static String getQueryFromStatement(Statement statement) {
        if (statement == null) return "Statement is null.";

        String str = statement.toString();
        if (!str.contains(":")) return "Invalid statement";

        return str.substring(str.indexOf(":", 1) + 2);
    }

    /**
     * Connects to the specified database
     */
    public void connect() {
        try {
            // connect
            String cS = getConnectionString(this.autoReconnect, this.useSsl);
            log("Connecting to \"" + cS + "\"...");

            this.connection = DriverManager.getConnection(cS, user, password);
            this.isReadOnly = this.connection.isReadOnly();

            // success
            log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log("The connection to the MySQL database was successfully established.");

            if (isReadOnly) {
                log("[!] Important! The connection is read-only. I won't be able to make updates. [!]");
            }

            log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (SQLException e) {

            // error
            log("------------------------------------------------------------------");
            System.out.println("The connection to the MySQL database could not be established: " + e.getMessage());
            log("------------------------------------------------------------------");
        }
    }

    /**
     * Returns the connection string of the login data
     *
     * @param autoReconnect Auto-Reconnect  | Should the client automatically reconnect when the connection is lost?
     * @param ssl           useSSL          | Should the client try to connect over secure connection?
     * @param maxReconnects Max-Reconnects  | How often should the client try to reconnect in case of an error?
     * @return String           jdbc - Connection String
     */
    public String getConnectionString(boolean autoReconnect, boolean ssl, int maxReconnects) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database +
                (autoReconnect ? "?autoReconnect=true" : "") +
                (ssl ? (autoReconnect ? "&" : "?") + "useSSL=" + ssl : "") +
                (ssl || autoReconnect ? "&" : "?") + "maxReconnects=" + maxReconnects;
    }

    /**
     * Returns the connection string of the login data
     *
     * @param autoReconnect Auto-Reconnect | Should the database automatically reconnect when the connection is lost?
     * @param ssl           useSSL          | Should the client try to connect over secure connection?
     * @return String           jdbc - Connection String
     */
    public String getConnectionString(boolean autoReconnect, boolean ssl) {
        return getConnectionString(autoReconnect, ssl, 10);
    }

    /**
     * Closes the connection
     */
    public void close() {
        if (getConnection() == null) return;

        try {
            // Close connection
            getConnection().close();
        } catch (SQLException sqlex) {
            // Could not close connection
            log("Attention! The connection to the MySQL database could not be closed: " + sqlex.getMessage());
            return;
        }

        log("The connection to the MySQL database was successfully closed.");
    }

    /**
     * Updates the database and returns the number of rows affected
     *
     * @param query Query
     * @param obj Objects
     * @return int | affected rows
     */
    public int update(String query, Object... obj) {
        updateLastQuery();
        checkConnectionAndReconnect();

        if(getConnection() == null) return -1;

        try {
            if(getConnection().isClosed()) return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement statement = null;
        int ret = -1;

        try {

            statement = getConnection().prepareStatement(query);

            for (int i = 1; i <= obj.length; i++) {
                Object object = obj[i - 1];
                statement.setObject(i, object);
            }

            ret = statement.executeUpdate();

            // Debug
            logQuery(statement);

        } catch (SQLException sqlex) {
            log("Error when updating query|update \"%s\": %s", (statement != null ? statement.toString() : query), sqlex.getMessage());
        }

        return ret;
    }

    /**
     * Executes a query returns its result set
     *
     * @param query Query
     * @param objects Objects
     *
     * @return ResultSet
     */
    public ResultSet executeQuery(String query, Object... objects) {
        updateLastQuery();
        checkConnectionAndReconnect();

        PreparedStatement statement = null;
        ResultSet ret = null;

        try {
            statement = getConnection().prepareStatement(query);

            for (int i = 1; i <= objects.length; i++) {
                statement.setObject(i, objects[i - 1]);
            }

            ret = statement.executeQuery();

            // Debug
            logQuery(statement);

        } catch (SQLException sqlex) {
            log("Error when updating query|query \"%s\": %s", query, sqlex.getMessage());
        }

        return ret;
    }

    /**
     * Returns a map. Key = column name, Value = column value
     *
     * @param query Query
     * @param objects Objects
     * @return List with result
     */
    public WseMap executeQueryMapGet(String query, Object... objects) {
        updateLastQuery();
        checkConnectionAndReconnect();

        ResultSet set = executeQuery(query, objects);
        WseMap ret = new WseMap();

        try {
            if (!(set.next())) return ret;
        } catch (SQLException e) {
            log("Error when executing (executeQueryMapGet): \"%s\": %s", query, e.getMessage());
            return ret;
        }

        ResultSetMetaData meta;
        try {
            meta = set.getMetaData();
        } catch (SQLException e) {
            log("MetaData from ResultSet could not be loaded: " + e.getMessage());
            return ret;
        }

        int columns = getColumnCount(meta);
        if (columns == -1) return ret;

        for (int i = 1; i <= columns; i++) {
            String name = getColumnName(meta, i);
            if (name == null) continue;

            Object object = getObject(set, i);
            if (object == null) continue;

            ret.put(name, object);
        }

        return ret;
    }

    /**
     * Returns a map. Key = column name, Value = column value
     *
     * @param query Query
     * @param objects Objects
     *
     * @return List of results
     */
    public WseMap executeQueryMapSelect(String query, Object... objects) {
        updateLastQuery();
        checkConnectionAndReconnect();

        ResultSet set = executeQuery(query, objects);
        WseMap ret = new WseMap();

        ResultSetMetaData meta;
        try {
            meta = set.getMetaData();
        } catch (SQLException e) {
            log("MetaData from ResultSet could not be loaded: " + e.getMessage());
            return ret;
        }

        int columns = getColumnCount(meta);
        if (columns == -1) return ret;

        try {

            int index = 0;
            while (set.next()) {
                WseMap map = new WseMap();

                for (int i = 1; i <= columns; i++) {
                    String name = getColumnName(meta, i);
                    if (name == null) continue;

                    Object object = getObject(set, i);
                    if (object == null) continue;

                    map.put(name, object);
                }

                ret.put(index, map);
                index++;
            }

        } catch (SQLException sqlex) {
            log("Error on fetching rows: " + sqlex.getMessage());
            return ret;
        }

        return ret;
    }

    /*
     * Misc
     */

    /**
     * Returns whether the connection to the database still exists.
     *
     * @return boolean | false on error
     */
    public boolean isOpen() {
        try {
            return !getConnection().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the column name of a ResultSet
     *
     * @param meta  (ResultSetMetaData) Meta
     * @param index (Integer) Index
     * @return String|null
     */
    private String getColumnName(ResultSetMetaData meta, int index) {
        String r = null;

        try {
            r = meta.getColumnName(index);
        } catch (SQLException e) {
            log("The name of the column (%d) could not be returned: ", index, e.getMessage());
        }

        return r;
    }

    /**
     * Returns an object from a ResultSet
     *
     * @param set   (ResultSet) ResultSet
     * @param index (Integer) Index
     * @return Object|null
     */
    private Object getObject(ResultSet set, int index) {
        Object r = null;

        try {
            r = set.getObject(index);
        } catch (SQLException e) {
            log("");
        }

        return r;
    }

    /**
     * Returns the number of columns from a ResultSet
     *
     * @param set (ResultSet) ResultSet
     * @return int|-1
     */
    private int getColumnCount(ResultSet set) {
        int r = -1;

        try {
            r = getColumnCount(set.getMetaData());
        } catch (SQLException e) {
            log("The number of columns could not be returned: " + e.getMessage());
        }

        return r;
    }

    /**
     * Returns the number of columns from a ResultSetMetaData
     *
     * @param meta (ResultSetMetaData) Meta
     * @return int|-1
     */
    public int getColumnCount(ResultSetMetaData meta) {
        int r = -1;

        try {
            r = meta.getColumnCount();
        } catch (SQLException e) {
            log("The number of columns could not be returned: " + e.getMessage());
        }

        return r;
    }

    /**
     * Checks if there is still a connection. If not, an attempt is made to establish a new connection.
     */
    public void checkConnectionAndReconnect() {
        try {
            if (getConnection() == null || getConnection().isClosed() || !getConnection().isValid(1000) || getConnection().isReadOnly()) {
                log("+-------------------+ Connection closed / read only. Reconnecting: +-------------------+");
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the console
     *
     * @param message (String) Message
     * @param obj     (Object) String#format-Values
     */
    private void log(String message, Object... obj) {
        log(String.format(message, obj));
    }

    /**
     * Sends a message to the console
     *
     * @param message (String) Message
     */
    public void log(String message) {
        System.out.println(String.format("[mysql @ %s ~ %s]: %s", getName(), new SimpleDateFormat("HH:mm:ss.SS").format(new Date(System.currentTimeMillis())), message));
    }

    /**
     * Sends the current query
     *
     * @param statement Statement
     */
    private void logQuery(Statement statement) {
        if (!debugMode) return;
        debug("Executing: \"" + WseMySqlWrapper.getQueryFromStatement(statement) + "\"...");
    }

    /**
     * Sends a debug message when debug mode is activated.
     *
     * @param message Message
     */
    public void debug(String message) {
        if (!isDebugMode()) return;
        System.out.println(String.format("[DEBUG - %s ~ %s]: %s", getName(), new SimpleDateFormat("HH:mm:ss.SS").format(new Date(System.currentTimeMillis())), message));
    }

    public class WseSqlInfoFileException extends Exception {
        WseSqlInfoFileException(String message) {
            super(message);
        }
    }

}