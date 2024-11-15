import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    static Lexer lexer = new Lexer();
    static int nextToken;

    static boolean requiredOutput = true; // set to true for term project requirement output
    static boolean fullOutput = true;
    static boolean exitOutput = true; // set to true to include entering terminals
   
    

    //mutually recursive procedures:
    // Call these for each EBNF Grammar rule

    // Rule 01: PROGRAM   ---> program DECL_SEC begin STMT_SEC end; | program begin STMT_SEC end;

    static void program() throws IOException {
        if (requiredOutput) System.out.println("PROGRAM");
        // Parse the DECL_SEC or skip straight to STMT_SEC if "begin" is found
        if (nextToken != Lexer.BEGIN){
            nextToken = Lexer.lex();
            decl_sec();
        } 
        nextToken = Lexer.lex();
        stmt_sec();
        if (exitOutput) System.out.println("Exit PROGRAM");
    }
    
    // Rule 02: DECL_SEC  ---> DECL | DECL DECL_SEC
    // TODO:
    // Redeclaration handling

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
            // TODO
            // Implement proper error
            System.out.println("expected colon, error");
        }
        nextToken = Lexer.lex();
        type();
        if (nextToken != Lexer.SEMI_COLON){
            // TODO
            // Implement proper error
            System.out.println("expected semi colon, error");
        }
        if (exitOutput) System.out.println("Exit DECL");
    }

    // Rule 04: ID_LIST   ---> ID | ID , ID_LIST

    static void id_list() throws IOException {
        if (requiredOutput) System.out.println("ID_LIST");
        // Parse the first term
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
        if (nextToken != Lexer.IDENT){
            // TODO
            // implement proper error
            System.out.println("Error: expected " + Lexer.IDENT + ", received " + nextToken + ". terminating program");
        }else{
            if (exitOutput) System.out.println("Terminal Identifier found");
        }
        nextToken = Lexer.lex();
        if (exitOutput && fullOutput) System.out.println("Exit <ID>");
    }

    // Rule 06: STMT_SEC  ---> STMT | STMT STMT_SEC

    static void stmt_sec() throws IOException {
        if (requiredOutput) System.out.println("STMT_SEC");
        // Parse the first STMT
        stmt();
        // As long as the next token is not "end" stmt_sec will recursively call (filling the entire statement section)
        nextToken = Lexer.lex();
        while (nextToken != Lexer.END && nextToken != Lexer.EOF ){
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
        }

        if (exitOutput) System.out.println("Exit STMT");
    }

    // Rule 08: ASSIGN    ---> ID := EXPR ;

    static void assign() throws IOException {
        if (requiredOutput) System.out.println("ASSIGN");
        id();
        if (nextToken != Lexer.ASSIGN_OP){
            // TODO
            // throw error
            System.out.println("Error: expected " + Lexer.ASSIGN_OP + ", received " + nextToken + ". terminating program");
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
            // TODO proper error
            System.out.println("Error THEN not found");
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
                // TODO proper error
                System.out.println("Error end IF not found");
            }
        } 

        if (exitOutput) System.out.println("Exit IFSTMT");
    }

    // Rule 10: WHILESTMT ---> while COMP loop STMT_SEC end loop ;
    
    static void whileStmt() throws IOException {
        if (requiredOutput) System.out.println("WHILESTMT");

        nextToken = Lexer.lex();
        comp();
        if (nextToken != Lexer.LOOP){
            // TODO proper error
            System.out.println("Error LOOP not found");
        } 
        nextToken = Lexer.lex();
        stmt_sec();
        if (nextToken == Lexer.END){
            nextToken = Lexer.lex();
            if (nextToken != Lexer.LOOP){
                // TODO proper error
                System.out.println("Error end LOOP not found");
            }
        } 

        if (exitOutput) System.out.println("Exit <whileStmt>");
    }

    // Rule 11: INPUT     ---> input ID_LIST;

    static void input() throws IOException {
        if (requiredOutput) System.out.println("INPUT");
        nextToken = Lexer.lex();
        id_list();
        if (exitOutput) System.out.println("Exit <input>");
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
        }
        else {
            // TODO throw error
        }
        nextToken = Lexer.lex();
        if (nextToken == Lexer.SEMI_COLON){
            // TODO throw error
        }
        if (exitOutput) System.out.println("Exit <output>");
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
        if (exitOutput) System.out.println("Exit <expr>");
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
        if (exitOutput) System.out.println("Exit <factor>");
    }

    // Rule 15: OPERAND   ---> NUM | ID | ( EXPR )

    static void operand() throws IOException {
        if (requiredOutput) System.out.println("OPERAND");
        if (nextToken == Lexer.INT_LIT){
            num();
        }
        else if (nextToken == Lexer.IDENT){
            id();
        }
        else if (nextToken == Lexer.LEFT_PAREN){
            // TODO
            System.out.println("ERROR: HAS ( EXPR ) HAS NOT BEEN IMPLEMENTED YET");
        }
        else {
            // TODO
            // Error
        }
        if (exitOutput) System.out.println("Exit <operand>");
    }

    // Rule 16: NUM       ---> (0 | 1 | ... | 9)+ [.(0 | 1 | ... | 9)+]

    static void num() throws IOException {
        if (fullOutput) System.out.println("Enter <num>");
        if (nextToken != Lexer.INT_LIT){
            // TODO
            // implement proper error
            System.out.println("Error: expected " + Lexer.IDENT + ", received " + nextToken + ". terminating program");
        }else{
            if (exitOutput) System.out.println("Terminal NUM found");
        }
        nextToken = Lexer.lex();
        if (exitOutput && fullOutput) System.out.println("Exit <num>");
    }

    // Rule 17: COMP      ---> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )

    static void comp() throws IOException {
        if (requiredOutput) System.out.println("COMP");
        if (nextToken != Lexer.LEFT_PAREN){
            // TODO throw error
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
            // TODO throw error comparison operator expected
        }

        if (nextToken != Lexer.RIGHT_PAREN){
            // TODO throw error
            System.out.println("Error Expected right parenthesis");
        } 
        nextToken = Lexer.lex();
        if (exitOutput) System.out.println("Exit <COMP>");
    }
    // Rule 18: TYPE      ---> int | float | double

    static void type() throws IOException {
        if (fullOutput) System.out.println("TYPE");
        if (nextToken != Lexer.INT_WORD && nextToken != Lexer.FLOAT && nextToken != Lexer.DOUBLE){
            System.out.println("No type found to parse.");
        }
        nextToken = Lexer.lex();
        if (exitOutput) System.out.println("Exit <TYPE>");
    }
    
    // Error reporting function
    static void error(String message) {
        System.out.println("Parse error: " + message);
        System.exit(1);
    }
    
        // Main entry point for parsing the Hawk script
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Parsing");
        Lexer.in_fp = new BufferedReader(new FileReader("hawk_script.txt"));
        
        while (nextToken != Lexer.PROGRAM && nextToken != Lexer.EOF){
            nextToken = Lexer.lex(); // Initialize lexer and get the first token
        }
        if (nextToken == Lexer.EOF){
            System.out.println("No program found to parse.");
        }
        if (nextToken == Lexer.PROGRAM){
            program(); // Start parsing the program
        }
        System.out.println("Parsing completed successfully!");
    }
}
