package net.sinender.bungeemsg.pubsub;

/**
 * @author Andrew R.
 */
@FunctionalInterface
public interface Callback {
    void onMessage(String[] args);
}

