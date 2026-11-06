package com.trendgauge.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeminiAnalysisServiceTest {

    @Test
    void apiConnectionTest() {

        String apiKey = System.getenv("GEMINI_API_KEY");
        assertNotNull(apiKey, "GEMINI_API_KEYが設定されていません");

        GeminiAnalysisService service =
                new GeminiAnalysisService(apiKey);

        System.out.println("Gemini API呼び出し開始");
        String response = service.analyze("こんにちは。短く挨拶してください。");
        System.out.println("Gemini API呼び出し完了");
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void extractTextTest() {

        String response = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "こんにちは！よろしくお願いします。"
                      }
                    ]
                  }
                }
              ]
            }
            """;

        GeminiAnalysisService service =
                new GeminiAnalysisService("dummy-key");

        String result = service.extractText(response);

        assertEquals("こんにちは！よろしくお願いします。", result);

        System.out.println(result);
    }
}