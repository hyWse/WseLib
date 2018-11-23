package eu.hywse.libv1_8.bukkit.extension;

import org.bukkit.block.Block;

public interface IterateBlockConsumer {

    void iterate(int current, int x, int y, int z, Block block);

}
