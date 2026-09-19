package com.codemind.app

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.util.Base64
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val AI_ENDPOINT =
    "https://codemind-ai.gurkanycl01.workers.dev/"

private const val NOTIFICATION_CHANNEL_ID = "codemind_ai"
private const val SETTINGS_PREFS = "codemind_settings"

private data class CodeFile(
    val name: String,
    var content: String
)

private data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val fileName: String? = null,
    val imagePath: String? = null
)

private enum class Page {
    CODE,
    FILES,
    PREVIEW,
    AI,
    ERRORS,
    SETTINGS
}

private enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

private enum class AppLanguage {
    TR,
    EN
}

private data class Palette(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val border: Color,
    val primary: Color,
    val primarySoft: Color,
    val text: Color,
    val muted: Color,
    val error: Color,
    val success: Color
)

private val DarkPalette = Palette(
    background = Color(0xFF05080D),
    surface = Color(0xFF0A111A),
    surface2 = Color(0xFF0E1824),
    border = Color(0xFF1C3547),
    primary = Color(0xFF2BD7FF),
    primarySoft = Color(0xFF16475B),
    text = Color(0xFFEAF7FF),
    muted = Color(0xFF8EA5B5),
    error = Color(0xFFFF6678),
    success = Color(0xFF4DDF9A)
)

private val LightPalette = Palette(
    background = Color(0xFFF2F5F7),
    surface = Color.White,
    surface2 = Color(0xFFE8EDF1),
    border = Color(0xFFB8C5CC),
    primary = Color(0xFF008DB8),
    primarySoft = Color(0xFFCDECF5),
    text = Color(0xFF102027),
    muted = Color(0xFF60717A),
    error = Color(0xFFD83D51),
    success = Color(0xFF178956)
)

private val LocalPalette = staticCompositionLocalOf {
    DarkPalette
}

private val LocalLanguage = staticCompositionLocalOf {
    AppLanguage.TR
}

private val P: Palette
    @Composable
    get() = LocalPalette.current

private val Lang: AppLanguage
    @Composable
    get() = LocalLanguage.current

@Composable
private fun L(key: String): String {
    return if (Lang == AppLanguage.EN) {
        when (key) {
            "code" -> "Code"
            "files" -> "Files"
            "run" -> "Run"
            "ai" -> "AI"
            "errors" -> "Errors"
            "settings" -> "Settings"
            "copy" -> "Copy"
            "findErrors" -> "Find Errors"
            "format" -> "Format"
            "undo" -> "Undo"
            "redo" -> "Redo"
            "addFeature" -> "Add Feature"
            "newFile" -> "New File"
            "import" -> "Import"
            "preview" -> "Preview"
            "send" -> "Send"
            "update" -> "Check for updates"
            "about" -> "About"
            "theme" -> "App Theme"
            "language" -> "Language"
            "autoSave" -> "Auto save"
            "funnyMode" -> "Funny mode"
            "clickSound" -> "Click sound"
            "notificationSound" -> "Notification sound"
            "owner" -> "Owner"
            "telegram" -> "Telegram"
            "close" -> "Close"
            "cancel" -> "Cancel"
            "create" -> "Create"
            "appVersion" -> "App version"
            "developer" -> "Developer"
            "aiReady" -> "CodeMind AI is ready. Ask me to build, fix or improve your project."
            "analyze" -> "Analyze code"
            "designSuggest" -> "Modern design"
            "findErrorsAi" -> "Find errors with AI"
            "featureAiTitle" -> "AI feature assistant"
            "featureAiText" -> "Choose a feature and AI will analyze the project and prepare suggestions."
            "attachFile" -> "Attach file"
            "noUpdate" -> "You are using the latest version."
            "updateFound" -> "A new version is available."
            "aboutText" -> "CodeMind is a modern Android code editor and AI assistant."
            else -> key
        }
    } else {
        when (key) {
            "code" -> "Kod"
            "files" -> "Dosyalar"
            "run" -> "Çalıştır"
            "ai" -> "AI"
            "errors" -> "Hatalar"
            "settings" -> "Ayarlar"
            "copy" -> "Kopyala"
            "findErrors" -> "Hata Bul"
            "format" -> "Format"
            "undo" -> "Geri Al"
            "redo" -> "İleri Al"
            "addFeature" -> "Özellik Ekle"
            "newFile" -> "Yeni Dosya"
            "import" -> "İçe Aktar"
            "preview" -> "Önizleme"
            "send" -> "Gönder"
            "update" -> "Güncellemeleri kontrol et"
            "about" -> "Hakkında"
            "theme" -> "Uygulama Teması"
            "language" -> "Dil"
            "autoSave" -> "Otomatik kaydet"
            "funnyMode" -> "Eğlenceli mod"
            "clickSound" -> "Tıklama sesi"
            "notificationSound" -> "Bildirim sesi"
            "owner" -> "Owner"
            "telegram" -> "Telegram"
            "close" -> "Kapat"
            "cancel" -> "İptal"
            "create" -> "Oluştur"
            "appVersion" -> "Uygulama sürümü"
            "developer" -> "Geliştirici"
            "aiReady" -> "CodeMind AI hazır. Projeni oluştur, düzelt veya geliştir diye bana söyle."
            "analyze" -> "Kodu analiz et"
            "designSuggest" -> "Modern tasarım"
            "findErrorsAi" -> "AI ile hata bul"
            "featureAiTitle" -> "AI özellik asistanı"
            "featureAiText" -> "Bir özellik seç; AI projeyi inceleyip öneri ve uygulanacak değişiklikleri hazırlasın."
            "attachFile" -> "Dosya ekle"
            "noUpdate" -> "Uygulamanın en güncel sürümünü kullanıyorsun."
            "updateFound" -> "Yeni bir sürüm mevcut."
            "aboutText" -> "CodeMind, modern Android kod editörü ve AI asistanıdır."
            else -> key
        }
    }
}

class MainActivity : ComponentActivity() {

    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri ?: return@registerForActivityResult

            try {
                val name = uri.lastPathSegment
                    ?.substringAfterLast("/")
                    ?.ifBlank { "imported.txt" }
                    ?: "imported.txt"

                val content = contentResolver
                    .openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?: ""

                ImportedFileHolder.fileName = name
                ImportedFileHolder.content = content

            } catch (_: Exception) {
                Toast.makeText(
                    this,
                    "Dosya okunamadı.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {
            CodeMindApp(
                onPickFile = {
                    filePicker.launch("*/*")
                }
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "CodeMind AI",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager.createNotificationChannel(channel)
        }
    }
}

private object ImportedFileHolder {
    var fileName: String? = null
    var content: String? = null
}

@Composable
private fun CodeMindApp(
    onPickFile: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(
            SETTINGS_PREFS,
            Context.MODE_PRIVATE
        )
    }

    var themeMode by rememberSaveable {
        mutableStateOf(
            when (
                prefs.getString(
                    "theme",
                    "DARK"
                )
            ) {
                "LIGHT" -> ThemeMode.LIGHT
                "SYSTEM" -> ThemeMode.SYSTEM
                else -> ThemeMode.DARK
            }
        )
    }

    var language by rememberSaveable {
        mutableStateOf(
            if (
                prefs.getString(
                    "language",
                    "TR"
                ) == "EN"
            ) {
                AppLanguage.EN
            } else {
                AppLanguage.TR
            }
        )
    }

