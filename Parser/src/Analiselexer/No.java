package Analiselexer;

import java.util.ArrayList;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 */
public class No {

    // Atributos da classe No
    private Token pai;
   
    // Constantes para tipos
    public static Integer TIPO_VAZIO	= 111;
    public static Integer TIPO_BOOLEAN = 100;
    public static Integer TIPO_INT		= 101;
    public static Integer TIPO_STRING	= 102;
    public static Integer TIPO_FLOAT	= 103;
    public static Integer TIPO_ERRO		= 104;
    public static Integer TIPO_NUMERICO		= 105;
     public static Integer TIPO_LOGICO		= 106;
    
    public int tipo = TIPO_VAZIO;
    

    public No(Token token) {
        this.pai = token;
        
    }

    public Token getPai() {
        return pai;
    }
    
    public void setPai(Token token) {
        this.pai = token;
    }
    // metodo nao necessario para o TP3: Apenas para visualizacao da arvore anotada
    
    }
