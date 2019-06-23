package Analiselexer;

import static Analiselexer.Lexer.ContErroSintatico;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 *
 * [TODO]: tratar retorno 'null' do Lexer que esta sem Modo Panico
 *
 *
 * Modo Pânico do Parser: para tomar a decisao de escolher uma das regras
 * (quando mais de uma disponivel), temos que olhar para o FIRST(). Essa
 * informacao eh dada pela TP. Caso nao existe a regra na TP que corresponda ao
 * token da entrada, informamos uma mensagem de erro e inicia-se o Modo Panico:
 * [1] calculamos o FOLLOW do NAO-TERMINAL (a esquerda) da regra atual - esta no
 * topo da pilha; [2] se o token da entrada esta neste FOLLOW, desempilha-se o
 * nao-terminal atual - metodo synch(); [3] caso contrario, avancamos a entrada
 * para nova comparacao e mantemos o nao-terminal no topo da pilha (recursiva) -
 * metodo skip().
 *
 * O Modo Panico encerra-se, 'automagicamente', quando um token esperado (FIRST)
 * ou (FOLLOW) aparece.
 *
 *
 */
public class Parser {

    private final Lexer lexer;
    private Token token;
    private ArrayList<Tag> tagsSincronizantes;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        token = lexer.proxToken(); // Leitura inicial obrigatoria do primeiro simbolo
        System.out.println("[DEBUG] token: " + token.toString());

