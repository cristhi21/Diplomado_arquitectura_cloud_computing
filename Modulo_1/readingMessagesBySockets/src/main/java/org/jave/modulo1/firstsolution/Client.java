package org.jave.modulo1.firstsolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.IntStream;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket socket;
    private DataOutputStream out;

    public Client() {
        startSession();
    }

    private void sendingMessage(int number) {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            sendingMessageToServerAndCalculateTime(String.valueOf(number));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendingMessageToServerAndCalculateTime(String value) throws IOException {
        // Capturar tiempo inicial
        long inicioNano = System.nanoTime();
        out.writeUTF(value); // Enviar mensaje
        // Capturar tiempo final
        long finNano = System.nanoTime();
        // Calcular diferencias
        long duracionNano = finNano - inicioNano;
        double duracionMili = (double) duracionNano / Constants.MILISEGUNDOS_CONVERSOR;

        logger.info("Procesando valor: {} - Respuesta en milisegundos: {} ms", value, duracionMili );
    }

    private void interactWithServer() {
        IntStream.rangeClosed(1, 50).forEach(number -> {
            sendingMessage(number);
            try {
                Thread.sleep(Constants.THREAD_SLEEP);
            } catch (InterruptedException e) {
                logger.error("El hilo fue interrumpido");
                Thread.currentThread().interrupt();
            }
        });
    }

    private void startSession() {
        try {
            socket = new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
            socket.setTcpNoDelay(true);      // Desactiva algoritmo de Nagle
            socket.setSoTimeout(Constants.TIMEOUT);       // Timeout controlado
            socket.setReceiveBufferSize(Constants.BUFFER_SIZE); // Buffer pequeño
            socket.setSendBufferSize(Constants.BUFFER_SIZE);    // Buffer pequeño para bajar el tiempo

            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void endSession() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.interactWithServer();
        client.endSession();
    }
}