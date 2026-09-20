package com.codemind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ============================================================
// CODEMIND API
// ============================================================

private const val CODEMIND_API =
    "https://codemind-ai.gurkanycl01.workers.dev/api/chat"

suspend fun sendMessageToCodeMind(message: String): Result<String> {

    return withContext(Dispatchers.IO) {

        try {

            val url = URL(CODEMIND_API)

            val connection =
                url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.doOutput = true

            val requestBody = JSONObject().apply {
                put("message", message)
            }.toString()

            connection.outputStream.use { output ->
                output.write(
                    requestBody.toByteArray(Charsets.UTF_8)
                )
            }

            val responseCode = connection.responseCode

            val responseText =
                if (responseCode in 200..299) {

                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                } else {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: "Sunucu hatası: $responseCode"
                }

            connection.disconnect()

            if (responseCode !in 200..299) {

                return@withContext Result.failure(
                    Exception(responseText)
                )
            }

            val json = JSONObject(responseText)

            if (json.optBoolean("success")) {

                Result.success(
                    json.optString(
                        "reply",
                        "AI cevap vermedi."
                    )
                )

            } else {

                Result.failure(
                    Exception(
                        json.optString(
                            "error",
                            "Bilinmeyen Worker hatası."
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}


// ============================================================
// DATA
// ============================================================

data class CodeFile(
    val name: String,
    val content: String
)

data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

enum class CodeMindScreen {
    CODE,
    FILES,
    RUN,
    AI,
    ERRORS,
    SETTINGS
}


// ============================================================
// MAIN ACTIVITY
// ============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeMindApp()
        }
    }
}


// ============================================================
// APP
// ============================================================

@Composable
fun CodeMindApp() {

    var darkMode by rememberSaveable {
        mutableStateOf(true)
    }

    CodeMindTheme(
        darkMode = darkMode
    ) {

        CodeMindMain(
            darkMode = darkMode,
            onDarkModeChange = {
                darkMode = it
            }
        )
    }
}


// ============================================================
// THEME
// ============================================================

@Composable
fun CodeMindTheme(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {

    val background = if (darkMode) {
        Color(0xFF07111F)
    } else {
        Color(0xFFF5F7FA)
    }

    val surface = if (darkMode) {
        Color(0xFF0D1B2A)
    } else {
        Color.White
    }

    val text = if (darkMode) {
        Color.White
    } else {
        Color(0xFF111827)
    }

    MaterialTheme(
        colorScheme = if (darkMode) {

            androidx.compose.material3.darkColorScheme(
                background = background,
                surface = surface,
                primary = Color(0xFF4DA3FF),
                onBackground = text,
                onSurface = text
            )

        } else {

            androidx.compose.material3.lightColorScheme(
                background = background,
                surface = surface,
                primary = Color(0xFF1976D2),
                onBackground = text,
                onSurface = text
            )
        },

        content = content
    )
}


// ============================================================
// MAIN SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeMindMain(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    var screen by rememberSaveable {
        mutableStateOf(CodeMindScreen.CODE)
    }

    val files = remember {

        mutableStateListOf(

            CodeFile(
                "index.html",
                """
<!DOCTYPE html>
<html>
<head>
    <title>CodeMind</title>
    <link rel="stylesheet" href="style.css">
</head>

<body>

    <h1>CodeMind</h1>

    <button id="myButton">
        Tıkla
    </button>

    <script src="script.js"></script>

</body>
</html>
                """.trimIndent()
            ),

            CodeFile(
                "style.css",
                """
body {
    background: #101827;
    color: white;
    font-family: Arial;
    text-align: center;
    padding-top: 80px;
}

button {
    padding: 14px 24px;
    border: none;
    border-radius: 10px;
    cursor: pointer;
}
                """.trimIndent()
            ),

            CodeFile(
                "script.js",
                """
const button = document.getElementById("myButton");

button.addEventListener("click", () => {
    document.body.style.background =
        document.body.style.background === "red"
            ? "#101827"
            : "red";
});
                """.trimIndent()
            )
        )
    }

    var selectedFile by rememberSaveable {
        mutableStateOf("index.html")
    }

    var codeText by rememberSaveable {
        mutableStateOf(files[0].content)
    }

    var showNewFileDialog by remember {
        mutableStateOf(false)
    }

    var showFeatureDialog by remember {
        mutableStateOf(false)
    }

    var copied by remember {
        mutableStateOf(false)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "CodeMind",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            )
        },

        bottomBar = {

            CodeMindBottomBar(
                currentScreen = screen,
                onScreenChange = {
                    screen = it
                }
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (screen) {

                CodeMindScreen.CODE -> {

                    CodeEditorScreen(
                        selectedFile = selectedFile,
                        code = codeText,

                        onCodeChange = {
                            codeText = it

                            val index =
                                files.indexOfFirst {
                                    file ->
                                    file.name == selectedFile
                                }

                            if (index >= 0) {

                                files[index] =
                                    files[index].copy(
                                        content = it
                                    )
                            }
                        },

                        onFileChange = {

                            val currentIndex =
                                files.indexOfFirst {
                                    it.name == selectedFile
                                }

                            if (currentIndex >= 0) {

                                files[currentIndex] =
                                    files[currentIndex].copy(
                                        content = codeText
                                    )
                            }

                            selectedFile = it

                            val newFile =
                                files.firstOrNull {
                                    file ->
                                    file.name == it
                                }

                            codeText =
                                newFile?.content ?: ""
                        },

                        onCopy = {

                            copied = true
                        },

                        onFeature = {
                            showFeatureDialog = true
                        }
                    )
                }

                CodeMindScreen.FILES -> {

                    FilesScreen(
                        files = files,

                        onFileClick = {

                            selectedFile = it.name
                            codeText = it.content
                            screen = CodeMindScreen.CODE
                        },

                        onNewFile = {
                            showNewFileDialog = true
                        }
                    )
                }

                CodeMindScreen.RUN -> {

                    RunScreen()
                }

                CodeMindScreen.AI -> {

                    AIScreen()
                }

                CodeMindScreen.ERRORS -> {

                    ErrorsScreen()
                }

                CodeMindScreen.SETTINGS -> {

                    SettingsScreen(
                        darkMode = darkMode,
                        onDarkModeChange = onDarkModeChange
                    )
                }
            }
        }
    }

    if (showNewFileDialog) {

        NewFileDialog(

            onDismiss = {
                showNewFileDialog = false
            },

            onCreate = { name ->

                if (name.isNotBlank()) {

                    files.add(
                        CodeFile(
                            name.trim(),
                            ""
                        )
                    )
                }

                showNewFileDialog = false
            }
        )
    }

    if (showFeatureDialog) {

        FeatureDialog(
            files = files.map {
                it.name
            },

            onDismiss = {
                showFeatureDialog = false
            },

            onSelect = { name ->

                selectedFile = name

                val file =
                    files.firstOrNull {
                        it.name == name
                    }

                codeText =
                    file?.content ?: ""

                showFeatureDialog = false
                screen = CodeMindScreen.CODE
            }
        )
    }
}


// ============================================================
// CODE EDITOR
// ============================================================

@Composable
fun CodeEditorScreen(
    selectedFile: String,
    code: String,
    onCodeChange: (String) -> Unit,
    onFileChange: (String) -> Unit,
    onCopy: () -> Unit,
    onFeature: () -> Unit
) {

    val clipboardManager =
        LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            listOf(
                "index.html",
                "style.css",
                "script.js"
            ).forEach { file ->

                val selected =
                    selectedFile == file

                Surface(

                    modifier = Modifier
                        .clickable {
                            onFileChange(file)
                        },

                    shape = RoundedCornerShape(8.dp),

                    color = if (selected) {
                        Color(0xFF173A5E)
                    } else {
                        Color(0xFF111D2C)
                    }
                ) {

                    Text(
                        file,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        ),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "Kod Editörü",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {}
            ) {

                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text("Çalıştır")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(

            value = code,

            onValueChange = onCodeChange,

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily =
                    androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 13.sp
            ),

            placeholder = {
                Text("Kodunuzu buraya yazın...")
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            SmallToolButton(
                "Kopyala",
                Icons.Default.ContentCopy
            ) {

                clipboardManager.setText(
                    AnnotatedString(code)
                )

                onCopy()
            }

            SmallToolButton(
                "Hata Bul",
                Icons.Default.Error
            ) {}

            SmallToolButton(
                "Format",
                Icons.Default.Build
            ) {}

            SmallToolButton(
                "Geri Al",
                Icons.Default.ArrowBack
            ) {}

            SmallToolButton(
                "İleri Al",
                Icons.Default.ArrowForward
            ) {}
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = onFeature,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(
                Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text("Özellik Ekle")
        }
    }
}


// ============================================================
// TOOL BUTTON
// ============================================================

@Composable
fun SmallToolButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick
    ) {

        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        Text(
            text,
            fontSize = 10.sp
        )
    }
}


// ============================================================
// FILES
// ============================================================

@Composable
fun FilesScreen(
    files: List<CodeFile>,
    onFileClick: (CodeFile) -> Unit,
    onNewFile: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Dosyalar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                onClick = {}
            ) {
                Text("İçe Aktar")
            }

            Button(
                onClick = onNewFile
            ) {

                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text("Yeni Dosya")
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(files) { file ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFileClick(file)
                        },

                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Code,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column {

                            Text(
                                file.name,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                "${file.content.length} karakter",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// RUN
// ============================================================

@Composable
fun RunScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Çalıştır",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value =
                "https://codemind-ai.gurkanycl01.workers.dev",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    "</>",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    "CodeMind",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text("Projen hazır.")

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = {}
                ) {

                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("Tıkla")
                }
            }
        }
    }
}


