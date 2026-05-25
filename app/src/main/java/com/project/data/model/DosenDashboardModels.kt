package com.project.data.model

data class DosenSupervisedStudentItem(
    val request: SupervisorRequest,
    val student: Profile?
)

data class DosenReviewItem(
    val request: SupervisorRequest,
    val student: Profile?,
    val chapter: ThesisChapter,
    val submissions: List<ChapterSubmission>
)