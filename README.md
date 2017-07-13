# Bursting Trends and Fake News on Twitter

This application is developed as an assignment for the lesson **Advanced Data Science and Mining**, a post graduate lesson in the National Technical Universtiy of Athens, Electrical and Computer Engineering school.
The purpose of the assignment is to use services from the IBM Bluemix framework in order to manage big data.

The application consists of three parts.
The first part collects Twitter's Trends for a location.
You can change the location or use your credentials for the Trends.
The collection interval is adjustable.
You can change it in the `gr.ntua.collector.TrendsCollector` class in the `@Scheduled(fixedRate = 7200000)` parameter changing the `fixedRate` value.
We collect trends every two hours.

The second part is to find bursting Trends.
For each collected Trend we seek in the database to find previous occurrences.
If there is none, the trend is marked as `firstSeen`.
In the other case, we compute the percent change from the last occurrence.

The third part is to classify a number of tweets as *fake* or *real* based on two trained classifiers.

## Credentials
To use the application you have to provide a properties file.
The application expects a file name `credentials.properties` in the class path (you can safely create it in the `src/main/resources` directory).
This file has to have the following properties:
```
twitter.consumer_key=
twitter.consumer_secret=
twitter.access_token=
twitter.access_token.secret=
twitter.trends.place.id=
bluemix.tone_analyzer.username=
bluemix.tone_analyzer.password=
bluemix.natural_language_classifier.classifier_id=
bluemix.natural_language_classifier.username=
bluemix.natural_language_classifier.password=
```
Apparently you will provide your own credentials for that services.

## Fake - Real News Data Sets
The data sets we used to train the Weka classifier and the Bluemix Natural Language Classifier is in the `src/main/resouces/dataset` directory.

There are three files.
The `fake_real_news.csv` is the original data set which has `id, title, text`, and `label`.
The `bluemix.csv` has only the `title` and `label` columns from the original data set and it is used to create the tone vectors using Bluemix Tone Analyzer and to train the Bluemix Natural Language Classifier.
The results from the Tone Analyzer are in the `weka.csv` file.

## Bluemix Tone Analyzer
We used this service to extract a feature vector for each train instance from the `bluemix.csv` file.
This service returns float values between zero and one for the attributes: `anger,disgust,fear,joy,sadness,analytical,confident,tentative,openness,conscientiousness,extraversion,agreeableness,neuroticism`.

### Weka Classifier
The file with the feature values from the Tone Analyzer is used to create a Random Forest classifier from the Weka classifiers.
This classifier had the best performance compared with others from the Weka tool box.
The trained model is provided in the `src/main/resources/weka/RF.model` file.

### Classification Using Tone Analyzer and Weka
In order to classify a number of tweets, we use the Tone Analyzer to get the features vectors.
This vector is given to the `RF.model` and we get a prediction for the class (fake / real) of the tweet.

## Bluemix Natural Language Classifier
To train this service we used the `bluemix.csv` file.
This services returns just a `classifier id`.
To use the service we just send the text of the tweet and it returns it's class.

# Run the Application
To run the application you have to change database credentials in the `src/main/resources/application.properties` file.
We used MySQL but you can use any database you like.

In a terminal type:
```
$ mvn spring-boot:run
```

and open a browser to the `localhost:8080` URL.
Also take in mind that the port 8080 should not be used by other processes.
It's prefered **not** to use Firefox because it does not support the HTML5 date fields.

To see results and be able to use the application it is recommended to let it run for at least 12 time intervals.
Each time interval is the time you have set in the `TrendsCollector` class.
So, if it is two hours, the application should run for 24 hours.

When you have collected some data, just select the dates (or let the form fields unfilled in order to use the whole collection)  and the *burstiness* of the Trends.
For example, if you choose `20` for the burstiness, you will see tweets that are `firstSeen` or their *burstiness* is above the selected value (20).
Also the results are between the dates you have chosen.

Choose the `more info` button of a Trend and the application returns a graph with the volume of that trend in the selected period and a number of tweets.
The tweets are collected using the Trend as a keyword.
Each tweet is then classified with the two classifiers.
The average score of the two classifiers is given as the final score for the fake / real class of each tweet.

# Notes
The result for the class (fake/real) for each tweet is just the "guess" of some classifiers and you should not take it as a fact.
Bare in mind that the classifiers are trained with data from *news* and not from tweets.
Tweets have quite different language, audience and philosophy.
So, the classifiers have been observed to be biased to the fake class.

# Disclaimer
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
  
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
