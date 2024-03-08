package com.sid.gl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Autowired
    private ChatClient chatClient;

    public String chat(@PathVariable(name = "message") String message){
        return chatClient.call(message);
    }

    public Map movies(String category,int year) throws JsonProcessingException {
        OpenAiApi openAiApi=new OpenAiApi(apiKey);
        OpenAiChatOptions options=
                OpenAiChatOptions.builder()
                        .withModel("gpt-3.5-turbo")
                        .withTemperature(0F)
                        .withMaxTokens(2000)
                        .build();
        OpenAiChatClient openAiChatClient=
                new OpenAiChatClient(openAiApi,options);
        SystemPromptTemplate promptTemplate=new SystemPromptTemplate(
                """ 
                        I need you to give me the best movie on the given Category :{category}
                        on the year : {year}
                        the output should be in json format including the following fields:
                        - category<the given category>
                        - year<the given year>
                        - title<the title of the movie>
                        - the producer<the producer of the movie>
                        - actors<A list of main actors of the movie>
                        - summary<a very small summary of the movie>
                        """
        );
        Prompt prompt = promptTemplate.create(Map.of("category",category,"year",year));
        ChatResponse response= openAiChatClient.call(prompt);

        String content= response.getResult().getOutput().getContent();
        return new ObjectMapper().readValue(content, Map.class);
    }

    public String sentimentAnalysis(String review){
        OpenAiApi openAiApi=
                new OpenAiApi(apiKey);
        OpenAiChatOptions options =
                OpenAiChatOptions.builder()
                        .withModel("gpt-3.5-turbo")
                        .withTemperature(0F)
                        .withMaxTokens(2000)
                        .build();
        OpenAiChatClient openAiChatClient =
                new OpenAiChatClient(openAiApi,options);
        String systemMessageText=
                """ 
                        I need you analyze the sentiment on following review : {review}
                        the outpout should be in json format including the following fields
                        - review<the given review>
                        - sentiment<the sentiment oh the review>
                        """;
        SystemMessage systemMessage=
                new SystemMessage(systemMessageText);
        UserMessage userMessage= new UserMessage("````"+review+"````");
        Prompt zeroShotPrompt = new Prompt(List.of(systemMessage,userMessage)); //zero shot prompt (on a system message et user message)
        ChatResponse response= openAiChatClient.call(zeroShotPrompt);
        //more exemples to evaluate few shot prompt
        return response.getResult().getOutput().getContent();
    }
}
