package eu.hywse.lib.misc.delay;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WseCooldown<T> {

  private long durationMillis;

  private Function<? super T, Object> function;
  private Cache<String, Long> cache;

  public WseCooldown(Function<? super T, Object> function, long duration, TimeUnit timeUnit) {
    this.function = function; // Convert-Function

    this.durationMillis = timeUnit.toMillis(duration); // Millisecond-Delay

    this.cache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(duration, timeUnit)
        .build(); // Google Guava-Cache
  }

  public WseCooldown(long duration, TimeUnit timeUnit) {
    this(null, duration, timeUnit);
  }

  public WseCooldown(Function<? super T, Object> function, long durationInMs) {
    this(function, durationInMs, TimeUnit.MILLISECONDS);
  }

  public WseCooldown(long durationInMs) {
    this(null, durationInMs);
  }

  /**
   * Prüft, ob ein Spieler sich in einer Cooldown-Phase befindet
   *
   * @param check         Das zu prüfende Objekt
   * @param updateIfFalse Sollte der sich noch nicht in einer Cooldown-Phase befinden, wird der
   *                      Cooldown für den Zeitpunkt geupdatet
   * @return TRUE|FALSE
   */
  public boolean isOnCooldown(T check, boolean updateIfFalse) {
    try {
      long diff = System.currentTimeMillis() - cache.get(convertKey(check), () -> 0L);
      boolean isOnTimeout = diff < durationMillis;

      if (!isOnTimeout && updateIfFalse) {
        updateCooldown(check);
      }

      return isOnTimeout;
    } catch (ExecutionException e) {
      return false;
    }
  }

  /**
   * Prüft, ob ein Spieler sich in einer Cooldown-Phase befindet
   *
   * @param check Das zu prüfende Objekt
   */
  public boolean isOnCooldown(T check) {
    return isOnCooldown(check, false);
  }

  /**
   * Aktualisiert die Zeit des zu prüfenden Objektes
   *
   * @param check Das zu prüfende Objekt
   */
  public void updateCooldown(T check) {
    cache.put(convertKey(check), System.currentTimeMillis());
  }

  private String convertKey(T check) {
    return (
        this.function != null
            ? this.function.apply(check)
            : check.toString())
        .toString();
  }

}
