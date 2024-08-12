package com.example.kongsikeretariders.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.kongsikeretariders.data.Rider
import com.example.kongsikeretariders.data.RiderInfo
import com.example.kongsikeretariders.data.Rides
import com.example.kongsikeretariders.ui.rides.UserDriverInfo
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val ridersCollection = firestore.collection("riders")
    private val rideCollection = firestore.collection("rides")
    private val driversCollection = firestore.collection("drivers")
    private val onGoingRideCollection = firestore.collection("onGoingRides")

    var currentRider: HashMap<String, File?>? = null

    suspend fun registerUser(
        uri: Uri,
        rider: Rider,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        val imageDownloadUri = FirebaseStorageUtil.uploadImage(uri, rider.ic, context)
        val userDataDownloadUri = FirebaseStorageUtil.uploadUserData(
            JsonHelper.createJsonFile(
                context,
                rider.ic,
                rider
            ),
            rider.ic,
        )

        Log.i("downloadUri", userDataDownloadUri.toString())

        if (imageDownloadUri != null && userDataDownloadUri != null) {
            val driverData = hashMapOf(
                "ic" to rider.ic,
                "password" to rider.password,
                "profilePicUrl" to imageDownloadUri,
                "userDataUrl" to userDataDownloadUri,
            )
            Log.i("driverData", driverData.toString())
            ridersCollection.document(rider.ic).set(driverData).addOnSuccessListener {
                Toast.makeText(context, "Successfully Registered", Toast.LENGTH_LONG).show()
                callback(true)
            }
        }
    }

    suspend fun signIn(
        userId: String,
        password: String,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        var isSuccess = false
        val userDocRef = ridersCollection.document(userId)
        var userDoc: RiderInfo? = null
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val user = document.toObject(RiderInfo::class.java)
                if (password == document.getString("password")) {
                    Log.i("currentUserRepository", user.toString())
                    userDoc = user
                    isSuccess = true
                } else {
                    Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_LONG).show()
                    isSuccess = false
                }
            } else {
                isSuccess = false
            }
        }.addOnFailureListener {
            isSuccess = false
            it.printStackTrace()
        }.await()
        if (userDoc != null) {
            parseUserData(userDoc!!, context)
            callback(isSuccess)
        }
    }

    suspend fun fetchRides(userId: String?): List<Rides> {
        return if (userId == null) {
            val snapshot = rideCollection.get().addOnFailureListener {
                Log.e(
                    "failed", it.toString()
                )
            }.await()
            Log.i("rides", snapshot.toObjects(Rides::class.java).toString())
            snapshot.toObjects(Rides::class.java)
        } else {
            val snapshot = rideCollection.whereEqualTo("userId", userId).get().await()
            snapshot.toObjects(Rides::class.java)
        }
    }

    suspend fun fetchDriverInfo(userId: String, context: Context): HashMap<String, File?> {
        val userDocRef = driversCollection.document(userId)
        var userDoc: UserDriverInfo? = null
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists() && document != null) {
                userDoc = document.toObject(UserDriverInfo::class.java)
            }else{
                userDoc = UserDriverInfo()
            }
        }.await()
        return if (userDoc != null) {
            parseUserData(userDoc!!, context)
        } else {
            Log.i("userDoc is Null","null")
            parseUserData(UserDriverInfo(), context)
        }
    }

    suspend fun updateRide(
        rides: Rides,
        callback: (Boolean) -> Unit,
        isJoin: Boolean
    ) {
        var currentRide: Rides? = null

        try {
            Log.i("rideIdIn Update",rides.rideId)
            onGoingRideCollection.document(rides.rideId).get()
                .addOnSuccessListener { currentRide = it.toObject(Rides::class.java) }
                .addOnFailureListener {
                }
        } catch (e: Exception) {
            Log.i("failed", e.toString())
            createOnGoingRide(rides)
        }

        if (isJoin) {
            val updatedJoined =
                currentRide?.joined?.plus(PreferencesManager.getStringPreference("userId"))
            Log.i("currentRide",currentRide.toString())
            onGoingRideCollection.document(rides.userId).update("joined", updatedJoined)

        } else {
            val updatedCancelled =
                currentRide?.cancelled?.plus(PreferencesManager.getStringPreference("userId"))
            Log.i("currentRide",currentRide.toString())
            onGoingRideCollection.document(rides.userId).update("cancelled", updatedCancelled)
        }


    }

    suspend fun createOnGoingRide(rides: Rides){
        val uniqueId = UUID.randomUUID().toString()

        val rideInfo = hashMapOf(
            "rideId" to uniqueId,
            "date" to rides.date,
            "time" to rides.time,
            "origin" to rides.origin,
            "destination" to rides.destination,
            "fare" to rides.fare,
            "userId" to rides.userId,
            "joined" to rides.joined,
            "cancelled" to rides.cancelled,
        )

        onGoingRideCollection.document(uniqueId).set(rideInfo).addOnFailureListener {
            it.printStackTrace()
        }
        //onGoingRideCollection.document(uniqueId).get().await().toObject(Rides::class.java)
    }

    suspend fun getOnGoingRide(
        rides: Rides
    ): Rides{
        var onGoingRides = Rides()
        var onGoingId: String? = null
        var onGoingRidesList: List<Rides>? = null
        try {
            onGoingRideCollection.whereEqualTo("date",rides.date).get().addOnSuccessListener {
                onGoingRidesList = it.toObjects(Rides::class.java)
            }.await()
            Log.i("onGoingRidesList",onGoingRidesList.toString())
        } catch (e:Exception){
            Log.i("invokedCreateOnGoingRides","yes")
            createOnGoingRide(rides)
        }

        rideCollection.get().addOnSuccessListener {
            val list: List<Rides> = it.toObjects(Rides::class.java)
            Log.i("rideCollection",list.toString())
            for (i in list) {
                for(j in onGoingRidesList!!){
                    if(i.date == j.date){
                        onGoingId = j.rideId
                        Log.i("matched Dates",onGoingId.toString())
                    }
                }
            }

        }.await()

        try{
            onGoingRides = onGoingRideCollection.document(onGoingId!!).get().await().toObject(Rides::class.java) ?: Rides()
        } catch (e: Exception){
            Log.i("null",e.toString())
        }
        Log.i("onGOignRides",onGoingRides.toString())
        return onGoingRides
    }


    suspend fun getAllOnGoingRides(): List<Rides>{
        val snapshot = onGoingRideCollection.get().await()
        val onGoingRides = snapshot.toObjects(Rides::class.java)
        return onGoingRides
    }

    private suspend fun parseUserData(
        userDoc: RiderInfo,
        context: Context
    ) {
        val profilePicFile =
            FirebaseStorageUtil.fetchUserData(userDoc.profilePicUrl, userDoc.ic, "png", context)
        val userDataFile =
            FirebaseStorageUtil.fetchUserData(userDoc.userDataUrl, userDoc.ic, "json", context)

        currentRider = hashMapOf(
            "profilePic" to profilePicFile,
            "userData" to userDataFile
        )

        Log.i("parsedUserData", currentRider.toString())
    }

    private suspend fun parseUserData(
        userDoc: UserDriverInfo,
        context: Context
    ): HashMap<String, File?> {
        val profilePicFile =
            FirebaseStorageUtil.fetchUserData(
                userDoc.profilePicUrl,
                "${userDoc.ic}_driver",
                "png",
                context
            )
        val userDataFile =
            FirebaseStorageUtil.fetchUserData(
                userDoc.userDataUrl,
                "${userDoc.ic}_driver",
                "json",
                context
            )

        Log.i(
            "parsedDriverData", hashMapOf(
                "profilePic" to profilePicFile,
                "userData" to userDataFile
            ).toString()
        )

        return hashMapOf(
            "profilePic" to profilePicFile,
            "userData" to userDataFile
        )

    }
}