    var autoSave by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean(
                "auto_save",
                true
            )
        )
    }

    var funnyMode by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean(
                "funny_mode",
                false
            )
        )
    }

    var clickSound by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean(
                "click_sound",
                true
            )
        )
    }

    var notificationSound by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean(
                "notification_sound",
                true
            )
        )
    }

    val systemDark =
        androidx.compose.foundation.isSystemInDarkTheme()

    val palette =
        when (themeMode) {
            ThemeMode.DARK -> DarkPalette
            ThemeMode.LIGHT -> LightPalette
            ThemeMode.SYSTEM ->
                if (systemDark) {
                    DarkPalette
                } else {
                    LightPalette
                }
        }

    CompositionLocalProvider(
        LocalPalette provides palette,
        LocalLanguage provides language
    ) {
        MaterialTheme {
            val files = remember {
                mutableStateListOf(
                    CodeFile(
                        "index.html",
                        defaultContentFor("index.html")
                    ),
                    CodeFile(
                        "style.css",
                        defaultContentFor("style.css")
                    ),
                    CodeFile(
                        "script.js",
                        defaultContentFor("script.js")
                    )
                )
            }

            var selectedFileIndex by rememberSaveable {
                mutableStateOf(0)
            }

            var page by rememberSaveable {
                mutableStateOf(Page.CODE)
            }

            var showNewFile by rememberSaveable {
                mutableStateOf(false)
            }

            var showFeatureDialog by rememberSaveable {
                mutableStateOf(false)
            }

            var showSettings by rememberSaveable {
                mutableStateOf(false)
            }

            var showAbout by rememberSaveable {
                mutableStateOf(false)
            }

            var showUpdateDialog by rememberSaveable {
                mutableStateOf(false)
            }

            var updateInfo by remember {
                mutableStateOf<AppUpdateInfo?>(null)
            }

            var updateChecking by rememberSaveable {
                mutableStateOf(false)
            }

            var aiInitialPrompt by remember {
                mutableStateOf<String?>(null)
            }

            val scope = rememberCoroutineScope()

            val saveSettings: () -> Unit = {
                prefs.edit()
                    .putString(
                        "theme",
                        themeMode.name
                    )
                    .putString(
                        "language",
                        language.name
                    )
                    .putBoolean(
                        "auto_save",
                        autoSave
                    )
                    .putBoolean(
                        "funny_mode",
                        funnyMode
                    )
                    .putBoolean(
                        "click_sound",
                        clickSound
                    )
                    .putBoolean(
                        "notification_sound",
                        notificationSound
                    )
                    .apply()
            }

            fun checkUpdates() {
                if (updateChecking) return

                updateChecking = true

                scope.launch {
                    updateInfo =
                        UpdateManager.checkForUpdate(context)

                    updateChecking = false
                    showUpdateDialog = true
                }
            }

            fun applyAiChanges(changesJson: String): Int {
                if (changesJson.isBlank()) return 0

                return try {
                    val raw = changesJson.trim()
                    val array = if (raw.startsWith("{")) {
                        JSONObject(raw).optJSONArray("changes")
                    } else {
                        JSONArray(raw)
                    } ?: return 0

                    var changedCount = 0

                    for (i in 0 until array.length()) {
                        val item = array.optJSONObject(i) ?: continue
                        val fileName = item.optString(
                            "fileName",
                            item.optString("name", "")
                        ).trim()

                        if (fileName.isBlank()) continue

                        val action = item.optString(
                            "action",
                            "update"
                        ).lowercase()

                        val index = files.indexOfFirst {
                            it.name.equals(fileName, ignoreCase = true)
                        }

                        when (action) {
                            "delete", "remove" -> {
                                if (index >= 0 && files.size > 1) {
                                    files.removeAt(index)
                                    changedCount++
                                }
                            }

                            "create", "add", "update", "replace" -> {
                                val content = item.optString(
                                    "content",
                                    item.optString("code", "")
                                )

                                if (index >= 0) {
                                    files[index].content = content
                                } else {
                                    files.add(
                                        CodeFile(
                                            fileName,
                                            content
                                        )
                                    )
                                }

                                changedCount++
                            }
                        }
                    }

                    if (files.isNotEmpty() &&
                        selectedFileIndex >= files.size
                    ) {
                        selectedFileIndex = files.lastIndex
                    }

                    changedCount
                } catch (_: Exception) {
                    0
                }
            }

            Scaffold(
                containerColor = P.background,
                bottomBar = {
                    ModernBottomNavigation(
                        page = page,
                        onPage = {
                            page = it
                        }
                    )
                }
            ) { padding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(P.background)
                ) {
                    when (page) {
                        Page.CODE -> {
                            CodeScreen(
                                files = files,
                                selectedFileIndex = selectedFileIndex,
                                onSelectedFile = {
                                    selectedFileIndex = it
                                },
                                onRun = {
                                    page = Page.PREVIEW
                                },
                                onAddFeature = {
                                    showFeatureDialog = true
                                },
                                onErrors = {
                                    aiInitialPrompt =
                                        if (
                                            language ==
                                            AppLanguage.EN
                                        ) {
                                            "Analyze this project and find HTML, CSS and JavaScript errors. Explain each error and prepare concrete fixes. If possible, provide corrected file contents."
                                        } else {
                                            "Bu projeyi analiz et; HTML, CSS ve JavaScript hatalarını bul. Her hatayı açıkla ve somut çözüm hazırla. Mümkünse düzeltilmiş dosya içeriklerini hazırla."
                                        }

                                    page = Page.AI
                                },
                                clickSound = clickSound
                            )
                        }

                        Page.FILES -> {
                            FilesScreen(
                                files = files,
                                selectedFileIndex = selectedFileIndex,
                                onSelect = {
                                    selectedFileIndex = it
                                },
                                onNewFile = {
                                    showNewFile = true
                                },
                                onImport = onPickFile,
                                onDelete = { index ->
                                    if (files.size > 1) {
                                        files.removeAt(index)

                                        if (
                                            selectedFileIndex >=
                                            files.size
                                        ) {
                                            selectedFileIndex =
                                                files.lastIndex
                                        }
                                    }
                                }
                            )
                        }

                        Page.PREVIEW -> {
                            PreviewScreen(
                                files = files
                            )
                        }

                        Page.AI -> {
                            AiScreen(
                                files = files,
                                initialPrompt = aiInitialPrompt,
                                onInitialPromptConsumed = {
                                    aiInitialPrompt = null
                                },
                                clickSound = clickSound,
                                onAttachFile = onPickFile,
                                onApplyChanges = ::applyAiChanges
                            )
                        }

                        Page.ERRORS -> {
                            ErrorsScreen(
                                files = files,
                                onAskAi = {
                                    aiInitialPrompt =
                                        if (
                                            language ==
                                            AppLanguage.EN
                                        ) {
                                            "Analyze this project and find all HTML, CSS and JavaScript errors. Explain each one and prepare concrete fixes."
                                        } else {
                                            "Bu projedeki HTML, CSS ve JavaScript hatalarını analiz et. Her hatayı açıkla ve somut çözümler hazırla."
                                        }

                                    page = Page.AI
                                }
                            )
                        }

                        Page.SETTINGS -> {
                            SettingsScreen(
                                themeMode = themeMode,
                                onThemeChange = {
                                    themeMode = it
                                    saveSettings()
                                },
                                language = language,
                                onLanguageChange = {
                                    language = it
                                    saveSettings()
                                },
                                autoSave = autoSave,
                                onAutoSaveChange = {
                                    autoSave = it
                                    saveSettings()
                                },
                                funnyMode = funnyMode,
                                onFunnyModeChange = {
                                    funnyMode = it
                                    saveSettings()
                                },
                                clickSound = clickSound,
                                onClickSoundChange = {
                                    clickSound = it
                                    saveSettings()
                                },
                                notificationSound = notificationSound,
                                onNotificationSoundChange = {
                                    notificationSound = it
                                    saveSettings()
                                },
                                onCheckUpdate = {
                                    checkUpdates()
                                },
                                onAbout = {
                                    showAbout = true
                                }
                            )
                        }
                    }
                }
            }

            if (showNewFile) {
                NewFileDialog(
                    onDismiss = {
                        showNewFile = false
                    },
                    onCreate = { name ->
                        val cleanName =
                            name.trim()

                        if (
                            cleanName.isNotEmpty() &&
                            files.none {
                                it.name.equals(
                                    cleanName,
                                    ignoreCase = true
                                )
                            }
                        ) {
                            files.add(
                                CodeFile(
                                    cleanName,
                                    defaultContentFor(
                                        cleanName
                                    )
                                )
                            )

                            selectedFileIndex =
                                files.lastIndex

                            showNewFile = false
                        }
                    }
                )
            }

            if (showFeatureDialog) {
                FeatureDialog(
                    onDismiss = {
                        showFeatureDialog = false
                    },
                    onAskAi = { prompt ->
                        aiInitialPrompt = prompt
                        showFeatureDialog = false
                        page = Page.AI
                    }
                )
            }

            if (showAbout) {
                AboutDialog(
                    onDismiss = {
                        showAbout = false
                    }
                )
            }

            if (showUpdateDialog) {
                UpdateDialog(
                    updateInfo = updateInfo,
                    checking = updateChecking,
                    onDismiss = {
                        showUpdateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ModernBottomNavigation(
    page: Page,
    onPage: (Page) -> Unit
) {
    val items = listOf(
        Triple(
            Page.CODE,
            Icons.Default.Code,
            L("code")
        ),
        Triple(
            Page.FILES,
            Icons.Default.Folder,
            L("files")
        ),
        Triple(
            Page.PREVIEW,
            Icons.Default.PlayArrow,
            L("run")
        ),
        Triple(
            Page.AI,
            Icons.Default.Android,
            L("ai")
        ),
        Triple(
            Page.SETTINGS,
            Icons.Default.Settings,
            L("settings")
        )
    )

    Surface(
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 8.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(
                        RoundedCornerShape(24.dp)
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                P.surface2,
                                P.surface
                            )
                        )
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            P.border
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 6.dp
                    ),
                horizontalArrangement =
                    Arrangement.SpaceEvenly,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                items.forEach { item ->

                    val selected =
                        page == item.first

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                if (selected) {
                                    P.primarySoft
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {
                                onPage(item.first)
                            }
                            .padding(
                                vertical = 6.dp
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(
                                    if (selected) {
                                        38.dp
                                    } else {
                                        30.dp
                                    }
                                )
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        P.primary
                                    } else {
                                        Color.Transparent
                                    }
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                item.second,
                                contentDescription =
                                    item.third,
                                tint =
                                    if (selected) {
                                        Color(0xFF031018)
                                    } else {
                                        P.muted
                                    }
                            )
                        }

                        Text(
                            text = item.third,
                            color =
                                if (selected) {
                                    P.text
                                } else {
                                    P.muted
                                },
                            fontSize = 10.sp,
                            fontWeight =
                                if (selected) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeScreen(
    files: List<CodeFile>,
    selectedFileIndex: Int,
    onSelectedFile: (Int) -> Unit,
    onRun: () -> Unit,
    onAddFeature: () -> Unit,
    onErrors: () -> Unit,
    clickSound: Boolean
) {
    val context = LocalContext.current

    var editorText by remember(
        selectedFileIndex,
        files
    ) {
        mutableStateOf(
            TextFieldValue(
                files.getOrNull(
                    selectedFileIndex
                )?.content ?: ""
            )
        )
    }

    var undoStack by remember {
        mutableStateOf(
            listOf<String>()
        )
    }

    var redoStack by remember {
        mutableStateOf(
            listOf<String>()
        )
    }

    fun setContent(
        newText: String
    ) {
        val current =
            files[selectedFileIndex].content

        if (current != newText) {
            undoStack =
                undoStack + current

            redoStack = emptyList()

            files[selectedFileIndex].content =
                newText

            editorText =
                TextFieldValue(newText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background)
    ) {
        TopBar(
            title = "CodeMind",
            leading = {
                Icon(
                    Icons.Default.Code,
                    contentDescription = null,
                    tint = P.primary
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            files.forEachIndexed { index, file ->
                FilterChip(
                    selected =
                        selectedFileIndex == index,
                    onClick = {
                        onSelectedFile(index)
                    },
                    label = {
                        Text(file.name)
                    }
                )
            }

            IconButton(
                onClick = onAddFeature
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = L("addFeature"),
                    tint = P.primary
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            ToolButton(
                icon = Icons.Default.ContentCopy,
                text = L("copy"),
                onClick = {
                    copyToClipboard(
                        context,
                        editorText.text
                    )
                }
            )

            ToolButton(
                icon = Icons.Default.BugReport,
                text = L("findErrors"),
                onClick = onErrors
            )

            ToolButton(
                icon = Icons.Default.FormatAlignLeft,
                text = L("format"),
                onClick = {
                    setContent(
                        formatCode(
                            editorText.text
                        )
                    )
                }
            )

            ToolButton(
                icon = Icons.Default.Undo,
                text = L("undo"),
                onClick = {
                    if (undoStack.isNotEmpty()) {
                        val previous =
                            undoStack.last()

                        undoStack =
                            undoStack.dropLast(1)

                        redoStack =
                            redoStack +
                                    editorText.text

                        files[selectedFileIndex]
                            .content = previous

                        editorText =
                            TextFieldValue(
                                previous
                            )
                    }
                }
            )

            ToolButton(
                icon = Icons.Default.Redo,
                text = L("redo"),
                onClick = {
                    if (redoStack.isNotEmpty()) {
                        val next =
                            redoStack.last()

                        redoStack =
                            redoStack.dropLast(1)

                        undoStack =
                            undoStack +
                                    editorText.text

                        files[selectedFileIndex]
                            .content = next

                        editorText =
                            TextFieldValue(next)
                    }
                }
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                )
                .clip(
                    RoundedCornerShape(18.dp)
                )
                .background(P.surface)
                .border(
                    BorderStroke(
                        1.dp,
                        P.border
                    ),
                    RoundedCornerShape(18.dp)
                )
        ) {
            CodeEditor(
                value = editorText,
                onValueChange = {
                    editorText = it
                    files[selectedFileIndex]
                        .content = it.text
                }
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Button(
            onClick = onRun,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                )
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = P.primary,
                contentColor = Color(0xFF031018)
            )
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                L("run"),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )
    }
}

@Composable
private fun CodeEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val lines =
            value.text.count {
                it == '\n'
            } + 1

        Column(
            horizontalAlignment =
                Alignment.End
        ) {
            repeat(lines) { index ->
                Text(
                    text = "${index + 1}",
                    color = P.muted,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .width(32.dp)
                        .padding(
                            vertical = 2.dp
                        )
                )
            }
        }

        Spacer(
            Modifier.width(8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize(),
            textStyle = TextStyle(
                color = P.text,
                fontSize = 13.sp
            ),
            cursorBrush =
                SolidColor(P.primary),
            decorationBox = { inner ->
                Box {
                    if (value.text.isEmpty()) {
                        Text(
                            "Kodunu buraya yaz...",
                            color = P.muted
                        )
                    }

                    inner()
                }
            }
        )
    }
}

@Composable
private fun FilesScreen(
    files: List<CodeFile>,
    selectedFileIndex: Int,
    onSelect: (Int) -> Unit,
    onNewFile: () -> Unit,
    onImport: () -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background)
    ) {
        TopBar(
            title = L("files"),
            leading = {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = P.primary
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onImport,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.UploadFile,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(6.dp)
                )

                Text(L("import"))
            }

            Button(
                onClick = onNewFile,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = P.primary,
                    contentColor = Color(0xFF031018)
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(6.dp)
                )

                Text(L("newFile"))
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 10.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(files) { index, file ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(index)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (
                                selectedFileIndex ==
                                index
                            ) {
                                P.primarySoft
                            } else {
                                P.surface
                            }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (
                            selectedFileIndex ==
                            index
                        ) {
                            P.primary
                        } else {
                            P.border
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = P.primary
                        )

                        Spacer(
                            Modifier.width(12.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {
                            Text(
                                file.name,
                                color = P.text,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                "${file.content.length} bytes",
                                color = P.muted,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                onDelete(index)
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = P.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewScreen(
    files: List<CodeFile>
) {
    val context = LocalContext.current
    val server = remember {
        LocalPreviewServer(files)
    }

    androidx.compose.runtime.DisposableEffect(server) {
        server.start()

        val url = "http://127.0.0.1:${server.port}/index.html"
        val chromeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.android.chrome")
        }

        try {
            if (context.packageManager.resolveActivity(chromeIntent, 0) != null) {
                context.startActivity(chromeIntent)
            } else {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            }
        } catch (_: Exception) {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            } catch (_: Exception) {
            }
        }

        onDispose {
            server.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = P.primary,
            modifier = Modifier.size(52.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Chrome'da açılıyor...",
            color = P.text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "HTML, CSS ve JavaScript önizlemesi Chrome üzerinden açılacak.",
            color = P.muted,
            fontSize = 13.sp
        )
    }
}

private class LocalPreviewServer(
    private val files: List<CodeFile>
) {
    private var serverSocket: ServerSocket? = null
    private var thread: Thread? = null

    val port: Int
        get() = serverSocket?.localPort ?: 0

    fun start() {
        if (serverSocket != null) return

        serverSocket = ServerSocket(
            0,
            50,
            InetAddress.getByName("127.0.0.1")
        )

        thread = Thread {
            while (serverSocket != null) {
                try {
                    val socket = serverSocket?.accept() ?: break
                    handle(socket)
                } catch (_: Exception) {
                    if (serverSocket != null) {
                        continue
                    }
                    break
                }
            }
        }.apply {
            name = "CodeMindPreviewServer"
            isDaemon = true
            start()
        }
    }

    fun stop() {
        try {
            serverSocket?.close()
        } catch (_: Exception) {
        }

        serverSocket = null
        thread = null
    }

    private fun handle(socket: Socket) {
        socket.use { client ->
            try {
                val reader = BufferedReader(
                    InputStreamReader(
                        client.getInputStream(),
                        StandardCharsets.US_ASCII
                    )
                )

                val requestLine = reader.readLine() ?: return

                while (true) {
                    val header = reader.readLine() ?: break
                    if (header.isEmpty()) break
                }

                val parts = requestLine.split(" ")
                val method = parts.getOrNull(0) ?: return
                if (method != "GET" && method != "HEAD") {
                    sendResponse(
                        client.getOutputStream(),
                        405,
                        "text/plain; charset=utf-8",
                        "Method Not Allowed".toByteArray(StandardCharsets.UTF_8),
                        method == "HEAD"
                    )
                    return
                }

                val rawPath = parts.getOrNull(1) ?: "/index.html"
                val path = URLDecoder.decode(
                    rawPath.substringBefore("?"),
                    "UTF-8"
                ).removePrefix("/").ifBlank { "index.html" }

                val file = files.firstOrNull { it.name == path }

                if (file == null) {
                    sendResponse(
                        client.getOutputStream(),
                        404,
                        "text/plain; charset=utf-8",
                        "404 Not Found".toByteArray(StandardCharsets.UTF_8),
                        method == "HEAD"
                    )
                    return
                }

                val body = file.content.toByteArray(StandardCharsets.UTF_8)
                sendResponse(
                    client.getOutputStream(),
                    200,
                    "${getMimeType(file.name)}; charset=utf-8",
                    body,
                    method == "HEAD"
                )
            } catch (_: Exception) {
            }
        }
    }

    private fun sendResponse(
        output: OutputStream,
        status: Int,
        contentType: String,
        body: ByteArray,
        headOnly: Boolean
    ) {
        val statusText = when (status) {
            200 -> "OK"
            404 -> "Not Found"
            405 -> "Method Not Allowed"
            else -> "Error"
        }

        val header = buildString {
            append("HTTP/1.1 ").append(status).append(' ').append(statusText).append("\r\n")
            append("Content-Type: ").append(contentType).append("\r\n")
            append("Content-Length: ").append(body.size).append("\r\n")
            append("Cache-Control: no-store, no-cache, must-revalidate\r\n")
            append("Connection: close\r\n")
            append("\r\n")
        }.toByteArray(StandardCharsets.US_ASCII)

        output.write(header)
        if (!headOnly) {
            output.write(body)
        }
        output.flush()
    }
}

private fun getMimeType(
    fileName: String
): String {
    return when (
        fileName.substringAfterLast(
            ".",
            ""
        ).lowercase()
    ) {
        "html", "htm" -> "text/html"
        "css" -> "text/css"
        "js" -> "application/javascript"
        "json" -> "application/json"
        "txt" -> "text/plain"
        "svg" -> "image/svg+xml"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        else -> "text/plain"
    }
}

@Composable
private fun AiScreen(
    files: List<CodeFile>,
    initialPrompt: String?,
    onInitialPromptConsumed: () -> Unit,
    clickSound: Boolean,
    onAttachFile: () -> Unit,
    onApplyChanges: (String) -> Int
) {
    val context = LocalContext.current
    val workManager =
        remember {
            WorkManager.getInstance(context)
        }

    val scope =
        rememberCoroutineScope()

    val messages =
        remember {
            mutableStateListOf<ChatMessage>()
        }

    var input by remember {
        mutableStateOf("")
    }

    var requestId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var attachedFile by remember {
        mutableStateOf<ChatMessage?>(null)
    }

    val listState =
        rememberLazyListState()

    val requestUuid =
        requestId?.let { id ->
            try {
                UUID.fromString(id)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

    val requestWorkInfo =
        requestUuid?.let { uuid ->
            workManager
                .getWorkInfoByIdFlow(uuid)
                .collectAsStateWithLifecycle(
                    initialValue = null
                )
                .value
        }

    val aiLanguage =
        if (Lang == AppLanguage.EN) {
            "EN"
        } else {
            "TR"
        }

    val aiRequestFailedText =
        if (Lang == AppLanguage.EN) {
            "AI request failed."
        } else {
            "AI isteği başarısız oldu."
        }

    val aiReadyText =
        if (Lang == AppLanguage.EN) {
            "CodeMind AI is ready. Ask me to build, fix or improve your project."
        } else {
            "CodeMind AI hazır. Projeni oluştur, düzelt veya geliştir diye bana söyle."
        }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    aiReadyText,
                    false
                )
            )
        }
    }

    LaunchedEffect(
        initialPrompt
    ) {
        val prompt =
            initialPrompt?.trim()

        if (
            !prompt.isNullOrBlank() &&
            requestId == null
        ) {
            onInitialPromptConsumed()

            val id =
                UUID.randomUUID()
                    .toString()

            messages.add(
                ChatMessage(
                    prompt,
                    true
                )
            )

            requestId = id

            val filesJson =
                buildFilesJson(files)

            scope.launch {
                try {
                    saveAiRequest(
                        context = context,
                        requestId = id,
                        prompt = prompt,
                        filesJson = filesJson,
                        language = aiLanguage
                    )

                    val request =
                        OneTimeWorkRequestBuilder<
                            CodeGenerationWorker
                            >()
                            .setInputData(
                                Data.Builder()
                                    .putString(
                                        "request_id",
                                        id
                                    )
                                    .build()
                            )
                            .build()

                    workManager.enqueue(
                        request
                    )
                } catch (e: Exception) {
                    messages.add(
                        ChatMessage(
                            e.message
                                ?: aiRequestFailedText,
                            false
                        )
                    )

                    requestId = null
                }
            }
        }
    }

    LaunchedEffect(
        messages.size,
        requestWorkInfo?.state
    ) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    LaunchedEffect(
        requestWorkInfo?.state
    ) {
        val workInfo =
            requestWorkInfo
                ?: return@LaunchedEffect

        when (workInfo.state) {

            androidx.work.WorkInfo.State.SUCCEEDED -> {
                val output =
                    workInfo.outputData

                val response =
                    output.getString(
                        "response"
                    )

                val changes =
                    output.getString(
                        "changes"
                    )

                val imagePath =
                    output.getString(
                        "image_path"
                    )

                if (
                    !response.isNullOrBlank() ||
                    !imagePath.isNullOrBlank()
                ) {
                    messages.add(
                        ChatMessage(
                            text = response.orEmpty(),
                            isUser = false,
                            imagePath = imagePath
                        )
                    )
                }

                if (!changes.isNullOrBlank()) {
                    val changedCount =
                        onApplyChanges(changes)

                    if (changedCount > 0) {
                        messages.add(
                            ChatMessage(
                                if (Lang == AppLanguage.EN) {
                                    "⚡ $changedCount file(s) updated by CodeMind. You can review the changes in the Code screen."
                                } else {
                                    "⚡ $changedCount dosya CodeMind tarafından güncellendi. Değişiklikleri Kod ekranından görebilirsin."
                                },
                                false
                            )
                        )
                    } else {
                        messages.add(
                            ChatMessage(
                                if (Lang == AppLanguage.EN) {
                                    "AI suggested changes, but they could not be applied."
                                } else {
                                    "AI değişiklik önerdi ancak dosya değişiklikleri uygulanamadı."
                                },
                                false
                            )
                        )
                    }
                }

                /*
                 * ÖNEMLİ:
                 * Eski sürümde burada requestId temizlenmediği
                 * için ikinci mesaj gönderilemiyordu.
                 *
                 * Her iş tamamlandığında requestId null yapılır.
                 */
                requestId = null
            }

            androidx.work.WorkInfo.State.FAILED -> {
                val output =
                    workInfo.outputData

                val response =
                    output.getString(
                        "response"
                    )

                messages.add(
                    ChatMessage(
                        response
                            ?: aiRequestFailedText,
                        false
                    )
                )

                requestId = null
            }

            androidx.work.WorkInfo.State.CANCELLED -> {
                messages.add(
                    ChatMessage(
                        "AI isteği iptal edildi.",
                        false
                    )
                )

                requestId = null
            }

            else -> Unit
        }
    }

    fun sendPrompt(
        rawPrompt: String
    ) {
        val prompt =
            rawPrompt.trim()

        if (
            prompt.isBlank() ||
            requestId != null
        ) {
            return
        }

        messages.add(
            ChatMessage(
                prompt,
                true
            )
        )

        input = ""

        val id =
            UUID.randomUUID()
                .toString()

        requestId = id

        val filesJson =
            buildFilesJson(files)

        val fileMessage =
            attachedFile

        if (
            fileMessage != null
        ) {
            messages.add(
                ChatMessage(
                    fileMessage.text,
                    true,
                    fileMessage.fileName
                )
            )

            attachedFile = null
        }

        scope.launch {
            try {
                saveAiRequest(
                    context = context,
                    requestId = id,
                    prompt =
                        if (
                            fileMessage != null
                        ) {
                            "$prompt\n\nEkli dosya: ${fileMessage.fileName}\n${fileMessage.text}"
                        } else {
                            prompt
                        },
                    filesJson = filesJson,
                    language = aiLanguage
                )

                val request =
                    OneTimeWorkRequestBuilder<
                        CodeGenerationWorker
                        >()
                        .setInputData(
                            Data.Builder()
                                .putString(
                                    "request_id",
                                    id
                                )
                                .build()
                        )
                        .build()

                workManager.enqueue(
                    request
                )
            } catch (e: Exception) {
                messages.add(
                    ChatMessage(
                        e.message
                            ?: aiRequestFailedText,
                        false
                    )
                )

                requestId = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background)
    ) {
        AiHeader(
            funnyMode =
                context.getSharedPreferences(
                    SETTINGS_PREFS,
                    Context.MODE_PRIVATE
                ).getBoolean(
                    "funny_mode",
                    false
                )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                ),
            state = listState,
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                messages
            ) { _, message ->
                AiMessageBubble(
                    message = message
                )
            }

            if (
                requestWorkInfo?.state ==
                androidx.work.WorkInfo.State.RUNNING
            ) {
                item {
                    AiTypingBubble()
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = {
                    sendPrompt(
                        "Kodumu analiz et ve nelerin geliştirilebileceğini açıkla."
                    )
                },
                label = {
                    Text(L("analyze"))
                }
            )

            AssistChip(
                onClick = {
                    sendPrompt(
                        "Bu proje için modern neon gri, responsive bir tasarım öner."
                    )
                },
                label = {
                    Text(L("designSuggest"))
                }
            )

            AssistChip(
                onClick = {
                    sendPrompt(
                        "Bu projedeki HTML, CSS ve JavaScript hatalarını bul."
                    )
                },
                label = {
                    Text(L("findErrorsAi"))
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    10.dp
                ),
            verticalAlignment =
                Alignment.Bottom
        ) {
            IconButton(
                onClick = {
                    onAttachFile()
                }
            ) {
                Icon(
                    Icons.Default.UploadFile,
                    contentDescription =
                        L("attachFile"),
                    tint = P.primary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(18.dp)
                    )
                    .background(P.surface)
                    .border(
                        BorderStroke(
                            1.dp,
                            P.border
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
            ) {
                BasicTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    textStyle = TextStyle(
                        color = P.text,
                        fontSize = 14.sp
                    ),
                    cursorBrush =
                        SolidColor(
                            P.primary
                        ),
                    decorationBox = { inner ->
                        Box {
                            if (
                                input.isEmpty()
                            ) {
                                Text(
                                    if (
                                        Lang ==
                                        AppLanguage.EN
                                    ) {
                                        "Ask AI what you want to build..."
                                    } else {
                                        "AI'ya ne yapmak istediğini yaz..."
                                    },
                                    color = P.muted
                                )
                            }

                            inner()
                        }
                    }
                )
            }

            Spacer(
                Modifier.width(6.dp)
            )

            IconButton(
                onClick = {
                    sendPrompt(input)
                },
                enabled =
                    input.isNotBlank() &&
                            requestId == null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (
                            input.isNotBlank() &&
                            requestId == null
                        ) {
                            P.primary
                        } else {
                            P.surface2
                        }
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription =
                        L("send"),
                    tint =
                        if (
                            input.isNotBlank() &&
                            requestId == null
                        ) {
                            Color(0xFF031018)
                        } else {
                            P.muted
                        }
                )
            }
        }
    }
}

@Composable
private fun AiHeader(
    funnyMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(
                8.dp,
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = P.surface
        ),
        border = BorderStroke(
            1.dp,
            P.border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(P.primarySoft),
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    tint = P.primary
                )
            }

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    "CodeMind AI",
                    color = P.text,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 17.sp
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                P.success
                            )
                    )

                    Spacer(
                        Modifier.width(5.dp)
                    )

                    Text(
                        "AI online",
                        color = P.success,
                        fontSize = 11.sp
                    )
                }
            }

            if (funnyMode) {
                Text(
                    "😎",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AiMessageBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.isUser) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {
        Card(
            modifier = Modifier.widthIn(
                max = 330.dp
            ),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart =
                    if (message.isUser) {
                        18.dp
                    } else {
                        4.dp
                    },
                bottomEnd =
                    if (message.isUser) {
                        4.dp
                    } else {
                        18.dp
                    }
            ),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (message.isUser) {
                        P.primarySoft
                    } else {
                        P.surface
                    }
            ),
            border = BorderStroke(
                1.dp,
                if (message.isUser) {
                    P.primary
                } else {
                    P.border
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    12.dp
                )
            ) {
                if (!message.imagePath.isNullOrBlank()) {
                    val bitmap = remember(message.imagePath) {
                        runCatching {
                            BitmapFactory.decodeFile(message.imagePath)
                        }.getOrNull()
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "CodeMind AI image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        if (message.text.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                if (
                    !message.fileName
                        .isNullOrBlank()
                ) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = P.primary
                        )

                        Spacer(
                            Modifier.width(6.dp)
                        )

                        Text(
                            message.fileName,
                            color = P.primary,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        Modifier.height(6.dp)
                    )
                }

                Text(
                    message.text,
                    color = P.text,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AiTypingBubble() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = P.surface
        )
    ) {
        Text(
            "AI yazıyor...",
            color = P.muted,
            modifier = Modifier.padding(
                12.dp
            )
        )
    }
}

private suspend fun saveAiRequest(
    context: Context,
    requestId: String,
    prompt: String,
    filesJson: String,
    language: String
) {
    val prefs =
        context.getSharedPreferences(
            "codemind_ai_requests",
            Context.MODE_PRIVATE
        )

    prefs.edit()
        .putString(
            "$requestId.prompt",
            prompt
        )
        .putString(
            "$requestId.files",
            filesJson
        )
        .putString(
            "$requestId.language",
            language
        )
        .apply()
}

class CodeGenerationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        val requestId =
            inputData.getString(
                "request_id"
            )
                ?: return Result.failure(
                    Data.Builder()
                        .putString(
                            "response",
                            "Request ID bulunamadı."
                        )
                        .build()
                )

        val prefs =
            applicationContext
                .getSharedPreferences(
                    "codemind_ai_requests",
                    Context.MODE_PRIVATE
                )

        val prompt =
            prefs.getString(
                "$requestId.prompt",
                ""
            )
                ?: ""

        val files =
            prefs.getString(
                "$requestId.files",
                "[]"
            )
                ?: "[]"

        val language =
            prefs.getString(
                "$requestId.language",
                "TR"
            )
                ?: "TR"

        val funnyMode =
            applicationContext
                .getSharedPreferences(
                    SETTINGS_PREFS,
                    Context.MODE_PRIVATE
                )
                .getBoolean(
                    "funny_mode",
                    false
                )

        return try {
            val result =
                sendAiRequest(
                    prompt = prompt,
                    filesJson = files,
                    language = language,
                    funnyMode = funnyMode
                )

            val response =
                result.optString(
                    "response",
                    result.optString(
                        "answer",
                        result.optString(
                            "message",
                            ""
                        )
                    )
                )

            if (response.isBlank()) {
                throw Exception(
                    "API'den veri alınamadı.\n" +
                    "API geçerli bir cevap döndürmedi."
                )
            }

            var imagePath: String? = null

            if (result.optString("type") == "image") {
                val image = result.optJSONObject("image")
                val base64 = image?.optString("base64").orEmpty()

                if (base64.isNotBlank()) {
                    val bytes = Base64.decode(
                        base64,
                        Base64.DEFAULT
                    )

                    val imageFile = File(
                        applicationContext.cacheDir,
                        "codemind_${requestId}.png"
                    )

                    imageFile.outputStream().use { output ->
                        output.write(bytes)
                    }

                    imagePath = imageFile.absolutePath
                }
            }

            val changes =
                result.optJSONArray(
                    "changes"
                )?.toString()

            showAiNotification(
                applicationContext,
                response
            )

            val outputData =
                Data.Builder()
                    .putString(
                        "response",
                        response
                    )
                    .putString(
                        "changes",
                        changes
                    )

            if (!imagePath.isNullOrBlank()) {
                outputData.putString(
                    "image_path",
                    imagePath
                )
            }

            Result.success(
                outputData.build()
            )

        } catch (e: Exception) {

            val error =
                when (e) {
                    is java.net.UnknownHostException -> {
                        "⚠️ API'den veri alınamadı.\n" +
                        "Worker adresini kontrol et."
                    }

                    is java.net.ConnectException -> {
                        "⚠️ API'den veri alınamadı.\n" +
                        "Worker'a bağlanılamadı."
                    }

                    is java.net.SocketTimeoutException -> {
                        "⚠️ API'den veri alınamadı.\n" +
                        "İstek zaman aşımına uğradı."
                    }

                    is IllegalArgumentException -> {
                        "⚠️ API'den veri alınamadı.\n" +
                        "Worker adresi geçersiz."
                    }

                    else -> {
                        e.message
                            ?: "⚠️ API'den veri alınamadı."
                    }
                }

            Result.success(
                Data.Builder()
                    .putString(
                        "response",
                        error
                    )
                    .build()
            )
        } finally {
            prefs.edit()
                .remove("$requestId.prompt")
                .remove("$requestId.files")
                .remove("$requestId.language")
                .apply()
        }
    }
}

private suspend fun sendAiRequest(
    prompt: String,
    filesJson: String,
    language: String,
    funnyMode: Boolean
): JSONObject =
    withContext(Dispatchers.IO) {

        val client =
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

        try {
            val filesArray =
                try {
                    JSONArray(filesJson)
                } catch (_: Exception) {
                    JSONArray()
                }

            var activeFileName = ""
            var activeCode = ""

            if (filesArray.length() > 0) {
                val firstFile = filesArray.optJSONObject(0)
                if (firstFile != null) {
                    activeFileName = firstFile.optString(
                        "name",
                        firstFile.optString("file", "")
                    )
                    activeCode = firstFile.optString("content", "")
                }
            }

            val bodyJson = JSONObject()
                .put("message", prompt)
                .put("prompt", prompt)
                .put("code", activeCode)
                .put("fileName", activeFileName)
                .put("projectFiles", filesArray)
                .put("files", filesArray)
                .put("funnyMode", funnyMode)
                .put("language", if (language.isBlank()) "TR" else language)

            val body = bodyJson.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(AI_ENDPOINT)
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    val detail = raw
                        .replace(Regex("\\s+"), " ")
                        .take(800)
                    throw Exception(
                        "Worker HTTP ${response.code}" +
                            if (detail.isNotBlank()) ": $detail" else ""
                    )
                }

                if (raw.isBlank()) {
                    throw Exception("Worker boş cevap döndürdü.")
                }

                val json = try {
                    JSONObject(raw)
                } catch (_: Exception) {
                    throw Exception(
                        "Worker JSON döndürmedi: " + raw.take(500)
                    )
                }

                if (!json.optBoolean("success", false)) {
                    val error = json.optString(
                        "error",
                        "Worker isteği başarısız oldu."
                    )
                    val details = json.optString("details", "")
                    throw Exception(
                        if (details.isBlank()) error
                        else "$error\n$details"
                    )
                }

                if (json.optString("response").isBlank()) {
                    throw Exception("Worker başarılı görünüyor ama response alanı boş.")
                }

                json
            }
        } catch (e: Exception) {
            when (e) {
                is java.net.UnknownHostException ->
                    throw Exception("Worker adresine ulaşılamadı. İnternet ve URL'yi kontrol et.")
                is java.net.ConnectException ->
                    throw Exception("Worker'a bağlanılamadı.")
                is java.net.SocketTimeoutException ->
                    throw Exception("AI isteği zaman aşımına uğradı.")
                else -> throw Exception(e.message ?: "AI isteği başarısız oldu.")
            }
        }
    }

private fun buildFilesJson(
    files: List<CodeFile>
): String {
    val array =
        JSONArray()

    files.forEach { file ->
        array.put(
            JSONObject()
                .put(
                    "name",
                    file.name
                )
                .put(
                    "content",
                    file.content
                )
        )
    }

    return array.toString()
}

private fun showAiNotification(
    context: Context,
    text: String
) {
    val prefs =
        context.getSharedPreferences(
            SETTINGS_PREFS,
            Context.MODE_PRIVATE
        )

    if (
        !prefs.getBoolean(
            "notification_sound",
            true
        )
    ) {
        return
    }

    val manager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    val notification =
        NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(
                android.R.drawable.ic_dialog_info
            )
            .setContentTitle(
                "CodeMind AI"
            )
            .setContentText(
                text.take(120)
            )
            .setAutoCancel(true)
            .build()

    manager.notify(
        System.currentTimeMillis()
            .toInt(),
        notification
    )
}

@Composable
private fun ErrorsScreen(
    files: List<CodeFile>,
    onAskAi: () -> Unit
) {
    val errors =
        remember(files) {
            findBasicErrors(files)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(12.dp)
    ) {
        TopBar(
            title = L("errors"),
            leading = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = P.primary
                )
            }
        )

        Spacer(
            Modifier.height(8.dp)
        )

        if (errors.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = P.surface
                ),
                border = BorderStroke(
                    1.dp,
                    P.border
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = P.success,
                        modifier = Modifier
                            .size(44.dp)
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        if (
                            Lang ==
                            AppLanguage.EN
                        ) {
                            "No basic errors found."
                        } else {
                            "Temel bir hata bulunamadı."
                        },
                        color = P.text
                    )
                }
            }
        } else {
            errors.forEach { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 8.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            P.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        P.border
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            14.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = P.error
                        )

                        Spacer(
                            Modifier.width(10.dp)
                        )

                        Text(
                            error,
                            color = P.text
                        )
                    }
                }
            }
        }

        Spacer(
            Modifier.height(10.dp)
        )

        Button(
            onClick = onAskAi,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = P.primary,
                contentColor = Color(0xFF031018)
            )
        ) {
            Icon(
                Icons.Default.Android,
                contentDescription = null
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                L("findErrorsAi")
            )
        }
    }
}

