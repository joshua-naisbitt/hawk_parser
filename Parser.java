import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    static Lexer lexer = new Lexer();
    static int nextToken;

    static boolean fullOutput = true; // set to true for full trace 
    static boolean requiredOutput = false; // set to true for term project requirement output
    

    //mutually recursive procedures:
    // Call these for each EBNF Grammar rule

    // Rule 01: PROGRAM   ---> program DECL_SEC begin STMT_SEC end; | program begin STMT_SEC end;

    static void program() throws IOException {
        if (requiredOutput) System.out.println("PROGRAM");
        if (fullOutput) System.out.println("Enter <PROGRAM>");
        // Parse the DECL_SEC or skip straight to STMT_SEC if "begin" is found
        if (nextToken != Lexer.BEGIN){
            nextToken = Lexer.lex();
            decl_sec();
        } 
        nextToken = Lexer.lex();
        stmt_sec();
        if (fullOutput) System.out.println("Exit <PROGRAM>");
    }
    
    // Rule 02: DECL_SEC  ---> DECL | DECL DECL_SEC
    // TODO:
    // Redeclaration handling

    static void decl_sec() throws IOException {
        if (requiredOutput) System.out.println("DECL_SEC");
        if (fullOutput) System.out.println("Enter <DECL_SEC>");
        // Parse the first DECL
        decl();
        // As long as the next token is not "begin" decl_sec will recursively call (filling the entire declaration section)
        nextToken = Lexer.lex();
        while (nextToken != Lexer.BEGIN){
            decl_sec();
        } 
        if (fullOutput) System.out.println("Exit <DECL_SEC>");
    }
    
    // Rule 03: DECL      ---> ID_LIST : TYPE ;

    static void decl() throws IOException {
        if (requiredOutput) System.out.println("DECL");
        if (fullOutput) System.out.println("Enter <DECL>");
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
        if (fullOutput) System.out.println("Exit <DECL>");
    }

    // Rule 04: ID_LIST   ---> ID | ID , ID_LIST

    static void id_list() throws IOException {
        if (requiredOutput) System.out.println("ID_LIST");
        if (fullOutput) System.out.println("Enter <ID_LIST>");
        // Parse the first term
        id();
        // As long as the next token is "," get the next token and parse the next id
        while (nextToken == Lexer.COMMA) {
            nextToken = Lexer.lex();
            id_list();
        }
        if (fullOutput) System.out.println("Exit <ID_LIST>");
    }

    // Rule 05: ID        ---> (_ | a | b | ... | z | A | ... | Z) (_ | a | b | ... | z | A | ... | Z | 0 | 1 | ... | 9)*

    static void id() throws IOException {
        if (fullOutput) System.out.println("Enter <ID>");
        if (nextToken != Lexer.IDENT){
            // TODO
            // implement proper error
            System.out.println("Error: expected " + Lexer.IDENT + ", received " + nextToken + ". terminating program");
        }else{
            if (fullOutput) System.out.println("Terminal Identifier found");
        }
        nextToken = Lexer.lex();
        if (fullOutput) System.out.println("Exit <ID>");
    }

    // Rule 06: STMT_SEC  ---> STMT | STMT STMT_SEC

    static void stmt_sec() throws IOException {
        if (requiredOutput) System.out.println("STMT_SEC");
        if (fullOutput) System.out.println("Enter <STMT_SEC>");
        // Parse the first STMT
        stmt();
        // As long as the next token is not "end" stmt_sec will recursively call (filling the entire statement section)
        nextToken = Lexer.lex();
        while (nextToken != Lexer.END){
            stmt_sec();
        } 
        if (fullOutput) System.out.println("Exit <STMT_SEC>");
    }

    // Rule 07: STMT      ---> ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT

    static void stmt() throws IOException {

        // TODO implemenet STMT
        if (requiredOutput) System.out.println("STMT_SEC");
        if (fullOutput) System.out.println("Enter <STMT>");
        // TODO switch/case
        switch (nextToken) {
            case Lexer.IDENT:
                System.out.println("<assign> wip");
                // TODO implement
                break;
            case Lexer.IF:
                System.out.println("<ifstmt> wip");
                // TODO implement
                break;
            case Lexer.WHILE:
                System.out.println("<whilestmt> wip");
                // TODO implement
                break;
            case Lexer.INPUT:
                System.out.println("<input> wip");
                // TODO implement
                break;
            case Lexer.OUTPUT:
                System.out.println("<output> wip");
                // TODO implement
                break;
        }

        if (fullOutput) System.out.println("Exit <STMT>");
    }

    // Rule 08: ASSIGN    ---> ID := EXPR ;

    static void assign() throws IOException {

    }

    // Rule 09: IFSTMT    ---> if COMP then STMT_SEC end if ; | if COMP then STMT_SEC else STMT_SEC end if ;

    static void ifStmt() throws IOException {

    }

    // Rule 10: WHILESTMT ---> while COMP loop STMT_SEC end loop ;
    
    static void whileStmt() throws IOException {

    }

    // Rule 11: INPUT     ---> input ID_LIST;

    static void input() throws IOException {

    }

    // Rule 12: OUTPUT    ---> output ID_LIST | output NUM;

    static void output() throws IOException {

    }

    // Rule 13: EXPR      ---> FACTOR | FACTOR + EXPR | FACTOR - EXPR

    static void expr() throws IOException {

    }

    // Rule 14: FACTOR    ---> OPERAND | OPERAND * FACTOR | OPERAND / FACTOR

    static void factor() throws IOException {

    }

    // Rule 15: OPERAND   ---> NUM | ID | ( EXPR )

    static void operand() throws IOException {

    }

    // Rule 16: NUM       ---> (0 | 1 | ... | 9)+ [.(0 | 1 | ... | 9)+]

    static void num() throws IOException {

    }

    // Rule 17: COMP      ---> ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )

    static void comp() throws IOException {

    }
    // Rule 18: TYPE      ---> int | float | double

    static void type() throws IOException {
        if (fullOutput) System.out.println("Enter <TYPE>");
        if (nextToken != Lexer.INT_WORD && nextToken != Lexer.FLOAT && nextToken != Lexer.DOUBLE){
            System.out.println("No type found to parse.");
        }
        nextToken = Lexer.lex();
        if (fullOutput) System.out.println("Exit <TYPE>");
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
        if (nextToken == Lexer.PROGRAM){
            program(); // Start parsing the program
        }
        if (nextToken == Lexer.EOF){
            System.out.println("No program found to parse.");
        }
        System.out.println("Parsing completed successfully!");
    }
}
