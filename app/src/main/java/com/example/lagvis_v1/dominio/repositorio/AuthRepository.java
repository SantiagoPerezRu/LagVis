// domain/repository/AuthRepository.java
package com.example.lagvis_v1.dominio.repositorio;

import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    FirebaseUser currentUser();      // puede ser null
    //String currentUid();             // null si no logueado
    boolean isSignedIn();
    void signOut();
    String uidOrNull();
    interface AuthCallback { void onSuccess(); void onError(String msg); }
    void signIn(String email, String password, AuthCallback cb);
    void sendPasswordReset(String email, AuthCallback cb);
    void signUp(String email, String password, AuthCallback cb);
    void sendEmailVerification(AuthCallback cb);
    String currentEmail(); // puede devolver null

}
