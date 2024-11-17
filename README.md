# Hawk Parser
UHCL Programming Language Concepts Term Project. 

This project implements a scanner and a recursive descent parser for a small made-up programming language called "Hawk," a programming language that follows the EBNF grammar below:

## Hawk Extended Backus-Naur Form grammar:

 *  Rule 01: PROGRAM   ---> program DECL_SEC begin STMT_SEC end; | program begin STMT_SEC end;
 *  Rule 02: DECL_SEC  ---> DECL | DECL DECL_SEC
 *  Rule 03: DECL      ---> ID_LIST : TYPE ;
 *  Rule 04: ID_LIST   ---> ID | ID , ID_LIST
 *  Rule 05: ID        ---> (_ | a | b | ... | z | A | ... | Z) (_ | a | b | ... | z | A | ... | Z | 0 | 1 | ... | 9)*
 *  Rule 06: STMT_SEC  ---> STMT | STMT STMT_SEC
 *  Rule 07: STMT      ---> ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT
 *  Rule 08: ASSIGN    ---> ID := EXPR ;
 *  Rule 09: IFSTMT    ---> if COMP then STMT_SEC end if ; | if COMP then STMT_SEC else STMT_SEC end if ;
 *  Rule 10: WHILESTMT ---> while COMP loop STMT_SEC end loop ;
 *  Rule 11: INPUT     ---> input ID_LIST;
 *  Rule 12: OUTPUT    ---> output ID_LIST; | output NUM;
 *  Rule 13: EXPR      ---> FACTOR | FACTOR + EXPR | FACTOR - EXPR
 *  Rule 14: FACTOR    ---> OPERAND | OPERAND * FACTOR | OPERAND / FACTOR
 *  Rule 15: OPERAND   ---> NUM | ID | ( EXPR )
 *  Rule 16: NUM       ---> (0 | 1 | ... | 9)+ [.(0 | 1 | ... | 9)+]
 *  Rule 17: COMP      ---> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )
 *  Rule 18: TYPE      ---> int | float | double

## Configuring options:
 * The `Lexer.java` file has a `lexerOutput` boolean (by default set to false) to optionally show outputs from the lexical analyzer.
 * The `Parser.java` file has `fullOutput` and `exitOutput` booleans (both by default set to false) to optionally output the recursive procedure exit points and additional output context.
 * The `Parser.java` file also has a `requiredOutput` boolean (by default set to true) which out the entry points of each recursive procedure.  

## How to run the project:

 * To compile the source code, put the `Lexer.java`, `Parser.java`, and `hawk_script.txt` files in the same directory open a terminal in that directory, and run `javac Lexer.java Parser.java` (or do an equivalent java source code compilation process).
 * Overwrite the contents of `hawk_script.txt` with the code you want to parse. 
 * Run `java parser` inside the terminal.