private fun findBasicErrors(
    files: List<CodeFile>
): List<String> {
    val result =
        mutableListOf<String>()

    files.forEach { file ->

        when (
            file.name
                .substringAfterLast(
                    ".",
                    ""
                )
                .lowercase()
        ) {
            "html" -> {
                if (
                    !file.content
                        .contains(
                            "<html",
                            ignoreCase = true
                        )
                ) {
                    result.add(
                        "${file.name}: <html> etiketi bulunamadı."
                    )
                }

                if (
                    file.content
                        .count {
                            it == '<'
                        } !=
                    file.content
                        .count {
                            it == '>'
                        }
                ) {
                    result.add(
                        "${file.name}: HTML etiketlerinde olası dengesizlik var."
                    )
                }
            }

            "css" -> {
                if (
                    file.content.count {
                        it == '{'
                    } !=
                    file.content.count {
                        it == '}'
                    }
                ) {
                    result.add(
                        "${file.name}: CSS süslü parantezleri dengeli değil."
                    )
                }
            }

            "js" -> {
                if (
                    file.content.count {
                        it == '('
                    } !=
                    file.content.count {
                        it == ')'
                    }
                ) {
                    result.add(
                        "${file.name}: JavaScript parantezlerinde olası hata var."
                    )
                }
            }
        }
    }

    return result
}

