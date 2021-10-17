package com.neige_i.go4lunch.domain.auth;

import androidx.annotation.NonNull;

public abstract class SignInResult {

    public static class Success extends SignInResult {
    }

    public static class Failure extends SignInResult {

        @NonNull
        private final Exception exception;

        public Failure(@NonNull Exception exception) {
            this.exception = exception;
        }

        @NonNull
        public Exception getException() {
            return exception;
        }
    }
}
