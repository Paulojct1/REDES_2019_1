package webserver;

/**
 * Servidor Multithread
 * 
 * @author Paulo José Carmona Teixeira
 */

/* Imports */
import java.io.*;
import java.net.*;
import java.util.*;

public class Webserver {

    public static void main(String[] args) throws Exception {
        
        int porta = 1750;
        
        ServerSocket socket = new ServerSocket(porta);
        
        /* Processa os serviços de requisição HTTP num loop infinito */
        
        while (true) {
        
            /* Requisição de conexão TCP */
            Socket connection = socket.accept();

            /* Construtor do objeto de requisição de mensagem HTTP */
            RequisicaoHttp requisicao = new RequisicaoHttp(connection);

            /* Cria um novo thread para processar a requisição */
            Thread thread = new Thread(requisicao);

            /* Inicia a Thread, dado que a próxima classe é uma Runnable */
            thread.start();
        }
    }    
}
