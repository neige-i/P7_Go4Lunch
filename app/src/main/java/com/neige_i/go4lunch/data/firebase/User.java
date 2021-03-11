package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.Nullable;

import java.util.Objects;

public class User {

    @Nullable
    private final String id;
    @Nullable
    private final String email;
    @Nullable
    private final String name;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public User() {
        id = null;
        email = null;
        name = null;
    }

    public User(@Nullable String id, @Nullable String email, @Nullable String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
            Objects.equals(email, user.email) &&
            Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }
}
