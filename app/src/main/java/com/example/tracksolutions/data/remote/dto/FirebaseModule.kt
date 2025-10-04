package com.example.tracksolutions.data.remote.dto

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object FirebaseModule {
    val db: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // Opcional: configura cache/offline
            // firestoreSettings = firestoreSettings { isPersistenceEnabled = true }
        }
    }
}
