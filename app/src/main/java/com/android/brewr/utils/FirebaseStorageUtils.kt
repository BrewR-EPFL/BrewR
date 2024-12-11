package com.android.brewr.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun deletePicture(imageUrl: String, onSuccess: () -> Unit) {
  val storagePath = "images/${imageUrl.substringAfter("%2F").substringBefore("?alt")}"
  val storageRef = FirebaseStorage.getInstance().getReference()
  val imgRefToDelete = storageRef.child(storagePath)

  imgRefToDelete
      .delete()
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener { Log.e("FirebaseStorageUtils", "Error deleting image: $it") }
}

fun uploadPicture(imageUri: Uri, onSuccess: (String) -> Unit) {
  val imgPath = "images/" + UUID.randomUUID().toString()
  val imgRef = FirebaseStorage.getInstance().getReference().child(imgPath)

  imgRef
      .putFile(imageUri)
      .addOnSuccessListener { imgRef.downloadUrl.addOnSuccessListener { onSuccess(it.toString()) } }
      .addOnFailureListener { Log.e("FirebaseStorageUtils", "Failed to upload image", it) }
}

/**
 * Updates the picture in Firebase Storage.
 *
 * @param imageUri The URI of the new image to upload.
 * @param oldImageUrl The URL of the old image to delete.
 * @param onSuccess Callback function to be invoked with the new image URL upon successful upload.
 */
fun updatePicture(imageUri: Uri, oldImageUrl: String, onSuccess: (String) -> Unit) {
  val storagePath = "images/${oldImageUrl.substringAfter("%2F").substringBefore("?alt")}"
  Log.d("EditJourneyScreen", "Deleting image with path $storagePath")
  Log.d("edit journey screen", "delete image of $oldImageUrl in storage")

  val storageRef = FirebaseStorage.getInstance().getReference()
  val imgRefToDelete = storageRef.child(storagePath)

  imgRefToDelete.delete().addOnFailureListener {
    Log.e("EditJourneyScreen", "Failed to delete image", it)
  }

  val newImagePath = "images/${UUID.randomUUID()}"
  val newImageRef = storageRef.child(newImagePath)

  newImageRef
      .putFile(imageUri)
      .addOnSuccessListener {
        newImageRef.downloadUrl.addOnSuccessListener { uri ->
          val u = uri.toString()
          Log.d("edit journey screen", "add on image of$u in storage")
          onSuccess(u)
        }
      }
      .addOnFailureListener { Log.e("EditJourneyScreen", "Failed to upload image", it) }
}
