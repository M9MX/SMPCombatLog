package org.m9mx.smpcombatlog.model;

import org.bukkit.entity.Player;

/**
 * Represents an active combat session between two players
 */
public class CombatSession {

    private final Player player;
    private final Player opponent;
    private long startTime;
    private boolean messagesSent;

    public CombatSession(Player player, Player opponent) {
        this.player = player;
        this.opponent = opponent;
        this.startTime = System.currentTimeMillis();
        this.messagesSent = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(boolean messagesSent) {
        this.messagesSent = messagesSent;
    }
}
