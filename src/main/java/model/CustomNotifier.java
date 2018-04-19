package model;

import java.util.Properties;

public class CustomNotifier {
    private String type;
    private boolean sendOnError = true;
    private boolean sendOnFailure = true;
    private boolean sendOnSuccess = true;
    private boolean sendOnWarning = true;
    private String address;
    private Properties configuration;

    public String getType() {
        return type;
    }

    public boolean isSendOnError() {
        return sendOnError;
    }

    public boolean isSendOnFailure() {
        return sendOnFailure;
    }

    public boolean isSendOnSuccess() {
        return sendOnSuccess;
    }

    public boolean isSendOnWarning() {
        return sendOnWarning;
    }

    public String getAddress() {
        return address;
    }

    public Properties getConfiguration() {
        return configuration;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSendOnError(boolean sendOnError) {
        this.sendOnError = sendOnError;
    }

    public void setSendOnFailure(boolean sendOnFailure) {
        this.sendOnFailure = sendOnFailure;
    }

    public void setSendOnSuccess(boolean sendOnSuccess) {
        this.sendOnSuccess = sendOnSuccess;
    }

    public void setSendOnWarning(boolean sendOnWarning) {
        this.sendOnWarning = sendOnWarning;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setConfiguration(Properties configuration) {
        this.configuration = configuration;
    }
}