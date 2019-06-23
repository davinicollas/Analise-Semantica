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
public enum Tag {
    
    // fim de arquivo
    EOF,
    
    //Operadores
    RELOP_LT,       // <
    RELOP_LE,       // <=
    RELOP_GT,       // >
    RELOP_GE,       // >=
    RELOP_EQ,       // ==
    RELOP_NE,       // !=
    RELOP_ASSIGN,   // =
    RELOP_SUM,      // +
    RELOP_MINUS,    // -
    RELOP_MULT,     // *
    RELOP_DIV,      // /
   
    //Simbolos
    SMB_OP,         // (
    SMB_CP,         // )
    SMB_SEMICOLON,  // ;
    SMB_ABCH, //{
    SMB_FCCH, //}
    SMB_VIR, //,
    //op.unitario
    OP_NAO, //!
    OP_NEGATIVO, //-
    OP_OR, //||
    OP_AND, //&&
    //identificador
    ID,
    
    //ConstString 
    ConstString,
    //constInteger
    ConstInteira,
    //constFloat
    contFloat,
    //constReal
    contReal,
    //True
    KW_true,
    //False
    KW_false,
    
    // palavra reservada
    KW_public,
    KW_class,
    KW_SystemOutDispln,
    KW_end,
    KW_integer,
    KW_string,
    KW_print,
    KW_println,
    KW_static,
    KW_return,
    KW_else,
    KW_while,
    KW_int,
    KW_float,
    KW_void,
    KW_boolean,
    KW_if,
    KW_main;
    
}
