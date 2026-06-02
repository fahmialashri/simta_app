package com.project.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.core.SimtaRed

private val PrimaryRed = SimtaRed
private val MutedText = Color(0xFF8B8B8B)
private val FieldBackground = Color(0xFFFAFAFA)
private val UploadBorder = Color(0xFFCFCFCF)
private val SoftGreen = Color(0xFFE6F3E9)
private val GreenText = Color(0xFF2E7D58)
private val WarningBg = Color(0xFFFFF7DF)
private val WarningBorder = Color(0xFFFFDF8A)
private val WarningText = Color(0xFF856404)

data class UploadFileItem(
    val key: String,
    val title: String,
    val description: String,
    val mimeTypes: List<String> = listOf(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
)

@Composable
fun FormTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color(0xFF202020)
                )
            }

            Text(
                text = title,
                color = Color(0xFF202020),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun UploadPageAlert(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryRed),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    required: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel(label = label, required = required)

        TextField(
            value = value,
            onValueChange = onValueChange,
            minLines = minLines,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (minLines > 1) 78.dp else 48.dp),
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(
                        text = placeholder,
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            },
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                disabledContainerColor = FieldBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryRed
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdownField(
    label: String,
    placeholder: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    required: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel(label = label, required = required)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MutedText,
                        fontSize = 12.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF777777)
                    )
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    disabledContainerColor = FieldBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryRed
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FieldLabel(
    label: String,
    required: Boolean = true
) {
    Text(
        text = buildAnnotatedString {
            append(label)
            if (required) {
                append(" ")
                withStyle(
                    SpanStyle(
                        color = PrimaryRed,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("*")
                }
            }
        },
        modifier = Modifier.padding(bottom = 5.dp),
        color = Color(0xFF282828),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier.padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryRed,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = title,
            color = Color(0xFF222222),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UploadFileCard(
    item: UploadFileItem,
    selectedFileName: String?,
    onFilePicked: (Uri, String) -> Unit,
    onRemoveFile: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermissionSafely(uri)

            onFilePicked(
                uri,
                getFileName(context, uri)
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            FieldLabel(item.title)

            Text(
                text = item.description,
                color = MutedText,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (selectedFileName == null) {
                EmptyUploadBox(
                    onClick = {
                        launcher.launch(item.mimeTypes.toTypedArray())
                    }
                )
            } else {
                UploadedFileBox(
                    fileName = selectedFileName,
                    onRemoveFile = onRemoveFile,
                    onChangeFile = {
                        launcher.launch(item.mimeTypes.toTypedArray())
                    }
                )
            }
        }
    }
}

@Composable
fun MultiSmallUploadCard(
    title: String,
    description: String,
    items: List<UploadFileItem>,
    selectedFileNames: Map<String, String>,
    onFilePicked: (String, Uri, String) -> Unit,
    onRemoveFile: (String) -> Unit
) {
    val context = LocalContext.current
    var activeKey by remember { mutableStateOf<String?>(null) }
    var activeMimeTypes by remember {
        mutableStateOf(
            listOf(
                "application/pdf",
                "image/jpeg",
                "image/png"
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        val key = activeKey

        if (uri != null && key != null) {
            context.contentResolver.takePersistableUriPermissionSafely(uri)

            onFilePicked(
                key,
                uri,
                getFileName(context, uri)
            )
        }

        activeKey = null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            FieldLabel(title)

            Text(
                text = description,
                color = MutedText,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEach { item ->
                    val selected = selectedFileNames[item.key]

                    if (selected == null) {
                        SmallAddTile(
                            label = item.title,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                activeKey = item.key
                                activeMimeTypes = item.mimeTypes
                                launcher.launch(activeMimeTypes.toTypedArray())
                            }
                        )
                    } else {
                        SmallFileTile(
                            title = item.title,
                            fileName = selected,
                            modifier = Modifier.weight(1f),
                            onRemoveClick = {
                                onRemoveFile(item.key)
                            }
                        )
                    }
                }
            }

            Text(
                text = "${items.count { selectedFileNames.containsKey(it.key) }} dari ${items.size} file terpilih",
                color = MutedText,
                fontSize = 10.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoBox(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(WarningBg)
            .border(
                width = 1.dp,
                color = WarningBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = WarningText,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                color = WarningText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                color = WarningText,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun EmptyUploadBox(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = UploadBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = null,
                tint = PrimaryRed,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "Ketuk untuk unggah",
                color = Color(0xFF333333),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "PDF, DOCX, JPG, atau PNG, maks. 10 MB",
                color = MutedText,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun UploadedFileBox(
    fileName: String,
    onRemoveFile: () -> Unit,
    onChangeFile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SoftGreen)
            .clickable(onClick = onChangeFile)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = GreenText,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fileName,
                color = Color(0xFF1E1E1E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = "File sudah dipilih, ketuk untuk ganti",
                color = GreenText,
                fontSize = 10.sp
            )
        }

        IconButton(
            onClick = onRemoveFile,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus",
                tint = Color(0xFF555555),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun SmallFileTile(
    title: String,
    fileName: String,
    modifier: Modifier = Modifier,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFEEEE))
            .border(
                width = 1.dp,
                color = Color(0xFFE2B2B2),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = PrimaryRed,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = title,
                color = PrimaryRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = fileName,
                color = MutedText,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onRemoveClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus",
                tint = PrimaryRed,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun SmallAddTile(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = UploadBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color(0xFF777777),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            color = MutedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

fun getPickedFileName(
    context: Context,
    uri: Uri
): String {
    return getFileName(context, uri)
}

private fun getFileName(
    context: Context,
    uri: Uri
): String {
    var result = "file_terpilih"

    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (nameIndex >= 0 && cursor.moveToFirst()) {
                result = cursor.getString(nameIndex).orEmpty().ifBlank {
                    "file_terpilih"
                }
            }
        }
    } else {
        result = uri.path?.substringAfterLast("/") ?: result
    }

    return result
}

private fun android.content.ContentResolver.takePersistableUriPermissionSafely(uri: Uri) {
    try {
        takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: Exception) {
        // Aman diabaikan karena tidak semua provider mendukung persistable permission.
    }
}