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

final class RequisicaoHttp implements Runnable {
    
    final static String CRLF = "\r\n";
    Socket s;
    
    /*Construtor*/
    public RequisicaoHttp(Socket s) {
        this.s = s;
    }
    
    /*Implementação da Interface*/
    public void run(){
        try{
            processarRequisicao();
        } 
        
        catch (Exception e){
            System.out.println( "Erro -> " + e.getMessage());
        }
    }
    
    private void processarRequisicao() throws IOException {
        
        /*Criamos o Input e o Output*/
        InputStream is = s.getInputStream();
        DataOutputStream os = new DataOutputStream(s.getOutputStream());

        /*Precisamos de um leitor de Buffer*/
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        /*Aqui receberemos os dados da requisição HTTP*/
        String requisicao = br.readLine();

        /*Extraimos o nome do Arquivo*/
        StringTokenizer tokens = new StringTokenizer(requisicao);
        tokens.nextToken();
        String nomeArquivo = tokens.nextToken();
        
        /*Arquivo no mesmo diretorio*/
        nomeArquivo = "." + nomeArquivo;

        /*Agora abrimos o arquivo solicitado*/
        FileInputStream fis = null;
        boolean arquivoExistente = true;

        try {
            fis = new FileInputStream(nomeArquivo);
        } 
        
        catch (FileNotFoundException fnfe) {
            arquivoExistente = false;
            System.out.println("Erro --> " + fnfe.getMessage());
        }
        
        /*Agora criaremos algumas partes para o DEBUG*/
        System.out.println("Arquivo em transporte!");
        System.out.println("Requisicao --> " + requisicao);
        String cabecalho = null;
        
        while((cabecalho = br.readLine()).length() != 0){
            System.out.println("Cabecalho --> " + cabecalho);
        }
        
        /*Agora montaremos a mensagem de resposta*/
        String status = null;
        String conteudo = null;
        String entidade = null;
        
        if(arquivoExistente) {
            status = "HTTP/1.0 200 OK" + CRLF;
            conteudo = "Content-Type: " + tipoDeConteudo(nomeArquivo) + CRLF;    
        } 
        
        else {
            status = "HTTP/1.0 404 Not Found" + CRLF;
            conteudo = "Content-Type: text/html" + CRLF;
            entidade = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
            + "<BODY>Not Found</BODY></HTML>";
        }
        
        /*Escreveremos os Bytes*/
        os.writeBytes(status);
        os.writeBytes(conteudo);
        os.writeBytes(CRLF);
        
        /*Envia os dados*/
        if(arquivoExistente) {
            enviaArquivos(fis, os);
            fis.close();
        } 
        
        else {
            os.writeBytes(entidade);
        }
        
        /*Finaliza*/
        os.close();
        br.close();
        s.close();
    }
    
    /* Aqui fazemos a verificação do tipo de conteúdo, baseado no arquivo */
    private String tipoDeConteudo(String nomeArquivo) {
        if (nomeArquivo.endsWith(".htm") || nomeArquivo.endsWith(".html")) {
            return "text/html";
        }
    
        if (nomeArquivo.endsWith(".ram") || nomeArquivo.endsWith(".ra")) {
            return "audio/x-pn-realaudio";
        }
    
        if (nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg")) {
            return "image/jpeg";
        }
    
        if (nomeArquivo.endsWith(".pdf")) {
            return "application/pdf";
        }
        
        return "application/octet-stream";
    }
    
    private void enviaArquivos(FileInputStream fis, DataOutputStream os) throws IOException {

    /*Construtor do um buffer para guardar os bytes que irão para o socket */
        byte[] buffer = new byte[1024];
        int bytes = 0;

        /* Copia o arquivo solicitado e envia para o output do socket */
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
}
