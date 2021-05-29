package me.limeice.lifecycle.rxjava3;

/**
 * 异常
 *
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public class RxLifecycleException extends RuntimeException {

    public RxLifecycleException() {
        super();
    }

    public RxLifecycleException(String message) {
        super(message);
    }

    public RxLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxLifecycleException(Throwable cause) {
        super(cause);
    }
}
