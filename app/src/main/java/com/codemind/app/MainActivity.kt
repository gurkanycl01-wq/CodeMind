package com.codemind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val AI_ENDPOINT =
    "https://codemind-ai.gurkanycl01.workers.dev"

private val Background = Color(0xFF050914)
private val SurfaceDark = Color(0xFF0B1220)
private val SurfaceLight = Color(0xFF111B2D)
private val Border = Color(0xFF263A5A)
private val Blue = Color(0xFF238BFF)
private val Purple = Color(0xFF8B3DFF)
private val Cyan = Color(0xFF20D9FF)
private val TextPrimary = Color(0xFFF4F7FF)
private val TextSecondary = Color(0xFF91A0B8)
private val ErrorRed = Color(0xFFFF5570)
private val SuccessGreen = Color(0xFF42E8A0)

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(90, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

data class ChatMessage(
    val text: String,
    val fromUser: Boolean,
    val code: Boolean = false
)

enum class AppPage {
    CHAT,
    EDITOR,
    PROJECTS,
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

    var page by remember {
        mutableStateOf(AppPage.CHAT)
    }

    var drawerOpen by remember {
        mutableStateOf(false)
    }

    var showAiModes by remember {
        mutableStateOf(false)
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Merhaba! 👋\n\nBen CodeMind. Senin AI kod asistanınım.\n\nKod yazabilir, hata ayıklayabilir, projelerini geliştirebilir ve sorularını cevaplayabilirim.\n\nBugün ne yapalım?",
                fromUser = false
            )
        )
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                when (page) {

                    AppPage.CHAT -> {
                        ChatScreen(
                            messages = messages,
                            onMenu = {
                                drawerOpen = true
                            },
                            onNewChat = {
                                messages.clear()
                                messages.add(
                                    ChatMessage(
                                        text = "Yeni sohbet hazır. Ne yapmak istersin?",
                                        fromUser = false
                                    )
                                )
                            },
                            onOpenModes = {
                                showAiModes = true
                            }
                        )
                    }

                    AppPage.EDITOR -> {
                        EditorScreen(
                            onBack = {
                                page = AppPage.CHAT
                            },
                            onPreview = {
                                page = AppPage.CHAT
                            }
                        )
                    }

                    AppPage.PROJECTS -> {
                        ProjectsScreen(
                            onBack = {
                                page = AppPage.CHAT
                            }
                        )
                    }

                    AppPage.SETTINGS -> {
                        SettingsScreen(
                            onBack = {
                                page = AppPage.CHAT
                            }
                        )
                    }
                }

                if (drawerOpen) {

                    SideDrawer(
                        currentPage = page,
                        onClose = {
                            drawerOpen = false
                        },
                        onChat = {
                            page = AppPage.CHAT
                            drawerOpen = false
                        },
                        onEditor = {
                            page = AppPage.EDITOR
                            drawerOpen = false
                        },
                        onProjects = {
                            page = AppPage.PROJECTS
                            drawerOpen = false
                        },
                        onSettings = {
                            page = AppPage.SETTINGS
                            drawerOpen = false
                        }
                    )
                }

                if (showAiModes) {

                    AiModesDialog(
                        onDismiss = {
                            showAiModes = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    title: String,
    onMenu: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (onBack != null) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Geri",
                    tint = TextPrimary
                )
            }

        } else if (onMenu != null) {

            IconButton(
                onClick = onMenu
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menü",
                    tint = TextPrimary
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CodeMindLogo(
                size = 38.dp
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (action != null) {
            action()
        }
    }
}

@Composable
fun CodeMindLogo(
    size: androidx.compose.ui.unit.Dp = 46.dp
) {

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(13.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0D7CFF),
                        Color(0xFF7038FF),
                        Color(0xFFE52CFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "C",
            color = Color.White,
            fontSize = (size.value * 0.48f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    onMenu: () -> Unit,
    onNewChat: () -> Unit,
    onOpenModes: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var input by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {

        TopBar(
            title = "CodeMind",
            onMenu = onMenu,
            action = {

                IconButton(
                    onClick = onNewChat
                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Yeni sohbet",
                        tint = TextPrimary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = false
        ) {

            items(messages) { message ->

                MessageBubble(
                    message = message
                )
            }

            if (loading) {

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        CodeMindLogo(
                            size = 34.dp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceLight
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Text(
                                text = "CodeMind düşünüyor...",
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            if (error.isNotBlank()) {

                item {

                    ErrorCard(
                        text = error,
                        onRetry = {
                            error = ""
                        }
                    )
                }
            }
        }

        ChatInput(
            value = input,
            loading = loading,
            onValueChange = {
                input = it
            },
            onModes = onOpenModes,
            onSend = {

                val prompt = input.text.trim()

                if (
                    prompt.isNotEmpty() &&
                    !loading
                ) {

                    input = TextFieldValue("")

                    messages.add(
                        ChatMessage(
                            text = prompt,
                            fromUser = true
                        )
                    )

                    loading = true
                    error = ""

                    scope.launch {

                        val result = sendMessage(
                            message = prompt
                        )

                        loading = false

                        if (result.first) {

                            messages.add(
                                ChatMessage(
                                    text = result.second,
                                    fromUser = false
                                )
                            )

                        } else {

                            error = result.second
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.Top
    ) {

        if (!message.fromUser) {

            CodeMindLogo(
                size = 34.dp
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }

        val bubbleBrush = if (message.fromUser) {

            Brush.linearGradient(
                listOf(
                    Color(0xFF176FFF),
                    Color(0xFF8739FF)
                )
            )

        } else {

            Brush.linearGradient(
                listOf(
                    Color(0xFF101B2D),
                    Color(0xFF152238)
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(
                    if (message.fromUser) 0.82f else 0.88f
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.fromUser) 18.dp else 4.dp,
                        bottomEnd = if (message.fromUser) 4.dp else 18.dp
                    )
                )
                .background(bubbleBrush)
                .padding(15.dp)
        ) {

            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ChatInput(
    value: TextFieldValue,
    loading: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    onModes: () -> Unit,
    onSend: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF070D18)
            )
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 10.dp,
                bottom = 8.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            IconButton(
                onClick = onModes,
                enabled = !loading
            ) {

                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "AI modları",
                    tint = Cyan
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                enabled = !loading,
                placeholder = {
                    Text(
                        "Mesajını yaz...",
                        color = TextSecondary
                    )
                },
                maxLines = 5,
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Cyan
                )
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            IconButton(
                onClick = onSend,
                enabled = value.text.isNotBlank() && !loading,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Blue,
                                Purple
                            )
                        )
                    )
            ) {

                Icon(
                    Icons.Default.Send,
                    contentDescription = "Gönder",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ErrorCard(
    text: String,
    onRetry: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF321522)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "AI isteği başarısız oldu",
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = text,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = onRetry
            ) {

                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = TextPrimary
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    "Kapat",
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun SideDrawer(
    currentPage: AppPage,
    onClose: () -> Unit,
    onChat: () -> Unit,
    onEditor: () -> Unit,
    onProjects: () -> Unit,
    onSettings: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable {
                onClose()
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(310.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0B1424),
                            Color(0xFF070C16)
                        )
                    )
                )
                .clickable { },
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CodeMindLogo(
                    size = 46.dp
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(
                        "CodeMind",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "AI Coding Assistant",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            DrawerItem(
                icon = Icons.Default.Home,
                title = "Sohbet",
                selected = currentPage == AppPage.CHAT,
                onClick = onChat
            )

            DrawerItem(
                icon = Icons.Default.Code,
                title = "Kod Editörü",
                selected = currentPage == AppPage.EDITOR,
                onClick = onEditor
            )

            DrawerItem(
                icon = Icons.Default.Folder,
                title = "Projeler",
                selected = currentPage == AppPage.PROJECTS,
                onClick = onProjects
            )

            DrawerItem(
                icon = Icons.Default.Settings,
                title = "Ayarlar",
                selected = currentPage == AppPage.SETTINGS,
                onClick = onSettings
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "CodeMind 1.0.0",
                color = TextSecondary,
                modifier = Modifier.padding(24.dp),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF125BCE),
                            Color(0xFF6127BE)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                }
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) Color.White else TextSecondary
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            title,
            color = if (selected) {
                Color.White
            } else {
                TextSecondary
            },
            fontSize = 15.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}

@Composable
fun EditorScreen(
    onBack: () -> Unit,
    onPreview: () -> Unit
) {

    var code by remember {

        mutableStateOf(
            """
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width,
          initial-scale=1.0">
    <title>CodeMind</title>
</head>

<body>

    <h1>Merhaba CodeMind 👋</h1>

    <p>
        Kodlamaya başlayalım.
    </p>

</body>
</html>
            """.trimIndent()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {

        TopBar(
            title = "Kod Editörü",
            onBack = onBack,
            action = {

                IconButton(
                    onClick = onPreview
                ) {

                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Preview",
                        tint = Cyan
                    )
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "index.html",
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    )
                    .background(SurfaceLight)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                color = TextPrimary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = SuccessGreen
            )
        }

        Divider(
            color = Border
        )

        OutlinedTextField(
            value = code,
            onValueChange = {
                code = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color(0xFFD5E1F2),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF070D17),
                unfocusedContainerColor = Color(0xFF070D17),
                focusedBorderColor = Purple,
                unfocusedBorderColor = Border,
                cursorColor = Cyan,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
fun ProjectsScreen(
    onBack: () -> Unit
) {

    val projects = remember {

        listOf(
            "Web Sitesi",
            "Mobil Uygulama",
            "Python Projesi",
            "Oyun Projesi"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {

        TopBar(
            title = "Projeler",
            onBack = onBack,
            action = {

                IconButton(
                    onClick = {}
                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Yeni proje",
                        tint = Cyan
                    )
                }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceLight
            )
        ) {

            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Blue,
                                    Purple
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Default.CreateNewFolder,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.width(15.dp)
                )

                Column {

                    Text(
                        "Yeni Proje",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Boş bir proje başlat",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Text(
            "Son Projeler",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {

            items(projects) { project ->

                ProjectRow(
                    name = project
                )
            }
        }
    }
}

@Composable
fun ProjectRow(
    name: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            )
            .clip(RoundedCornerShape(15.dp))
            .background(SurfaceLight)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.Folder,
            contentDescription = null,
            tint = Color(0xFFFFC44D),
            modifier = Modifier.size(30.dp)
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                name,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Text(
                "CodeMind projesi",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = {}
        ) {

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    var notifications by remember {
        mutableStateOf(true)
    }

    var temperature by remember {
        mutableFloatStateOf(0.7f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {

        TopBar(
            title = "Ayarlar",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {

            item {

                SettingsHeader(
                    title = "CodeMind",
                    subtitle = "v1.0.0"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                SettingRow(
                    icon = Icons.Default.DarkMode,
                    title = "Tema",
                    subtitle = "Koyu Mod",
                    onClick = {}
                )

                SettingRow(
                    icon = Icons.Default.Settings,
                    title = "Bildirimler",
                    subtitle = if (notifications) {
                        "Açık"
                    } else {
                        "Kapalı"
                    },
                    onClick = {
                        notifications = !notifications
                    }
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    "AI",
                    color = Cyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 6.dp
                    )
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceLight
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Text(
                            "Model",
                            color = TextSecondary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            "OpenRouter Free",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Text(
                            "Yaratıcılık",
                            color = TextSecondary
                        )

                        Slider(
                            value = temperature,
                            onValueChange = {
                                temperature = it
                            },
                            valueRange = 0f..1f
                        )

                        Text(
                            String.format(
                                "%.1f",
                                temperature
                            ),
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                SettingsHeader(
                    title = "Uygulama",
                    subtitle = "CodeMind 1.0.0"
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    "CodeMind",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(
                        horizontal = 8.dp
                    )
                )

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsHeader(
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CodeMindLogo(
                size = 52.dp
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .clickable {
                onClick()
            }
            .padding(17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = Cyan
        )

        Spacer(
            modifier = Modifier.width(15.dp)
        )

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

        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}

@Composable
fun AiModesDialog(
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0C1526)
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "AI Modları",
                        color = TextPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismiss
                    ) {

                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = TextSecondary
                        )
                    }
                }

                Text(
                    "İhtiyacına göre bir mod seç.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                AiModeItem(
                    title = "Standart Mod",
                    description = "Genel amaçlı sohbet ve kod desteği",
                    selected = true
                )

                AiModeItem(
                    title = "Kodlama Modu",
                    description = "Kod odaklı yardım",
                    selected = false
                )

                AiModeItem(
                    title = "Hata Ayıklama Modu",
                    description = "Hataları analiz et ve çözüm öner",
                    selected = false
                )

                AiModeItem(
                    title = "Yaratıcı Mod",
                    description = "Daha yaratıcı ve özgün cevaplar",
                    selected = false
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {

                    Text(
                        "Tamam",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AiModeItem(
    title: String,
    description: String,
    selected: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (selected) {
                    Color(0xFF18264B)
                } else {
                    Color(0xFF101A2B)
                }
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    if (selected) {
                        Purple
                    } else {
                        Color(0xFF26354D)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                description,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

private suspend fun sendMessage(
    message: String
): Pair<Boolean, String> {

    return withContext(Dispatchers.IO) {

        try {

            val body = JSONObject()
                .put("message", message)
                .put("code", "")
                .put("fileName", "index.html")
                .put("projectFiles", "")
                .put("funnyMode", false)
                .put("language", "TR")

            val requestBody = body
                .toString()
                .toRequestBody(
                    "application/json; charset=utf-8"
                        .toMediaType()
                )

            val request = Request.Builder()
                .url(AI_ENDPOINT)
                .post(requestBody)
                .addHeader(
                    "Accept",
                    "application/json"
                )
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .build()

            httpClient.newCall(request)
                .execute()
                .use { response ->

                    val raw = response.body
                        ?.string()
                        .orEmpty()

                    if (!response.isSuccessful) {

                        return@withContext Pair(
                            false,
                            "Sunucu HTTP ${response.code} döndürdü.\n$raw"
                        )
                    }

                    if (raw.isBlank()) {

                        return@withContext Pair(
                            false,
                            "Sunucudan boş cevap geldi."
                        )
                    }

                    val json = JSONObject(raw)

                    val success = json.optBoolean(
                        "success",
                        false
                    )

                    if (!success) {

                        val error = json.optString(
                            "error",
                            "Bilinmeyen sunucu hatası."
                        )

                        val details = json.optString(
                            "details",
                            ""
                        )

                        val finalError =
                            if (details.isNotBlank()) {
                                "$error\n$details"
                            } else {
                                error
                            }

                        return@withContext Pair(
                            false,
                            finalError
                        )
                    }

                    val answer = json.optString(
                        "response",
                        ""
                    )

                    if (answer.isBlank()) {

                        return@withContext Pair(
                            false,
                            "AI boş cevap döndürdü."
                        )
                    }

                    Pair(
                        true,
                        answer
                    )
                }

        } catch (error: Exception) {

            Pair(
                false,
                "Bağlantı hatası: ${
                    error.message ?: "Bilinmeyen hata"
                }"
            )
        }
    }
}
