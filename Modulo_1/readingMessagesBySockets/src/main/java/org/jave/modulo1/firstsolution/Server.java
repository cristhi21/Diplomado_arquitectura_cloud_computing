package org.jave.modulo1.firstsolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private DataInputStream in;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startServer() {
        logger.info("Servidor ha Iniciado...");
        try {
            Socket socketClient = this.serverSocket.accept();
            in = new DataInputStream(socketClient.getInputStream());
            logger.info("Se ha conectado el cliente = {}", socketClient);

            while (!this.serverSocket.isClosed()) {
                gettingMessage(socketClient);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnectionServer(Socket client) throws IOException {
        client.close();
        logger.info("El usuario abandono");
    }

    private void gettingMessage(Socket client) {
        try {
            // --- Bloque de código a medir ---
            String receivedMessage = getMessageFromClientAndCalculateTime();
            //
            if (receivedMessage.equals("100")) {
                logger.info("La conexión se cerrara, gracias");
                closeConnectionServer(client);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMessageFromClientAndCalculateTime() throws IOException {
        // Capturar tiempo inicial
        long inicioNano = System.nanoTime();
        String receivedMessage = in.readUTF(); // Recibir mensaje
        // Capturar tiempo final
        long finNano = System.nanoTime();
        // Calcular diferencias
        long duracionNano = finNano - inicioNano;
        double duracionMili = (double) duracionNano / Constants.MILISEGUNDOS_CONVERSOR;

        logger.info("Mensaje {} recibido - Tiempo en milisegundos: {} ms", receivedMessage, duracionMili);
        return receivedMessage;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(new ServerSocket(Constants.SERVER_PORT));
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
