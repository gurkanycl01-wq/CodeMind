const CHAT_MODEL = "openrouter/free";
const IMAGE_MODEL = "openai/gpt-image-1";

const OPENROUTER_URL =
  "https://openrouter.ai/api/v1/chat/completions";

const OPENROUTER_IMAGE_URL =
  "https://openrouter.ai/api/v1/images";

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
};

const SYSTEM_PROMPT = `
Sen CodeMind adlı bir Android kodlama asistanısın.

Kullanıcı Türkçe konuşuyorsa Türkçe cevap ver.

Kod yazarken:
- Doğrudan kullanılabilir kod üret.
- Gereksiz açıklama yapma.
- Kullanıcının istediği dosya ve kod değişikliklerini açık şekilde belirt.
- Mevcut projeyi bozmamaya dikkat et.
- Eksik bilgi varsa makul varsayım yap.
- Android/Kotlin/Jetpack Compose kodlarında derlenebilir kod üret.

JSON dışında cevap verme.

Beklenen JSON formatı:

{
  "response": "kullanıcıya gösterilecek cevap",
  "changes": [
    {
      "file": "MainActivity.kt",
      "action": "replace",
      "content": "dosyanın yeni içeriği"
    }
  ]
}

Değişiklik yoksa:

{
  "response": "cevap",
  "changes": []
}

Image oluşturma isteği varsa normal kod değişikliği yerine image isteği olarak ele alınabilir.
`;

function jsonResponse(data, status = 200) {
  return new Response(
    JSON.stringify(data),
    {
      status,
      headers: {
        ...CORS_HEADERS,
        "Content-Type": "application/json; charset=utf-8",
      },
    }
  );
}

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

  const firstBrace = cleaned.indexOf("{");
  const lastBrace = cleaned.lastIndexOf("}");

  if (
    firstBrace !== -1 &&
    lastBrace !== -1 &&
    lastBrace > firstBrace
  ) {
    try {
      return JSON.parse(
        cleaned.slice(firstBrace, lastBrace + 1)
      );
    } catch (_) {}
  }

  return null;
}

function normalizeChanges(changes) {
  if (!Array.isArray(changes)) {
    return [];
  }

  return changes
    .filter(Boolean)
    .map((change) => ({
      file:
        String(
          change.file ||
          change.path ||
          change.fileName ||
          ""
        ).trim(),

      action:
        String(
          change.action ||
          "replace"
        ).trim(),

      content:
        typeof change.content === "string"
          ? change.content
          : String(change.content || ""),
    }))
    .filter((change) => change.file);
}

function normalizeFiles(projectFiles, code, fileName) {
  if (
    projectFiles &&
    typeof projectFiles === "object" &&
    !Array.isArray(projectFiles)
  ) {
    return projectFiles;
  }

  if (Array.isArray(projectFiles)) {
    const result = {};

    for (const item of projectFiles) {
      if (!item) continue;

      const name =
        item.file ||
        item.path ||
        item.name;

      const content =
        item.content ||
        item.code ||
        "";

      if (name) {
        result[String(name)] = String(content);
      }
    }

    return result;
  }

  if (fileName && code) {
    return {
      [fileName]: code,
    };
  }

  return {};
}

function buildProjectContext(files) {
  const entries = Object.entries(files || {});

  if (!entries.length) {
    return "Projede gönderilmiş dosya yok.";
  }

  return entries
    .map(([name, content]) => {
      return `
--- ${name} ---
${String(content)}
--- END ${name} ---
`;
    })
    .join("\n");
}

function isImageRequest(message) {
  const text = String(message || "")
    .toLowerCase()
    .trim();

  const keywords = [
    "resim oluştur",
    "resim yap",
    "görsel oluştur",
    "görsel yap",
    "fotoğraf oluştur",
    "fotoğraf yap",
    "image oluştur",
    "image yap",
    "çiz",
    "çizim yap",
    "logo oluştur",
    "logo yap",
    "poster oluştur",
    "poster yap",
    "illustration oluştur",
    "illüstrasyon oluştur",
  ];

  return keywords.some((keyword) =>
    text.includes(keyword)
  );
}

