package org.webdifftool.client.model;

import java.time.Instant;

public class GitInfoParams {
    private final String leftCommitUri;

    private final String rightCommitUri;

    private final String leftDatetime;

    private final String rightDatetime;

    private final String leftMessage;

    private final String rightMessage;

    public GitInfoParams(String leftCommitUri, String rightCommitUri, String leftDatetime, String rightDatetime, String leftMessage, String rightMessage) {
        this.leftCommitUri = leftCommitUri;
        this.rightCommitUri = rightCommitUri;
        this.leftDatetime = leftDatetime;
        this.rightDatetime = rightDatetime;
        this.leftMessage = leftMessage;
        this.rightMessage = rightMessage;
    }


    public String getLeftCommitUri() {
        return leftCommitUri;
    }

    public String getRightCommitUri() {
        return rightCommitUri;
    }

    public String getLeftDatetime() {
        return leftDatetime;
    }

    public String getRightDatetime() {
        return rightDatetime;
    }

    public String getLeftMessage() {
        return leftMessage;
    }

    public String getRightMessage() {
        return rightMessage;
    }

    @Override
    public String toString() {
        return "GitInfoParams{" +
                "leftCommitUri='" + leftCommitUri + '\'' +
                ", rightCommitUri='" + rightCommitUri + '\'' +
                ", leftDatetime='" + leftDatetime + '\'' +
                ", rightDatetime='" + rightDatetime + '\'' +
                ", leftMessage='" + leftMessage + '\'' +
                ", rightMessage='" + rightMessage + '\'' +
                '}';
    }
}