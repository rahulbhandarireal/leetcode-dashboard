package com.example.aiservice.config;

import com.openai.client.OpenAIClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ChatClientConfig {

    @Bean(name = "openAiChatClient")
    public ChatClient openAiChatModel(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-4o-mini")
                                .temperature(0.3)
                               // Pass the builder directly without calling .build() here
                )
                .build(); // This outer build is for the ChatClient itself
    }
}
