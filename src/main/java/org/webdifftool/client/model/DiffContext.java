package org.webdifftool.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DiffContext {

    private final String fileLeft;

    private final String fileRight;

    private final String rawUrlLeft;

    private final String rawUrlRight;

    private final String leftCommitUri;

    private final String rightCommitUri;

    private final String leftDatetime;

    private final String rightDatetime;

    private final String leftMessage;

    private final String rightMessage;

    private final String outputFile;

    private final String allDiffsNQuadFile;
}