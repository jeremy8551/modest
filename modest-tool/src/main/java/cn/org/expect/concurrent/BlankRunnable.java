package cn.org.expect.concurrent;

import cn.org.expect.util.WaitForCondition;

public class BlankRunnable implements Runnable {

    public final static BlankRunnable INSTANCE = new BlankRunnable();

    public final static WaitForCondition CONDITION = new WaitForCondition() {
        public boolean test() {
            return true;
        }
    };

    private BlankRunnable() {
    }

    public void run() {
    }
}
