package eu.hywse.lib.mysql.raw;

import eu.hywse.lib.mysql.WseMySqlWrapper;

public class WseMySql extends WseMySqlWrapper {

    public WseMySql(String name, String host, String database, String user, String password, int port) {
        super(name, host, database, user, password, port);
    }

    public WseMySql(String name) throws WseSqlInfoFileException {
        super(name);
    }

    public WseMySql(String name, String sqlInfoFile) throws WseSqlInfoFileException {
        super(name, sqlInfoFile);
    }

    public WseMySql(String name, String sqlInfoFile, String database) throws WseSqlInfoFileException {
        super(name, sqlInfoFile, database);
    }

}
