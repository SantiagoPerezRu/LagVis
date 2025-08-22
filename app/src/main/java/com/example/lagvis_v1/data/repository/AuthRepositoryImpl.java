// com/example/lagvis_v1/data/repository/AuthRepositoryImpl.java
package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.dominio.repositorio.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth auth;

    public AuthRepositoryImpl(FirebaseAuth auth){ this.auth = auth; }

    // 👇 IMPLEMENTA EL QUE FALTA
    @Override
    public FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    @Override
    public String uidOrNull() {
        FirebaseUser u = auth.getCurrentUser();
        return u != null ? u.getUid() : null;
    }

    @Override
    public boolean isSignedIn() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public void signOut() { auth.signOut(); }

    @Override
    public void signIn(String email, String password, AuthCallback cb) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) cb.onSuccess();
                    else cb.onError(task.getException() != null ? task.getException().getMessage()
                            : "Error de autenticación");
                });
    }

    @Override
    public void sendPasswordReset(String email, AuthCallback cb) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException()!=null ? t.getException().getMessage() : "No se pudo enviar el email");
                });
    }

    @Override
    public void signUp(String email, String password, AuthCallback cb) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException()!=null ? t.getException().getMessage() : "Registro fallido");
                });
    }

    @Override
    public void sendEmailVerification(AuthCallback cb) {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) { cb.onError("Usuario no autenticado"); return; }
        u.sendEmailVerification().addOnCompleteListener(t -> {
            if (t.isSuccessful()) cb.onSuccess();
            else cb.onError(t.getException()!=null ? t.getException().getMessage() : "No se pudo enviar verificación");
        });
    }

    @Override public String currentEmail() {
        com.google.firebase.auth.FirebaseUser u = auth.getCurrentUser();
        return u != null ? u.getEmail() : null;
    }
}
