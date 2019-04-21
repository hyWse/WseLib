package eu.hywse.test.command;

import eu.hywse.lib.misc.delay.WseCooldown;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CooldownTest {

    @Test
    public void testCooldown() {
        WseCooldown<String> cooldown = new WseCooldown<>(String::valueOf, 1, TimeUnit.SECONDS);

        Assert.assertFalse(cooldown.isOnCooldown("TEST"));
        cooldown.updateCooldown("TEST");

        Assert.assertFalse(cooldown.isOnCooldown("TEST2", true));
        Assert.assertTrue(cooldown.isOnCooldown("TEST2"));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertFalse(cooldown.isOnCooldown("TEST"));
        Assert.assertFalse(cooldown.isOnCooldown("TEST2"));
    }

}
