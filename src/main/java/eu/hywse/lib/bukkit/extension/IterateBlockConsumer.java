package eu.hywse.lib.bukkit.extension;

import org.bukkit.block.Block;

public interface IterateBlockConsumer {

  void iterate(int current, int x, int y, int z, Block block);

}