        tagsSincronizantes = new ArrayList<Tag>();

    }

    // Fecha os arquivos de entrada e de tokens
    public void fechaArquivos() {
        lexer.fechaArquivo();

    }

    public void erroSintatico(String mensagem) {

        System.out.print("[Erro Sintatico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
        System.out.println(mensagem + "\n");
        ContErroSintatico++;
    }

    public void erroSemantico(String mensagem, Token token) {

        System.out.print("[Erro Semantico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
        System.out.println(mensagem + "\n");
    }

    public void advance() {
        token = lexer.proxToken();
        System.out.println("[DEBUG] token: " + token.toString());
    }

    public void skip(String mensagem) {
        erroSintatico(mensagem);
        advance();
        ContErroSintatico++;
    }
    public void skip() {
        advance();
    }
    // verifica token esperado t
    public boolean eat(Tag t) {
        if (token.getClasse() == t) {
            advance();
            return true;
        } else {
            return false;
        }

    }

    public void sincronizaToken(String mensagem) {
        boolean casaToken = false;

        while (!casaToken && token.getClasse() != Tag.EOF) {
            if (tagsSincronizantes.contains(token.getClasse())) {
                casaToken = true;
            } else {
                skip();
            }
        }
        tagsSincronizantes.clear(); // limpa a lista para a proxima sincronizacao
    }

    /* LEMBRETE:
   // Todas as decisoes do Parser, sao guiadas
   // pela Tabela Preditiva.
   //
     */
    //First--> Follow
    // Programa → Classe $ 
    //first-->public
    //follow-->$
    public void Programa() {
        if (token.getClasse() != Tag.KW_public) // TP[Programa][public]
        {
            skip("Esperado \"public\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        Classe();
        if (!eat(Tag.EOF)) // TP[Programa][public]
        {
            erroSintatico("Esperado \"EOF\", encontrado " + "\"" + token.getLexema() + "\"");

        }

    }

    // Classe → "public" "class" ID "{" ListaMetodo Main "}"
    //first--> public 
    //follow--> $
    public void Classe() {
        if (eat(Tag.KW_public)) {
            //skip("Esperado \"public\", encontrado " + "\"" + token.getLexema() + "\"");
            if (!eat(Tag.KW_class)) {
                erroSintatico("Esperado \"class\", encontrado " + "\"" + token.getLexema() + "\"");
            }

            Token t = this.token;
            if (eat(Tag.ID)) { // espera "ID"
                t.setTipo(No.TIPO_VAZIO);
                lexer.tabelaSimbolos.put(t.getLexema(), t);
                
            }
            else {
                erroSintatico("Esperado um \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            }
           
            if (!eat(Tag.SMB_ABCH)) {
                erroSintatico("Esperado \"{\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            ListaMetodo();
            Main();

               if (!eat(Tag.SMB_FCCH)) {
                erroSintatico("Esperado \"}\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        } else {
            erroSintatico("Esperado \"public\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Classe)
            tagsSincronizantes.add(Tag.EOF);
            sincronizaToken("[Modo Panico] Esperado \"EOF\", encontrado " + token.getLexema()); // Follow(Classe)

        }
        
    }

    //DeclaracaoVar → Tipo ID ";"
    // first--> int, boolean, String, float, void
    //follow-->boolean, int, string, float, void, if, while,print, println, ID, return, }
    public void DeclaraVar() {
      //  No noDeclaraVar = new No(null);
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float || token.getClasse() == Tag.KW_void) {
            No noTipo = Tipo();
            Token t = this.token;
            if (eat(Tag.ID)) {
                Token id = lexer.tabelaSimbolos.retornaToken(t.getLexema());
                if(id!=null){
                    erroSemantico("variavel:"+ t.getLexema()+" duplicada",t);
                }
                else {
                    t.setTipo(noTipo.tipo);
                    lexer.tabelaSimbolos.put(t.getLexema(),t);
                }
            
            }
            else {
                 erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");

            }
               
            }
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }

         //follow-->boolean, int, string, float, void, if, while,print, println, ID, return, }
        else if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int
                || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float
                || token.getClasse() == Tag.KW_void || token.getClasse() == Tag.KW_if
                || token.getClasse() == Tag.KW_while || token.getClasse() == Tag.KW_print
                || token.getClasse() == Tag.KW_println || token.getClasse() == Tag.ID
                || token.getClasse() == Tag.KW_return || token.getClasse() == Tag.SMB_FCCH) {
            //  erroSintatico("Esperado \"int, boolean, String, float, void \", encontrado " + "\"" + token.getLexema() + "\"");
            return;

        } else {
            erroSintatico("Esperado \"int, boolean, String, float, void\", encontrado " + "\"" + token.getLexema() + "\"");
            tagsSincronizantes.add(Tag.KW_boolean);
            tagsSincronizantes.add(Tag.KW_int);
            tagsSincronizantes.add(Tag.KW_string);
            tagsSincronizantes.add(Tag.KW_float);
            tagsSincronizantes.add(Tag.KW_void);
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
            tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
            tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
            tagsSincronizantes.add(Tag.SMB_FCCH);
            sincronizaToken("[Modo Panico] Esperado \"boolean, int, string,float,void,if,while,print,println,id,return,}\", encontrado " + token.getLexema());
        }
        
    }

    //ListaMetodo → ListaMetodo’ 
    //first-->ε, boolean, int, string, float, void
    //follow-->public
   public No ListaMetodo() {
    No noListaMetodo = new No(null);
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float || token.getClasse() == Tag.KW_void) {
            ListaMetodoLinha();
        } //follow-->public
        else if (token.getClasse() == Tag.KW_public) {
            return noListaMetodo;
        } else {
            skip("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaMetodo();
            }
        }
        return noListaMetodo;
    }

    //ListaMetodo’ → Metodo ListaMetodo’ 5 | ε 6
    //first-->ε, boolean, int, string, float, void
    //follow--> public
    public void ListaMetodoLinha() {
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float || token.getClasse() == Tag.KW_void) {
            Metodo();
            ListaMetodoLinha();
        }//follow--> public
        else if (token.getClasse() == Tag.KW_public) {
            return;
        } else {
            skip("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaMetodoLinha();
            }
        }
    }

    //Metodo → Tipo ID "(" RegexListaParam ")" "{" RegexDeclaraVar ListaCmd Retorno "}" 
    //fist-->boolean, int, string, float, void
    //follow--> if, while, print, println, ID, public
    public No Metodo() {
        No noMetodo = new No(null);
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float || token.getClasse() == Tag.KW_void) {
            No noTipo = Tipo();
            Token t = this.token;
            if (eat(Tag.ID)) {
                Token id = lexer.tabelaSimbolos.retornaToken(t.getLexema());
                if(id!=null){
                                        erroSemantico("variavel:"+ t.getLexema()+" duplicada",t);

                }
                else {
                    id.setTipo(noTipo.tipo);
                    lexer.tabelaSimbolos.put(id.getLexema(),t);
                }
            
            }
            else {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");    
            }
            if (!eat(Tag.SMB_OP)) {
                erroSintatico("Esperado um \"(\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            RegexListaParam();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado um \")\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            if (!eat(Tag.SMB_ABCH)) {
                erroSintatico("Esperado \"{\", encontrado " + "\"" + token.getLexema() + "\"");
            }
           RegexListaParam();
          
            ListaCmd();
            No noRetorno = Retorno();
            if (!eat(Tag.SMB_FCCH)) {
                erroSintatico("Esperado \"}\", encontrado " + "\"" + token.getLexema() + "\"");
            }
             if (noRetorno.tipo != No.TIPO_ERRO) {
                 erroSemantico("sinalizar erro para tipo de retorno incompativel" + 
                " <"+noRetorno.tipo+","+noMetodo.tipo+">", noMetodo.getPai());
         } 
            //follow--> if, while, print, println, ID, public
        }  else {
            erroSintatico("Esperado \"boolean, int, string, float, void\", encontrado " + "\"" + token.getLexema() + "\"");
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
            tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
            tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_public);

            sincronizaToken("[Modo Panico] Esperado \"if,while,print,println,id,public\", encontrado " + token.getLexema());

        }
        return noMetodo;
    }

    //RegexListaParam → ListaParam 8 | ε 9
    //fist-->ε, boolean, int, string, float, void
    //follow--> )
   public void RegexListaParam() {
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int
                || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float
                || token.getClasse() == Tag.KW_void) {
            ListaParam();
            //follow--> )
        } else if (token.getClasse() == Tag.SMB_CP) {
            return;

        } else {
            skip("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                RegexListaParam();
            }
        }
    }

    //RegexDeclaraVar → DeclaracaoVar RegexDeclaraVar 10 | ε 11
    //first-->ε, boolean, int, string, float, void
    //follow-->if, while, print, println, ID, return, }
   public void RegexDeclaraVar() {
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int
                || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float
                || token.getClasse() == Tag.KW_void) {
            DeclaraVar();
            RegexDeclaraVar();
            //FOLLOW--> if, while, print, println, ID, return, }
        } else if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while || token.getClasse() == Tag.KW_print
                || token.getClasse() == Tag.KW_println || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                || token.getClasse() == Tag.SMB_FCCH) {
            return;
        } else {
            skip("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                RegexDeclaraVar();
            }
        }
    }
//ListaParam → Param ListaParam’ 12
//first-->boolean, int, string, float, void
//follow--> )   

   public void ListaParam() {
        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int
                || token.getClasse() == Tag.KW_string || token.getClasse() == Tag.KW_float
                || token.getClasse() == Tag.KW_void) {
            Param();
            ListaParamLinha();
        } else {
            // synch: FOLLOW(ListaParam)
            if (token.getClasse() == Tag.SMB_CP) {
                erroSintatico("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
                skip("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    ListaParam();
                }
            }
        }
    }

//ListaParam’ → ”, ” ListaParam 13 | ε 14
//first--> ε, “,”
//follow--> )
//Estou aqui
   public void ListaParamLinha() {
        if (eat(Tag.SMB_VIR)) {
            ListaParam();
        } //follow--> ).
        else if (token.getClasse() == Tag.SMB_CP) {
            return;
        } else {
            skip("Esperado \" ," + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaParamLinha();
            }
        }
    }
    //Param → Tipo ID 15
    //first-->boolean, int, string, float, void
    //follow-->“,”, )
    public No Param() {
        No noParam = new No(null);

        if (token.getClasse() == Tag.KW_boolean || token.getClasse() == Tag.KW_int || token.getClasse() == Tag.KW_string
                || token.getClasse() == Tag.KW_float || token.getClasse() == Tag.KW_void) {
            No noTipo=Tipo();
                Token t = this.token;
           
            if (eat(Tag.ID)) {
                Token id = lexer.tabelaSimbolos.retornaToken(t.getLexema());
                if(id!=null){
                                        erroSemantico("variavel:"+ t.getLexema()+" duplicada",t);

                }
                else {
                    id.setTipo(noTipo.tipo);
                    lexer.tabelaSimbolos.put(id.getLexema(),t);
                }
            
            }
            else {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");    
            }
            
        }//follow--> “,”, )
        else if (token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_VIR) {
                //erroSintatico("Esperado \"Boolean | Int | String | Float | Void \", encontrado " + "\"" + token.getLexema() + "\"");
                return noParam;
            } else {
               erroSintatico("Esperado \"Interge, Float, String, Boolean, Void\", encontrado " + "\"" + token.getLexema() + "\"");
               tagsSincronizantes.add(Tag.SMB_CP);
               tagsSincronizantes.add(Tag.SMB_VIR);
            sincronizaToken("[Modo Panico] Esperado \"), virgula\", encontrado " + token.getLexema());

            }
        return noParam;
    }

//Retorno → "return" Expressao";" 16 | ε 17
    //first--> ε, return
    //follow--> }
    public No Retorno() {
        No noRetorno = new No(null);

        if (eat(Tag.KW_return)) {
           No  noExpressao=Expressao();
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }
           noRetorno.tipo=noExpressao.tipo;
        } //follow--> } 
        else if (token.getClasse() == Tag.SMB_FCCH) {
            noRetorno.tipo=No.TIPO_VAZIO;
            return noRetorno;
        } else {
               erroSintatico("Esperado \"return\", encontrado " + "\"" + token.getLexema() + "\"");
               tagsSincronizantes.add(Tag.SMB_FCCH);
            sincronizaToken("[Modo Panico] Esperado \"}\", encontrado " + token.getLexema());

        }
       return noRetorno;
    }

    //Main → public | static | void | main | ( | |) | { |  RegexDeclaraVar ListaCmd |}| 18
    //first-->public
    //follow-->}
     public No Main() {
         No noMain = new No(null);
         
        if (!eat(Tag.KW_public)) {
            erroSintatico("Esperado \"public\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.KW_static)) {
            erroSintatico("Esperado \"static\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.KW_void)) {
            erroSintatico("Esperado \"void\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.KW_main)) {
            erroSintatico("Esperado \"main\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"( \", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \") \", encontrado " + "\"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_ABCH)) {
            erroSintatico("Esperado \"{\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        RegexDeclaraVar();
        ListaCmd();
        if (!eat(Tag.SMB_FCCH)) {
            erroSintatico("Esperado \"}\", encontrado " + "\"" + token.getLexema() + "\"");

        } else {
            if (token.getClasse() == Tag.SMB_FCCH) {
                //erroSintatico("Esperado \"}\", encontrado " + "\"" + token.getLexema() + "\"");
                return noMain;
            } else {
                skip("Esperado \"  Public\" " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    Main();
                }
            }
        }
        return noMain;
    }


    // Tipo --> boolean" | "int" | "string" | "float" | "void"
    //first-->boolean, int, string, float, void
    //follow-->ID
    public No Tipo() {
        No noTipo = new No(null);
        
        if (eat(Tag.KW_int)) {
            noTipo.tipo = No.TIPO_NUMERICO;
        }
        else if (eat(Tag.KW_boolean)) {
          //  noTipo.setPai(t);
            noTipo.tipo = No.TIPO_LOGICO;
        }
        else if (eat(Tag.KW_string)) {
            //noTipo.setPai(t);
            noTipo.tipo = No.TIPO_STRING;
        }
        else if (eat(Tag.KW_float)) {
            //noTipo.setPai(t);
            noTipo.tipo = No.TIPO_NUMERICO;
        }
        else if (!eat(Tag.KW_void)) {
            //noTipo.setPai(t);
            noTipo.tipo = No.TIPO_VAZIO;
        } 
        else {
            erroSintatico("Esperado \"Int, boolean, String, float,void\", encontrado " + token.getLexema());

            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.ID);
            sincronizaToken("[Modo Panico] Esperado \"ID\", encontrado " + token.getLexema());
        }
        return noTipo;
    }

    //ListaCmd → ListaCmd’
    //first-->ε, if, while, print, println, ID
    //follow-->return, }
      public void ListaCmd() {
        //fist--> ListaCmd' ε, if, while, print, println, ID
        if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                || token.getClasse() == Tag.KW_print
                || token.getClasse() == Tag.KW_println || token.getClasse() == Tag.ID) {
            ListaCmdLinha();
            //follow--> return, }   
        } else if (token.getClasse() == Tag.KW_return || token.getClasse() == Tag.SMB_FCCH) {
            return;
        } else {
            skip("Esperado \"if, while, print, println, ID \", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaCmd();
            }
        }
    }
//ListaCmd’ → Cmd ListaCmd’ 25 | ε 26
// first--> ε, if, while, print, println, ID
//follow-->return, }
 public void ListaCmdLinha() {
        if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                || token.getClasse() == Tag.ID) {
            Cmd();
            ListaCmdLinha();
        } //follow-->return, }
        else if (token.getClasse() == Tag.KW_return || token.getClasse() == Tag.SMB_FCCH) {
            return;
        } else {
            skip("Esperado \"if, while, print , println , ID\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaCmdLinha();
            }
        }
    }///Cmd → CmdIF 27 | CmdWhile 28 | CmdPrint 29 | CmdPrintln 30 | ID Cmd’ 31
//first-->if, while, print, println, ID
    //follow-->if, while, print, println, ID, return, }

    public void Cmd() {
      //  No noCmd = new No(null);
              Token t = this.token;

        //first--> cmdIF
        if (eat(Tag.ID)) {
            No noCmdLinha=CmdLinha();
          //  No noID = new No(null);
            Token id = lexer.tabelaSimbolos.retornaToken(t.getLexema());
            if (id == null) {
         	//noCmd.tipo = No.TIPO_ERRO;
              erroSemantico("Variavel " + t.getLexema() + " nao declarada", t);
         } 
           else if(noCmdLinha.tipo!=No.TIPO_VAZIO&&id.getTipo()!=noCmdLinha.tipo){
                         erroSemantico("Variavel " +t.getLexema()+ "\nsinalizar erro de atribuição incompatível",t);
           }
        }
        //falta fazer
        else if (token.getClasse() == Tag.KW_if) {

           cmdIf();

        } else if (token.getClasse() == Tag.KW_println) {

         cmdPrintln();

        } else if (token.getClasse() == Tag.KW_while) {
           cmdWhile();

        } else if (token.getClasse() == Tag.KW_print) {
           cmdPrint();

        } //follow--> if, while, print, println, ID, return, }
        else {
            if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while || token.getClasse() == Tag.KW_print
                    || token.getClasse() == Tag.KW_println || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
                //erroSintatico("Esperado \"if, while, print, println , ID\", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
            erroSintatico("Esperado \"if, while, print, println, ID\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
             tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
             tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
             tagsSincronizantes.add(Tag.SMB_FCCH);
            
            sincronizaToken("[Modo Panico] Esperado \"if, while, print, println, ID, return, }\", encontrado " + token.getLexema());

            }
        }
        
    }

    //Cmd’ → CmdAtrib 32 | CmdMetodo 33
    //first--> =,(
    //follow-->if, while, print, println, ID, return, }
    public No CmdLinha() {
        No noCmdLinha = new No(null);

        if (token.getClasse() == Tag.RELOP_ASSIGN) {

            No noCmdAtrib=CmdAtrib();

         //follow-->if, while, print, println, ID, return, }
            noCmdLinha.tipo=noCmdAtrib.tipo;
           
            
        }
        else if (token.getClasse() == Tag.SMB_OP) {
           cmdMetodo();
           noCmdLinha.tipo=No.TIPO_VAZIO;
                
        } else {
            if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
                //erroSintatico("Esperado \"= , (\", encontrado " + "\"" + token.getLexema() + "\"");
                return noCmdLinha;
            } else {
                
            erroSintatico("Esperado \"=,(\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
             tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
             tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
             tagsSincronizantes.add(Tag.SMB_FCCH);
            
            sincronizaToken("[Modo Panico] Esperado \"if, while, print, println, ID, return, }\", encontrado " + token.getLexema());

            }
        }
        return noCmdLinha;
    }
//CmdIF → "if" "(" Expressao ")" "{" Cmd "}" CmdIF’ 34
//fisrt--> if
    //follow--> if, while, print, println, ID, return, }

    public No cmdIf() {
        No noCmdIf = new No(null);

        if (eat(Tag.KW_if)) {
            if (!eat(Tag.SMB_OP)) {
                erroSintatico("Esperado \" ( \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Token t = this.token;
            No noExpressao=Expressao();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \" ) \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            if(noExpressao.tipo!=No.TIPO_LOGICO){
                 erroSemantico("Erro" + t.getLexema(), t);
            }
            if (!eat(Tag.SMB_ABCH)) {
                erroSintatico("Esperado \" { \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Cmd();
            if (!eat(Tag.SMB_FCCH)) {
                erroSintatico("Esperado \" } \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            cmdIfLinha();
        } else if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
               // erroSintatico("Esperado \"if\", encontrado " + "\"" + token.getLexema() + "\"");
                return noCmdIf;
            } 
        else {
                erroSintatico("Esperado \"if\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
             tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
             tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
             tagsSincronizantes.add(Tag.SMB_FCCH);
            
            sincronizaToken("[Modo Panico] Esperado \"if, while, print, println, ID, return, }\", encontrado " + token.getLexema());

                }
            

        return noCmdIf;
    }
//CmdIF’ → "else" "{" Cmd "}" 35 | ε 36
//fist-->else, ε
    //follow-->if, while, print, println, ID, return, }

     public void cmdIfLinha() {
        if (eat(Tag.KW_else)) {
            if (!eat(Tag.SMB_ABCH)) {
                erroSintatico("Esperado \"{ \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Cmd();
            if (!eat(Tag.SMB_FCCH)) {
                erroSintatico("Esperado \" } \", encontrado " + "\"" + token.getLexema() + "\"");
            }

        } //follow-->if, while, print, println, ID, return, }
        else if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                || token.getClasse() == Tag.SMB_FCCH) {
            return;
        } else {
            skip("Esperado \" else \", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                cmdIfLinha();
            }
        }
    }
//CmdWhile → "while" "(" Expressao ")" "{" Cmd "}" 37
//fist-->while
    //follow-->if, while, print, println, ID, return, }

    public No cmdWhile() {
        No nocmdWhile = new No(null);

        if (eat(Tag.KW_while)) {
            if (!eat(Tag.SMB_OP)) {
                erroSintatico("Esperado \" (  \", encontrado " + "\"" + token.getLexema() + "\"");
            }
              Token t = this.token;
            No noExpressao=Expressao();
            if(noExpressao.tipo!=No.TIPO_LOGICO){
                 erroSemantico("Erro " + t.getLexema(), t);

            }
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \" ) \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            if (!eat(Tag.SMB_ABCH)) {
                erroSintatico("Esperado \" { \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Cmd();
            if (!eat(Tag.SMB_FCCH)) {
                erroSintatico("Esperado \" } \", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }//follow-->if | while | print | println | ID | return | }
        else if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
               // erroSintatico("Esperado \"while\", encontrado " + "\"" + token.getLexema() + "\"");

                return nocmdWhile;
            } else {
                erroSintatico("Esperado \"while\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
             tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
             tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
             tagsSincronizantes.add(Tag.SMB_FCCH);
            
            sincronizaToken("[Modo Panico] Esperado \"if, while, print, println, ID, return, }\", encontrado " + token.getLexema());

            }
        return nocmdWhile;
    }
//CmdPrint → "print" "(" Expressao ")" ";" 38
//fist-->print
    //follow-->if, while, print, println, ID, return, }
public void cmdPrint() {
        if (eat(Tag.KW_print)) {
            if (!eat(Tag.SMB_OP)) {
                erroSintatico("Esperado \" (  \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Expressao();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \" )  \", encontrado " + "\"" + token.getLexema() + "\"");

            }
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \" ; \", encontrado " + "\"" + token.getLexema() + "\"");

            } //follow-->if, while, print, println, ID, return, }
        } else {

            if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
                erroSintatico("Esperado \"print\", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
                skip("Esperado \" print \", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    cmdPrint();
                }
            }
        }
    }
//CmdPrintln → "println" "(" Expressao ")" ";" 39
//fist-->println
    //follow-->if, while, print, println, ID, return, }

    public void cmdPrintln() {
        if (eat(Tag.KW_println)) {
            if (!eat(Tag.SMB_OP)) {
                erroSintatico("Esperado \"( \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            Expressao();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \" ) \", encontrado " + "\"" + token.getLexema() + "\"");

            }
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \" ; \", encontrado " + "\"" + token.getLexema() + "\"");

            } //follow-->if | while | print | println | ID | return | }
        } else {

            if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
                erroSintatico("Esperado \" println\", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
                skip("Esperado \"println\", encontrado " + "\"" + token.getLexema() + "\"");

                if (token.getClasse() != Tag.EOF) {
                    cmdPrintln();
                }
            }
        }
    }
    //CmdAtrib → "=" Expressao ";" 40
    //fist-->=
    //follow-->if, while, print, println, ID, return, }
    public No CmdAtrib() {
        No noCmdAtrib = new No(null);

        if (eat(Tag.RELOP_ASSIGN)) {
           No noExpressao= Expressao();
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \" ; \", encontrado " + "\"" + token.getLexema() + "\"");
            }
            noCmdAtrib.tipo=noExpressao.tipo;
            
        } else if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print
                    || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
               // erroSintatico("Esperado \" = \", encontrado " + "\"" + token.getLexema() + "\"");
                return noCmdAtrib;
            } else {
                  erroSintatico("Esperado \"=\", encontrado " + token.getLexema());
            // token sincronizante: FOLLOW(Tipo)
            tagsSincronizantes.add(Tag.KW_if);
            tagsSincronizantes.add(Tag.KW_while);
             tagsSincronizantes.add(Tag.KW_print);
            tagsSincronizantes.add(Tag.KW_println);
             tagsSincronizantes.add(Tag.ID);
            tagsSincronizantes.add(Tag.KW_return);
             tagsSincronizantes.add(Tag.SMB_FCCH);
            
            sincronizaToken("[Modo Panico] Esperado \"if, while, print, println, ID, return, }\", encontrado " + token.getLexema());

            }
        
        return noCmdAtrib;
    }

//CmdMetodo → "(" RegexExp4 ")" ";" 41
//fist-->(
//follow-->if, while, print, println, ID, return, }
     public void cmdMetodo() {
        if (eat(Tag.SMB_OP)) {
            RegexExp4();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        } else {
            if (token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while
                    || token.getClasse() == Tag.KW_print || token.getClasse() == Tag.KW_println
                    || token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_return
                    || token.getClasse() == Tag.SMB_FCCH) {
                erroSintatico("Esperado \"( \", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
                skip("Esperado \"(  \", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    cmdMetodo();
                }
            }
        }
    }
    //Expressao → Exp1 Exp’ 42
    //first--> ID, ConstInteira, contFloat, ConstString, true, false,“-” (negação), “!”, (
    //follow--> ; ,  ) , ","
    //ID, ConstInteira, ConstReal, ConstString, true, false,“-” (negação), “!”, (
    public No Expressao() {
        No noExpressao= new No(null);
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira
                || token.getClasse() == Tag.contFloat || token.getClasse() == Tag.ConstString
                || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO
                || token.getClasse() == Tag.SMB_OP) {

            No noExpressao1= Expressao1();
            No noExpressaoLinha= ExpressaoLinha();
          
            if (noExpressaoLinha.tipo==No.TIPO_VAZIO) {
                noExpressao.tipo=noExpressao1.tipo;
            }
            
            else if(noExpressaoLinha.tipo == noExpressao1.tipo && noExpressao1.tipo == No.TIPO_LOGICO) {
                noExpressao.tipo = No.TIPO_LOGICO;
            } 
            else {
                noExpressao.tipo = No.TIPO_ERRO;
                erroSemantico("Comparacao logica entre tipos incompativeis" + 
                " <"+noExpressaoLinha.tipo+","+noExpressao1.tipo+">", noExpressao1.getPai());
               // return noExpressaoLinha;
            }
            
             return noExpressao;
        } 
         //else if (token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
            //        || token.getClasse() == Tag.SMB_VIR) {
             
            //  erroSintatico("Esperado \"ID, int, float, string, true, false,“-” (negação), “!”, (\", encontrado " + "\"" + token.getLexema() + "\"");
    
else {
              erroSintatico("Esperado \"Inteito, Double, String, ID\", encontrado " + token.getLexema());
			
			// token sincronizante: FOLLOW(Expressao)
			tagsSincronizantes.add(Tag.SMB_CP);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
                        tagsSincronizantes.add(Tag.SMB_VIR);    
			sincronizaToken("[Modo Panico] Esperado \") , ;\", encontrado " + token.getLexema());
            }
return new No(null);
    }

    //ExpressaoLinha → "&&" Exp1 Exp’ 43 | "||" Exp1 Exp’ 44 | ε 45
    //first-->&&, ||, ε
    //follow-->“;”, ), “,”
    public No ExpressaoLinha() {
        No noExpressaoLinhaPai = new No(null);
        if (eat(Tag.OP_AND) || eat(Tag.OP_OR)) {
           No noExpressao1= Expressao1();
           No noExpressaoLinhafilho = ExpressaoLinha();
           if(noExpressaoLinhafilho.tipo == No.TIPO_VAZIO && noExpressao1.tipo == No.TIPO_LOGICO){
         	noExpressaoLinhaPai.tipo = No.TIPO_LOGICO;
         } else if (noExpressaoLinhafilho.tipo == noExpressao1.tipo &&noExpressao1.tipo == No.TIPO_LOGICO) {
         	noExpressaoLinhaPai.tipo = No.TIPO_LOGICO;
         } else {
         	noExpressaoLinhaPai.tipo = No.TIPO_ERRO;
         }
         return noExpressaoLinhaPai;
        } else if (token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
                || token.getClasse() == Tag.SMB_VIR) {
            noExpressaoLinhaPai.tipo=No.TIPO_VAZIO;
            return noExpressaoLinhaPai;

        } else {
            erroSintatico("Esperado \"&&, || \", encontrado " + "\"" + token.getLexema() + "\"");
			
			// token sincronizante: FOLLOW(Expressao')
			tagsSincronizantes.add(Tag.SMB_CP);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
                        tagsSincronizantes.add(Tag.SMB_VIR);

    sincronizaToken("[Modo Panico] Esperado \"), ;\", encontrado " + token.getLexema());		
		
        }
        return noExpressaoLinhaPai;   
    }


    //Expressao1 → Exp2 Exp1’ 46
    //fist-->ID, ConstInteira, ConstReal, ConstString, true, false,“-” (negação), “!”, (
    //follow-->&&, ||, “;”, ), “,”
    public No Expressao1() {
        //first-->ID | ConstInteira | ConstReal | ConstString | true | false | “-” (negação) | “!” | (
        No noExpressao1= new No(null);
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira
                || token.getClasse() == Tag.contFloat || token.getClasse() == Tag.ConstString
                || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO
                || token.getClasse() == Tag.SMB_OP) {
            No noExpressao2= Expressao2();
            No noExpressao1Linha= Expressao1Linha();
           
				if(noExpressao1Linha.tipo == No.TIPO_VAZIO) {
					noExpressao1.tipo = noExpressao2.tipo;
				}
				else if(noExpressao1Linha.tipo == noExpressao2.tipo && noExpressao1Linha.tipo == No.TIPO_NUMERICO) {
					noExpressao1.tipo = No.TIPO_LOGICO;
				}
				else {
					noExpressao1.tipo = No.TIPO_ERRO;
               erroSemantico("Operacao entre tipos incompativeis" + 
               " <"+noExpressao1Linha.tipo+","+noExpressao2.tipo+">", noExpressao2.getPai()); // segundo parametro p/ identificar a linha e coluna
				}
            
       
            return noExpressao1;
        }
        else {
                erroSintatico("Esperado \"ID, ConstInteira, ConstReal, ConstString, true, false,“-” (negação), “!”, ( \", encontrado " + "\"" + token.getLexema() + "\"");
                        tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.OP_AND);
			tagsSincronizantes.add(Tag.SMB_CP);
			tagsSincronizantes.add(Tag.SMB_VIR);
			
			sincronizaToken("[Modo Panico] Esperado \"||, &&, , ) , ;\", encontrado " + token.getLexema());     
                        //return noExpressao2;
                }

		return new No(null);
    }

    //Expressao1Linha--> "<" Exp2 Exp1’  | "<=" Exp2 Exp1’ | ">" Exp2 Exp1’ | ">=" Exp2 Exp1’  | "==" Exp2 Exp1’  | "!=" Exp2 Exp1’  | ε 
    //fist--> <, <=, >, >=, ==, !=, ε
    //follow-->&&, ||, “;”, ), “,”
    public No Expressao1Linha() {
        No noExpressao1LinhaPai = new No(null);
              Token t = this.token;
        if (eat(Tag.RELOP_LT) || eat(Tag.RELOP_LE) || eat(Tag.RELOP_GT)
                || eat(Tag.RELOP_GE) || eat(Tag.RELOP_EQ) || eat(Tag.RELOP_NE)) {
            No noExpressao2= Expressao2();
            No noExpressao1LinhaFilho= Expressao1Linha();
            
            if (noExpressao1LinhaFilho.tipo == No.TIPO_VAZIO && noExpressao2.tipo == No.TIPO_NUMERICO ) {
         	noExpressao1LinhaPai.tipo =  No.TIPO_NUMERICO;
         } else if (noExpressao1LinhaFilho.tipo == noExpressao2.tipo && noExpressao2.tipo == No.TIPO_NUMERICO) {
         	noExpressao1LinhaPai.tipo = No.TIPO_NUMERICO;
         } else {
         	noExpressao1LinhaPai.tipo = No.TIPO_ERRO;
         }
          return noExpressao1LinhaPai;
        } else if (token.getClasse() == Tag.OP_OR || token.getClasse() == Tag.OP_AND
                || token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
                || token.getClasse() == Tag.SMB_VIR) {
            noExpressao1LinhaPai.tipo= No.TIPO_VAZIO;
            return noExpressao1LinhaPai;
        } else {
            erroSintatico("Esperado \"<,<=,>,>=, ==, !=\", encontrado " + "\"" + token.getLexema() + "\"");
			tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.OP_AND);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.SMB_CP);
			tagsSincronizantes.add(Tag.SMB_VIR);
			
	sincronizaToken("[Modo Panico] Esperado \"||, &&, ;, ), ,\", encontrado " + token.getLexema());
        }
        return noExpressao1LinhaPai;
    }


    //Expressao2-->Exp3 Exp2’
    //fist-->ID, ConstInteira, constFloat, ConstString, true, false,“-” (negação), “!”, (
    //follow--> <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”
    public No Expressao2() {
        No noExpressao2= new No (null);
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira
                || token.getClasse() == Tag.contFloat || token.getClasse() == Tag.ConstString
                || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO
                || token.getClasse() == Tag.SMB_OP) {
           No noExpressao3=Expressao3();
           No noExpressao2Linha=Expressao2Linha();
         	if (noExpressao2Linha.tipo == No.TIPO_VAZIO) {
         		noExpressao2.tipo =noExpressao3.tipo;
         	}
         	else if(noExpressao2Linha.tipo == noExpressao3.tipo && noExpressao2Linha.tipo == No.TIPO_NUMERICO) {
         		noExpressao2.tipo = No.TIPO_NUMERICO;
         	}
         	else {
         	noExpressao2.tipo = No.TIPO_ERRO;
            	erroSemantico("Operacao entre tipos incompativeis" + 
            	" <"+noExpressao2Linha.tipo+","+noExpressao3.tipo+">", noExpressao3.getPai());
         	}  
       
           return noExpressao3;
        }//follow--> <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”
        else                 erroSintatico("Esperado \"ID, ConstInteira, constFloat, ConstString, true, false,“-” (negação), “!”, ( \", encontrado " + "\"" + token.getLexema() + "\"");
			
			// token sincronizante: FOLLOW(Expressao2)
			tagsSincronizantes.add(Tag.RELOP_LT);
			tagsSincronizantes.add(Tag.RELOP_LE);
			tagsSincronizantes.add(Tag.RELOP_GT);
			tagsSincronizantes.add(Tag.RELOP_GE);
			tagsSincronizantes.add(Tag.RELOP_EQ);
			tagsSincronizantes.add(Tag.RELOP_NE);
			tagsSincronizantes.add(Tag.OP_AND);
			tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.SMB_CP);
                        tagsSincronizantes.add(Tag.SMB_VIR);
                        
			sincronizaToken("[Modo Panico] Esperado \"<, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”\", encontrado " + token.getLexema());		
		           
            		return new No(null);

        }
 
    
    //Expressao2Linha → + Exp3 Exp2’ 55 | - Exp3 Exp2’ 56 | ε 57
    //fist-->+, -, ε
    //follow--><, <=, >, >=, ==, !=, &&, ||, ;, ), “,”
    public No Expressao2Linha() {
        No noExpressao2LinhaPai = new No(null);
              Token t = this.token;


        if (eat(Tag.RELOP_SUM) || eat(Tag.RELOP_MINUS)) {
            No noExpressao3= Expressao3();
            No noExpressao2LinhaFilho= Expressao2Linha();
            if (noExpressao2LinhaFilho.tipo == No.TIPO_VAZIO && noExpressao3.tipo == No.TIPO_NUMERICO) {
         	noExpressao2LinhaPai.tipo = No.TIPO_NUMERICO;
         } else if (noExpressao2LinhaFilho.tipo == noExpressao3.tipo &&  noExpressao3.tipo == No.TIPO_NUMERICO) {
         	noExpressao2LinhaPai.tipo = No.TIPO_NUMERICO;
         } else {
         	noExpressao2LinhaPai.tipo = No.TIPO_ERRO;
         }
            
                
         return noExpressao2LinhaPai;
        } else if (token.getClasse() == Tag.RELOP_LT || token.getClasse() == Tag.RELOP_LE
                || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_GE
                || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE
                || token.getClasse() == Tag.OP_AND
                || token.getClasse() == Tag.OP_OR || token.getClasse() == Tag.SMB_SEMICOLON
                || token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_VIR) {
            return noExpressao2LinhaPai;
        } else {
            erroSintatico("Esperado \"+, -, ε\", encontrado " + token.getLexema());
			
			// token sincronizante: FOLLOW(Expressao2')
			tagsSincronizantes.add(Tag.RELOP_LT);
			tagsSincronizantes.add(Tag.RELOP_LE);
			tagsSincronizantes.add(Tag.RELOP_GT);
			tagsSincronizantes.add(Tag.RELOP_GE);
			tagsSincronizantes.add(Tag.RELOP_EQ);
			tagsSincronizantes.add(Tag.RELOP_NE);
			tagsSincronizantes.add(Tag.OP_AND);
			tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.SMB_CP);
                        tagsSincronizantes.add(Tag.SMB_VIR);
			sincronizaToken("[Modo Panico] Esperado \"<, <=, >, >=, ==, !=, &&, ||, ;, ), “,”\", encontrado " + token.getLexema());
		
		
        }
        return noExpressao2LinhaPai;
    }
//Expressao3 → Exp4 Exp3’ 58
//fist → ID, , ConstInteira, constFloat, ConstString, true, false,“-” (negação), !, (
//follow-->+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”

    public No Expressao3() {
        No noExpressao3= new No(null);
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira
                || token.getClasse() == Tag.contFloat || token.getClasse() == Tag.ConstString
                || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO
                || token.getClasse() == Tag.SMB_OP) {
            No noExpressao4= Expressao4();
            No noExpressao3linha= Expressao3linha();
            
         	if (noExpressao3linha.tipo == No.TIPO_VAZIO) {
         		noExpressao3.tipo =noExpressao4.tipo;
         	}
         	else if(noExpressao3linha.tipo == noExpressao4.tipo && noExpressao3linha.tipo == No.TIPO_NUMERICO) {
         		noExpressao3.tipo = No.TIPO_NUMERICO;
         	}
         	else {
         		noExpressao3.tipo = No.TIPO_ERRO;
            	erroSemantico("Operacao entre tipos incompativeis" + 
            	" <"+noExpressao3linha.tipo+","+noExpressao4.tipo+">", noExpressao4.getPai());
         	}  
                return noExpressao3;
       
        } // follow-->+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”
        else {
             erroSintatico("Esperado \"ID, , ConstInteira, ConstReal, ConstString, true, false,“-” (negação), !, (\" , encontrado " + "\"" + token.getLexema() + "\"");
                        tagsSincronizantes.add(Tag.RELOP_SUM);
			tagsSincronizantes.add(Tag.RELOP_MINUS);
			tagsSincronizantes.add(Tag.RELOP_LT);
			tagsSincronizantes.add(Tag.RELOP_LE);
			tagsSincronizantes.add(Tag.RELOP_GT);
			tagsSincronizantes.add(Tag.RELOP_GE);
			tagsSincronizantes.add(Tag.RELOP_EQ);
			tagsSincronizantes.add(Tag.RELOP_NE);
			tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.OP_AND);
                        tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.SMB_CP);
                        tagsSincronizantes.add(Tag.SMB_VIR);
                        
			sincronizaToken("[Modo Panico] Esperado \"+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”\", encontrado " + token.getLexema());	
        }
                    		return new No(null);

    }

    //Expressao3linha →* Exp4 Exp3’ 59 | / Exp4 Exp3’ 60 | ε 61
    //fist-->*, /, ε
    //follow-->+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”
    public No Expressao3linha() {
        
        No noExpressao3LinhaPai = new No(null);
              Token t = this.token;

        if (eat(Tag.RELOP_MULT) || eat(Tag.RELOP_DIV)) {
            No noExpressao4= Expressao4();
            No noExpressao3linhafilho= Expressao3linha();
             if (noExpressao3linhafilho.tipo == No.TIPO_VAZIO && noExpressao4.tipo == No.TIPO_NUMERICO) {
         	noExpressao3LinhaPai.tipo = No.TIPO_NUMERICO;
         } else if (noExpressao3linhafilho.tipo == noExpressao4.tipo &&  noExpressao4.tipo == No.TIPO_NUMERICO) {
         	noExpressao3LinhaPai.tipo = No.TIPO_NUMERICO;
         } else {
         	noExpressao3LinhaPai.tipo = No.TIPO_ERRO;
         }
         return noExpressao3LinhaPai;
        } //follow-->+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”
        else if (token.getClasse() == Tag.RELOP_SUM || token.getClasse() == Tag.RELOP_MINUS
                || token.getClasse() == Tag.RELOP_LT || token.getClasse() == Tag.RELOP_LE
                || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_GE
                || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE
                || token.getClasse() == Tag.OP_AND || token.getClasse() == Tag.OP_OR
                || token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
                || token.getClasse() == Tag.SMB_VIR) {
            noExpressao3LinhaPai.tipo=No.TIPO_VAZIO;
            return noExpressao3LinhaPai;
// return;
        } else {
            erroSintatico("Esperado \"*, /, ε\", encontrado " + token.getLexema());
			
			// token sincronizante: FOLLOW(Expressao2')
			tagsSincronizantes.add(Tag.RELOP_LT);
			tagsSincronizantes.add(Tag.RELOP_LE);
			tagsSincronizantes.add(Tag.RELOP_GT);
			tagsSincronizantes.add(Tag.RELOP_GE);
			tagsSincronizantes.add(Tag.RELOP_EQ);
			tagsSincronizantes.add(Tag.RELOP_NE);
			tagsSincronizantes.add(Tag.OP_AND);
			tagsSincronizantes.add(Tag.OP_OR);
			tagsSincronizantes.add(Tag.SMB_SEMICOLON);
			tagsSincronizantes.add(Tag.SMB_CP);
                        tagsSincronizantes.add(Tag.SMB_VIR);
			sincronizaToken("[Modo Panico] Esperado \"<+, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ), “,”\", encontrado " + token.getLexema());
		
	
    }
        return noExpressao3LinhaPai;
    }
