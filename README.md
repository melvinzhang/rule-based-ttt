# Analysis of Simple Tic Tac Toe AIs

Consider a class of Tic Tac Toe AI that follows the following rules and executes the first one that applies

1. win if I have two symbols in a row
2. block if opponent has two symbols in a row
3. place in the lower left corner
4. place in the upper left corner
5. place in the upper right corner
6. place in the lower right corner
7. place in the center
8. place in the bottom edge
9. place in the left edge
10. place in the right edge
11. place in the top edge

Rules 3 - 11 can be represented as follows:
```
   |   |
 2 | 9 | 3
---|---|---
 7 | 5 | 8
---|---|---
 1 | 6 | 4
   |   |
```
Rules 1 and 2 are fixed, but rules 3 - 11 can be reorderd. There are a total of 9! (362880) different AIs.

rule-based-ttt/check takes as input a matrix representing rules 3 - 11 and
output the number of win/lose/draw games against a player which tries all
possible moves.

```
=> (check [[2 9 3] [7 5 8] [1 6 4]])
Testing AI as first player
win: 9
lose: 2
draw: 8
Testing AI as second player
win: 36
lose: 42
draw: 121
```
