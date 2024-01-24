package com.anuradha.quizapp.service;

import com.anuradha.quizapp.dao.QuestionDao;
import com.anuradha.quizapp.dao.QuizDao;
import com.anuradha.quizapp.model.QuestionWrapper;
import com.anuradha.quizapp.model.Quiz;
import com.anuradha.quizapp.model.Question;
import com.anuradha.quizapp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;
    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz Created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {

        try{
            Optional<Quiz> quiz = quizDao.findById(id);
            List<Question> questionsFromDB = quiz.get().getQuestions();
            List<QuestionWrapper> questionForUser = new ArrayList<>();

            for(Question q : questionsFromDB){
                QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3());
                questionForUser.add(qw);

            }
            return new ResponseEntity<>(questionForUser, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);


    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int right = 0;
        int i = 0;
        for(Response response : responses){
            if(response.getResponse().equals(questions.get(i).getRightAnswer()))
                right++;

            i++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
