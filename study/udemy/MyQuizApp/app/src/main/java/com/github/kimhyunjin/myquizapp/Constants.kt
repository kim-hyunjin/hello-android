package com.github.kimhyunjin.myquizapp

object Constants {

    const val USER_NAME = "userNameKey"
    const val CORRECT_CNT = "correctAnswerCount"
    const val TOTAL_CNT = "totalQuestionCount"

    fun getQuestions(): ArrayList<Question> {
        val questionList = ArrayList<Question>()

        val q1 = Question(1, "What country does this flag belong to?", R.drawable.ic_flag_of_argentina,
            listOf("Argentina", "Australia", "Armenia", "Austria"), 0
            )
        questionList.add(q1)

        // 2
        val que2 = Question(
            2, "What country does this flag belong to?",
            R.drawable.ic_flag_of_australia,
            listOf("Angola", "Austria",
            "Australia", "Armenia"), 2
        )

        questionList.add(que2)

        // 3
        val que3 = Question(
            3, "What country does this flag belong to?",
            R.drawable.ic_flag_of_brazil,
            listOf("Belarus", "Belize",
            "Brunei", "Brazil"), 3
        )

        questionList.add(que3)

        // 4
        val que4 = Question(
            4, "What country does this flag belong to?",
            R.drawable.ic_flag_of_belgium,
            listOf("Bahamas", "Belgium",
            "Barbados", "Belize"), 1
        )

        questionList.add(que4)

        // 5
        val que5 = Question(
            5, "What country does this flag belong to?",
            R.drawable.ic_flag_of_fiji,
            listOf("Gabon", "France",
            "Fiji", "Finland"), 2
        )

        questionList.add(que5)

        // 6
        val que6 = Question(
            6, "What country does this flag belong to?",
            R.drawable.ic_flag_of_germany,
            listOf("Germany", "Georgia",
            "Greece", "none of these"), 0
        )

        questionList.add(que6)

        // 7
        val que7 = Question(
            7, "What country does this flag belong to?",
            R.drawable.ic_flag_of_denmark,
            listOf("Dominica", "Egypt",
            "Denmark", "Ethiopia"), 2
        )

        questionList.add(que7)

        // 8
        val que8 = Question(
            8, "What country does this flag belong to?",
            R.drawable.ic_flag_of_india,
            listOf("Ireland", "Iran",
            "Hungary", "India"), 3
        )

        questionList.add(que8)

        // 9
        val que9 = Question(
            9, "What country does this flag belong to?",
            R.drawable.ic_flag_of_new_zealand,
            listOf("Australia", "New Zealand",
            "Tuvalu", "United States of America"), 1
        )

        questionList.add(que9)

        // 10
        val que10 = Question(
            10, "What country does this flag belong to?",
            R.drawable.ic_flag_of_kuwait,
            listOf("Kuwait", "Jordan",
            "Sudan", "Palestine"), 0
        )

        questionList.add(que10)

        return questionList
    }
}