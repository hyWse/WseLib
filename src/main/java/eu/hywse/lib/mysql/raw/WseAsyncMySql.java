package eu.hywse.lib.mysql.raw;

import eu.hywse.lib.mysql.WseAsyncMySqlWrapper;
import eu.hywse.lib.mysql.WseMySqlWrapper;

public class WseAsyncMySql extends WseAsyncMySqlWrapper {

    public WseAsyncMySql(WseMySqlWrapper mySql) {
        super(mySql);
    }

    @Override
    public void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

}
