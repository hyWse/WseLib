package eu.hywse.libv1_11.databases.mysql.raw;

import eu.hywse.libv1_11.databases.mysql.WseAsyncMySqlWrapper;
import eu.hywse.libv1_11.databases.mysql.WseMySqlWrapper;

public class WseAsyncMySql extends WseAsyncMySqlWrapper {

    public WseAsyncMySql(WseMySqlWrapper mySql) {
        super(mySql);
    }

    @Override
    public void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

}