@Composable
private fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    autoSave: Boolean,
    onAutoSaveChange: (Boolean) -> Unit,
    funnyMode: Boolean,
    onFunnyModeChange: (Boolean) -> Unit,
    clickSound: Boolean,
    onClickSoundChange: (Boolean) -> Unit,
    notificationSound: Boolean,
    onNotificationSoundChange: (Boolean) -> Unit,
    onCheckUpdate: () -> Unit,
    onAbout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(P.background)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                bottom = 20.dp
            )
    ) {
        TopBar(
            title = L("settings"),
            leading = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = P.primary
                )
            }
        )

        SettingsSection(
            title = L("theme"),
            icon = Icons.Default.Palette
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected =
                        themeMode ==
                                ThemeMode.DARK,
                    onClick = {
                        onThemeChange(
                            ThemeMode.DARK
                        )
                    },
                    label = {
                        Text(
                            if (
                                Lang ==
                                AppLanguage.EN
                            ) {
                                "Dark"
                            } else {
                                "Koyu"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DarkMode,
                            contentDescription = null
                        )
                    }
                )

                FilterChip(
                    selected =
                        themeMode ==
                                ThemeMode.LIGHT,
                    onClick = {
                        onThemeChange(
                            ThemeMode.LIGHT
                        )
                    },
                    label = {
                        Text(
                            if (
                                Lang ==
                                AppLanguage.EN
                            ) {
                                "Light"
                            } else {
                                "Beyaz"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LightMode,
                            contentDescription = null
                        )
                    }
                )

                FilterChip(
                    selected =
                        themeMode ==
                                ThemeMode.SYSTEM,
                    onClick = {
                        onThemeChange(
                            ThemeMode.SYSTEM
                        )
                    },
                    label = {
                        Text(
                            if (
                                Lang ==
                                AppLanguage.EN
                            ) {
                                "Device"
                            } else {
                                "Cihaz"
                            }
                        )
                    }
                )
            }
        }

        SettingsSection(
            title = L("language"),
            icon = Icons.Default.Language
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected =
                        language ==
                                AppLanguage.TR,
                    onClick = {
                        onLanguageChange(
                            AppLanguage.TR
                        )
                    },
                    label = {
                        Text("Türkçe")
                    }
                )

                FilterChip(
                    selected =
                        language ==
                                AppLanguage.EN,
                    onClick = {
                        onLanguageChange(
                            AppLanguage.EN
                        )
                    },
                    label = {
                        Text("English")
                    }
                )
            }
        }

        SettingRow(
            title = L("autoSave"),
            icon = Icons.Default.Done,
            checked = autoSave,
            onCheckedChange =
                onAutoSaveChange
        )

        SettingRow(
            title = L("funnyMode"),
            icon = Icons.Default.AutoFixHigh,
            checked = funnyMode,
            onCheckedChange =
                onFunnyModeChange
        )

        SettingsSection(
            title = if (
                Lang ==
                AppLanguage.EN
            ) {
                "Sound settings"
            } else {
                "Ses Ayarları"
            },
            icon = Icons.Default.VolumeUp
        ) {
            SettingRow(
                title = L("clickSound"),
                icon =
                    if (clickSound) {
                        Icons.Default.VolumeUp
                    } else {
                        Icons.Default.VolumeOff
                    },
                checked = clickSound,
                onCheckedChange =
                    onClickSoundChange
            )

            SettingRow(
                title = L("notificationSound"),
                icon =
                    Icons.Default.Notifications,
                checked = notificationSound,
                onCheckedChange =
                    onNotificationSoundChange
            )
        }

        SettingsSection(
            title = L("owner"),
            icon = Icons.Default.Share
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "https://t.me/codemindowner"
                                    )
                                )
                            )
                        } catch (_: Exception) {
                        }
                    }
                    .background(P.surface2)
                    .padding(14.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Telegram",
                    tint = P.primary
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                Column {
                    Text(
                        "@codemindowner",
                        color = P.text,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        L("ownerChannel"),
                        color = P.muted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        SettingsAction(
            icon = Icons.Default.Refresh,
            title = L("update"),
            onClick = onCheckUpdate
        )

        SettingsAction(
            icon = Icons.Default.Info,
            title = L("about"),
            onClick = onAbout
        )

        Text(
            "CodeMind",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 18.dp
                ),
            color = P.muted,
            fontSize = 11.sp,
            textAlign =
                androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
    ) {
        Row(
            modifier = Modifier.padding(
                bottom = 7.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = P.primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(
                Modifier.width(7.dp)
            )

            Text(
                title,
                color = P.text,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = P.surface
            ),
            border = BorderStroke(
                1.dp,
                P.border
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = P.muted
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            title,
            modifier = Modifier.weight(1f),
            color = P.text
        )

        Switch(
            checked = checked,
            onCheckedChange =
                onCheckedChange
        )
    }
}

@Composable
private fun SettingsAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(P.surface)
            .border(
                BorderStroke(
                    1.dp,
                    P.border
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = P.primary
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            title,
            color = P.text,
            modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = P.muted
        )
    }
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val version =
        try {
            context.packageManager
                .getPackageInfo(
                    context.packageName,
                    0
                )
                .versionName
                ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = P.surface,
        title = {
            Text(
                "CodeMind",
                color = P.text
            )
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 10.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Android,
                        contentDescription = null,
                        tint = P.primary,
                        modifier = Modifier.size(62.dp)
                    )
                }

                Text(
                    "v$version",
                    color = P.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign =
                        androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(
                    L("aboutText"),
                    color = P.text
                )

                Spacer(
                    Modifier.height(16.dp)
                )

                Divider(
                    color = P.border
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(
                    "${L("appVersion")}: $version",
                    color = P.muted
                )

                Text(
                    "${L("developer")}: CodeMind Team",
                    color = P.muted
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            try {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                            "https://t.me/codemindowner"
                                        )
                                    )
                                )
                            } catch (_: Exception) {
                            }
                        }
                        .background(P.surface2)
                        .padding(12.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = P.primary
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text(
                        "@codemindowner",
                        color = P.text
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    L("close"),
                    color = P.primary
                )
            }
        }
    )
}

