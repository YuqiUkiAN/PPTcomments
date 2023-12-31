package com.example.pptcomments.learningGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class GroupViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 实例
    private val auth = Firebase.auth // 获取 Firebase Auth 实例

    // 获取当前匿名用户的 ID
    private val currentUserId: String?
        get() = if (auth.currentUser != null && auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.uid
        } else {
            null
        }

    private val _groups = MutableStateFlow<List<CourseGroup>>(emptyList())
    val sortedGroups = _groups.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            db.collection("groups").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // 处理错误
                    return@addSnapshotListener
                }

                val groupsList = snapshot?.documents?.mapNotNull {
                    it.toObject(CourseGroup::class.java)
                } ?: emptyList()
                _groups.value = groupsList.sortedByDescending { it.lastAccessedTime }
            }
        }
    }

    // 创建小组的集合和文档
    fun createGroup(name: String, description: String, creatorId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val creatorId = currentUserId
        if (creatorId == null) {
            onError(Exception("No user ID found. Make sure the user is logged in."))
            return
        }
        val newGroupId = db.collection("groups").document().id // 自动生成文档ID
        val currentTime = System.currentTimeMillis() // 获取当前时间的时间戳
        val group = hashMapOf(
            "name" to name,
            "description" to description,
            "creator" to creatorId,
            "members" to listOf(creatorId), // 初始成员列表包含创建者
            "lastAccessedTime" to currentTime // 设置当前时间的时间戳
        )

        db.collection("groups").document(newGroupId).set(group)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    // 加入小组
    fun joinGroup(groupId: String, groupName: String, groupLink: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val groupRef = db.collection("groups").document(groupId)
        val currentTime = System.currentTimeMillis() // 获取当前时间的时间戳

        db.runTransaction { transaction ->
            val snapshot = transaction.get(groupRef)
            val currentName = snapshot.getString("name") ?: ""
            if (currentName == groupName) {
                val members = snapshot.get("members") as? List<String> ?: listOf()
                if (!members.contains("userId")) { // 假设 "userId" 是当前用户的ID
                    transaction.update(groupRef, "members", members + "userId")
                    transaction.update(groupRef, "lastAccessedTime", currentTime)
                    onSuccess()
                } else {
                    onError(Exception("您已是该小组成员"))
                }
            } else {
                onError(Exception("小组名称不匹配"))
            }
        }.addOnFailureListener { e ->
            onError(e)
        }
    }

    // 更新小组的最后访问时间
    fun updateGroupLastAccessedTime(groupId: String) {
        val currentTime = System.currentTimeMillis()
        db.collection("groups").document(groupId)
            .update("lastAccessedTime", currentTime)
            .addOnSuccessListener {
                // 成功更新时间
            }
            .addOnFailureListener {
                // 处理更新失败
            }
    }
}
