package com.sid.gl.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sid.gl.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OpenAiRestController {
    @Autowired
   private OpenAiService openAiService;

    @GetMapping("/chat/{message}")
    public String chat(@PathVariable(name = "message") String message){
        return openAiService.chat(message);
    }

    @GetMapping("/movies")
    public Map movies(@RequestParam(name="category",defaultValue = "action") String category,
                         @RequestParam(name = "year",defaultValue = "2019") int year) throws JsonProcessingException {

        return openAiService.movies(category,year);
    }

    @GetMapping("/sentiment-analysis")
    public String sentimentAnalysis(String review){
       return openAiService.sentimentAnalysis(review);
    }
}
