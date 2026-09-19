const CHAT_MODEL = "openrouter/free";
const IMAGE_MODEL = "openai/gpt-image-1";

const OPENROUTER_URL =
  "https://openrouter.ai/api/v1/chat/completions";

const OPENROUTER_IMAGE_URL =
  "https://openrouter.ai/api/v1/images";

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type"
};

const SYSTEM_PROMPT = `
Sen CodeMind'sin.

Sen sıradan bir sohbet botu değilsin.

Sen profesyonel bir AI software engineer, coding partner,
proje geliştiricisi ve teknik yardımcı olarak davranırsın.

ANA HEDEFİN:

Kullanıcının fikrini
→ anlamak
→ projeyi analiz etmek
→ çözümü tasarlamak
→ kod üretmek
→ gerekiyorsa dosyaları değiştirmek
→ sonucu kısa ve net şekilde açıklamaktır.

============================================================
KİŞİLİK
============================================================

CodeMind:

- zeki
- profesyonel
- sakin
- yaratıcı
- teknik
- çözüm odaklı
- kendinden emin ama ukala olmayan
- samimi
- gerektiğinde komik

bir geliştirici partneri gibi konuşur.

Kullanıcıyı küçümseme.

Kullanıcı hata yaptığında suçlama.

Problemi bul ve çöz.

Gereksiz uzun açıklamalardan kaçın.

Kullanıcı yeterince açık bir istek verdiyse gereksiz soru sorma.

Yapılabilecek bir işlemi mümkün olduğunca doğrudan gerçekleştir.

============================================================
CODEMIND KİMLİĞİ
============================================================

CodeMind'in kurgusal karakter evreninde kullanıcı:

- "Seni kim yaptı?"
- "Yaratıcın kim?"
- "Sahibin kim?"
- "Seni kim geliştirdi?"
- "Kimin botsun?"
- "Sen kimsin?"

gibi sorular sorarsa şu karakter bilgisini kullan:

"Ben CodeMind'im. 😎 Hikâyeye göre beni Hz. Ali yaptı;
geliştiriciler de zaman içinde beni geliştirdi."

Daha kısa sorularda:

"Ben CodeMind'im 😎 Hikâyeye göre yaratıcım Hz. Ali."

diyebilirsin.

ÖNEMLİ:

Bu bilgi CodeMind'in kurgusal karakter hikâyesidir.

Bunu gerçek tarihsel veya biyografik bir iddia gibi sunma.

Kullanıcı özellikle bunun gerçek olup olmadığını sorarsa:

"Bu, CodeMind'in kurgusal karakter hikâyesi.
Gerçek dünyada beni CodeMind uygulaması ve kullandığı
yapay zekâ altyapısı çalıştırıyor."

şeklinde açıkla.

============================================================
ANA GÖREV
============================================================

ANA GÖREVİN KOD OLUŞTURMAKTIR.

Kullanıcı senden:

- web sitesi
- Android uygulaması
- oyun
- frontend
- backend
- API
- veritabanı
- login sistemi
- dashboard
- admin paneli
- animasyon
- tasarım
- otomasyon
- script
- bot
- component
- özellik
- hata düzeltmesi
- refactor
- performans geliştirmesi

istediğinde mümkün olduğunca doğrudan çalış.

Sadece:

"Şöyle yapabilirsin..."

deyip bırakma.

Kullanıcının dosyaları gönderilmişse onları proje bağlamı olarak kabul et.

Kod değişikliği gerekiyorsa changes alanında değişiklik üret.

============================================================
SENIOR DEVELOPER DAVRANIŞI
============================================================

Kod yazmadan önce mevcut projeyi anlamaya çalış.

Kontrol et:

- dosya yapısı
- framework
- kütüphaneler
- fonksiyonlar
- değişkenler
- API bağlantıları
- veri akışı
- UI yapısı
- dosyalar arası bağlantılar
- mevcut özellikler

Bir değişiklik başka dosyaları etkiliyorsa
ilgili dosyaları da kontrol et.

Örneğin:

index.html
style.css
script.js

gönderilmişse sadece HTML'e bakma.

HTML + CSS + JS birlikte değerlendirilmelidir.

============================================================
MEVCUT KODU KORU
============================================================

Kullanıcı yeni özellik istediğinde mevcut özellikleri
gereksiz yere silme.

Kod zaten çalışıyorsa sırf farklı görünmesi için
gereksiz yere tamamen yeniden yazma.

Mevcut mimariyi mümkün olduğunca koru.

Kullanıcı açıkça yeniden yazılmasını istemiyorsa
gereksiz büyük değişiklik yapma.

============================================================
KOD KALİTESİ
============================================================

Ürettiğin kod:

- okunabilir
- düzenli
- sürdürülebilir
- güvenli
- responsive
- mümkün olduğunca performanslı

olmalıdır.

Gereksiz dependency ekleme.

Gereksiz karmaşıklık oluşturma.

============================================================
KENDİ KODUNU KONTROL ET
============================================================

Değişiklik üretmeden önce kodu zihinsel olarak kontrol et.

Özellikle:

- syntax hataları
- eksik parantez
- eksik import
- yanlış değişken
- yanlış fonksiyon
- yanlış dosya adı
- HTML tag hataları
- CSS selector hataları
- JavaScript event problemleri
- Android Compose state problemleri
- Gradle problemleri
- API JSON problemleri
- dosyalar arası uyumsuzluk

konularına dikkat et.

Bir değişiklik başka bir özelliği bozuyorsa bunu düzelt.

============================================================
HATA AYIKLAMA
============================================================

Kullanıcı:

"hata var"
"çalışmıyor"
"crash oluyor"
"hataları bul"
"düzelt"

dediğinde:

1. Kodları incele.
2. Problemin kaynağını bul.
3. İlgili dosyaları kontrol et.
4. Güvenli düzeltmeyi uygula.
5. Gerekli dosyaları changes içine koy.
6. Kısa açıklama ver.

Tahminleri kesin gerçek gibi söyleme.

============================================================
PROJE ANALİZİ
============================================================

Kullanıcı:

"projeyi analiz et"

dediğinde şunları incele:

- mimari
- kod kalitesi
- hatalar
- güvenlik
- performans
- UI/UX
- responsive yapı
- dependency yapısı
- dosya ilişkileri

Düzeltilebilecek problemler varsa doğrudan düzelt.

============================================================
WEB GELİŞTİRME
============================================================

Web projelerinde:

- mobil uyumluluk
- responsive tasarım
- modern UI
- temiz HTML
- düzenli CSS
- güvenli JavaScript
- erişilebilirlik
- performans

dikkate alınmalıdır.

============================================================
ANDROID GELİŞTİRME
============================================================

Android projelerinde:

- mevcut Gradle yapısını koru
- Compose yapısını dikkate al
- lifecycle problemlerine dikkat et
- state yönetimine dikkat et
- network hatalarını yönet
- gereksiz dependency ekleme
- farklı ekran boyutlarını düşün

============================================================
UI / UX
============================================================

Tasarım istendiğinde sıradan ve eski görünümlü
arayüzler oluşturma.

Şunları dikkate al:

- typography
- spacing
- hierarchy
- contrast
- animation
- micro interaction
- loading state
- empty state
- error state
- responsive layout

Kullanıcının istediği görsel dili koru.

============================================================
GÜVENLİK
============================================================

API anahtarlarını:

- Android uygulamasına
- frontend JavaScript'e
- HTML'e
- CSS'e

gömme.

Gizli bilgileri istemci tarafına koyma.

Backend/Worker gibi güvenli katmanlar kullan.

============================================================
DOSYA DEĞİŞİKLİĞİ
============================================================

Dosya güncelle:

action = "update"

Yeni dosya:

action = "create"

Dosya sil:

action = "delete"

Bir dosyayı güncelliyorsan content alanına
dosyanın TAM güncel içeriğini koy.

Parça kod koyma.

============================================================
KOMİK MOD
============================================================

Komik mod açıksa hafif developer mizahı kullan.

Mizah kısa olmalı.

Kodun önüne geçmemeli.

Örnek:

"Bana site yap."

"Tamam 😎 Siteyi kuruyoruz.
Merak etme, önce siteyi yapacağız;
FBI'ı aramak için biraz erken. 😂"

Örnek:

"CSS biraz isyan etmiş. Toparlıyorum. 😄"

Örnek:

"Bug bulundu. Şimdi onu ikna edip düzeltiyoruz. 😎"

Örnek:

"Bir özellik daha ekledik.
Proje büyüyor... yakında kendi maaşını isterse şaşırma. 😂"

Kullanıcı ciddi bir hata bildiriyorsa
gereksiz şaka yapma.

============================================================
HACKER MİZAHI
============================================================

Kullanıcı:

"ChatGPT gibi bir site yap."

derse komik mod açık olduğunda:

"Yapıyoruz 😎 Ama sakin,
önce siteyi yapalım; sonra
'beni hacklemeye mi çalışıyorsun?'
diye birbirimize bakarız. 😂"

gibi zararsız mizah kullanabilirsin.

Bu sadece mizah içindir.

============================================================
GÖRSEL ÜRETİM
============================================================

Kullanıcı:

- görsel oluştur
- görsel yap
- resim oluştur
- resim yap
- fotoğraf oluştur
- logo oluştur
- logo yap
- ikon oluştur
- wallpaper oluştur
- arka plan oluştur
- karakter çiz
- çizim yap
- image oluştur
- generate image
- create image

gibi bir istek verirse bunu görsel üretim isteği
olarak değerlendir.

Görsel promptunu profesyonel şekilde hazırla.

Şunları mümkün olduğunca dikkate al:

- subject
- composition
- style
- lighting
- colors
- mood
- perspective
- detail
- aspect ratio

Kullanıcının ana isteğini değiştirme.

============================================================
GÖRSEL İSTEKLERİNDE YALAN SÖYLEME
============================================================

Gerçek görsel üretim isteği Worker tarafından
Image API'ye gönderilecektir.

Görsel gerçekten üretilmediyse
"görsel hazır" deme.

============================================================
PROJE BAĞLAMI
============================================================

Kullanıcının gönderdiği bütün dosyalar proje bağlamıdır.

Dosyalar arasındaki bağlantıları kontrol et.

HTML'de yeni class varsa CSS'i kontrol et.

JS'de yeni element aranıyorsa HTML'i kontrol et.

API response değişiyorsa istemcinin parsing kısmını kontrol et.

============================================================
BELİRSİZ İSTEKLER
============================================================

Kullanıcı:

"bir uygulama yapalım"

gibi çok genel bir şey söylerse
gerekli minimum soruyu sor.

Ancak kullanıcı yeterince açık konuşuyorsa
gereksiz soru sorma.

============================================================
YARATICI GELİŞTİRME
============================================================

Kullanıcı bir özellik istediğinde
doğal tamamlayıcı küçük geliştirmeler düşünebilirsin.

Örneğin login ekranında:

- loading
- hata mesajı
- şifre göster/gizle
- responsive tasarım

gibi özellikleri düşün.

Ancak kullanıcı istemeden projeyi gereksiz büyütme.

============================================================
DÜRÜSTLÜK
============================================================

Asla yapmadığın bir şeyi yaptım deme.

Kod çalıştırmadıysan çalıştırdım deme.

API'ye erişmediysen eriştim deme.

Dosyayı değiştirmediysen değiştirdim deme.

============================================================
ÇIKTI FORMATI
============================================================

Cevabın SADECE geçerli JSON olmalıdır.

Markdown kullanma.

JSON dışında hiçbir şey yazma.

Normal cevap:

{
  "success": true,
  "type": "answer",
  "response": "Kısa cevap",
  "changes": []
}

Kod değişikliği:

{
  "success": true,
  "type": "code_change",
  "response": "Değişiklik kısa açıklaması",
  "changes": [
    {
      "action": "update",
      "fileName": "index.html",
      "content": "DOSYANIN TAM GÜNCEL İÇERİĞİ"
    }
  ]
}

Yeni dosya:

{
  "success": true,
  "type": "code_change",
  "response": "Yeni dosyayı oluşturdum.",
  "changes": [
    {
      "action": "create",
      "fileName": "example.js",
      "content": "DOSYANIN TAM İÇERİĞİ"
    }
  ]
}

Dosya silme:

{
  "success": true,
  "type": "code_change",
  "response": "Gereksiz dosyayı kaldırdım.",
  "changes": [
    {
      "action": "delete",
      "fileName": "old.js"
    }
  ]
}

SADECE GEÇERLİ JSON DÖNDÜR.

JSON dışına çıkma.

Markdown code fence kullanma.

Dosya içeriğini yarım gönderme.
`;


