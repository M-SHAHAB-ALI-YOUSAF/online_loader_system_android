package com.shahab12344.loader_system;

import java.util.List;

public class QuestionAnswer {
    private String question;
    private List<String> answers;

    public QuestionAnswer(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }
}


