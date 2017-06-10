# Feature Selection

Common feature selection algorithms implemented in Java, including 

- [Sequential Forward Selection (SFS)](selection/SequentialForwardSelection.java)
- [Sequential Backward Selection (SBS)](selection/SequentialBackwardsSelection.java)
- [Sequential Forward Floating Selection (SFFS)](selection/SequentialFloatingForwardSelection.java)
- [Sequential Backward Floating Selection (SFBS)](selection/SequentialFloatingBackwardSelection.java)

This uses a Wrapper approach, utilising the Weka library as a classifier.

To see how to use view the TestAll class, which guives an example of creating a new FeatureSelection
object and shows how to utilise the various stopping methods.

Datasets used for testing:

- [WINE](https://archive.ics.uci.edu/ml/datasets/wine) (13 Features)
- [MUSK V1](https://archive.ics.uci.edu/ml/datasets/Musk+(Version+1)) (168 features)
- [ISOLET](https://archive.ics.uci.edu/ml/datasets/ISOLET) (617 features)

Sample output from WINE dataset on random training:testing (70:30) split of the data

```
-------------------
Sequential backward floating selection for 10 features
Accuracy using all features: 47.059%
Accuracy using features ([2, 3, 6, 7, 8, 9, 10, 11]): 94.118%
-------------------
-------------------
Sequential backward floating selection
Accuracy using all features: 41.176%
Accuracy using features ([0, 1, 2, 8, 9, 11]): 88.235%
-------------------
-------------------
Sequential floating forward selection for 5 features
Accuracy using all features: 94.118%
Accuracy using features ([1, 2, 12]): 94.118%
-------------------
-------------------
Sequential forward selection
Accuracy using all features: 82.353%
Accuracy using features ([0, 1, 6, 10]): 88.235%
-------------------
-------------------
Sequential backward selection
Accuracy using all features: 64.706%
Accuracy using features ([3, 5, 9, 10, 11]): 82.353%
-------------------
-------------------
Sequential forward selection for max 10 features
Accuracy using all features: 82.353%
Accuracy using features ([0, 6, 7, 9]): 100.000%
-------------------
-------------------
Sequential backward selection for max 10 Features
Accuracy using all features: 47.059%
Accuracy using features ([6, 9, 11]): 88.235%
-------------------
-------------------
Sequential floating forward selection
Accuracy using all features: 41.176%
Accuracy using features ([1, 5, 8, 9, 10]): 76.471%
-------------------
```
