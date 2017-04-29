package nz.co.jammehcow.jenkinsdiscord.exception;

/**
 * @author jammehcow
 */

public class WebhookException extends Exception {
    public WebhookException() { super(); }

    public WebhookException(String message) { super(message); }

    public WebhookException(String message, Throwable cause) { super(message, cause); }

    public WebhookException(Throwable cause) { super(cause); }
}