// ============================================================
// AI SCREEN
// ============================================================

@Composable
fun AIScreen() {

    val scope = rememberCoroutineScope()

    var messageText by rememberSaveable {
        mutableStateOf("")
    }

    var loading by rememberSaveable {
        mutableStateOf(false)
    }

    val messages = remember {

        mutableStateListOf(

            ChatMessage(
                "Merhaba! Ben CodeMind AI. Kod yazmana ve hataları düzeltmene yardımcı olabilirim.",
                false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text(
            "CodeMind AI",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "OpenRouter destekli kod asistanı",
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            items(messages) { message ->

                ChatBubble(message)
            }

            if (loading) {

                item {

                    Text(
                        "CodeMind düşünüyor...",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            OutlinedTextField(

                value = messageText,

                onValueChange = {
                    messageText = it
                },

                modifier = Modifier.weight(1f),

                placeholder = {
                    Text(
                        "Kodla ilgili bir şey sor..."
                    )
                },

                maxLines = 4
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            IconButton(

                enabled =
                    messageText.isNotBlank() &&
                    !loading,

                onClick = {

                    val text =
                        messageText.trim()

                    if (text.isEmpty()) return@IconButton

                    messages.add(
                        ChatMessage(
                            text = text,
                            fromUser = true
                        )
                    )

                    messageText = ""
                    loading = true

                    scope.launch {

                        val result =
                            sendMessageToCodeMind(text)

                        result.onSuccess { reply ->

                            messages.add(
                                ChatMessage(
                                    text = reply,
                                    fromUser = false
                                )
                            )
                        }

                        result.onFailure { error ->

                            messages.add(
                                ChatMessage(
                                    text =
                                        "Bağlantı hatası:\n${error.message}",
                                    fromUser = false
                                )
                            )
                        }

                        loading = false
                    }
                }
            ) {

                Icon(
                    Icons.Default.Send,
                    contentDescription = "Gönder"
                )
            }
        }
    }
}


// ============================================================
// CHAT BUBBLE
// ============================================================

@Composable
fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.fromUser) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Surface(

            shape = RoundedCornerShape(12.dp),

            color = if (message.fromUser) {
                Color(0xFF155B96)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ) {

            Text(
                message.text,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


// ============================================================
// ERRORS
// ============================================================

@Composable
fun ErrorsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Hatalar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        ErrorCard(
            icon = Icons.Default.Check,
            title = "Kod kontrolü başarılı",
            description = "index.html içerisinde hata bulunamadı."
        )

        ErrorCard(
            icon = Icons.Default.Warning,
            title = "Uyarı",
            description = "style.css optimize edilebilir."
        )

        ErrorCard(
            icon = Icons.Default.Error,
            title = "Hata",
            description = "script.js kontrol edilmeli."
        )
    }
}


// ============================================================
// ERROR CARD
// ============================================================

@Composable
fun ErrorCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                icon,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    description,
                    fontSize = 13.sp
                )
            }
        }
    }
}


// ============================================================
// SETTINGS
// ============================================================

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Ayarlar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            "Tema",
            fontWeight = FontWeight.Bold
        )

        ThemeOption(
            "Koyu",
            darkMode
        ) {
            onDarkModeChange(true)
        }

        ThemeOption(
            "Beyaz",
            !darkMode
        ) {
            onDarkModeChange(false)
        }

        ThemeOption(
            "Cihaz varsayılanı",
            false
        ) {}

        Divider(
            modifier = Modifier.padding(
                vertical = 15.dp
            )
        )

        SettingRow(
            "Dil",
            "Türkçe"
        )

        SettingRow(
            "Otomatik kaydet",
            "Açık"
        )

        SettingRow(
            "Eğlenceli AI modu",
            "Açık"
        )

        SettingRow(
            "Güncellemeler",
            "Otomatik"
        )
    }
}


