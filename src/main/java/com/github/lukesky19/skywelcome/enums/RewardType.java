package com.github.lukesky19.skywelcome.enums;

import javax.annotation.Nullable;

public enum RewardType {
    ITEM,
    CASH,
    COMMANDS;

    @Nullable
    public static RewardType getType(String type) {
        switch(RewardType.valueOf(type)) {
            case ITEM -> {
                return RewardType.ITEM;
            }

            case CASH -> {
                return RewardType.CASH;
            }

            case COMMANDS -> {
                return RewardType.COMMANDS;
            }

            default -> {
                return null;
            }
        }
    }
}