//Expressao4 → ID Exp4’ 62 | ConstInteira 63 | ConstReal 64 | ConstString 65| "true" 66 | "false" 67 | OpUnario Expressao 68 | "(" Expressao")"
//fist-->ID, ConstInteira, ConstReal, ConstString, true, false,“-” (negação), “!”, (
//follow-->*, /, +, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ),“,”
    public No Expressao4() {
        No noExpressao4 = new No(null);
      Token t = this.token;
        if (eat(Tag.ConstInteira)){
         noExpressao4.tipo = No.TIPO_INT;
        }
        else if(eat(Tag.contFloat)){
         noExpressao4.tipo = No.TIPO_FLOAT;
        }
        else if (eat(Tag.ID)) {
            Token token= lexer.tabelaSimbolos.retornaToken(t.getLexema());
            Expressao4linha();
        if(token==null){
             erroSemantico("erro", t);
             noExpressao4.tipo=No.TIPO_ERRO;
        }
        else{
         noExpressao4.tipo=token.getTipo();

        }
        }
                else if (eat(Tag.ConstString)) {
         noExpressao4.tipo = No.TIPO_STRING;
            
        }
        else if(eat(Tag.KW_true) ){
            noExpressao4.setPai(t);
         noExpressao4.tipo = No.TIPO_BOOLEAN;
        }else if(eat(Tag.KW_false)){
            noExpressao4.setPai(t);
                     noExpressao4.tipo = No.TIPO_BOOLEAN;

        }
        else if (eat(Tag.OP_NEGATIVO) || eat(Tag.OP_NAO)) {
            No nooperadorUnario=operadorUnario();
            No noExpressao=Expressao();
           if(noExpressao.tipo==nooperadorUnario.tipo&&nooperadorUnario.tipo==No.TIPO_NUMERICO){
               noExpressao4.tipo=No.TIPO_NUMERICO;
           }
           else if(noExpressao.tipo==nooperadorUnario.tipo&&nooperadorUnario.tipo==No.TIPO_BOOLEAN){
                            noExpressao4.tipo=No.TIPO_BOOLEAN;

                      }
           else{
                noExpressao4.tipo=No.TIPO_ERRO;
           }
               
        } else if (eat(Tag.SMB_OP)) {
            // erroSintatico("Esperado \"( \", encontrado " + "\"" + token.getLexema() + "\"");
            No noExpressao=Expressao();
             noExpressao4.tipo= noExpressao.tipo;
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        } else {
            //follow-->*, /, +, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ),“,”

            if (token.getClasse() == Tag.RELOP_MULT || token.getClasse() == Tag.RELOP_DIV
                    || token.getClasse() == Tag.RELOP_SUM || token.getClasse() == Tag.RELOP_MINUS
                    || token.getClasse() == Tag.RELOP_LT || token.getClasse() == Tag.RELOP_LE
                    || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_GE
                    || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE
                    || token.getClasse() == Tag.OP_AND || token.getClasse() == Tag.OP_OR
                    || token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
                    || token.getClasse() == Tag.SMB_VIR) {
                erroSintatico("Esperado \"ID| ConstInteira | Real | String | true | false | OpUnitario| ( \" " + "\"" + token.getLexema() + "\"");
                return noExpressao4;
            } else {
                skip("Esperado \"ID| ConstInteira | Real | String | true | false | OpUnitario| ( \", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    Expressao4();
                }
            }
        }
        return noExpressao4;
    }

    //Expressao4linha → "(" RegexExp4 ")" 70 | ε 71
    //fist-->(, ε
    //follow-->*, /, +, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ),“,”
   public void Expressao4linha() {

        if (eat(Tag.SMB_OP)) {
            //erroSintatico("Esperado \" ( \", encontrado " + "\"" + token.getLexema() + "\"");
            RegexExp4();

            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \") \", encontrado " + "\"" + token.getLexema() + "\"");

            }
        }//follow-->*, /, +, -, <, <=, >, >=, ==, !=, &&, ||, “;”, ),“,”
        else if (token.getClasse() == Tag.RELOP_MULT || token.getClasse() == Tag.RELOP_DIV
                || token.getClasse() == Tag.RELOP_SUM || token.getClasse() == Tag.RELOP_MINUS
                || token.getClasse() == Tag.RELOP_LT || token.getClasse() == Tag.RELOP_LE
                || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_GE
                || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE
                || token.getClasse() == Tag.OP_AND || token.getClasse() == Tag.OP_OR
                || token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.SMB_CP
                || token.getClasse() == Tag.SMB_VIR) {

            return;                // return;
        } else {
            skip("Esperado \"( \", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                Expressao4linha();
            }
        }

    }

    //RegexExp4 → Expressao RegexExp4’ 72 | ε 73
    //fist-->ε, ID, ConstInteira, contFloat, ConstString, true, false,“-” (negação), “!”, (
    //follow--> )
     public void RegexExp4() {

        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira
                || token.getClasse() == Tag.contFloat || token.getClasse() == Tag.ConstString
                || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO
                || token.getClasse() == Tag.SMB_OP) {
            Expressao();
            RegexExp4Linha();
        } else if (token.getClasse() == Tag.SMB_CP) {
            return;

        } else {
            skip("Esperado \"ID, ConstInteira, ConstReal, ConstString, true, false,“-” (negação), “!”, ( \", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                RegexExp4();
            }

        }
    }
    //RegexExp4Linha → "," Expressao RegexExp4’ 74 | ε 75
    //fist-->“,”, ε
    //follow-->)
   public void RegexExp4Linha() {
        if (eat(Tag.SMB_VIR)) {
            Expressao();
            RegexExp4Linha();
        } else if (token.getClasse() == Tag.SMB_CP) {
            return;
        } else {
            skip("Esperado \" , \", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                RegexExp4Linha();
            }
        }
    }

