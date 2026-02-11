package org.jave.modulo1.firstsolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.IntStream;

/**
 * Cliente TCP que envía mensajes al servidor, espera una respuesta y mide el tiempo de procesamiento de cada mensaje.
 * El cliente envía un número específico de mensajes, según lo definido en Constants.NUMBER_OF_MESSAGES,
 * y luego cierra la conexión.
 *
 * @autor grupo3
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public static final String RESPUESTA_DEL_SERVIDOR_EN_MILISEGUNDOS_MS =
            "Procesando valor: {}, respuesta del servidor: {}, tiempo en milisegundos: {} ms";
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client() throws IOException {
        startSession();
    }

    /**
     * Envía un mensaje al servidor con el número proporcionado como argumento, espera una respuesta y
     * mide el tiempo de procesamiento de cada mensaje.
     *
     * @param number
     * @throws IOException
     */
    private void sendingMessage(int number) throws IOException {
        sendingMessageToServerAndCalculateTime(String.valueOf(number));
    }

    /**
     * Envía un mensaje al servidor, espera una respuesta y mide el tiempo de procesamiento de cada mensaje.
     * El mensaje enviado es el valor proporcionado como argumento.
     *
     * @param value
     * @throws IOException
     */
    private void sendingMessageToServerAndCalculateTime(String value) throws IOException {
        long inicioNano = System.nanoTime(); // Capturar tiempo inicial
        out.writeUTF(value); // Enviar mensaje
        String receivedMessage = in.readUTF(); // Esperar respuesta del servidor
        long finNano = System.nanoTime(); // Capturar tiempo final
        long duracionNano = finNano - inicioNano; // Calcular diferencias
        double duracionMili = (double) duracionNano / Constants.MILISEGUNDOS_CONVERSOR;

        logger.info(RESPUESTA_DEL_SERVIDOR_EN_MILISEGUNDOS_MS, value, receivedMessage, duracionMili);
    }

    /**
     * Interactúa con el servidor enviando un número específico de mensajes, según lo definido en
     * Constants.NUMBER_OF_MESSAGES. Después de enviar cada mensaje, el cliente espera una respuesta del servidor
     * y mide el tiempo de procesamiento de cada mensaje.
     * El cliente también introduce un retraso entre el envío de cada mensaje para simular un comportamiento más realista.
     *
     * @throws IOException
     */
    private void interactWithServer() throws IOException {
        IntStream.rangeClosed(1, Constants.NUMBER_OF_MESSAGES).forEach(number -> {
            try {
                sendingMessage(number);
                Thread.sleep(Constants.THREAD_SLEEP);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                logger.error("El hilo fue interrumpido");
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Inicia la sesión del cliente estableciendo una conexión TCP con el servidor y configurando los flujos de entrada y salida.
     * La conexión se establece utilizando la dirección IP y el puerto definidos en Constants.SERVER_HOST y Constants.SERVER_PORT, respectivamente.
     * Además, se configuran opciones de socket para optimizar el rendimiento, como
     * desactivar el algoritmo de Nagle, establecer un timeout controlado y configurar buffers pequeños.
     *
     * @throws IOException
     */
    private void startSession() throws IOException {
        socket = new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
        socket.setTcpNoDelay(true);      // Desactiva algoritmo de Nagle
        socket.setSoTimeout(Constants.TIMEOUT);       // Timeout controlado
        socket.setReceiveBufferSize(Constants.BUFFER_SIZE); // Buffer pequeño
        socket.setSendBufferSize(Constants.BUFFER_SIZE);    // Buffer pequeño para bajar el tiempo

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    /**
     * Cierra la sesión del cliente cerrando la conexión TCP con el servidor.
     * Esto libera los recursos asociados a la conexión y asegura que el cliente se desconecte de manera ordenada.
     *
     * @throws IOException
     */
    private void endSession() throws IOException {
        socket.close();
    }

    /**
     * Punto de entrada del programa. Crea una instancia del cliente, inicia la interacción con el servidor y luego cierra la sesión.
     * Si ocurre una excepción de E/S durante la ejecución, se lanza una RuntimeException para manejar el error de manera adecuada.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.interactWithServer();
            client.endSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}