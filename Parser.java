import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Parser {

    //Configuration options
    static boolean requiredOutput = true; // set to true to show show entrypoints for nonterminals (term project requirement output).
    static boolean fullOutput = false; // set to true to show entry points for ID, TYPE, and NUM (for debugging).
    static boolean exitOutput = false; // set to true to show exit points for all recursive function (for debugging).
    static String filepath = "hawk_script.txt"; // The name of the hawk script text file to use as input for the parser.

    // variable redeclaration handler
    static HashSet<String> symbolTable = new HashSet<>();
    static boolean declaring = true;

    static Lexer lexer = new Lexer();
    static int nextToken;
    static int lineNum;

    //mutually recursive procedures:
    // Call these for each EBNF Grammar rule

    // Rule 01: PROGRAM   ---> program DECL_SEC begin STMT_SEC end; | program begin STMT_SEC end;

    static void program() throws IOException {
        if (requiredOutput) System.out.println("PROGRAM");
        // Parse the DECL_SEC or skip straight to STMT_SEC if "begin" is found
        nextToken = Lexer.lex();
        if (nextToken != Lexer.BEGIN){
            decl_sec();
        } 
        declaring = false;
        nextToken = Lexer.lex();
        stmt_sec();
        nextToken = Lexer.lex();
        if (exitOutput) System.out.println("Exit PROGRAM");
    }
    
    // Rule 02: DECL_SEC  ---> DECL | DECL DECL_SEC

    static void decl_sec() throws IOException {
        if (requiredOutput) System.out.println("DECL_SEC");
        // Parse the first DECL
        decl();
        // As long as the next token is not "begin" decl_sec will recursively call (filling the entire declaration section)
        nextToken = Lexer.lex();
        while (nextToken != Lexer.BEGIN){
            decl_sec();
        } 
        if (exitOutput) System.out.println("Exit DECL_SEC");
    }
    
    // Rule 03: DECL      ---> ID_LIST : TYPE ;

    static void decl() throws IOException {
        if (requiredOutput) System.out.println("DECL");
        // Parse id_list
        id_list();
        // As long as the next token is : , get the next token and parse the type, otherwise throw an error
        
        if (nextToken != Lexer.COLON){
            error("expected colon ':'", 3);
        }
        nextToken = Lexer.lex();
        type();
        if (nextToken != Lexer.SEMI_COLON){
            error("expected semi-colon ';'", 3);
        }
        if (exitOutput) System.out.println("Exit DECL");
    }

    // Rule 04: ID_LIST   ---> ID | ID , ID_LIST

    static void id_list() throws IOException {
        if (requiredOutput) System.out.println("ID_LIST");
        // Parse the first ID
        id();
        // As long as the next token is "," get the next token and parse the next id
        while (nextToken == Lexer.COMMA) {
            nextToken = Lexer.lex();
            id_list();
        }
        if (exitOutput) System.out.println("Exit ID_LIST");
    }

    // Rule 05: ID        ---> (_ | a | b | ... | z | A | ... | Z) (_ | a | b | ... | z | A | ... | Z | 0 | 1 | ... | 9)*

    static void id() throws IOException {
        if (fullOutput) System.out.println("ID");
        if (fullOutput) System.out.println("current lexeme in id(): " + Lexer.lexeme.toString() + " | declaring: " + declaring + " | table: " + symbolTable);

        if (nextToken != Lexer.IDENT){
            error("expected identifier", 5);
        }else{
            // Handle identifier declaration
            if (declaring){
                if (symbolTable.contains(Lexer.lexeme.toString())) {
                    // if identifier is in the symbol table already, throw 
                    error("Redeclaration of identifier '" + Lexer.lexeme + "'", 2);
                } else {
                    // if identifier is not in the symbol table already, add identifier to the  table 
                    symbolTable.add(Lexer.lexeme.toString()); 
                } 
            } else {
                // check if identifiers have been declared when used in statements
                if (!symbolTable.contains(Lexer.lexeme.toString())) {
                    // if identifier is not in the symbol table already, throw an error
                    error("Missing declaration of identifier '" + Lexer.lexeme + "'", 6);
                }
            }

            if (fullOutput) System.out.println("Terminal ID found");
        }
        if (fullOutput) System.out.println("updated table: " + symbolTable);
        nextToken = Lexer.lex();
        if (exitOutput && fullOutput) System.out.println("Exit ID");
    }

    // Rule 06: STMT_SEC  ---> STMT | STMT STMT_SEC

    static void stmt_sec() throws IOException {
        if (requiredOutput) System.out.println("STMT_SEC");
        // Parse the first STMT
        stmt();
        // recursively call stmt_sec (filling the entire statement section) until a valid ender to a statement section is found
        nextToken = Lexer.lex();
        while (nextToken != Lexer.END && nextToken != Lexer.EOF && nextToken != Lexer.SEMI_COLON && nextToken != Lexer.ELSE){
            stmt_sec();
        } 
        if (exitOutput) System.out.println("Exit STMT_SEC");
    }

    // Rule 07: STMT      ---> ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT

    static void stmt() throws IOException {

        if (requiredOutput) System.out.println("STMT");
        switch (nextToken) {
            case Lexer.IDENT:
                assign();
                break;
            case Lexer.IF:
                ifStmt();
                break;
            case Lexer.WHILE:
                whileStmt();
                break;
            case Lexer.INPUT:
                input();
                break;
            case Lexer.OUTPUT:
                output();
                break;
            case Lexer.EOF:
                error("no valid statement found. Statement must be ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT", 7);
                break;
        }

        if (exitOutput) System.out.println("Exit STMT");
    }

    // Rule 08: ASSIGN    ---> ID := EXPR ;

    static void assign() throws IOException {
        if (requiredOutput) System.out.println("ASSIGN");
        id();
        if (nextToken != Lexer.ASSIGN_OP){
            error("expected assignment operator", 8);
        }
        else
        nextToken = Lexer.lex();
        expr();
        if (exitOutput) System.out.println("Exit ASSIGN");
    }

    // Rule 09: IFSTMT    ---> if COMP then STMT_SEC end if ; | if COMP then STMT_SEC else STMT_SEC end if ;

    static void ifStmt() throws IOException {
        if (requiredOutput) System.out.println("IFSTMT");

        nextToken = Lexer.lex();
        comp();
        if (nextToken != Lexer.THEN){
            error("expected keyword 'then'", 9);
        } 
        nextToken = Lexer.lex();
        stmt_sec();
        if (nextToken == Lexer.ELSE){
            nextToken = Lexer.lex();
            stmt_sec();
        }
        if (nextToken == Lexer.END){
            nextToken = Lexer.lex();
            if (nextToken != Lexer.IF){
                error("expected keyword 'end if'", 9);
            }
        } 
        nextToken = Lexer.lex();
        if (nextToken != Lexer.SEMI_COLON) {
            error("expected semi-colon", 9);
        }
        
        if (exitOutput) System.out.println("Exit IFSTMT");
    }

    // Rule 10: WHILESTMT ---> while COMP loop STMT_SEC end loop ;
    
    static void whileStmt() throws IOException {
        if (requiredOutput) System.out.println("WHILE_STMT");

        nextToken = Lexer.lex();
        comp();
        if (nextToken != Lexer.LOOP){
            error("expected keyword 'loop'", 10);
        } 
        nextToken = Lexer.lex();
        stmt_sec();
        if (nextToken == Lexer.END){
            nextToken = Lexer.lex();
            if (nextToken != Lexer.LOOP){
                error("expected keyword 'end loop'", 10);
            }
        } 
        nextToken = Lexer.lex();
        if (nextToken != Lexer.SEMI_COLON) {
            error("expected semi-colon ';'", 10);
        }

        if (exitOutput) System.out.println("Exit WHILE_STMT");
    }

    // Rule 11: INPUT     ---> input ID_LIST;

    static void input() throws IOException {
        if (requiredOutput) System.out.println("INPUT");
        nextToken = Lexer.lex();
        id_list();
        if (exitOutput) System.out.println("Exit INPUT");
    }

    // Rule 12: OUTPUT    ---> output ID_LIST; | output NUM;

    static void output() throws IOException {
        if (requiredOutput) System.out.println("OUTPUT");
        nextToken = Lexer.lex();
        if (nextToken == Lexer.IDENT){
            id_list();
        }
        else if (nextToken == Lexer.INT_LIT){
            num();
            nextToken = Lexer.lex();
        }
        else {
            error("expected ID_LIST or NUM", 12);
        }
        // nextToken = Lexer.lex();
        if (nextToken != Lexer.SEMI_COLON){
            error("expected semi-colon", 12);
        }
        if (exitOutput) System.out.println("Exit OUTPUT");
    }

    // Rule 13: EXPR      ---> FACTOR | FACTOR + EXPR | FACTOR - EXPR

    static void expr() throws IOException {
        if (requiredOutput) System.out.println("EXPR");
        // Parse the first factor
        factor();
        // As long as the next token is + or -, get the next token and parse the next expr
        while (nextToken == Lexer.ADD_OP || nextToken == Lexer.SUB_OP) {
            nextToken = Lexer.lex();
            expr();
        }
        if (exitOutput) System.out.println("Exit EXPR");
    }

    // Rule 14: FACTOR    ---> OPERAND | OPERAND * FACTOR | OPERAND / FACTOR

    static void factor() throws IOException {
        if (requiredOutput) System.out.println("FACTOR");
        // Parse the first operand
        operand();
        // As long as the next token is + or -, get the next token and parse the next expr
        while (nextToken == Lexer.MULT_OP || nextToken == Lexer.DIV_OP) {
            nextToken = Lexer.lex();
            factor();
        }
        if (exitOutput) System.out.println("Exit FACTOR");
    }

    // Rule 15: OPERAND   ---> NUM | ID | ( EXPR )

    static void operand() throws IOException {
        if (requiredOutput) System.out.println("OPERAND");
        if (nextToken == Lexer.INT_LIT){
            num();
            nextToken = Lexer.lex();
        }
        else if (nextToken == Lexer.IDENT){
            id();
        }
        else if (nextToken == Lexer.LEFT_PAREN){
            nextToken = Lexer.lex();
            expr();
            nextToken = Lexer.lex();
            if (nextToken != Lexer.RIGHT_PAREN){
                error("expected right parenthesis ')'", 15);
            }
            
        }
        else {
            error("expected NUM, ID, or ( EXPR )", 15);
        }
        if (exitOutput) System.out.println("Exit OPERAND");
    }

    // Rule 16: NUM       ---> (0 | 1 | ... | 9)+ [.(0 | 1 | ... | 9)+]
    static void num() throws IOException {
        if (fullOutput) System.out.println("Enter NUM");
        if (nextToken != Lexer.INT_LIT){
            error("expected integer literal", 16);
        }else{
            if (fullOutput) System.out.println("Terminal NUM found");
        }
        // nextToken = Lexer.lex();
        if (exitOutput && fullOutput) System.out.println("Exit NUM");
    }

    // Rule 17: COMP      ---> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )

    static void comp() throws IOException {
        if (requiredOutput) System.out.println("COMP");
        if (nextToken != Lexer.LEFT_PAREN){
            error("expected left parenthesis '('", 15);
        }
        nextToken = Lexer.lex();
        operand();
        // Handle =
        if (nextToken == Lexer.EQUAL){
            nextToken = Lexer.lex();
            operand();
        }
        // Handle < or <>
        else if (nextToken == Lexer.LESS_THAN){
            nextToken = Lexer.lex();
            if (nextToken == Lexer.GREATER_THAN){
                nextToken = Lexer.lex();
            }
            operand();
        }
        // Handle >
        else if (nextToken == Lexer.GREATER_THAN){
            nextToken = Lexer.lex();
            operand();
        }
        else {
            error("expected comparison operator", 17);
        }

        if (nextToken != Lexer.RIGHT_PAREN){
            error("expected right parenthesis '('", 17);
        } 
        nextToken = Lexer.lex();
        if (exitOutput) System.out.println("Exit COMP");
    }
    
    // Rule 18: TYPE      ---> int | float | double

    static void type() throws IOException {
        if (fullOutput) System.out.println("TYPE");
        if (nextToken != Lexer.INT_WORD && nextToken != Lexer.FLOAT && nextToken != Lexer.DOUBLE){
            System.out.println("No type found to parse.");
        }
        nextToken = Lexer.lex();
        if (exitOutput) System.out.println("Exit TYPE");
    }
    
    // Error reporting function
    // errCode corresponds to number of the grammar rule the error was found in. errCode 100 = number lexeme too long 
    static void error(String message, int errCode) {
        System.out.println("ERROR !! " + message + " in line " + Lexer.lineNumber);
        System.exit(errCode);
    }
    
        // Main entry point for parsing the Hawk script
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Parsing.");
        Lexer.in_fp = new BufferedReader(new FileReader(filepath));
        
        while (nextToken != Lexer.PROGRAM && nextToken != Lexer.EOF){
            nextToken = Lexer.lex(); // Initialize lexer and get the first token
        }
        if (nextToken == Lexer.EOF){
            System.out.println("No program found to parse.");
            System.exit(0);
        }
        if (nextToken == Lexer.PROGRAM){
            program(); // Start parsing the program
        }
        nextToken = Lexer.lex();
        if (nextToken == Lexer.EOF){
            System.out.println("Parsing completed successfully!");
            System.exit(0);
        }
    }
}
