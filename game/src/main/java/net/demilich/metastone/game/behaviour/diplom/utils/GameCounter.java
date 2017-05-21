package net.demilich.metastone.game.behaviour.diplom.utils;

/**
 * Created by ilya2 on 19.05.2017.
 */
public class GameCounter {
    private static int count = -2;
    private static int statecount = 0;

    public synchronized static int getCount() {
        count++;
        return count;
    }

    public synchronized static int getStateCount() {
        return statecount;
    }

    public synchronized static void addStateCount() {
        statecount++;
    }
}
