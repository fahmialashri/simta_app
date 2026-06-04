package com.project.data.model

data class DosenSupervisedStudentItem(
    val request: SupervisorRequest,
    val student: Profile?,
    val supervisorRole: String = "Pembimbing 1"
)

data class DosenReviewItem(
    val request: SupervisorRequest,
    val student: Profile?,
    val chapter: ThesisChapter,
    val submissions: List<ChapterSubmission>,
    val supervisorRole: String = "Pembimbing 1"
)