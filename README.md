<div align="center">
  <img align="center" src="https://socialify.git.ci/TimNekk/QuizHero/image?description=1&font=Inter&language=1&name=1&pattern=Plus&theme=Light" alt="Chatter" width="640" height="320" />
</div>

## Overview

Java CLI application for quizzes based on [jService Trivia Questions](http://jservice.io/)


## Usage

Install client using Maven

```
mvn clean package -DfinalName=quiz-hero-reborn
```

Run it

```
java -jar target\quiz-hero-1.0.jar
```

You can specify amount of questions per request to [jService Trivia Questions](http://jservice.io/) with `-U` parameter _(default: 10)_. 

**More = faster loading**

```
java -jar target\quiz-hero-1.0.jar -U 20
```
