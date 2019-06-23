package Analiselexer;

/**
 *
 * @author Davi Nicollas,Pedro Augusto Marcelino, Bernardo DelRio. 
 */
public class Compiladores {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      
    /**
     */ 
     Lexer lexer = new Lexer("C:\\Users\\davin\\Desktop\\lexer\\Parser\\src\\parser\\programa2.txt");
      Parser parser = new Parser(lexer);

      // primeiro procedimento do Javinha: Programa()
      parser.Programa();

      parser.fechaArquivos();
      
      //Imprimir a tabela de simbolos
      lexer.printTS();
      
       System.out.println("--------------------TABELA DE TIPO--------------------");
       System.out.println("--------TIPO_VAZIO=[111]----------");
       System.out.println("--------TIPO_BOOLEAN=[100]--------");
       System.out.println("--------TIPO_INT=[101]------------");
       System.out.println("--------TIPO_STRING=[102]---------");
       System.out.println("--------TIPO_FLOAT=[103]----------");
       System.out.println("--------TIPO_ERRO=[104]-----------");
       System.out.println("--------TIPO_NUMERICO=[105]-------");
       System.out.println("--------TIPO_LOGICO=[106]---------");
       System.out.println("-----------------------------------------------------");

      System.out.println("Compilação de Programa Realizada!");
   }
   
} 