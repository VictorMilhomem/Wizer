package com.github.wizerapp.repository

import android.util.Log
import com.github.wizerapp.model.Meeting
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MeetingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("meetings")

    suspend fun addMeeting(meeting: Meeting): Result<String> = try {
        val meetingData = hashMapOf(
            "groupId" to meeting.groupId,
            "date" to meeting.date,
            "location" to meeting.location,
            "attendees" to meeting.attendees
        )
        val docRef = collection.add(meetingData).await()
        Log.d("Firestore", "Meeting created with ID: ${docRef.id}")
        Result.success(docRef.id)
    } catch (e: Exception) {
        Log.e("Firestore", "Error creating meeting", e)
        Result.failure(e)
    }

    suspend fun getMeeting(meetingId: String): Result<Meeting> = try {
        val doc = collection.document(meetingId).get().await()
        val meeting = doc.toObject(Meeting::class.java)
        if (meeting != null) Result.success(meeting)
        else Result.failure(Exception("Meeting not found"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addAttendee(meetingId: String, studentId: String): Result<Unit> {
        return try {
            val docRef = collection.document(meetingId)
            val document = docRef.get().await()

            if (!document.exists()) {
                return Result.failure(Exception("Meeting not found"))
            }

            val attendees = (document.get("attendees") as? MutableList<String>) ?: mutableListOf()
            if (studentId in attendees) {
                return Result.failure(Exception("Student already confirmed"))
            }

            attendees.add(studentId)
            docRef.update("attendees", attendees.toList()).await()
            Log.d("Firestore", "✅ Attendance confirmed for $studentId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Error in addAttendee", e)
            Result.failure(e)
        }
    }

    suspend fun updateMeeting(meetingId: String, newDate: Timestamp, newLocation: String): Result<Unit> = try {
        collection.document(meetingId).update(
            "date", newDate,
            "location", newLocation
        ).await()
        Log.d("Firestore", "Meeting updated: $newDate, $newLocation")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("Firestore", "Error updating meeting", e)
        Result.failure(e)
    }

    suspend fun deleteMeeting(meetingId: String): Result<Unit> = try {
        collection.document(meetingId).delete().await()
        Log.d("Firestore", "Meeting deleted successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("Firestore", "Error deleting meeting", e)
        Result.failure(e)
    }

    suspend fun getMeetingsByGroup(groupId: String): Result<List<Meeting>> = try {
        val result = collection.whereEqualTo("groupId", groupId).get().await()
        val meetings = result.documents.mapNotNull { it.toObject(Meeting::class.java) }
        Result.success(meetings)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUpcomingMeetings(): Result<List<Meeting>> = try {
        val result = collection.whereGreaterThan("date", Timestamp.now()).get().await()
        val meetings = result.documents.mapNotNull { it.toObject(Meeting::class.java) }
        Result.success(meetings)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
