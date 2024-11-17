import java.io.BufferedReader;
import java.io.IOException;

/*
 *  Hawk Backus-Naur Form grammar:
 * 
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
 *  Rule 12: OUTPUT    ---> output ID_LIST | output NUM;
 *  Rule 13: EXPR      ---> FACTOR | FACTOR + EXPR | FACTOR - EXPR
 *  Rule 14: FACTOR    ---> OPERAND | OPERAND * FACTOR | OPERAND / FACTOR
 *  Rule 15: OPERAND   ---> NUM | ID | ( EXPR )
 *  Rule 16: NUM       ---> (0 | 1 | ... | 9)+ [.(0 | 1 | ... | 9)+]
 *  Rule 17: COMP      ---> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )
 *  Rule 18: TYPE      ---> int | float | double
 * 
 */

public class Lexer {

    static boolean lexerOutput = false; // set to true to see lexer output (for debugging)

     // field to store line number
     static int lineNumber = 1;

    static int charClass;
    static StringBuilder lexeme = new StringBuilder();
    static char nextChar;
    static int lexLen;
    static int token;
    static int nextToken;
    static BufferedReader in_fp;
    /* Character classes */
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int DOT = 2;
    static final int UNKNOWN = 99;

    /* Token codes */
    static final int INT_LIT = 10;
    static final int IDENT = 11;
    static final int ASSIGN_OP = 20;
    static final int ADD_OP = 21;
    static final int SUB_OP = 22;
    static final int MULT_OP = 23;
    static final int DIV_OP = 24;
    static final int LEFT_PAREN = 25;
    static final int RIGHT_PAREN = 26;
    static final int EQUAL = 30;
    static final int LESS_THAN = 31;
    static final int GREATER_THAN = 32;
    static final int NOT_EQUAL = 33;
    static final int COMMA = 40;
    static final int COLON = 41;
    static final int SEMI_COLON = 42;
    static final int EOF = -1;
    /* Reserved Words */
    static final int PROGRAM = 51;
    static final int BEGIN = 52;
    static final int END = 53;
    static final int IF = 54;
    static final int THEN = 55;
    static final int ELSE = 56;
    static final int INPUT = 57;
    static final int OUTPUT = 58;
    static final int INT_WORD = 59;
    static final int WHILE = 60;
    static final int LOOP = 61;
    /* Reserved Words by type */
    static final int FLOAT = 62;
    static final int DOUBLE = 63;


    /* addChar - a function to add nextChar to lexeme */
    
    static void addChar(){    
        if (lexLen <= 98) {
            lexeme.append(nextChar);
            lexLen++;
        }
        else
            System.out.println("ERROR - lexeme is too long \n");
    }
    static void getChar() throws IOException{
        int charRead = in_fp.read();
        if (charRead != -1) {
            nextChar = (char) charRead;
            if (nextChar == '\n') {
                lineNumber++; // Increment line number when encountering a newline
            }

            if (Character.isLetter(nextChar)) {
                charClass = LETTER;
            } else if (Character.isDigit(nextChar)) {
                charClass = DIGIT;
            } else if (nextChar == '.') {
                charClass = DOT;
            }else {
                charClass = UNKNOWN;
            }
        } else {
            charClass = EOF;
        }
    }
    static void getNonBlank() throws IOException {
        while (Character.isWhitespace(nextChar)) {
            getChar();
            if (charClass == EOF) {
                return; // Exit the loop if EOF is encountered
            }
        }
    }
    static int lex() throws IOException{
        lexLen = 0;
        lexeme.setLength(0);// Reset lexeme
        getNonBlank();
        switch (charClass) {
            /* Parse identifiers 
             * 
             * TODO: 
             * Check if identifiers starting with '_' are handled correctly
             * Redeclaration of a variable should produce an error and exit the program
            */
            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                // switch/case checks for all reserved words, default to identifier 
                switch (lexeme.toString().trim()) {
                    case "program":
                        nextToken = PROGRAM;
                        break;
                    case "begin":
                        nextToken = BEGIN;
                        break;
                    case "end":
                        nextToken = END;
                        break;
                    case "if":
                        nextToken = IF;
                        break;
                    case "then":
                        nextToken = THEN;
                        break;
                    case "else":
                        nextToken = ELSE;
                        break;
                    case "input":
                        nextToken = INPUT;
                        break;
                    case "output":
                        nextToken = OUTPUT;
                        break;
                    case "int":
                        nextToken = INT_WORD;
                        break;
                    case "while":
                        nextToken = WHILE;
                        break;
                    case "loop":
                        nextToken = LOOP;
                        break;
                    case "float":
                        nextToken = FLOAT;
                        break;
                    case "double":
                        nextToken = DOUBLE;
                        break;
                    default:
                        nextToken = IDENT;
                        // TODO: 
                        // add redeclaration handler (throw error)
                        // idea: 
                        // Initialize and store an empty lookup table of identifiers, 
                        // if the identifier IS NOT in the table, add it to the table
                        // if the identifier IS in the table, throw error
                        break;
                }
                break;
            /* Parse integer literals */
            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                if (charClass == DOT) {
                    addChar();
                    getChar();
                    while (charClass == DIGIT) {
                        addChar();
                        getChar();
                    }
                }
                nextToken = INT_LIT;
                break;
            /* Parentheses and operators */
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;
            /* EOF */
            case EOF:
                nextToken = EOF;
                lexeme = new StringBuilder("EOF");
                break;
        } /* End of switch */
        if (lexerOutput) System.out.printf("Line: %d, Next token is: %d, Next lexeme is %s\n", lineNumber, nextToken, lexeme);
        return nextToken;
    } /* End of function lex */
    
    /* lookup - a function to lookup operators and parentheses and return the token */
    static int lookup(char ch) throws IOException {
        switch (ch) {
            /* Operators 
             * '(' 
             * ')' 
             * '+' 
             * '-' 
             * '*' 
             * '/'
             * 
            */ 
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                addChar();
                nextToken = RIGHT_PAREN;
                break;
            case '+':
                addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                addChar();
                nextToken = DIV_OP;
                break;
            /* Comparison Operators 
             * '=' 
             * '<'
             * '>'
             * includes handler for "<>" not equal op
            */ 
            case '=':
                addChar();
                nextToken = EQUAL;
                break;
            case '<':
                addChar();
                nextToken = LESS_THAN;
                getChar(); // Look ahead for '>'
                if (nextChar == '>') {
                    addChar();
                    nextToken = NOT_EQUAL; 
                }
                break;
            case '>':
                addChar();
                nextToken = GREATER_THAN;
                break;
            /* Terminators and Declarations
             * ',' 
             * ':' 
             * ';'
             * includes handler for ':=' assignment op
            */ 

            case ',':
                addChar();
                nextToken = COMMA;
                break;
            case ':':
                addChar();
                nextToken = COLON;
                getChar(); // Look ahead for '='
                if (nextChar == '=') {
                    addChar();
                    nextToken = ASSIGN_OP;
                }
                break;
            case ';':
                addChar();
                nextToken = SEMI_COLON;
                break;
            default:
                addChar();
                nextToken = EOF;
                break;
            }
        return nextToken;
    }
};