@Composable
private fun UpdateDialog(
    updateInfo: AppUpdateInfo?,
    checking: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!checking) {
                onDismiss()
            }
        },
        containerColor = P.surface,
        icon = {
            Icon(
                if (updateInfo != null) {
                    Icons.Default.Download
                } else {
                    Icons.Default.Refresh
                },
                contentDescription = null,
                tint = P.primary,
                modifier = Modifier.size(42.dp)
            )
        },
        title = {
            Text(
                if (checking) {
                    if (
                        Lang ==
                        AppLanguage.EN
                    ) {
                        "Checking..."
                    } else {
                        "Kontrol ediliyor..."
                    }
                } else if (
                    updateInfo != null
                ) {
                    L("updateFound")
                } else {
                    if (
                        Lang ==
                        AppLanguage.EN
                    ) {
                        "Application up to date"
                    } else {
                        "Uygulama güncel"
                    }
                },
                color = P.text
            )
        },
        text = {
            if (checking) {
                Text(
                    if (
                        Lang ==
                        AppLanguage.EN
                    ) {
                        "Checking the latest CodeMind version..."
                    } else {
                        "En yeni CodeMind sürümü kontrol ediliyor..."
                    },
                    color = P.muted
                )
            } else if (
                updateInfo != null
            ) {
                Column {
                    Text(
                        "v${updateInfo.versionName}",
                        color = P.primary,
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        updateInfo.releaseNotes.ifBlank {
                            if (
                                Lang ==
                                AppLanguage.EN
                            ) {
                                "A new CodeMind version is available."
                            } else {
                                "Yeni bir CodeMind sürümü mevcut."
                            }
                        },
                        color = P.text
                    )
                }
            } else {
                Text(
                    L("noUpdate"),
                    color = P.text
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    L("close"),
                    color = P.primary
                )
            }
        }
    )
}

