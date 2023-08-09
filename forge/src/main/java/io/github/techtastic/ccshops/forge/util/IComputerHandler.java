package io.github.techtastic.ccshops.forge.util;

import dan200.computercraft.api.peripheral.IComputerAccess;

public interface IComputerHandler {
    void ccshops$attach(IComputerAccess access);
    void ccshops$detach(IComputerAccess access);

    void ccshops$fireEvent(String string, Object... objects);
}
