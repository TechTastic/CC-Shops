package io.github.techtastic.ccshops.forge.mixin;

import io.github.techtastic.ccshops.forge.util.ICreativeAccess;
import org.spongepowered.asm.mixin.Mixin;
import wolforce.simpleshops.SimpleShopBlock;

@Mixin(SimpleShopBlock.class)
public class MixinSimpleShopBlock implements ICreativeAccess {
    @Override
    public boolean ccshops$isCreative() {
        return SimpleShopBlock.class.cast(this).isCreative;
    }
}
