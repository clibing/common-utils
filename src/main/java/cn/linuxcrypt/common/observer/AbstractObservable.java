package cn.linuxcrypt.common.observer;

/**
 * @author clibing
 */
public abstract class AbstractObservable implements Cloneable {
    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Long getId();
}