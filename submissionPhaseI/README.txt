# Compilers & Interpreters Project Phase I

## Brian Murphy, Cliff Kuan-Yu Li

To view the code for the scanner and parser, you can clone it on github at this link:
https://github.com/brain-murphy/tiger-compiler

This repo also contains the code that we used to generate the first and follow sets.
See /src/main/java/parser/ParsingTable.java

The Hand-modified grammar is in the file grammarRules.csv. The first column contains the non-terminal
in question, and the second column holds its expansion.

The parser table is in the file parseTable.csv, where the first column is the current non-terminal focus,
and the second column represents the terminal lookahead. The third column contains the rule that should be
used to expand the non-terminal.