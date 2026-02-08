package org.jave.modulo1.firstsolution;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.IntStream;

public class Client {

    private Socket socket;
    private DataOutputStream out;

    public Client() {
        out = null;
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
        //long duracionMicro = duracionNano / Constants.MICROSEGUNDOS_CONVERSOR;
        double duracionMili = (double) duracionNano / Constants.MILISEGUNDOS_CONVERSOR;

        //System.out.println("Tiempo en nanosegundos: " + duracionNano + " ns");
        //System.out.println("Tiempo en microsegundos: " + duracionMicro + " µs");
        System.out.printf("Tiempo en milisegundos: %.4f ms - ", duracionMili );
    }

    private void interactWithServer() {
        IntStream.rangeClosed(1, 50).forEach(number -> {
            sendingMessage(number);
            System.out.println("Procesando numero: " + number);
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                System.err.println("El hilo fue interrumpido");
                Thread.currentThread().interrupt();
            }
        });
    }

    private void startSession() {
        try {
            String HOST = "127.0.0.1";
            int PORT = 5001;
            socket = new Socket(HOST, PORT);
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
        client.startSession();
        client.interactWithServer();
        client.endSession();
    }
}