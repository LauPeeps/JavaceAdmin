package com.example.javaceadminpanel;

import static com.example.javaceadminpanel.Category.category_index;
import static com.example.javaceadminpanel.Category.category_list;
import static com.example.javaceadminpanel.Questions.questionsModelList;
import static com.example.javaceadminpanel.Sets.idOfSets;
import static com.example.javaceadminpanel.Sets.set_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuestionsAdderActivity extends AppCompatActivity {

    EditText question, option1, option2, option3, option4, answer;
    Button addQuestionBtn;
    String questionStr, option1Str, option2Str, option3Str, option4Str, answerStr;
    Dialog progressDialog;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_adder);


        Toolbar toolbar = findViewById(R.id.addquestions_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Question " + String.valueOf(questionsModelList.size() + 1));

        question = findViewById(R.id.questionInfo);
        option1 = findViewById(R.id.choice1);
        option2 = findViewById(R.id.choice2);
        option3 = findViewById(R.id.choice3);
        option4 = findViewById(R.id.choice4);
        answer = findViewById(R.id.correctAnswer);

        addQuestionBtn = findViewById(R.id.addQbtn);


        progressDialog = new Dialog(QuestionsAdderActivity.this);
        progressDialog.setContentView(R.layout.loading_progressbar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbar_background);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();


        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionStr = question.getText().toString();
                option1Str = option1.getText().toString();
                option2Str = option2.getText().toString();
                option3Str = option3.getText().toString();
                option4Str = option4.getText().toString();
                answerStr = answer.getText().toString();
                int checkAnswerStr = Integer.parseInt(answerStr);
                if (questionStr.isEmpty()) {
                    question.setError("Please enter a question");
                    return;
                }
                if (option1Str.isEmpty()) {
                    option1.setError("Please enter option 1");
                    return;
                }
                if (option2Str.isEmpty()) {
                    option2.setError("Please enter option 2");
                    return;
                }
                if (option3Str.isEmpty()) {
                    option3.setError("Please enter option 3");
                    return;
                }
                if (option4Str.isEmpty()) {
                    option4.setError("Please enter option 4");
                    return;
                }
                if (answerStr.isEmpty()) {
                    answer.setError("Please enter the correct answer");
                    return;
                }
                if (checkAnswerStr > 4) {
                    answer.setError("Please enter the 1 - 4");
                    return;
                }
                addQuestion();
            }
        });
    }

    private void addQuestion() {
        progressDialog.show();

        Map<String, Object> question_data = new ArrayMap<>();

        question_data.put("QUESTION", questionStr);
        question_data.put("A", option1Str);
        question_data.put("B", option2Str);
        question_data.put("C", option3Str);
        question_data.put("D", option4Str);
        question_data.put("CORRECT", answerStr);

        String document_id = firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                .collection(idOfSets.get(set_index)).document().getId();

        firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                .collection(idOfSets.get(set_index)).document(document_id)
                .set(question_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String, Object> question_document = new ArrayMap<>();

                        question_document.put("Q" + String.valueOf(questionsModelList.size() + 1) + "_ID", document_id);
                        question_document.put("QNO", String.valueOf(questionsModelList.size() + 1));

                        firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                                .collection(idOfSets.get(set_index)).document("QUESTION_LIST")
                                .update(question_document)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(QuestionsAdderActivity.this, "Question added successfully", Toast.LENGTH_SHORT).show();

                                        questionsModelList.add(new QuestionsModel(
                                                document_id, questionStr, option1Str, option2Str, option3Str, option4Str, Integer.valueOf(answerStr)
                                        ));
                                        progressDialog.dismiss();

                                        QuestionsAdderActivity.this.finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QuestionsAdderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsAdderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}