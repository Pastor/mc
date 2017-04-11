package mc.minecraft.data.game.world.effect;

import mc.minecraft.data.game.world.block.BlockState;

public class BreakBlockEffectData implements WorldEffectData {
    private BlockState blockState;

    public BreakBlockEffectData(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BreakBlockEffectData && this.blockState.equals(((BreakBlockEffectData) o).blockState);
    }

    @Override
    public int hashCode() {
        return this.blockState.hashCode();
    }
}
