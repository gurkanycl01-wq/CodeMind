package com.codemind.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private const val CODEMIND_API =
    "https://codemind-ai.gurkanycl01.workers.dev/api/chat"

private val Background = Color(0xFF090D14)
private val Surface = Color(0xFF111720)
private val Surface2 = Color(0xFF171E29)
private val Surface3 = Color(0xFF1C2532)
private val Border = Color(0xFF303B4B)
private val TextPrimary = Color(0xFFF2F5FA)
private val TextSecondary = Color(0xFF9CA8BA)
private val Neon = Color(0xFFE6EDF7)
private val NeonSoft = Color(0xFFB8C4D6)
private val UserBubble = Color(0xFF202A38)
private val Success = Color(0xFFB9F6CA)

private data class AiModel(
    val name: String,
    val id: String,
    val description: String
)

private val models = listOf(
    AiModel(
        "GPT-4o",
        "openai/gpt-4o",
        "Genel kullanım"
    ),
    AiModel(
        "GPT-4o Mini",
        "openai/gpt-4o-mini",
        "Hızlı ve verimli"
    ),
    AiModel(
        "Claude Sonnet",
        "anthropic/claude-3.5-sonnet",
        "Kodlama için güçlü"
    ),
    AiModel(
        "Gemini Flash",
        "google/gemini-2.0-flash-exp:free",
        "Hızlı model"
    ),
    AiModel(
        "DeepSeek R1",
        "deepseek/deepseek-r1",
        "Kod ve mantık"
    ),
    AiModel(
        "OpenRouter Free",
        "openrouter/free",
        "Ücretsiz model yönlendirici"
    )
)

private data class ChatMessage(
    val id: Long = System.nanoTime(),
    val text: String,
    val fromUser: Boolean,
    val code: String? = null,
    val language: String? = null,
    val isSuccess: Boolean = false
)

private data class CodeFile(
    val name: String,
    var content: String
)

private enum class Screen {
    CHAT,
    FILES,
    CODE,
    AI,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeMindApp()
        }
    }
}

