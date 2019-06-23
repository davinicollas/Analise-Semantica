/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analiselexer;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 */
public class Token {
    
   private Tag nome;
   private String lexema;
   private int linha;
   private int coluna;
      private int tipo; // novo atributo de um token

   
   public Token(Tag nome, String lexema, int linha, int coluna) {

      this.nome = nome;
      this.lexema = lexema;
      this.linha = linha;
      this.coluna = coluna;
       tipo = No.TIPO_VAZIO;
      
   }
	
   public Tag getClasse() {
		
      return nome;
   }

    public Tag getNome() {
        return nome;
    }

    public void setNome(Tag nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
	
   public void setClasse(Tag nome) {
		
      this.nome = nome;
   }
	
   public String getLexema() {
	
      return lexema;
   }
	
   public void setLexema(String lexema) {
		
      this.lexema = lexema;
   }
    
   public int getLinha() {
      return linha;
   }

   public void setLinha(int linha) {
      this.linha = linha;
   }

   public int getColuna() {
      return coluna;
   }

   public void setColuna(int coluna) {
      this.coluna = coluna;
   }
    
   @Override
   public String toString() {
        return "<" + nome + ", \"" + lexema + "\">, tipo: " + tipo + " linha: " + linha + " coluna: " + coluna + "";
    }
}