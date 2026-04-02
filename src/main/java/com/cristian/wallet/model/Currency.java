package com.cristian.wallet.model;

public enum Currency {
    PESOS_URUGUAYOS("Pesos Uruguayos"),
    DOLARES("Dolares"),
    UI("UI (unidades indexadas)");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
