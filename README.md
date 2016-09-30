# Tiger Compiler

A compiler for the Tiger programming language. Written as part of the Georgia Tech Compilers and Interpreters course.


## Scanner
The scanner will transform a string that represents a program in Tiger into a 
sequence of `Token`s to be read by the parser. 

### Token
`Token`s contain their text, and their `TokenType`.

A token's text is simply the characters that were matched by the scanner.
For example, in the following lines:

```
for i := 1 to 100 do 
    sum := sum + X[i] * Y[i];
```

the token representing the start of the for loop (`for`) would have text "for".
The token that starts the assignment statement would have text "sum".

The `TokenType` represents the lexographic type of the token. In the above example,
the `for` token would have `TokenType` FOR, as it is a keyword. The `sum` token would
have the `TokenType` ID.

### Lexical Errors

The Parser interacts with the Scanner by calling `Scanner#nextToken()`, or 
`Scanner#getTokenAtIndex()`. The Parser then checks whether the `TokenType` 
matches any of the `TokenTypes` expected by the grammar. If it does not match any rules 
in the grammar, then the parser should can get more information from the scanner about
 the error by calling `Scanner#getLexicalError()`. The scanner will then provide a `LexicalError`
with more information about why the `Token` does not fit any of the expected `TokenTypes`.
`LexicalError` details the line number of the error, and the exact character where 
the `Token` no longer matched the expected `TokenType`s.

