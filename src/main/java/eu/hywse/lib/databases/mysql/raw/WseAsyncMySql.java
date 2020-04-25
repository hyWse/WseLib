package eu.hywse.lib.databases.mysql.raw;

import eu.hywse.lib.databases.mysql.WseAsyncMySqlWrapper;
import eu.hywse.lib.databases.mysql.WseMySqlWrapper;

public class WseAsyncMySql extends WseAsyncMySqlWrapper {

  public WseAsyncMySql(WseMySqlWrapper mySql) {
    super(mySql);
  }

  @Override
  public void runAsync(Runnable runnable) {
    new Thread(runnable).start();
  }

}