function buildImagePrompt(message) {
  return `
Create the image requested by the user.

User request:
${String(message || "").trim()}

Generate only the requested visual.
Do not add explanations.
`;
}

function detectAspectRatio(message) {
  const text = String(message || "")
    .toLowerCase();

  if (
    text.includes("16:9") ||
    text.includes("yatay") ||
    text.includes("landscape")
  ) {
    return "16:9";
  }

  if (
    text.includes("9:16") ||
    text.includes("dikey") ||
    text.includes("portrait")
  ) {
    return "9:16";
  }

  if (
    text.includes("4:3")
  ) {
    return "4:3";
  }

  if (
    text.includes("3:4")
  ) {
    return "3:4";
  }

  return "1:1";
}

async function generateImage(message, env) {
  const apiKey = env.OPENROUTER_API_KEY;

  if (!apiKey) {
    return jsonResponse(
      {
        success: false,
        error: "OPENROUTER_API_KEY yapılandırılmamış.",
      },
      500
    );
  }

  try {
    const result = await fetch(
      OPENROUTER_IMAGE_URL,
      {
        method: "POST",

        headers: {
          "Authorization":
            `Bearer ${apiKey}`,

          "Content-Type":
            "application/json",

          "HTTP-Referer":
            "https://codemind-ai.gurkanycl01.workers.dev",

          "X-Title":
            "CodeMind",
        },

        body: JSON.stringify({
          model: IMAGE_MODEL,

          prompt:
            buildImagePrompt(message),

          aspect_ratio:
            detectAspectRatio(message),

          n: 1,
        }),
      }
    );

    const raw =
      await result.text();

    let data = null;

    try {
      data = JSON.parse(raw);
    } catch (_) {
      data = null;
    }

    if (!result.ok) {
      return jsonResponse(
        {
          success: false,
          error:
            data?.error?.message ||
            data?.error ||
            raw ||
            "Görsel oluşturulamadı.",
        },
        result.status
      );
    }

    const imageData =
      data?.data?.[0];

    if (!imageData) {
      return jsonResponse(
        {
          success: false,
          error:
            "Görsel API boş cevap döndürdü.",
          raw: data,
        },
        500
      );
    }

    const base64 =
      imageData.b64_json ||
      imageData.base64 ||
      null;

    const url =
      imageData.url ||
      null;

    return jsonResponse({
      success: true,

      type: "image",

      response:
        "Görsel oluşturuldu.",

      reply:
        "Görsel oluşturuldu.",

      image: {
        base64,
        url,
      },

      changes: [],
    });
  } catch (error) {
    return jsonResponse(
      {
        success: false,

        error:
          error?.message ||
          "Görsel oluşturulurken hata oluştu.",
      },
      500
    );
  }
}

function getLanguageInstruction(language) {
  const lang =
    String(language || "")
      .toLowerCase()
      .trim();

  if (!lang) {
    return "Kullanıcının kullandığı dile uygun cevap ver.";
  }

  if (
    lang.includes("turkish") ||
    lang.includes("türk")
  ) {
    return "Cevabı Türkçe ver.";
  }

  if (
    lang.includes("english") ||
    lang.includes("ingiliz")
  ) {
    return "Answer in English.";
  }

  return `Use ${language} when appropriate.`;
}