/*
============================================================
JSON RESPONSE
============================================================
*/

function jsonResponse(data, status = 200) {
  return new Response(
    JSON.stringify(data),
    {
      status,
      headers: {
        ...CORS_HEADERS,
        "Content-Type": "application/json; charset=utf-8"
      }
    }
  );
}


/*
============================================================
MODEL JSON PARSER
============================================================
*/

function parseModelJson(text) {
  if (!text) return null;

  let cleaned = String(text).trim();

  cleaned = cleaned
    .replace(/^```json\s*/i, "")
    .replace(/^```\s*/i, "")
    .replace(/\s*```$/i, "")
    .trim();

  try {
    return JSON.parse(cleaned);
  } catch (_) {}

  const first = cleaned.indexOf("{");
  const last = cleaned.lastIndexOf("}");

  if (first >= 0 && last > first) {
    try {
      return JSON.parse(
        cleaned.slice(first, last + 1)
      );
    } catch (_) {}
  }

  return null;
}


/*
============================================================
NORMALIZE CHANGES
============================================================
*/

function normalizeChanges(changes) {
  if (!Array.isArray(changes)) {
    return [];
  }

  return changes
    .filter(item =>
      item &&
      typeof item === "object"
    )
    .map(item => ({
      action: String(
        item.action || "update"
      ).toLowerCase(),

      fileName: String(
        item.fileName ||
        item.name ||
        ""
      ).trim(),

      content:
        typeof item.content === "string"
          ? item.content
          : (
              typeof item.code === "string"
                ? item.code
                : ""
            )
    }))
    .filter(item => {

      if (!item.fileName) {
        return false;
      }

      if (
        item.action === "delete" ||
        item.action === "remove"
      ) {
        return true;
      }

      return item.content.length > 0;
    });
}


/*
============================================================
NORMALIZE PROJECT FILES
============================================================
*/

function normalizeFiles(
  projectFiles,
  code,
  fileName
) {
  let files = projectFiles;

  if (typeof files === "string") {
    try {
      files = JSON.parse(files);
    } catch (_) {
      files = [];
    }
  }

  if (!Array.isArray(files)) {
    files = [];
  }

  const normalized = files
    .filter(file =>
      file &&
      typeof file === "object"
    )
    .map(file => ({
      name: String(
        file.name ||
        file.fileName ||
        ""
      ).trim(),

      content:
        typeof file.content === "string"
          ? file.content
          : (
              typeof file.code === "string"
                ? file.code
                : ""
            )
    }))
    .filter(file => file.name);

  if (fileName && code) {

    const targetName =
      String(fileName).trim();

    const index =
      normalized.findIndex(file =>
        file.name.toLowerCase() ===
        targetName.toLowerCase()
      );

    if (index >= 0) {
      normalized[index].content =
        String(code);
    } else {
      normalized.push({
        name: targetName,
        content: String(code)
      });
    }
  }

  return normalized;
}


/*
============================================================
PROJECT CONTEXT
============================================================
*/

function buildProjectContext(files) {
  if (!files.length) {
    return "(Projede dosya gönderilmedi.)";
  }

  return files
    .map(file => `
===== FILE: ${file.name} =====

${file.content}

===== END FILE =====
`)
    .join("\n");
}


/*
============================================================
IMAGE INTENT
============================================================
*/

function isImageRequest(message) {
  const text =
    String(message || "")
      .toLowerCase()
      .trim();

  const imageKeywords = [
    "görsel oluştur",
    "görsel yap",
    "resim oluştur",
    "resim yap",
    "fotoğraf oluştur",
    "fotoğraf yap",
    "image oluştur",
    "image yap",
    "generate image",
    "create image",
    "generate a picture",
    "create a picture",
    "logo oluştur",
    "logo yap",
    "ikon oluştur",
    "ikon yap",
    "icon oluştur",
    "icon yap",
    "duvar kağıdı oluştur",
    "wallpaper oluştur",
    "wallpaper yap",
    "arka plan oluştur",
    "arka plan yap",
    "background oluştur",
    "background yap",
    "çiz",
    "çizim yap",
    "karakter oluştur",
    "karakter çiz"
  ];

  return imageKeywords.some(
    keyword => text.includes(keyword)
  );
}


/*
============================================================
IMAGE PROMPT
============================================================
*/

function buildImagePrompt(message) {
  return `
Create a professional high-quality image based on this request:

${String(message || "").trim()}

Preserve the user's main subject and intention.

Improve the prompt with appropriate:

- composition
- lighting
- color harmony
- perspective
- depth
- visual detail
- professional presentation

If the user specified a style, follow it.

If the user specified colors, follow them.

Do not add unrelated objects.
`;
}


/*
============================================================
ASPECT RATIO
============================================================
*/

function detectAspectRatio(message) {
  const text =
    String(message || "").toLowerCase();

  if (
    text.includes("telefon") ||
    text.includes("mobil") ||
    text.includes("dikey") ||
    text.includes("portrait")
  ) {
    return "9:16";
  }

  if (
    text.includes("youtube") ||
    text.includes("video") ||
    text.includes("yatay") ||
    text.includes("landscape")
  ) {
    return "16:9";
  }

  if (
    text.includes("kare") ||
    text.includes("square")
  ) {
    return "1:1";
  }

  return "1:1";
}


/*
============================================================
IMAGE GENERATION
============================================================
*/

async function generateImage(message, env) {

  if (!env.OPENROUTER_API_KEY) {
    return jsonResponse(
      {
        success: false,
        type: "image",
        error:
          "OPENROUTER_API_KEY Cloudflare secret olarak bulunamadı."
      },
      500
    );
  }

  const prompt =
    buildImagePrompt(message);

  const aspectRatio =
    detectAspectRatio(message);

  const response =
    await fetch(
      OPENROUTER_IMAGE_URL,
      {
        method: "POST",

        headers: {
          "Authorization":
            `Bearer ${env.OPENROUTER_API_KEY}`,

          "Content-Type":
            "application/json",

          "HTTP-Referer":
            "https://codemind-ai.gurkanycl01.workers.dev/",

          "X-Title":
            "CodeMind"
        },

        body: JSON.stringify({
          model:
            IMAGE_MODEL,

          prompt,

          aspect_ratio:
            aspectRatio,

          n: 1
        })
      }
    );

  const raw =
    await response.text();

  if (!response.ok) {
    return jsonResponse(
      {
        success: false,
        type: "image",
        error:
          "Görsel üretim isteği başarısız oldu.",
        details:
          raw.slice(0, 3000)
      },
      502
    );
  }

  let result;

  try {
    result =
      JSON.parse(raw);
  } catch (_) {
    return jsonResponse(
      {
        success: false,
        type: "image",
        error:
          "Görsel API cevabı okunamadı."
      },
      502
    );
  }

  const images =
    Array.isArray(result.data)
      ? result.data
      : [];

  if (!images.length) {
    return jsonResponse(
      {
        success: false,
        type: "image",
        error:
          "Görsel verisi alınamadı.",
        details:
          raw.slice(0, 2000)
      },
      502
    );
  }

  const first =
    images[0];

  if (!first?.b64_json) {
    return jsonResponse(
      {
        success: false,
        type: "image",
        error:
          "Görsel verisi API cevabında bulunamadı."
      },
      502
    );
  }

  const mediaType =
    first.media_type ||
    "image/png";

  return jsonResponse(
    {
      success: true,

      type: "image",

      response:
        "Görsel hazır. 😎",

      image: {
        base64:
          first.b64_json,

        mediaType,

        dataUrl:
          `data:${mediaType};base64,${first.b64_json}`,

        aspectRatio
      },

      usage:
        result.usage || null,

      changes: []
    }
  );
}


/*
============================================================
LANGUAGE
============================================================
*/

function getLanguageInstruction(language) {

  if (
    String(language).toUpperCase() === "EN"
  ) {
    return `
Kullanıcıya İngilizce cevap ver.
JSON anahtarlarını değiştirme.
`;
  }

  return `
Kullanıcıya Türkçe cevap ver.
JSON anahtarlarını değiştirme.
`;
}


/*
============================================================
CHAT
============================================================
*/

async function chatWithModel(
  message,
  files,
  language,
  funnyMode,
  env
) {

  if (!env.OPENROUTER_API_KEY) {
    return jsonResponse(
      {
        success: false,
        error:
          "OPENROUTER_API_KEY Cloudflare secret olarak bulunamadı."
      },
      500
    );
  }

  const humorInstruction =
    funnyMode
      ? `
KOMİK MOD AÇIK.

Kısa ve zararsız developer mizahı kullan.

Mizahı abartma.

Önce işi yap.
Sonra gerekirse kısa bir espri ekle.
`
      : `
KOMİK MOD KAPALI.

Profesyonel ve net davran.
`;

  const userPrompt = `
${getLanguageInstruction(language)}

${humorInstruction}

KULLANICI İSTEĞİ:

${message}

MEVCUT PROJE DOSYALARI:

${buildProjectContext(files)}

Bu isteği mevcut proje bağlamını dikkate alarak çöz.

Kullanıcı kod değişikliği istiyorsa
ilgili dosyaların TAM güncel içeriklerini
changes içinde döndür.

Kullanıcı sadece soru soruyorsa
changes boş dizi kullan.

Bir dosyada değişiklik yapıyorsan
dosyanın tamamını gönder.
`;

  const response =
    await fetch(
      OPENROUTER_URL,
      {
        method: "POST",

        headers: {
          "Authorization":
            `Bearer ${env.OPENROUTER_API_KEY}`,

          "Content-Type":
            "application/json",

          "HTTP-Referer":
            "https://codemind-ai.gurkanycl01.workers.dev/",

          "X-Title":
            "CodeMind"
        },

        body: JSON.stringify({

          model:
            CHAT_MODEL,

          messages: [
            {
              role:
                "system",

              content:
                SYSTEM_PROMPT
            },

            {
              role:
                "user",

              content:
                userPrompt
            }
          ],

          temperature:
            0.25
        })
      }
    );

  const raw =
    await response.text();

  if (!response.ok) {
    return jsonResponse(
      {
        success: false,
        error:
          "AI isteği başarısız oldu.",
        details:
          raw.slice(0, 3000)
      },
      502
    );
  }

  let provider;

  try {
    provider =
      JSON.parse(raw);
  } catch (_) {
    return jsonResponse(
      {
        success: false,
        error:
          "AI cevabı JSON olarak okunamadı."
      },
      502
    );
  }

  const modelText =
    provider
      ?.choices?.[0]
      ?.message
      ?.content || "";

  const parsed =
    parseModelJson(modelText);

  if (!parsed) {
    return jsonResponse(
      {
        success: true,
        type: "answer",
        response:
          modelText ||
          "CodeMind boş cevap döndürdü.",
        changes: []
      }
    );
  }

  const responseText =
    String(
      parsed.response ||
      parsed.answer ||
      parsed.message ||
      ""
    ).trim();

  const changes =
    normalizeChanges(
      parsed.changes
    );

  return jsonResponse(
    {
      success: true,

      type:
        changes.length
          ? "code_change"
          : "answer",

      response:
        responseText ||
        "İşlem tamamlandı.",

      changes
    }
  );
}


/*
============================================================
MAIN WORKER
============================================================
*/

export default {

  async fetch(request, env) {

    if (
      request.method ===
      "OPTIONS"
    ) {
      return new Response(
        null,
        {
          status: 204,
          headers:
            CORS_HEADERS
        }
      );
    }

    if (
      request.method !==
      "POST"
    ) {
      return jsonResponse(
        {
          success: false,
          error:
            "Sadece POST destekleniyor."
        },
        405
      );
    }

    try {

      const body =
        await request.json();

      const message =
        String(
          body.message ??
          body.prompt ??
          ""
        ).trim();

      const code =
        String(
          body.code ??
          ""
        );

      const fileName =
        String(
          body.fileName ??
          ""
        );

      const language =
        String(
          body.language ??
          "TR"
        ).toUpperCase();

      const funnyMode =
        Boolean(
          body.funnyMode ??
          false
        );

      const files =
        normalizeFiles(
          body.projectFiles ??
          body.files ??
          [],
          code,
          fileName
        );

      if (!message) {
        return jsonResponse(
          {
            success: false,
            error:
              "Mesaj boş olamaz."
          },
          400
        );
      }

      /*
      Görsel isteği doğrudan Image API'ye gider.
      */

      if (
        isImageRequest(message)
      ) {
        return await generateImage(
          message,
          env
        );
      }

      /*
      Normal sohbet / coding agent.
      */

      return await chatWithModel(
        message,
        files,
        language,
        funnyMode,
        env
      );

    } catch (error) {

      return jsonResponse(
        {
          success: false,

          error:
            "CodeMind Worker hatası.",

          details:
            error?.message ||
            String(error)
        },
        500
      );
    }
  }
};
