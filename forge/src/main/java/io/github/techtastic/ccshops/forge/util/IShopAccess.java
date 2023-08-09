package io.github.techtastic.ccshops.forge.util;

import java.util.UUID;

public interface IShopAccess {
    UUID ccshops$getOwner();

    int ccshops$getStockNr();

    int ccshops$getGainsNr();
}
