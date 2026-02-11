package org.jave.modulo1.firstsolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor TCP que recibe mensajes de un cliente, responde con "OK" y mide el tiempo de procesamiento de cada mensaje.
 * El servidor se cierra automáticamente después de recibir un número específico de mensajes.
 *
 * @autor grupo3
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private DataInputStream in;
    private DataOutputStream out;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Inicia el servidor, acepta conexiones de clientes y procesa los mensajes recibidos. El servidor se cerrará automáticamente
     * después de recibir un número específico de mensajes, según lo definido en Constants.NUMBER_OF_MESSAGES.
     */
    private void startServer() throws IOException {
        logger.info("Servidor ha Iniciado...");
        Socket socketClient = this.serverSocket.accept();
        in = new DataInputStream(socketClient.getInputStream());
        out = new DataOutputStream(socketClient.getOutputStream());
        logger.info("Se ha conectado el cliente = {}", socketClient);

        while (!this.serverSocket.isClosed()) {
            String gotMessage = getMessageFromClientAndCalculateTime();
            if (isTheLastMessage(gotMessage)) {
                logger.info("Se han enviado todos los mensajes, cerrando conexión...");
                closeConnectionServer(socketClient);
                break;
            }
        }
    }

    /**
     * Cierra la conexión con el cliente y registra que el usuario ha abandonado.
     *
     * @param client
     * @throws IOException
     */
    private void closeConnectionServer(Socket client) throws IOException {
        client.close();
        logger.info("El usuario abandono");
    }

    /**
     * Recibe un mensaje del cliente, responde con "OK" y mide el tiempo de procesamiento del mensaje. El tiempo se registra en milisegundos.
     *
     * @return
     * @throws IOException
     */
    private String getMessageFromClientAndCalculateTime() throws IOException {
        long inicioNano = System.nanoTime(); // Capturar tiempo inicial
        String receivedMessage = in.readUTF(); // Recibir mensaje
        out.writeUTF("OK"); //Respuesta al cliente
        long finNano = System.nanoTime(); // Capturar tiempo final
        long duracionNano = finNano - inicioNano; // Calcular diferencias
        //double duracionMili = (double) duracionNano / Constants.MILISEGUNDOS_CONVERSOR;
        //logger.info("Mensaje {} recibido - Tiempo en milisegundos: {} ms", receivedMessage, duracionMili);
        return receivedMessage;
    }

    /**
     * Verifica si el mensaje recibido es el último mensaje esperado, según lo definido en Constants.NUMBER_OF_MESSAGES.
     *
     * @param gotMessage
     * @return
     */
    private static boolean isTheLastMessage(String gotMessage) {
        return gotMessage.equals(String.valueOf(Constants.NUMBER_OF_MESSAGES));
    }

    /**
     * Punto de entrada del programa. Inicia el servidor TCP en el puerto definido en Constants.SERVER_PORT.
     *
     */
    public static void main(String[] args) {
        try {
            Server server = new Server(new ServerSocket(Constants.SERVER_PORT));
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