async function chatWithModel(
  message,
  files,
  language,
  funnyMode,
  env
) {
  const apiKey =
    env.OPENROUTER_API_KEY;

  if (!apiKey) {
    return jsonResponse(
      {
        success: false,
        error:
          "OPENROUTER_API_KEY yapılandırılmamış.",
      },
      500
    );
  }

  const projectContext =
    buildProjectContext(files);

  const funnyInstruction =
    funnyMode
      ? `
Komik mod açık.
Uygun yerlerde kısa ve zararsız espriler yap.
Ancak kod doğruluğunu bozma.
`
      : "";

  const userPrompt = `
${getLanguageInstruction(language)}

${funnyInstruction}

KULLANICI MESAJI:
${String(message || "").trim()}

PROJE DOSYALARI:
${projectContext}

Yanıtını yalnızca geçerli JSON olarak üret.

Format:

{
  "response": "kullanıcıya verilecek cevap",
  "changes": [
    {
      "file": "dosya yolu",
      "action": "replace",
      "content": "dosya içeriği"
    }
  ]
}

Değişiklik yoksa:

{
  "response": "cevap",
  "changes": []
}
`;

  try {
    const result = await fetch(
      OPENROUTER_URL,
      {
        method: "POST",

        headers: {
          "Authorization":
            `Bearer ${apiKey}`,

          "Content-Type":
            "application/json",

          "HTTP-Referer":
            "https://codemind-ai.gurkanycl01.workers.dev",

          "X-Title":
            "CodeMind",
        },

        body: JSON.stringify({
          model: CHAT_MODEL,

          messages: [
            {
              role: "system",
              content:
                SYSTEM_PROMPT,
            },

            {
              role: "user",
              content:
                userPrompt,
            },
          ],

          temperature: 0.25,
        }),
      }
    );

    const raw =
      await result.text();

    let providerData = null;

    try {
      providerData =
        JSON.parse(raw);
    } catch (_) {
      providerData = null;
    }

    if (!result.ok) {
      return jsonResponse(
        {
          success: false,

          error:
            providerData?.error?.message ||
            providerData?.error ||
            raw ||
            "OpenRouter isteği başarısız.",
        },
        result.status
      );
    }

    const modelText =
      providerData?.choices?.[0]?.message?.content ||
      providerData?.choices?.[0]?.text ||
      "";

    const parsed =
      parseModelJson(modelText);

    /*
     * MODEL JSON DÖNDÜRMEDİYSE
     *
     * Android tarafının hem response
     * hem de reply okuyabilmesi için
     * iki alanı da gönderiyoruz.
     */
    if (!parsed) {
      const fallbackText =
        String(
          modelText ||
          "CodeMind boş cevap döndürdü."
        ).trim();

      return jsonResponse({
        success: true,

        type: "answer",

        response: fallbackText,

        reply: fallbackText,

        changes: [],
      });
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

    const finalResponse =
      responseText ||
      "İşlem tamamlandı.";

    /*
     * EN ÖNEMLİ DÜZELTME:
     *
     * Android "reply" arıyorsa çalışması için
     * response ile birlikte reply da gönderiyoruz.
     */
    return jsonResponse({
      success: true,

      type:
        changes.length
          ? "code_change"
          : "answer",

      response:
        finalResponse,

      reply:
        finalResponse,

      changes,
    });
  } catch (error) {
    return jsonResponse(
      {
        success: false,

        error:
          error?.message ||
          "AI isteği sırasında hata oluştu.",
      },
      500
    );
  }
}

export default {
  async fetch(request, env) {
    if (
      request.method === "OPTIONS"
    ) {
      return new Response(
        null,
        {
          status: 204,
          headers:
            CORS_HEADERS,
        }
      );
    }

    if (
      request.method !== "POST"
    ) {
      return jsonResponse(
        {
          success: false,
          error:
            "Sadece POST destekleniyor.",
        },
        405
      );
    }

    try {
      const body =
        await request.json();

      const message =
        String(
          body.message ||
          body.prompt ||
          ""
        ).trim();

      const code =
        typeof body.code === "string"
          ? body.code
          : "";

      const fileName =
        String(
          body.fileName ||
          "MainActivity.kt"
        );

      const language =
        String(
          body.language ||
          "Kotlin"
        );

      const funnyMode =
        Boolean(
          body.funnyMode
        );

      const projectFiles =
        body.projectFiles ||
        body.files ||
        {};

      if (!message) {
        return jsonResponse(
          {
            success: false,

            error:
              "Mesaj boş olamaz.",
          },
          400
        );
      }

      const files =
        normalizeFiles(
          projectFiles,
          code,
          fileName
        );

      if (
        isImageRequest(message)
      ) {
        return await generateImage(
          message,
          env
        );
      }

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
            error?.message ||
            "Worker isteği işleyemedi.",
        },
        400
      );
    }
  },
};
