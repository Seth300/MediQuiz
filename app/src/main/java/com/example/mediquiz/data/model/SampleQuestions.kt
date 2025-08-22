package com.example.mediquiz.data.model

// funzione test, utilizzata per fornire domande di esempio offline
fun getSampleQuestions(): List<Question> {
    val defaultExamId = "au2"
    return listOf(
        Question(
            id = 1,
            examId = defaultExamId,// Assuming server IDs start from 1 and are unique
            questionText = "What is the capital of France?",
            listOfAnswers = listOf("Berlin", "Madrid", "Paris", "Rome"),
            correctAnswer = "Paris",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 2,
            examId = defaultExamId,
            questionText = "Which planet is known as the Red Planet?",
            listOfAnswers = listOf("Earth", "Mars", "Jupiter", "Venus"),
            correctAnswer = "Mars",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 3,
            examId = defaultExamId,
            questionText = "What is the largest ocean on Earth?",
            listOfAnswers = listOf("Atlantic", "Indian", "Arctic", "Pacific"),
            correctAnswer = "Pacific",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 4,
            examId = defaultExamId,
            questionText = "Who wrote 'Romeo and Juliet'?",
            listOfAnswers = listOf("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
            correctAnswer = "William Shakespeare",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 5,
            examId = defaultExamId,
            questionText = "What is the chemical symbol for water?",
            listOfAnswers = listOf("O2", "CO2", "H2O", "NaCl"),
            correctAnswer = "H2O",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 6,
            examId = defaultExamId,
            questionText = "How many continents are there?",
            listOfAnswers = listOf("5", "6", "7", "8"),
            correctAnswer = "7",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 7,
            examId = defaultExamId,
            questionText = "What is the tallest mammal?",
            listOfAnswers = listOf("Elephant", "Giraffe", "Whale", "Horse"),
            correctAnswer = "Giraffe",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 8,
            examId = defaultExamId,
            questionText = "Which gas do plants absorb from the atmosphere?",
            listOfAnswers = listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"),
            correctAnswer = "Carbon Dioxide",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 9,
            examId = defaultExamId,
            questionText = "What is the currency of Japan?",
            listOfAnswers = listOf("Won", "Yuan", "Yen", "Dollar"),
            correctAnswer = "Yen",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 10,
            examId = defaultExamId,
            questionText = "Which artist painted the Mona Lisa?",
            listOfAnswers = listOf("Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Claude Monet"),
            correctAnswer = "Leonardo da Vinci",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 11,
            examId = defaultExamId,
            questionText = "How many sides does a hexagon have?",
            listOfAnswers = listOf("5", "6", "7", "8"),
            correctAnswer = "6",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 12,
            examId = defaultExamId,
            questionText = "What is the boiling point of water in Celsius?",
            listOfAnswers = listOf("0°C", "50°C", "100°C", "200°C"),
            correctAnswer = "100°C",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 13,
            examId = defaultExamId,
            questionText = "Which country is known as the Land of the Rising Sun?",
            listOfAnswers = listOf("China", "South Korea", "Japan", "Thailand"),
            correctAnswer = "Japan",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 14,
            examId = defaultExamId,
            questionText = "What is the main ingredient in guacamole?",
            listOfAnswers = listOf("Tomato", "Onion", "Avocado", "Pepper"),
            correctAnswer = "Avocado",
            subject = QuestionSubject.MICROBIOLOGY // Or Food?
        ),
        Question(
            id = 15,
            examId = defaultExamId,
            questionText = "Who was the first President of the United States?",
            listOfAnswers = listOf("Abraham Lincoln", "Thomas Jefferson", "George Washington", "John Adams"),
            correctAnswer = "George Washington",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 16,
            examId = defaultExamId,
            questionText = "What is the hardest natural substance on Earth?",
            listOfAnswers = listOf("Gold", "Iron", "Diamond", "Quartz"),
            correctAnswer = "Diamond",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 17,
            examId = defaultExamId,
            questionText = "Which element has the chemical symbol 'O'?",
            listOfAnswers = listOf("Gold", "Oxygen", "Osmium", "Oganesson"),
            correctAnswer = "Oxygen",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 18,
            examId = defaultExamId,
            questionText = "In which city is the Eiffel Tower located?",
            listOfAnswers = listOf("London", "Rome", "Paris", "Berlin"),
            correctAnswer = "Paris",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 19,
            examId = defaultExamId,
            questionText = "What is the square root of 64?",
            listOfAnswers = listOf("6", "7", "8", "9"),
            correctAnswer = "8",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 20,
            examId = defaultExamId,
            questionText = "Which is the largest desert in the world?",
            listOfAnswers = listOf("Sahara", "Arabian", "Gobi", "Antarctic"),
            correctAnswer = "Antarctic",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 21,
            examId = defaultExamId,
            questionText = "What is the name of the galaxy that contains our Solar System?",
            listOfAnswers = listOf("Andromeda", "Triangulum", "Whirlpool", "Milky Way"),
            correctAnswer = "Milky Way",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 22,
            examId = defaultExamId,
            questionText = "Who discovered penicillin?",
            listOfAnswers = listOf("Marie Curie", "Alexander Fleming", "Louis Pasteur", "Isaac Newton"),
            correctAnswer = "Alexander Fleming",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 23,
            examId = defaultExamId,
            questionText = "What is the capital of Australia?",
            listOfAnswers = listOf("Sydney", "Melbourne", "Canberra", "Perth"),
            correctAnswer = "Canberra",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 24,
            examId = defaultExamId,
            questionText = "How many colors are in a rainbow?",
            listOfAnswers = listOf("5", "6", "7", "8"),
            correctAnswer = "7",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 25,
            examId = defaultExamId,
            questionText = "What is the primary language spoken in Brazil?",
            listOfAnswers = listOf("Spanish", "English", "Portuguese", "French"),
            correctAnswer = "Portuguese",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 26,
            examId = defaultExamId,
            questionText = "Which ocean is the Bermuda Triangle located in?",
            listOfAnswers = listOf("Pacific", "Atlantic", "Indian", "Arctic"),
            correctAnswer = "Atlantic",
            subject = QuestionSubject.MICROBIOLOGY
        ),
        Question(
            id = 27,
            examId = defaultExamId,
            questionText = "What is the chemical symbol for Gold?",
            listOfAnswers = listOf("Go", "Gd", "Au", "Ag"),
            correctAnswer = "Au",
            subject = QuestionSubject.MICROBIOLOGY
        )
    )
}