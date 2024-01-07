package com.example.pptcomments.learningGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pptcomments.uploadAndShare.PPT
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GroupViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 实例
    private val auth = Firebase.auth // 获取 Firebase Auth 实例

    private val _searchResults = MutableStateFlow<List<CourseGroup>>(emptyList())
    val searchResults: StateFlow<List<CourseGroup>> = _searchResults

    // 获取当前匿名用户的 ID
    val currentUserId: String?
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
    fun createGroup(name: String, description: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val creatorId = currentUserId
        if (creatorId == null) {
            onError(Exception("No user ID found. Make sure the user is logged in."))
            return
        }
        val newGroupId = db.collection("groups").document().id // 自动生成文档ID
        val currentTime = System.currentTimeMillis() // 获取当前时间的时间戳
        val group = CourseGroup(
            id = newGroupId,
            name = name,
            description = description,
            creator = creatorId,
            members = listOf(creatorId),
            lastAccessedTime = currentTime // 使用 Long 类型的时间戳
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
    fun joinGroup(groupId: String, groupName: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val groupRef = db.collection("groups").document(groupId)
        val currentTime = System.currentTimeMillis() // 获取当前时间的时间戳

        db.runTransaction { transaction ->
            val snapshot = transaction.get(groupRef)
            val currentName = snapshot.getString("name") ?: ""
            if (currentName == groupName) {
                val members = snapshot.get("members") as? List<String> ?: listOf()
                if (!members.contains("userId")) {
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

    // 搜索小组
    fun searchGroups(query: String) {
        viewModelScope.launch {
            db.collection("groups")
                .whereEqualTo("name", query)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 处理错误
                        return@addSnapshotListener
                    }
                    val groups = snapshot?.documents?.mapNotNull {
                        it.toObject(CourseGroup::class.java)
                    } ?: emptyList()
                    _searchResults.value = groups
                }
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

    // 获取特定小组的详细信息
    fun getGroupById(groupId: String): StateFlow<CourseGroup?> {
        val groupFlow = MutableStateFlow<CourseGroup?>(null)
        viewModelScope.launch {
            db.collection("groups").document(groupId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 处理错误
                        return@addSnapshotListener
                    }
                    groupFlow.value = snapshot?.toObject(CourseGroup::class.java)
                }
        }
        return groupFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    // 获取小组成员
    fun getGroupMembers(groupId: String): StateFlow<List<String>> {
        val membersFlow = MutableStateFlow<List<String>>(emptyList())
        viewModelScope.launch {
            db.collection("groups").document(groupId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 处理错误
                        return@addSnapshotListener
                    }
                    val group = snapshot?.toObject(CourseGroup::class.java)
                    membersFlow.value = group?.members ?: emptyList()
                }
        }
        return membersFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    // 退出小组
    fun leaveGroup(groupId: String, onResult: (Boolean, String?) -> Unit) {
        val userId = currentUserId ?: return onResult(false, "User not logged in")
        viewModelScope.launch {
            db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener { document ->
                    val group = document.toObject(CourseGroup::class.java)
                    val updatedMembers = group?.members?.filterNot { it == userId } ?: listOf()
                    db.collection("groups").document(groupId)
                        .update("members", updatedMembers)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Error leaving group") }
                }
                .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Error leaving group") }
        }
    }

    // 删除小组成员
    fun removeMemberFromGroup(groupId: String, memberId: String, onResult: (Boolean, String?) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        viewModelScope.launch {
            db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener { document ->
                    val group = document.toObject(CourseGroup::class.java)
                    if (group == null) {
                        onResult(false, "Group not found")
                        return@addOnSuccessListener
                    }

                    if (group.creator != userId) {
                        onResult(false, "Only the group creator can remove members")
                        return@addOnSuccessListener
                    }

                    if (!group.members.contains(memberId)) {
                        onResult(false, "Member not in group")
                        return@addOnSuccessListener
                    }

                    val updatedMembers = group.members.filterNot { it == memberId }
                    db.collection("groups").document(groupId)
                        .update("members", updatedMembers)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Error removing member") }
                }
                .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Error removing member") }
        }
    }

    // 获取特定小组的 PPT 列表
    fun getGroupPPTs(groupId: String): StateFlow<List<PPT>> {
        val pptsFlow = MutableStateFlow<List<PPT>>(emptyList())
        viewModelScope.launch {
            db.collection("ppts")
                .whereEqualTo("groupId", groupId)
                .orderBy("uploadedAt", Query.Direction.DESCENDING) // 从新到旧排序
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 处理错误
                        return@addSnapshotListener
                    }
                    val pptsList = snapshot?.documents?.mapNotNull {
                        it.toObject(PPT::class.java)
                    } ?: emptyList()
                    pptsFlow.value = pptsList
                }
        }
        return pptsFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
}
