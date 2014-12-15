# Analysis of Simple Tic Tac Toe AIs
Code for the "Design your own Tic-Tac-Toe AI activity" developed during the Hour of Code SG event at the Singapore Science Center. Participants code their own AI (by giving a preference order for each square) and we will try to play against it and discover its strengths and weakness.

Finally we show how programs can be used to automate the tedious task of testing the AI. We'll use the code in the repo to find counterplays and quantitatively compute the strength of the AI.

The code can also be used to enumerate all 9! possible programs. There are some interesting ones, such as [[2 6 8] [5 9 1] [3 4 7]] that has only one way to beat it when it plays as first player.

# Model for a simple Tic Tac Toe AI
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

# Usage
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
