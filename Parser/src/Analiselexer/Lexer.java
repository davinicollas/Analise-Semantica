/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analiselexer;

import java.io.*;
import static java.lang.System.exit;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 */
public class Lexer {

    private static int cont_string = 0;//responsavel por contagem de string.
    private Tag auxnega;// responsavel pelo armazenamento de token
    private static int cont_erro = 0;// usado para contar os erros Lexico.
    public static int ContErroSintatico=0;// usado para contar os erros Sintatico.
    private static final int END_OF_FILE = -1; // contante para fim do arquivo
    private static int lookahead = 0; // armazena o último caractere lido do arquivo	
    public static int n_line = 1; // contador de linhas
    public static int n_column = 1; // contador de linhas
    private RandomAccessFile instance_file; // referencia para o arquivo
    public static TS tabelaSimbolos; // tabela de simbolos

    public Lexer(String input_data) {

        // Abre instance_file de input_data
        try {
            instance_file = new RandomAccessFile(input_data, "r");
        } catch (IOException e) {
            System.out.println("Erro de abertura do arquivo " + input_data + "\n" + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro do programa ou falha da Tabela de Simbolos\n" + e);
            System.exit(2);
        }
         tabelaSimbolos = new TS(); 
    }
    public void printTS() {
      System.out.println("");
      System.out.println("--------Tabela de Simbolos--------");
      System.out.println(tabelaSimbolos.toString());  
      System.out.println();
   }

    // Fecha instance_file de input_data
    public void fechaArquivo() {

        try {
            instance_file.close();
        } catch (IOException errorFile) {
            System.out.println("Erro ao fechar arquivo\n" + errorFile);
            System.exit(3);
        }
    }
     
    // Reporta erro para o usuário
    public void sinalizaErroLexico(String mensagem) {
        System.out.println("[Erro Lexico]: " + mensagem + "\n");
    }

    // Volta uma posição do buffer de leitura
    public void retornaPonteiro() {
        try {
            // Não é necessário retornar o ponteiro em caso de Fim de Arquivo
            if (lookahead != END_OF_FILE) {
                instance_file.seek(instance_file.getFilePointer() - 1);
                n_column--;
            }
        } catch (IOException e) {
            System.out.println("Falha ao retornar a leitura\n" + e);
            System.exit(4);
        }
    }

    /* TODO:
    //[1]   Voce devera se preocupar quando incremetar as linhas e colunas,
    //      assim como quando decrementar ou reseta-las.
    //[2]   Toda vez que voce encontrar um lexema completo, voce deve retornar
    //      um objeto new Token(Tag, "lexema", linha, coluna). Cuidado com as
    //      palavras reservadas que ja sao cadastradas na TS. Essa consulta
    //      voce devera fazer somente quando encontrar um Identificador.
    //[3]   Se o caractere lido nao casar com nenhum caractere esperado,
    //      apresentar a mensagem de erro na linha e coluna correspondente.
    //Obs.: lembre-se de usar o metodo retornaPonteiro() quando necessario. 
            lembre-se de usar o metodo sinalizaErroLexico() para mostrar
            a ocorrencia de um erro lexico.
     */
    // Obtém próximo token: esse metodo simula um AFD
    public Token proxToken() {

        StringBuilder lexema = new StringBuilder();
        int estado = 1;
        char c;

        while (true) {
            c = '\u0000'; // null char

            // avanca caractere ou retorna token
            try {
                // read() retorna um inteiro. -1 em caso de EOF
                lookahead = instance_file.read();

                if (lookahead != END_OF_FILE) {
                    c = (char) lookahead; // conversao int para char

                }
            } catch (IOException e) {
                System.out.println("Erro na leitura do arquivo");
                System.exit(3);
            }
            if (cont_erro >= 5) {
                System.out.println("Foram encontrados 5 erros Lexico, desta forma compilação não foi concluida");
                exit(1);
            }
            if (ContErroSintatico >= 5) {
                System.out.println("Foram encontrados 5 erros Sintatico, desta forma compilação não foi concluida");
                exit(1);
            }
            n_column++;
            //No Switch sera testado se a palavra lida é adequada as regras
            //foi programado conforme o automato
            switch (estado) {
                // estado 1
                case 1:
                    if (lookahead == END_OF_FILE) {
                        return new Token(Tag.EOF, "EOF", n_line, n_column);
                    } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r')//Se o caracter atual for: Vazio, tab,qebra de linha ou \r
                    {
                        // Permance no estado = 1
                        if (c == '\n') {
                            //adiciona uma linha    a  
                            n_line++;
                            n_column = 0;//retorna a coluna um
                        } else if (c == '\t') {
                            n_column += 2;
                        }
                        estado = 1;
                    } else if (c == '=') {
                        estado = 2;
                    } else if (c == '<') {
                        estado = 5;
                    } else if (c == '-') {
                        estado = 4;
                    } else if (c == '>') {
                        estado = 8;
                    } else if (c == '!') {
                        estado = 11;
                    } else if (c == '*') {
                        estado = 13;
                        auxnega = Tag.RELOP_MULT;
                        return new Token(Tag.RELOP_MULT, "*", n_line, n_column);
                    } else if (c == '/') {
                        estado = 14;
                    } else if (c == '+') {
                        estado = 15;
                        auxnega = Tag.RELOP_SUM;
                        return new Token(Tag.RELOP_SUM, "+", n_line, n_column);
                    } else if (c == '{') {
                        estado = 17;
                        return new Token(Tag.SMB_ABCH, "{", n_line, n_column);
                    } else if (c == '}') {
                        estado = 18;
                        return new Token(Tag.SMB_FCCH, "}", n_line, n_column);
                    } else if (c == ',') {
                        estado = 19;
                        return new Token(Tag.SMB_VIR, ",", n_line, n_column);
                    } else if (c == ';') {
                        estado = 20;
                        return new Token(Tag.SMB_SEMICOLON, ";", n_line, n_column);
                    } else if (c == '(') {
                        estado = 21;
                        auxnega = Tag.SMB_OP;
                        return new Token(Tag.SMB_OP, "(", n_line, n_column);
                    } else if (c == ')') {
                        estado = 22;
                        auxnega = Tag.SMB_CP;
                        return new Token(Tag.SMB_CP, ")", n_line, n_column);

                    } else if (c == '&') {
                        estado = 28;
                    } else if (c == '|') {
                        estado = 30;
                    } else if (c == '"') {
                        estado = 29;
                    } else if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 23;
                    } else if (Character.isLetter(c)) {
                        lexema.append(c);
                        estado = 27;
                    } else {
                        sinalizaErroLexico("Caractere invalido " + c + " na linha " + n_line + " e coluna " + n_column);
                        cont_erro++;
                    }
                    break;
                case 2:
                    if (c == '=') {
                        estado = 3;
                        return new Token(Tag.RELOP_EQ, "==", n_line, n_column);
                    } else {
                        estado = 2;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_ASSIGN, "=", n_line, n_column);
                    }
                case 4:
                        if (auxnega == Tag.contFloat || auxnega == Tag.ID || auxnega == Tag.ConstInteira || auxnega == Tag.SMB_OP || auxnega== Tag.RELOP_MULT ) {                      
                        estado = 4;
                        retornaPonteiro();
                        return new Token(Tag.OP_NEGATIVO, "-", n_line, n_column);
                    } else {
                        estado = 16;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_MINUS, "-", n_line, n_column);
                    }
                    case 5:
                    if (c == '=') {
                        estado = 6;
                        return new Token(Tag.RELOP_GE, ">=", n_line, n_column);
                     } else {
                        estado = 8;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_GT, ">", n_line, n_column);
                    }
                case 8:
                    if (c == '=') {
                        estado = 9;
                        return new Token(Tag.RELOP_LE, "<=", n_line, n_column);
                    } else {
                        estado = 5;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_LT, "<", n_line, n_column);
                    }
                case 11:
                    if (c == '=') {
                        estado = 12;
                        return new Token(Tag.RELOP_NE, "!=", n_line, n_column);
                    } else {
                        estado = 11;
                        return new Token(Tag.OP_NAO, "!", n_line, n_column);
                    }
                case 14:
                    if (c == '/') {
                        estado = 34;
                    } else if (c == '*') {
                        estado = 35;
                    } else {
                        return new Token(Tag.RELOP_DIV, "/", n_line, n_column);
                    }

                    break;
                case 23:
             
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // Permanece no estado 19
                    } else if (c == '.') {
                        lexema.append(c);
                        estado = 24;
                    } else {
                        //estado = 20;
                        retornaPonteiro();
                        auxnega =Tag.ConstInteira;
                        return new Token(Tag.ConstInteira, lexema.toString(), n_line, n_column);
                    }
                    break;

                case 24:

                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 26;
                    } else {
                        sinalizaErroLexico("Padrao para double invalido na linha " + n_line + " coluna " + n_column);
                    }
                case 26:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // permanece no esrado 22
                    } else {
                        //estado = 23;
                        retornaPonteiro();
                        auxnega = Tag.contFloat;
                        return new Token(Tag.contFloat, lexema.toString(), n_line, n_column);
                    }
                    break;
                case 27:
                if(Character.isLetterOrDigit(c) || c == '_') {
                   lexema.append(c);
                                 
                 }
                 else {
                   retornaPonteiro();  
                   Token token = tabelaSimbolos.retornaToken(lexema.toString()); // consulta TS

                   if(token == null) {
                      token = new Token(Tag.ID, lexema.toString(), n_line, n_column);
                  //    tabelaSimbolos.put(lexema.toString(), token);
                   }
                   else {
                    //  token.setLinha(n_line);
                    //  token.setColuna(n_column);
                   }
                   return token;
                 }
             break;
                case 28:
                    if (c == '&') {
                        estado = 28;
                        return new Token(Tag.OP_AND, "&&", n_line, n_column);
                    } else {
                        sinalizaErroLexico("Token incompleto para o caractere [&&] na linha " + n_line + " e coluna " + n_column);
                        estado = 1;
                        cont_erro++;
                    }
                    break;
                case 29:
                    if (c == '"') {
                        if (cont_string == 0) {
                            sinalizaErroLexico("String vazia na linha: " + n_line + " e coluna: " + n_column);
                            n_line++;
                            n_column = 1;
                            estado = 1;
                            cont_erro++;
                        } else {
                            retornaPonteiro();
                            estado = 39;
                        }

                    } else if (lookahead == END_OF_FILE) {
                        sinalizaErroLexico("A Aspa precisa ser fechada na linha: " + n_line + "coluna: " + n_column);
                        estado = 1;
                        cont_erro++;
                    } else {
                        lexema.append(c);
                        cont_string++;
                    }
                    break;
                case 30:
                    if (c == '|') {
                        estado = 30;
                        return new Token(Tag.OP_OR, "||", n_line, n_column);
                    } else {
                        sinalizaErroLexico("Token incompleto para o caractere [||] na linha " + n_line + " e coluna " + n_column);
                        estado = 1;
                        cont_erro++;
                    }
                    break;
                case 31:
                    if (c != '\'') {
                        lexema.append(c);
                        estado = 31;
                    } else {
                        estado = 31;
                    }
                    break;
                case 34:
                    if (c == '\n') {
                        n_line++;
                        estado = 1;
                    }
                    ;
                    break;
                case 35:

                    if (c == '*') {
                        estado = 36;
                    } else if (lookahead == END_OF_FILE) {
                        sinalizaErroLexico("O comentário não foi fechado na linha: " + n_line + " coluna: " + n_column);
                        estado = 1;
                        cont_erro++;
                        if (c == '\n') {
                            n_line++;
                        }
                    }
                    break;
                case 36:
                    if (c == '/') {
                        estado = 1;
                    }
                    break;
                case 39:
                    if (c == '"') {
                        estado = 1;
                        cont_string = 0;
                        return new Token(Tag.ConstString, lexema.toString(), n_line, n_column);
                    }
                    break;

            } // fim switch
        } // fim while
    } // fim proxToken()
}
    /**
     * @param args the command line arguments
     */
   
