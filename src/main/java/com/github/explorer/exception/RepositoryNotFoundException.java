package com.github.explorer.exception;

public class RepositoryNotFoundException extends RuntimeException {
    public RepositoryNotFoundException(String owner, String repositoryName) {
        super(String.format("Repository '%s/%s' not found", owner, repositoryName));
    }
}