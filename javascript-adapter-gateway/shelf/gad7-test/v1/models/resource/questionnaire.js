var Questionnaire = (function () {

  /**
   *
   * @param {String} title title of questionnaire
   * @param {String} prompt Question prompt
   * @param {Array} questions array of questions
   * @param {Array} range range of options (ie 1-5 means possible options for each questions is a number from 1 to 5)
   * @param {Array} rangeLabels labels for the columns
   */
  function matrixBuilder(title, prompt, questions, range, rangeLabels) {
    var questionnaire = {
      title: title,
      prompt: prompt,
      questions: []
    }

    for (var question in questions) {
      questionnaire.questions.push({
        question: questions[question],
        options: rangeLabels,
        option_scores: (() => {
          let scores = {};
      for (let i = range[0]; i <= range[1]; i += 1) {
        scores[rangeLabels[i]] = i
      }

      return scores;
    })
      ()
    })
    }
    this.questionnaire = questionnaire
  }

  /**
   * Questionnaire class
   *
   * @param {String} type
   */
  function Questionnaire(type, scoreHandler, interpretationHandler) {
    this.type = type
    this.questionnaire = null;
    this.scorer = scoreHandler;
    this.interpreter = interpretationHandler;
  }

  Questionnaire.prototype.jsonBuilder = function () {
    var builderFunction = new Function();
    if (this.type === "matrix") {
      builderFunction = matrixBuilder.bind(this)
    }

    return {
      build: builderFunction
    }
  }

  Questionnaire.prototype.getAnswerScore = function (questionNumber, answer) {
    return this.questionnaire.questions[questionNumber].option_scores[answer]
  }

  Questionnaire.prototype.getJson = function () {
    return JSON.stringify(this.questionnaire, null, 4)
  }

  Questionnaire.prototype.scoreAnswers = function (answers) {
    return this.scorer(answers)
  }

  Questionnaire.prototype.interpret = function (score) {
    return this.interpreter(score)
  }

  return Questionnaire;
})();

module.exports = Questionnaire;