@Composable
private fun FeatureDialog(
    onDismiss: () -> Unit,
    onAskAi: (String) -> Unit
) {
    val features =
        if (
            Lang ==
            AppLanguage.EN
        ) {
            listOf(
                "Modern home page" to
                        "Create a modern responsive home page for my project.",
                "Responsive design" to
                        "Make my project fully responsive for phones and tablets.",
                "Animations" to
                        "Add modern lightweight animations and transitions.",
                "Contact form" to
                        "Add a complete contact form with validation.",
                "Dark / Light theme" to
                        "Add a dark and light theme switcher.",
                "Local storage" to
                        "Add localStorage support for project data.",
                "API connection" to
                        "Add a clean API integration structure.",
                "Mobile menu" to
                        "Add a modern mobile navigation menu."
            )
        } else {
            listOf(
                "Modern ana sayfa" to
                        "Projem için modern ve responsive bir ana sayfa oluştur.",
                "Responsive tasarım" to
                        "Projeyi telefon ve tabletlerde tamamen responsive yap.",
                "Animasyonlar" to
                        "Modern ve hafif animasyonlar ve geçişler ekle.",
                "İletişim formu" to
                        "Doğrulamalı tam bir iletişim formu ekle.",
                "Dark / Light tema" to
                        "Dark ve Light tema değiştirme sistemi ekle.",
                "Local storage" to
                        "Proje verileri için localStorage desteği ekle.",
                "API bağlantısı" to
                        "Temiz bir API bağlantı yapısı ekle.",
                "Mobil menü" to
                        "Modern bir mobil navigasyon menüsü ekle."
            )
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = P.surface,
        title = {
            Text(
                L("featureAiTitle"),
                color = P.text
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    L("featureAiText"),
                    color = P.muted,
                    fontSize = 13.sp
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                features.forEach { feature ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 3.dp
                            )
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                P.surface2
                            )
                            .clickable {
                                onAskAi(
                                    feature.second
                                )
                            }
                            .padding(13.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = P.primary
                        )

                        Spacer(
                            Modifier.width(10.dp)
                        )

                        Text(
                            feature.first,
                            color = P.text
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    L("close"),
                    color = P.primary
                )
            }
        }
    )
}