@Composable
fun CodeMindApp() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentScreen by remember {
        mutableStateOf(Screen.CHAT)
    }

    var selectedModel by remember {
        mutableStateOf(models.first())
    }

    var modelMenuOpen by remember {
        mutableStateOf(false)
    }

    var menuOpen by remember {
        mutableStateOf(false)
    }

    var showNewFile by remember {
        mutableStateOf(false)
    }

    var showNewFolder by remember {
        mutableStateOf(false)
    }

    var selectedFile by remember {
        mutableStateOf<CodeFile?>(null)
    }

    var files by remember {
        mutableStateOf(
            listOf(
                CodeFile(
                    "main.py",
                    "print(\"Merhaba CodeMind!\")"
                ),
                CodeFile(
                    "README.md",
                    "# CodeMind\n\nAI destekli kod projesi."
                ),
                CodeFile(
                    "MainActivity.kt",
                    """
package com.codemind.app

import android.os.Bundle

class MainActivity
""".trimIndent()
                )
            )
        )
    }

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    text = "Merhaba! 👋\n\nKod yazabilir, dosyalarını düzenleyebilir, hataları inceleyebilir ve projeni birlikte geliştirebiliriz.",
                    fromUser = false
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            TopBar(
                selectedModel = selectedModel,
                modelMenuOpen = modelMenuOpen,
                onModelMenu = {
                    modelMenuOpen = !modelMenuOpen
                },
                onSelectModel = {
                    selectedModel = it
                    modelMenuOpen = false

                    Toast.makeText(
                        context,
                        "${it.name} seçildi",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onMenu = {
                    menuOpen = !menuOpen
                },
                onNew = {
                    messages.clear()
                    messages.add(
                        ChatMessage(
                            text = "Yeni sohbet başlatıldı.",
                            fromUser = false
                        )
                    )
                }
            )

            if (menuOpen) {
                SideMenu(
                    onClose = {
                        menuOpen = false
                    },
                    onSelect = {
                        currentScreen = it
                        menuOpen = false
                    }
                )
            }

            when (currentScreen) {

                Screen.CHAT -> {
                    ChatScreen(
                        messages = messages,
                        selectedModel = selectedModel,
                        onSend = { message ->

                            if (message.isBlank()) {
                                return@ChatScreen
                            }

                            messages.add(
                                ChatMessage(
                                    text = message,
                                    fromUser = true
                                )
                            )

                            scope.launch {

                                val result =
                                    sendMessageToCodeMind(
                                        message = message,
                                        model = selectedModel.id,
                                        files = files
                                    )

                                result
                                    .onSuccess { answer ->

                                        messages.add(
                                            ChatMessage(
                                                text = answer,
                                                fromUser = false
                                            )
                                        )
                                    }
                                    .onFailure { error ->

                                        messages.add(
                                            ChatMessage(
                                                text =
                                                    "Bağlantı hatası:\n\n" +
                                                        (error.message
                                                            ?: "Bilinmeyen hata"),
                                                fromUser = false
                                            )
                                        )
                                    }
                            }
                        },
                        onCopy = {
                            copyText(
                                context,
                                it
                            )
                        },
                        onAddToFile = {
                            selectedFile?.let { file ->

                                val updated =
                                    files.map { f ->
                                        if (f.name == file.name) {
                                            f.copy(
                                                content =
                                                    f.content +
                                                        "\n\n" +
                                                        it
                                            )
                                        } else {
                                            f
                                        }
                                    }

                                files = updated

                                Toast.makeText(
                                    context,
                                    "${file.name} dosyasına eklendi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }

                Screen.FILES -> {

                    FilesScreen(
                        files = files,
                        onNewFile = {
                            showNewFile = true
                        },
                        onNewFolder = {
                            showNewFolder = true
                        },
                        onFileClick = {
                            selectedFile = it
                            currentScreen = Screen.CODE
                        }
                    )
                }

                Screen.CODE -> {

                    CodeEditorScreen(
                        file = selectedFile
                            ?: files.first(),
                        onBack = {
                            currentScreen = Screen.FILES
                        },
                        onSave = { newContent ->

                            val name =
                                selectedFile?.name
                                    ?: files.first().name

                            files =
                                files.map { file ->
                                    if (file.name == name) {
                                        file.copy(
                                            content = newContent
                                        )
                                    } else {
                                        file
                                    }
                                }

                            selectedFile =
                                selectedFile?.copy(
                                    content = newContent
                                )

                            Toast.makeText(
                                context,
                                "Dosya kaydedildi",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onRun = {

                            messages.add(
                                ChatMessage(
                                    text = "Kod çalıştırıldı.",
                                    fromUser = false,
                                    isSuccess = true
                                )
                            )

                            currentScreen = Screen.CHAT
                        }
                    )
                }

                Screen.AI -> {

                    AiScreen(
                        selectedModel = selectedModel,
                        onModel = {
                            selectedModel = it
                        },
                        onChat = {
                            currentScreen = Screen.CHAT
                        }
                    )
                }

                Screen.SETTINGS -> {

                    SettingsScreen()
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            BottomNavigation(
                current = currentScreen,
                onSelect = {
                    currentScreen = it
                }
            )
        }

        if (showNewFile) {

            NewFileDialog(
                onDismiss = {
                    showNewFile = false
                },
                onCreate = { name ->

                    if (
                        name.isNotBlank() &&
                        files.none { it.name == name }
                    ) {

                        val newFile =
                            CodeFile(
                                name,
                                ""
                            )

                        files =
                            files + newFile

                        selectedFile = newFile

                        showNewFile = false

                        currentScreen = Screen.CODE
                    }
                }
            )
        }

        if (showNewFolder) {

            NewFolderDialog(
                onDismiss = {
                    showNewFolder = false
                },
                onCreate = { name ->

                    Toast.makeText(
                        context,
                        "Klasör oluşturuldu: $name",
                        Toast.LENGTH_SHORT
                    ).show()

                    showNewFolder = false
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    selectedModel: AiModel,
    modelMenuOpen: Boolean,
    onModelMenu: () -> Unit,
    onSelectModel: (AiModel) -> Unit,
    onMenu: () -> Unit,
    onNew: () -> Unit
) {

    Box {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(Background)
                .padding(
                    horizontal = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onMenu
            ) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Menü",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF7F8998),
                                Color(0xFF171D26)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "CodeMind",
                    color = TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "AI Destekli Kod Editörü",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box {

                Row(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .border(
                            1.dp,
                            Border,
                            RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            onModelMenu()
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 11.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        selectedModel.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        "⌄",
                        color = TextSecondary,
                        fontSize = 18.sp
                    )
                }

                DropdownMenu(
                    expanded = modelMenuOpen,
                    onDismissRequest = onModelMenu,
                    modifier = Modifier
                        .background(Surface)
                ) {

                    Text(
                        "Model Seç",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 12.dp
                        )
                    )

                    models.forEach { model ->

                        DropdownMenuItem(
                            text = {

                                Column {

                                    Text(
                                        model.name,
                                        color = TextPrimary
                                    )

                                    Text(
                                        model.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            },
                            onClick = {
                                onSelectModel(model)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.AutoAwesome,
                                    null,
                                    tint = Neon
                                )
                            },
                            trailingIcon = {

                                if (
                                    model.id ==
                                    selectedModel.id
                                ) {
                                    Icon(
                                        Icons.Outlined.Check,
                                        null,
                                        tint = Neon
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            IconButton(
                onClick = onNew,
                modifier = Modifier
                    .size(44.dp)
                    .border(
                        1.dp,
                        Border,
                        CircleShape
                    )
            ) {

                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Yeni sohbet",
                    tint = TextPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(Border)
        )
    }
}

@Composable
private fun SideMenu(
    onClose: () -> Unit,
    onSelect: (Screen) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = .45f)
            )
            .clickable {
                onClose()
            }
    ) {

        Column(
            modifier = Modifier
                .width(285.dp)
                .fillMaxHeight()
                .background(Surface)
                .clickable(enabled = false) {}
                .padding(
                    top = 90.dp,
                    start = 18.dp,
                    end = 18.dp
                )
        ) {

            Text(
                "CodeMind",
                color = TextPrimary,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Çalışma alanı",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            DrawerItem(
                "Sohbet",
                Icons.Outlined.ChatBubbleOutline
            ) {
                onSelect(Screen.CHAT)
            }

            DrawerItem(
                "Dosyalar",
                Icons.Outlined.FolderOpen
            ) {
                onSelect(Screen.FILES)
            }

            DrawerItem(
                "Kod Editörü",
                Icons.Outlined.Code
            ) {
                onSelect(Screen.CODE)
            }

            DrawerItem(
                "AI Asistan",
                Icons.Outlined.AutoAwesome
            ) {
                onSelect(Screen.AI)
            }

            DrawerItem(
                "Ayarlar",
                Icons.Outlined.Settings
            ) {
                onSelect(Screen.SETTINGS)
            }
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 15.dp,
                vertical = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = TextSecondary
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Text(
            text,
            color = TextPrimary,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun ChatScreen(
    messages: List<ChatMessage>,
    selectedModel: AiModel,
    onSend: (String) -> Unit,
    onCopy: (String) -> Unit,
    onAddToFile: (String) -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val listState =
        rememberLazyListState()

    val scope =
        rememberCoroutineScope()

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = 18.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(18.dp),
            contentPadding =
                androidx.compose.foundation.layout.PaddingValues(
                    top = 18.dp,
                    bottom = 15.dp
                )
        ) {

            items(
                messages,
                key = { it.id }
            ) { message ->

                MessageBubble(
                    message = message,
                    onCopy = onCopy,
                    onAddToFile = onAddToFile
                )
            }

            if (loading) {

                item {

                    LoadingBubble()
                }
            }
        }

        Composer(
            value = input,
            loading = loading,
            onValueChange = {
                input = it
            },
            onSend = {

                if (
                    input.isNotBlank() &&
                    !loading
                ) {

                    val text =
                        input.trim()

                    input = ""

                    loading = true

                    scope.launch {

                        onSend(text)

                        loading = false
                    }
                }
            }
        )
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onAddToFile: (String) -> Unit
) {

    if (message.fromUser) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.End
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(.78f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 22.dp,
                            topEnd = 22.dp,
                            bottomStart = 22.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .background(UserBubble)
                    .border(
                        1.dp,
                        Border,
                        RoundedCornerShape(22.dp)
                    )
                    .padding(16.dp)
            ) {

                Text(
                    message.text,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            }
        }

        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.Start,
        verticalAlignment =
            Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Surface2)
                .border(
                    1.dp,
                    Border,
                    CircleShape
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = Neon,
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(.94f)
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(Surface2)
                .border(
                    1.dp,
                    Border,
                    RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
                .animateContentSize()
        ) {

            if (message.isSuccess) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(
                                    alpha = .12f
                                )
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            Icons.Outlined.Check,
                            null,
                            tint = Success,
                            modifier =
                                Modifier.size(19.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        "Kod dosyaya eklendi ve çalıştırıldı!",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutputBox(
                    "Çıktı:",
                    "Merhaba CodeMind!"
                )

                return@Column
            }

            Text(
                message.text,
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 25.sp
            )

            message.code?.let { code ->

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                CodeBlock(
                    code = code,
                    language =
                        message.language
                            ?: "kotlin",
                    onCopy = {
                        onCopy(code)
                    }
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    SmallAction(
                        "Kopyala",
                        Icons.Outlined.ContentCopy
                    ) {
                        onCopy(code)
                    }

                    SmallAction(
                        "Düzenle",
                        Icons.Outlined.Edit
                    ) {
                        onCopy(code)
                    }

                    SmallAction(
                        "Dosyaya Ekle",
                        Icons.Outlined.Description
                    ) {
                        onAddToFile(code)
                    }

                    SmallAction(
                        "Daha Fazla",
                        Icons.Outlined.MoreHoriz
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun CodeBlock(
    code: String,
    language: String,
    onCopy: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(Color(0xFF0A0F16))
            .border(
                1.dp,
                Border,
                RoundedCornerShape(15.dp)
            )
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

            Text(
                language,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(30.dp)
            ) {

                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = "Kopyala",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border)
        )

        Text(
            code,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun OutputBox(
    title: String,
    output: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(Color(0xFF0A0F16))
            .border(
                1.dp,
                Border,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {

        Text(
            title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            output,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SmallAction(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clip(
                RoundedCornerShape(22.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(22.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 13.dp,
                vertical = 9.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(17.dp)
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Text(
            text,
            color = TextPrimary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun LoadingBubble() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Surface2),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                Icons.Outlined.AutoAwesome,
                null,
                tint = Neon
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            "CodeMind düşünüyor...",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun Composer(
    value: String,
    loading: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
            .clip(
                RoundedCornerShape(30.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(30.dp)
            )
            .background(Surface)
            .padding(
                horizontal = 7.dp,
                vertical = 6.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {},
            modifier = Modifier.size(44.dp)
        ) {

            Icon(
                Icons.Outlined.Add,
                contentDescription = "Ekle",
                tint = TextPrimary,
                modifier = Modifier.size(25.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Mesajınızı yazın...",
                    color = TextSecondary
                )
            },
            colors =
                androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =
                        Color.Transparent,
                    unfocusedBorderColor =
                        Color.Transparent,
                    focusedContainerColor =
                        Color.Transparent,
                    unfocusedContainerColor =
                        Color.Transparent,
                    cursorColor = Neon,
                    focusedTextColor =
                        TextPrimary,
                    unfocusedTextColor =
                        TextPrimary
                ),
            maxLines = 5
        )

        IconButton(
            onClick = {},
            enabled = !loading,
            modifier = Modifier.size(44.dp)
        ) {

            Icon(
                Icons.Outlined.Mic,
                contentDescription = "Ses",
                tint = TextSecondary
            )
        }

        IconButton(
            onClick = onSend,
            enabled =
                value.isNotBlank() &&
                    !loading,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (
                        value.isNotBlank() &&
                        !loading
                    )
                        Neon
                    else
                        Surface3
                )
        ) {

            Icon(
                Icons.Outlined.Send,
                contentDescription = "Gönder",
                tint =
                    if (
                        value.isNotBlank() &&
                        !loading
                    )
                        Color(0xFF10151D)
                    else
                        TextSecondary
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    current: Screen,
    onSelect: (Screen) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .border(
                1.dp,
                Border
            )
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceEvenly,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        NavigationItem(
            "Sohbet",
            Icons.Outlined.ChatBubbleOutline,
            current == Screen.CHAT
        ) {
            onSelect(Screen.CHAT)
        }

        NavigationItem(
            "Dosyalar",
            Icons.Outlined.Folder,
            current == Screen.FILES
        ) {
            onSelect(Screen.FILES)
        }

        NavigationItem(
            "Kod Editörü",
            Icons.Outlined.Code,
            current == Screen.CODE
        ) {
            onSelect(Screen.CODE)
        }

        NavigationItem(
            "AI Asistan",
            Icons.Outlined.AutoAwesome,
            current == Screen.AI
        ) {
            onSelect(Screen.AI)
        }

        NavigationItem(
            "Ayarlar",
            Icons.Outlined.Settings,
            current == Screen.SETTINGS
        ) {
            onSelect(Screen.SETTINGS)
        }
    }
}

@Composable
private fun NavigationItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(
                if (selected)
                    Surface2
                else
                    Color.Transparent
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 11.dp,
                vertical = 8.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            icon,
            contentDescription = text,
            tint =
                if (selected)
                    Neon
                else
                    TextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text,
            color =
                if (selected)
                    TextPrimary
                else
                    TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FilesScreen(
    files: List<CodeFile>,
    onNewFile: () -> Unit,
    onNewFolder: () -> Unit,
    onFileClick: (CodeFile) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(18.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                "Dosyalar",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNewFolder
            ) {

                Icon(
                    Icons.Outlined.CreateNewFolder,
                    null,
                    tint = TextPrimary
                )
            }

            IconButton(
                onClick = onNewFile
            ) {

                Icon(
                    Icons.Outlined.Add,
                    null,
                    tint = TextPrimary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(files) { file ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(Surface)
                        .border(
                            1.dp,
                            Border,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onFileClick(file)
                        }
                        .padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        if (
                            file.name.endsWith(".py")
                        )
                            Icons.Outlined.Code
                        else
                            Icons.Outlined.Description,
                        null,
                        tint = Neon
                    )

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {

                        Text(
                            file.name,
                            color = TextPrimary,
                            fontWeight =
                                FontWeight.Medium
                        )

                        Text(
                            "${file.content.length} karakter",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeEditorScreen(
    file: CodeFile,
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    onRun: () -> Unit
) {

    var content by remember(
        file.name
    ) {
        mutableStateOf(
            file.content
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    Icons.Outlined.ArrowBack,
                    null,
                    tint = TextPrimary
                )
            }

            Text(
                file.name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    onSave(content)
                }
            ) {

                Icon(
                    Icons.Outlined.Check,
                    null,
                    tint = Neon
                )
            }

            IconButton(
                onClick = onRun
            ) {

                Icon(
                    Icons.Outlined.PlayArrow,
                    null,
                    tint = Success
                )
            }
        }

        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            textStyle =
                androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontFamily =
                        FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 21.sp
                ),
            colors =
                androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =
                        Border,
                    unfocusedBorderColor =
                        Border,
                    focusedContainerColor =
                        Color(0xFF080D13),
                    unfocusedContainerColor =
                        Color(0xFF080D13)
                )
        )
    }
}

@Composable
private fun AiScreen(
    selectedModel: AiModel,
    onModel: (AiModel) -> Unit,
    onChat: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(20.dp)
    ) {

        Text(
            "AI Asistan",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            "Kod yaz, hata ayıkla ve projeni geliştir.",
            color = TextSecondary
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            "Aktif model",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        models.forEach { model ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        if (
                            model.id ==
                            selectedModel.id
                        )
                            Surface3
                        else
                            Surface
                    )
                    .border(
                        1.dp,
                        if (
                            model.id ==
                            selectedModel.id
                        )
                            NeonSoft
                        else
                            Border,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onModel(model)
                    }
                    .padding(15.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.AutoAwesome,
                    null,
                    tint = Neon
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        model.name,
                        color = TextPrimary
                    )

                    Text(
                        model.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (
                    model.id ==
                    selectedModel.id
                ) {

                    Icon(
                        Icons.Outlined.Check,
                        null,
                        tint = Neon
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onChat,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(
                Icons.Outlined.ChatBubbleOutline,
                null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("Sohbete Git")
        }
    }
}

@Composable
private fun SettingsScreen() {

    var notifications by remember {
        mutableStateOf(true)
    }

    var autoSave by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(20.dp)
    ) {

        Text(
            "Ayarlar",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        SettingRow(
            "Otomatik kaydet",
            "Kod değişikliklerini otomatik kaydet",
            autoSave
        ) {
            autoSave = it
        }

        SettingRow(
            "Bildirimler",
            "AI işlemleri tamamlandığında bildir",
            notifications
        ) {
            notifications = it
        }

        SettingInfo(
            "Tema",
            "Koyu / Neon Gri"
        )

        SettingInfo(
            "AI bağlantısı",
            "Cloudflare Worker"
        )

        SettingInfo(
            "Sürüm",
            "CodeMind 1.0"
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 14.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                title,
                color = TextPrimary
            )

            Text(
                subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onChecked
        )
    }
}

@Composable
private fun SettingInfo(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 14.dp
            )
    ) {

        Text(
            title,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            value,
            color = TextSecondary
        )
    }
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
        title = {
            Text("Yeni Dosya")
        },
        text = {

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Dosya adı")
                },
                singleLine = true
            )
        },
        confirmButton = {

            TextButton(
                onClick = {
                    onCreate(name)
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

@Composable
private fun NewFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yeni Klasör")
        },
        text = {

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Klasör adı")
                },
                singleLine = true
            )
        },
        confirmButton = {

            TextButton(
                onClick = {
                    onCreate(name)
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

private suspend fun sendMessageToCodeMind(
    message: String,
    model: String,
    files: List<CodeFile>
): Result<String> {

    return withContext(Dispatchers.IO) {

        try {

            val url =
                URL(CODEMIND_API)

            val connection =
                url.openConnection()
                    as HttpURLConnection

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
            connection.readTimeout = 60000
            connection.doOutput = true

            val filesJson =
                JSONArray()

            files.forEach { file ->

                filesJson.put(
                    JSONObject().apply {
                        put(
                            "name",
                            file.name
                        )
                        put(
                            "content",
                            file.content
                        )
                    }
                )
            }

            val requestBody =
                JSONObject().apply {

                    put(
                        "message",
                        message
                    )

                    put(
                        "prompt",
                        message
                    )

                    put(
                        "model",
                        model
                    )

                    put(
                        "language",
                        "kotlin"
                    )

                    put(
                        "projectFiles",
                        filesJson
                    )

                    put(
                        "files",
                        filesJson
                    )

                    put(
                        "funnyMode",
                        false
                    )
                }.toString()

            connection.outputStream.use { output ->

                output.write(
                    requestBody.toByteArray(
                        Charsets.UTF_8
                    )
                )

                output.flush()
            }

            val responseCode =
                connection.responseCode

            val responseText =
                if (
                    responseCode in 200..299
                ) {

                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                } else {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use {
                            it.readText()
                        }
                        ?: "Sunucu hatası: $responseCode"
                }

            connection.disconnect()

            if (
                responseCode !in 200..299
            ) {

                return@withContext Result.failure(
                    Exception(
                        "HTTP $responseCode\n$responseText"
                    )
                )
            }

            val json =
                JSONObject(responseText)

            if (
                !json.optBoolean(
                    "success",
                    false
                )
            ) {

                return@withContext Result.failure(
                    Exception(
                        json.optString(
                            "error",
                            "Worker hatası."
                        )
                    )
                )
            }

            val reply =
                json.optString(
                    "reply"
                ).ifBlank {
                    json.optString(
                        "response"
                    )
                }.ifBlank {
                    json.optString(
                        "answer"
                    )
                }.ifBlank {
                    "Worker cevap döndürdü fakat metin alanı boş.\n\n$responseText"
                }

            Result.success(reply)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}

private fun copyText(
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
        "Kopyalandı",
        Toast.LENGTH_SHORT
    ).show()
}
