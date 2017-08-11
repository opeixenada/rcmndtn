# Recommendation engine

Calculates similarity of articles identified by SKU based on their attributes values. The number
 of matching attributes is the most important metric for defining similarity. In case of a draw,
  attributes with names higher in alphabet (a is higher than z) are weighted with heavier weight.

### CLI launch

```
sbt "run <path to input file>"

Starting engine...
Enter SKU to get a list of recommendations >
```

### Tests

```
sbt test
```
