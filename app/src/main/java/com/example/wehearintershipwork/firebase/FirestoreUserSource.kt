package com.example.wehearintershipwork.firebase

import android.net.Uri
import com.example.wehearintershipwork.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class FirestoreUserSource {
    companion object {
        private const val USER_COLLECTION = "users"
    }

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val dbStorage:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    /**
     * save the user who is newly registered in app
     */
    suspend fun saveUser(user: User): String {
        return db.collection(USER_COLLECTION)
            .add(user)
            .await()
            .id
    }

    suspend fun uploadImageToStore(uri: Uri, name: String):String{
        val storageReference=dbStorage.getReference("profilePicture")
        val fileRef =storageReference.child(name);
        val file=File(uri.path)
        val stream:FileInputStream=file.inputStream()
        fileRef.putStream(stream).await()
        val url=fileRef.downloadUrl.await().toString()
        return url
    }

}