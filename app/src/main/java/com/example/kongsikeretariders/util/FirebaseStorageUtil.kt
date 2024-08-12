package com.example.kongsikeretariders.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseStorageUtil {
    companion object {
        suspend fun uploadImage(uri: Uri?, userId: String, context: Context): Uri? {
            var downloadUri: Uri? = null

            val storage = Firebase.storage

            val storageRef = storage.reference

            val profileRef = storageRef.child("riders/$userId/profilePic.jpg")

            val byteArray: ByteArray? =
                context.contentResolver.openInputStream(uri!!)?.use { it.readBytes() }

            byteArray?.let {
                try {
                    var uploadTask = profileRef.putBytes(byteArray).await()
                    downloadUri = profileRef.downloadUrl.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return downloadUri
        }

        suspend fun uploadUserData(json: File, userId: String): Uri? {
            val storage = Firebase.storage

            val storageRef = storage.reference

            val jsonRef = storageRef.child("riders/$userId/accountData.json")

            //val data = File(context.cacheDir,"$userId")

            val uploadTask = jsonRef.putFile(Uri.fromFile(json)).await()

            return jsonRef.downloadUrl.await()
        }

        suspend fun fetchUserData(
            url: String, userId: String, fileType: String, context: Context
        ): File? {
            val storage = Firebase.storage

            val storageRef = storage.getReferenceFromUrl(url)

            val data = File(context.cacheDir,"$userId.$fileType")

            return try{
                storageRef.getFile(data).await()
                Log.i("dataFromFirebaseStorage",data.toString())
                data
            } catch(e : Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}