//OpUnario → "-" 76 | "!" 77
//fist-->“-” (negação), “!”
    //follow-->ID, ConstInteira, ConstReal, ConstString,true, false, “-” (negação), “!”, (
    public No operadorUnario() {
        No nooperadorUnario= new No(null);

//first--> -,!
        if (!eat(Tag.OP_NEGATIVO) ) {
        nooperadorUnario.tipo=No.TIPO_NUMERICO;
        }
        else if(!eat(Tag.OP_NAO)){
                nooperadorUnario.tipo=No.TIPO_BOOLEAN;

        }
        
            //  erroSintatico("Esperado \"- | ! \", encontrado " + "\"" + token.getLexema() + "\"");
            // follow de OpUnario -->ID, ConstInteira, ConstReal, ConstString,true, false, “-” (negação), “!”, ( 
            else if (token.getClasse() == Tag.ID || token.getClasse() == Tag.ConstInteira || token.getClasse() == Tag.contFloat
                    || token.getClasse() == Tag.ConstString || token.getClasse() == Tag.KW_true || token.getClasse() == Tag.KW_false
                    || token.getClasse() == Tag.OP_NEGATIVO || token.getClasse() == Tag.OP_NAO || token.getClasse() == Tag.SMB_OP) {

                erroSintatico("Esperado \"negativo -, e não !\", encontrado " + "\"" + token.getLexema() + "\"");
            } else {
                skip("Esperado \"negativo -, e não ! \", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    operadorUnario();
                }

            }
		return new No(null);
        }
    }

