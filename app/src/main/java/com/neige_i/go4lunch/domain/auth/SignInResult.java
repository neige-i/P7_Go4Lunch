package com.neige_i.go4lunch.domain.auth;

import androidx.annotation.NonNull;

public abstract class SignInResult {

    public static class Success extends SignInResult {

        @Override
        public boolean equals(Object o) {
            return o instanceof Success;
        }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Failure failure = (Failure) o;
            return failure.exception.getClass().equals(exception.getClass());
        }
    }
}