// ============================================================
// THEME OPTION
// ============================================================

@Composable
fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 4.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(title)
    }
}


// ============================================================
// SETTING ROW
// ============================================================

@Composable
fun SettingRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(title)

        Text(
            value,
            fontWeight = FontWeight.Bold
        )
    }
}


// ============================================================
// NEW FILE DIALOG
// ============================================================

@Composable
fun NewFileDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {

    var fileName by remember {
        mutableStateOf("")
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Yeni dosya")
        },

        text = {

            OutlinedTextField(
                value = fileName,
                onValueChange = {
                    fileName = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("örn. app.js")
                }
            )
        },

        confirmButton = {

            Button(
                onClick = {
                    onCreate(fileName)
                }
            ) {

                Text("Oluştur")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("İptal")
            }
        }
    )
}


// ============================================================
// FEATURE DIALOG
// ============================================================

@Composable
fun FeatureDialog(
    files: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Özellik / dosya ekle")
        },

        text = {

            Column {

                files.forEach { file ->

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(file)
                            }
                            .padding(12.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Code,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(file)
                    }
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Kapat")
            }
        }
    )
}


// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
fun CodeMindBottomBar(
    currentScreen: CodeMindScreen,
    onScreenChange: (CodeMindScreen) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),

        tonalElevation = 5.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                    horizontal = 4.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            BottomItem(
                "Kod",
                Icons.Default.Code,
                currentScreen == CodeMindScreen.CODE
            ) {
                onScreenChange(CodeMindScreen.CODE)
            }

            BottomItem(
                "Dosyalar",
                Icons.Default.Folder,
                currentScreen == CodeMindScreen.FILES
            ) {
                onScreenChange(CodeMindScreen.FILES)
            }

            BottomItem(
                "Çalıştır",
                Icons.Default.PlayArrow,
                currentScreen == CodeMindScreen.RUN
            ) {
                onScreenChange(CodeMindScreen.RUN)
            }

            BottomItem(
                "AI",
                Icons.Default.Build,
                currentScreen == CodeMindScreen.AI
            ) {
                onScreenChange(CodeMindScreen.AI)
            }

            BottomItem(
                "Hatalar",
                Icons.Default.Error,
                currentScreen == CodeMindScreen.ERRORS
            ) {
                onScreenChange(CodeMindScreen.ERRORS)
            }

            BottomItem(
                "Ayarlar",
                Icons.Default.Settings,
                currentScreen == CodeMindScreen.SETTINGS
            ) {
                onScreenChange(CodeMindScreen.SETTINGS)
            }
        }
    }
}


// ============================================================
// BOTTOM ITEM
// ============================================================

@Composable
fun BottomItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(

        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 5.dp,
                vertical = 3.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp)
        )

        Text(
            title,
            fontSize = 9.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}
