package com.example.lagvis_v1.core.ui;


public abstract class UiState<T> {
    public static final class Initial<T> extends UiState<T> {}
    public static final class Loading<T> extends UiState<T> {}
    public static final class Success<T> extends UiState<T> {
        public final T data; public Success(T d){ this.data=d; }
    }
    public static final class Error<T> extends UiState<T> {
        public final String message; public Error(String m){ this.message=m; }
    }
}

