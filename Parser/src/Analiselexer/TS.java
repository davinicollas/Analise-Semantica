/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analiselexer;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 */
public class TS {
    
    private HashMap<String, Token> tabelaSimbolos; // Tabela de símbolos do ambiente

    public TS() {
        tabelaSimbolos = new HashMap();

        // Inserindo as palavras reservadas
        Token word;
        word = new Token(Tag.KW_public, "public", 0, 0);
        this.tabelaSimbolos.put("public", word);
        
        word = new Token(Tag.KW_class, "class", 0, 0);
        this.tabelaSimbolos.put("class", word);
        
        word = new Token(Tag.KW_SystemOutDispln, "SystemOutDispln", 0, 0);
        this.tabelaSimbolos.put("SystemOutDispln", word);
    
        
        word = new Token(Tag.KW_float, "float", 0, 0);
        this.tabelaSimbolos.put("float", word);
     
        word = new Token(Tag.KW_int, "int", 0, 0);
        this.tabelaSimbolos.put("int", word);
        
        word = new Token(Tag.KW_string, "string", 0, 0);
        this.tabelaSimbolos.put("string", word);
       
             
        word = new Token(Tag.KW_print, "print", 0, 0);
        this.tabelaSimbolos.put("print", word);
        
        word = new Token(Tag.KW_println, "println", 0, 0);
        this.tabelaSimbolos.put("println", word);
        
        word = new Token(Tag.KW_static, "static", 0, 0);
        this.tabelaSimbolos.put("static", word);
                
          word = new Token(Tag.KW_return, "return", 0, 0);
        this.tabelaSimbolos.put("return", word);   
               
          word = new Token(Tag.KW_else, "else", 0, 0);
        this.tabelaSimbolos.put("else", word);
        
        word = new Token(Tag.KW_while, "while", 0, 0);
        this.tabelaSimbolos.put("while", word);   
        
        word = new Token(Tag.KW_if, "if", 0, 0);
        this.tabelaSimbolos.put("if", word);
        
        word = new Token(Tag.KW_boolean, "boolean", 0, 0);
        this.tabelaSimbolos.put("boolean", word);
        
        word = new Token(Tag.KW_void, "void", 0, 0);
        this.tabelaSimbolos.put("void", word);
         
        word = new Token(Tag.KW_false, "false", 0, 0);
        this.tabelaSimbolos.put("false", word);
        
        word = new Token(Tag.KW_true, "true", 0, 0);
        this.tabelaSimbolos.put("true", word);
        
        word = new Token(Tag.KW_main, "main", 0, 0);
        this.tabelaSimbolos.put("main", word);
        
        
    }
    
    public void put(String s, Token w) {
        tabelaSimbolos.put(s, w);
    }

    // Pesquisa na tabela de símbolos se há algum token com determinado lexema
    // vamos usar esse metodo somente para diferenciar ID e KW
    public Token retornaToken(String lexema) {
		  Token token = tabelaSimbolos.get(lexema); 
		  return token;   	
    }
    
    @Override
    public String toString() {
        String saida = "";
        int i = 1;
        
        for(Map.Entry<String, Token> entry : tabelaSimbolos.entrySet()) {
            saida += ("posicao " + i + ": \t" + entry.getValue().toString()) + "\n";
            i++;
        }
        return saida;
    }
}