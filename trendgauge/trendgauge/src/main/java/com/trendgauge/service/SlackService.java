package com.trendgauge.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SlackService {
    private final Slack slack = Slack.getInstance();

    public void sendMessage(String message) {
        String token = System.getenv("SLACK_BOT_TOKEN");
        String channelId = System.getenv("SLACK_CHANNEL_ID");

        try {
            ChatPostMessageResponse response = slack.methods(token).chatPostMessage(req -> req
                    .channel(channelId)
                    .text(message)
            );

            if (!response.isOk()) {
                throw new IllegalStateException("Slack送信に失敗しました: " + response.getError());
            }

        } catch (SlackApiException | IOException e) {
            throw new IllegalStateException("Slack APIとの通信に失敗しました", e);
        }
    }
}