@Composable
private fun NewFileDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = P.surface,
        title = {
            Text(
                L("newFile"),
                color = P.text
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Dosya adı")
                },
                placeholder = {
                    Text("ör. app.js")
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    L("cancel"),
                    color = P.muted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(name)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = P.primary,
                    contentColor = Color(0xFF031018)
                )
            ) {
                Text(
                    L("create")
                )
            }
        }
    )
}

@Composable
private fun TopBar(
    title: String,
    leading: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    P.primarySoft
                ),
            contentAlignment =
                Alignment.Center
        ) {
            leading()
        }

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            title,
            color = P.text,
            fontSize = 20.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

@Composable
private fun ToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )

        Spacer(
            Modifier.width(4.dp)
        )

        Text(
            text,
            fontSize = 10.sp
        )
    }
}

private fun copyToClipboard(
    context: Context,
    text: String
) {
    val clipboard =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    clipboard.setPrimaryClip(
        ClipData.newPlainText(
            "CodeMind",
            text
        )
    )

    Toast.makeText(
        context,
        "Kopyalandı.",
        Toast.LENGTH_SHORT
    ).show()
}

private fun defaultContentFor(
    name: String
): String {
    return when (
        name.substringAfterLast(
            ".",
            ""
        ).lowercase()
    ) {
        "html" -> """
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >
    <title>CodeMind</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <main class="container">
        <h1>CodeMind</h1>
        <p>Projen hazır.</p>
        <button onclick="hello()">Tıkla</button>
    </main>

    <script src="script.js"></script>
</body>
</html>
        """.trimIndent()

        "css" -> """
body {
    margin: 0;
    min-height: 100vh;
    display: grid;
    place-items: center;
    background: #05080d;
    color: #eaf7ff;
    font-family: sans-serif;
}

.container {
    text-align: center;
}

button {
    padding: 12px 24px;
    border: 0;
    border-radius: 12px;
    background: #2bd7ff;
    color: #031018;
    cursor: pointer;
}
        """.trimIndent()

        "js" -> """
function hello() {
    alert("CodeMind hazır!");
}
        """.trimIndent()

        "json" -> """
{
    "name": "CodeMind",
    "version": "1.0.0"
}
        """.trimIndent()

        "md" -> """
# CodeMind

Modern Android kod editörü.
        """.trimIndent()

        else -> ""
    }
}

private fun formatCode(
    code: String
): String {
    return code
        .replace(
            "\r\n",
            "\n"
        )
        .lines()
        .joinToString("\n") {
            it.trimEnd()
        }
        .trim()
}
