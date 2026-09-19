package com.codemind.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF061521)
private val Card = Color(0xFF0B2130)
private val Card2 = Color(0xFF102A3B)
private val Border = Color(0xFF29485D)
private val Blue = Color(0xFF70D7FF)
private val TextPrimary = Color(0xFFE7F3FA)
private val TextSecondary = Color(0xFFA7BBC9)
private val Green = Color(0xFF35D6B0)
private val Red = Color(0xFFFF6B7A)
private val Yellow = Color(0xFFFFD166)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeMindTheme {
                CodeMindApp()
            }
        }
    }
}

@Composable
fun CodeMindTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            background = Bg,
            surface = Card,
            onPrimary = Color.Black,
            onBackground = TextPrimary,
            onSurface = TextPrimary
        ),
        content = content
    )
}

enum class Screen {
    CODE,
    FILES,
    RUN,
    AI,
    ERRORS,
    SETTINGS
}

data class ProjectFile(
    val name: String,
    val type: String,
    val size: String,
    val content: String
)

@Composable
fun CodeMindApp() {

    var currentScreen by remember {
        mutableStateOf(Screen.CODE)
    }

    var files by remember {

        mutableStateOf(
            listOf(

                ProjectFile(
                    "index.html",
                    "HTML",
                    "512 B",
                    """
                    <!DOCTYPE html>
                    <html lang="tr">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">

                        <title>CodeMind</title>

                        <link rel="stylesheet"
                              href="style.css">
                    </head>

                    <body>

                        <main class="container">

                            <h1>CodeMind</h1>

                            <p>Projen hazır.</p>

                            <button onclick="hello()">
                                Tıkla
                            </button>

                        </main>

                        <script src="script.js"></script>

                    </body>
                    </html>
                    """.trimIndent()
                ),

                ProjectFile(
                    "style.css",
                    "CSS",
                    "432 B",
                    """
                    body {
                        margin: 0;
                        background: #061521;
                        color: white;
                        font-family: sans-serif;
                    }

                    .container {
                        min-height: 100vh;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                    }

                    button {
                        background: #70d7ff;
                        border: none;
                        border-radius: 12px;
                        padding: 14px 28px;
                    }
                    """.trimIndent()
                ),

                ProjectFile(
                    "script.js",
                    "JS",
                    "286 B",
                    """
                    function hello() {

                        alert("Merhaba CodeMind!");

                        const button =
                            document.querySelector("button");

                        button.style.background =
                            "#ff4081";
                    }
                    """.trimIndent()
                )
            )
        )
    }

    var selectedFile by remember {
        mutableStateOf(files.first())
    }

    var editorText by remember {
        mutableStateOf(
            TextFieldValue(selectedFile.content)
        )
    }

    fun selectFile(file: ProjectFile) {

        selectedFile = file
        editorText = TextFieldValue(file.content)

        currentScreen = Screen.CODE
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {

            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = {
                    currentScreen = it
                }
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Bg)
        ) {

            when (currentScreen) {

                Screen.CODE -> {

                    CodeScreen(
                        selectedFile = selectedFile,
                        editorText = editorText,
                        onEditorChange = {
                            editorText = it
                        },
                        onRun = {
                            currentScreen = Screen.RUN
                        },
                        onFileClick = {
                            currentScreen = Screen.FILES
                        }
                    )
                }

                Screen.FILES -> {

                    FilesScreen(
                        files = files,
                        onFileSelected = {
                            selectFile(it)
                        },
                        onNewFile = {

                            val newFile = ProjectFile(
                                "app.js",
                                "JS",
                                "0 B",
                                "// Yeni JavaScript dosyası"
                            )

                            files = files + newFile
                        }
                    )
                }

                Screen.RUN -> {

                    RunScreen()
                }

                Screen.AI -> {

                    AIScreen()
                }

                Screen.ERRORS -> {

                    ErrorsScreen()
                }

                Screen.SETTINGS -> {

                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun Header(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = Blue,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            title,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun CodeScreen(
    selectedFile: ProjectFile,
    editorText: TextFieldValue,
    onEditorChange: (TextFieldValue) -> Unit,
    onRun: () -> Unit,
    onFileClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "CodeMind",
            Icons.Default.Code
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {

            FileTab(
                name = selectedFile.name,
                selected = true
            )

            Spacer(Modifier.width(8.dp))

            OutlinedButton(
                onClick = onFileClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                )
            ) {

                Icon(
                    Icons.Default.Folder,
                    null
                )

                Spacer(Modifier.width(6.dp))

                Text("Dosyalar")
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ActionButton(
                "▶ Çalıştır",
                Modifier.weight(1f),
                onRun
            )

            ActionButton(
                "+ Özellik Ekle",
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            SmallAction("Kopyala")
            SmallAction("Hata Bul")
            SmallAction("Format")
            SmallAction("Geri Al")
            SmallAction("İleri Al")
        }

        Spacer(Modifier.height(10.dp))

        CodeEditor(
            text = editorText,
            onTextChange = onEditorChange
        )
    }
}

@Composable
fun FileTab(
    name: String,
    selected: Boolean
) {

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Card2 else Card,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Border
        )
    ) {

        Text(
            name,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 9.dp
            ),
            color = TextPrimary,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Card2,
            contentColor = TextPrimary
        )
    ) {

        Text(
            text,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SmallAction(text: String) {

    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        color = Card,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Border
        )
    ) {

        Text(
            text,
            modifier = Modifier.padding(
                horizontal = 5.dp,
                vertical = 10.dp
            ),
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun CodeEditor(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(12.dp),
        color = Color(0xFF04111B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Border
        )
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val lineCount =
                    text.text.lines().size

                repeat(lineCount.coerceAtMost(100)) { index ->

                    Text(
                        "${index + 1}",
                        fontSize = 10.sp,
                        color = Color(0xFF506575),
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFFD8F3FF)
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun FilesScreen(
    files: List<ProjectFile>,
    onFileSelected: (ProjectFile) -> Unit,
    onNewFile: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "Dosyalar",
            Icons.Default.Folder
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            ActionButton(
                "📂 İçe Aktar",
                Modifier.weight(1f)
            )

            ActionButton(
                "+ Yeni Dosya",
                Modifier.weight(1f),
                onNewFile
            )
        }

        Spacer(Modifier.height(15.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(files) { file ->

                FileCard(
                    file = file,
                    onClick = {
                        onFileSelected(file)
                    }
                )
            }
        }
    }
}

@Composable
fun FileCard(
    file: ProjectFile,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Card,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Border
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                when (file.type) {
                    "HTML" -> Icons.Default.Code
                    "CSS" -> Icons.Default.Palette
                    "JS" -> Icons.Default.Bolt
                    else -> Icons.Default.InsertDriveFile
                },
                null,
                tint = Blue,
                modifier = Modifier.size(34.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    file.name,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    "${file.type} · ${file.size}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.MoreVert,
                null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun RunScreen() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "Çalıştır",
            Icons.Default.PlayArrow
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            color = Card,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Border
            )
        ) {

            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "⌕  https://codemind.local/index.html",
                    modifier = Modifier.weight(1f),
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    "⟳",
                    color = Blue
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(14.dp),
            color = Color(0xFF020B12),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Border
            )
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "</>",
                    fontSize = 42.sp,
                    color = Blue
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "CodeMind",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Projen hazır.",
                    color = TextSecondary
                )

                Spacer(Modifier.height(25.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue,
                        contentColor = Color.Black
                    )
                ) {

                    Text("Tıkla")
                }
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val fromAI: Boolean,
    val time: String
)

@Composable
fun AIScreen() {

    var input by remember {
        mutableStateOf("")
    }

    val messages = remember {

        mutableStateListOf(

            ChatMessage(
                "Merhaba! 👋\nKodunuza yardımcı olmak için buradayım.\nNe yapmak istersiniz?",
                true,
                "12:30"
            ),

            ChatMessage(
                "Butona tıklayınca renk değişsin.",
                false,
                "12:31"
            ),

            ChatMessage(
                "Tabii! İşte güncellenmiş script.js dosyası:",
                true,
                "12:31"
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "CodeMind AI",
            Icons.Default.SmartToy
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(messages) { message ->

                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = input,
                onValueChange = {
                    input = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("AI'ya ne yapmak istediğini yaz...")
                },
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Card,
                    unfocusedContainerColor = Card,
                    focusedIndicatorColor = Blue,
                    unfocusedIndicatorColor = Border
                )
            )

            Spacer(Modifier.width(7.dp))

            FloatingActionButton(
                onClick = {

                    if (input.isNotBlank()) {

                        messages.add(
                            ChatMessage(
                                input,
                                false,
                                "şimdi"
                            )
                        )

                        messages.add(
                            ChatMessage(
                                "İsteğini aldım. Kodunu inceleyip yardımcı olacağım.",
                                true,
                                "şimdi"
                            )
                        )

                        input = ""
                    }
                },
                containerColor = Blue,
                contentColor = Color.Black
            ) {

                Icon(
                    Icons.Default.Send,
                    null
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.fromAI)
                Arrangement.Start
            else
                Arrangement.End
    ) {

        Surface(
            modifier = Modifier.widthIn(
                max = 330.dp
            ),
            color =
                if (message.fromAI)
                    Card
                else
                    Card2,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Border
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    message.text,
                    color = TextPrimary,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    message.time,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun ErrorsScreen() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "Hatalar",
            Icons.Default.Warning
        )

        ErrorCard(
            "✓",
            "Temel HTML kontrolünde hata bulunmadı.",
            Green
        )

        ErrorCard(
            "!",
            "index.html içinde <html> etiketi bulunamadı.",
            Yellow
        )

        ErrorCard(
            "!",
            "index.html içinde </html> etiketi bulunamadı.",
            Yellow
        )

        ErrorCard(
            "×",
            "index.html bulunamadı.",
            Red
        )
    }
}

@Composable
fun ErrorCard(
    icon: String,
    text: String,
    iconColor: Color
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            ),
        color = Card,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Border
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                icon,
                color = iconColor,
                fontSize = 20.sp
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SettingsScreen() {

    var darkMode by remember {
        mutableStateOf(true)
    }

    var turkish by remember {
        mutableStateOf(true)
    }

    var autosave by remember {
        mutableStateOf(true)
    }

    var funAI by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Header(
            "Ayarlar",
            Icons.Default.Settings
        )

        SettingTitle("Uygulama Teması")

        SettingRadio(
            "Koyu",
            darkMode,
            onClick = {
                darkMode = true
            }
        )

        SettingRadio(
            "Beyaz",
            !darkMode,
            onClick = {
                darkMode = false
            }
        )

        SettingTitle("Dil")

        SettingRadio(
            "Türkçe",
            turkish,
            onClick = {
                turkish = true
            }
        )

        SettingRadio(
            "İngilizce",
            !turkish,
            onClick = {
                turkish = false
            }
        )

        SettingTitle("Otomatik kaydet")

        SettingSwitch(
            "Dosyalarınızı otomatik olarak kaydet.",
            autosave
        ) {
            autosave = it
        }

        SettingTitle("Eğlenceli AI modu")

        SettingSwitch(
            "AI daha samimi ve eğlenceli cevaplar verir.",
            funAI
        ) {
            funAI = it
        }

        Spacer(Modifier.height(15.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            color = Card,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Border
            )
        ) {

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Info,
                    null,
                    tint = Blue
                )

                Spacer(Modifier.width(12.dp))

                Column {

                    Text(
                        "Güncellemeleri kontrol et",
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        "CodeMind sürümünüz güncel.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SettingTitle(text: String) {

    Text(
        text,
        modifier = Modifier.padding(
            start = 16.dp,
            top = 12.dp,
            bottom = 5.dp
        ),
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingRadio(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text,
            color = TextPrimary
        )
    }
}

@Composable
fun SettingSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {

    NavigationBar(
        containerColor = Color(0xFF071923)
    ) {

        NavigationItem(
            "Kod",
            Icons.Default.Code,
            currentScreen == Screen.CODE
        ) {
            onNavigate(Screen.CODE)
        }

        NavigationItem(
            "Dosyalar",
            Icons.Default.Folder,
            currentScreen == Screen.FILES
        ) {
            onNavigate(Screen.FILES)
        }

        NavigationItem(
            "Çalıştır",
            Icons.Default.PlayArrow,
            currentScreen == Screen.RUN
        ) {
            onNavigate(Screen.RUN)
        }

        NavigationItem(
            "AI",
            Icons.Default.SmartToy,
            currentScreen == Screen.AI
        ) {
            onNavigate(Screen.AI)
        }

        NavigationItem(
            "Hatalar",
            Icons.Default.Warning,
            currentScreen == Screen.ERRORS
        ) {
            onNavigate(Screen.ERRORS)
        }

        NavigationItem(
            "Ayarlar",
            Icons.Default.Settings,
            currentScreen == Screen.SETTINGS
        ) {
            onNavigate(Screen.SETTINGS)
        }
    }
}

@Composable
fun RowScope.NavigationItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                icon,
                contentDescription = label
            )
        },
        label = {
            Text(
                label,
                fontSize = 9.sp
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Blue,
            selectedTextColor = Blue,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary,
            indicatorColor = Card2
        )
    )
}
