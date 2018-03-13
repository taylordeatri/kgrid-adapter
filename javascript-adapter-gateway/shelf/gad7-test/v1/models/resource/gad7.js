var questionnaire = {
  questions: [
    {
      id: "1",
      type: "matrix",
      prompt: "Over the last 2 weeks, how often have you been bothered by the following problems?",

      columns: [
        {
          text: "Not at all sure",
          score: 0,
        },
        {
          text: "Several days",
          score: 1,
        },
        {
          text: "Over half the days",
          score: 2,
        },
        {
          text: "Nearly every day",
          score: 3
        }
      ],

      rows: [
        {
          id: "1a",
          text: "Feeling nervous, anxious, or on edge"
        },
        {
          id: "1b",
          text: "Not being able to stop or control worrying"
        },
        {
          id: "1c",
          text: "Worrying too much about different things"
        },
        {
          id: "1d",
          text: "Trouble relaxing"
        },
        {
          id: "1e",
          text: "Being so restless that it's hard to sit still"
        },
        {
          id: "1f",
          text: "Becoming easily annoyed or irratable"
        },
        {
          id: "1g",
          text: "Feeling afraid as if something awful might happen"
        }

      ]
    },
    {
      id: "2",
      type: "uniform checkboxes",
      prompt: "If you checked off any problems, how difficult have these made it for you to do your work"
      +
      "take care of things at home, or get along with other people?",
      options: [
        {
          value: 0,
          text: "Not difficult at all"
        },
        {
          value: 1,
          text: "Somewhat difficult"
        },
        {
          value: 2,
          text: "Very difficult"
        },
        {
          value: 3,
          text: "Extremely difficult"
        }
      ]

    }
  ]
}

var answers = {
  "answers": [
    {
      "id": "1",
      "scores": [
        {
          "id": "1a",
          "score": 1
        },
        {
          "id": "1b",
          "score": 2
        },
        {
          "id": "1c",
          "score": 1
        },
        {
          "id": "1d",
          "score": 3
        },
        {
          "id": "1e",
          "score": 0
        },
        {
          "id": "1f",
          "score": 1
        },
        {
          "id": "1g",
          "score": 2
        }
      ]
    }
  ]
}

function status() {
  return {status: "OK"}
}

function question() {
  return questionnaire
}

function score(answerJson) {
  var sum = 0
  for (var i = 0; i < answerJson.answers[0].scores.length; i += 1) {
    sum += answerJson.answers[0].scores[i].score;
  }
  return {
    score: sum
  }
}

function interpret(score) {
  var interpretation = "";

  if (score < 5) {
    interpretation = "Minimal anxiety"
  }//between 5 and 10
  else if (score < 10) {
    interpretation = "Mild anxiety"
  }//between 10 and 15
  else if (score < 15) {
    interpretation = "Moderate anxiety"
  } else {
    interpretation = "Severe anxiety"
  }

  return JSON.stringify({
    interpretation: interpretation
  })
}

// print(question());
//
// print(score(answers))
//
// print(interpret(score))

{
  answers: [
    {
      id: 3,
      options: [
        {
          "Several days": 1
        }
      ]
    }
  ]

}

{
  questions : [
    {
      id: 6,
      options: [
        {
          "not at all sure": 0
        },
        {
          "some days": 1
        }
      ]
    }
  ]
}

{
  answers: [
    {
      id: 6,
      answer: 2
    }
  ]
}