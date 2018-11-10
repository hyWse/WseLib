package eu.hywse.libv1_7.databases.mysql;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class WseAsyncMySqlWrapper {

    private WseMySqlWrapper mySql;
    private ExecutorService executor;

    public WseAsyncMySqlWrapper(WseMySqlWrapper mySql) {
        this.mySql = mySql;

        this.executor = Executors.newCachedThreadPool();
    }

    public WseMySqlWrapper getMySql() {
        return mySql;
    }
    public ExecutorService getExecutor() {
        return executor;
    }

    // Functions
    public void update(String query, Runnable runnable, Object...args) {
        executor.execute(() -> {
            getMySql().update(query, args);
            runAsync(runnable);
        });
    }

    public void query(String query, Consumer<ResultSet> consumer, Object...args) {
        executor.execute(() -> {
            ResultSet result = getMySql().executeQuery(query, args);
            runAsync(() -> consumer.accept(result));
        });
    }

    public abstract void runAsync(Runnable runnable);
}
