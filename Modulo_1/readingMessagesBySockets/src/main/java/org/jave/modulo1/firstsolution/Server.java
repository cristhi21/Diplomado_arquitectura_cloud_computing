package org.jave.modulo1.firstsolution;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PUERTO = 5001;

    private DataInputStream in;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startServer() {
        System.out.println("Servidor ha iniciado...");
        try {
            Socket socketClient = this.serverSocket.accept();
            in = new DataInputStream(socketClient.getInputStream());
            System.out.println("Se ha conectado el cliente = " + socketClient);

            while (!this.serverSocket.isClosed()) {
                String receivedMessage = gettingMessage(socketClient);
                System.out.println("Mensaje recibido: " + receivedMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnectionServer(Socket client) throws IOException {
        client.close();
        System.out.println("El usuario abandono");
    }

    private String gettingMessage(Socket client) {
        try {
            // --- Bloque de código a medir ---
            String receivedMessage = getMessageFromClientAndCalculateTime();
            //
            if (receivedMessage.equals("100")) {
                System.out.println("La conexion se cerrara, gracias");
                closeConnectionServer(client);
            }
            return receivedMessage;
        } catch (IOException e) {
            e.printStackTrace();
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
        //long duracionMicro = duracionNano / Constants.MICROSEGUNDOS_CONVERSOR;
        long duracionMili = duracionNano / Constants.MILISEGUNDOS_CONVERSOR;

        //System.out.println("Tiempo en nanosegundos: " + duracionNano + " ns");
        //System.out.println("Tiempo en microsegundos: " + duracionMicro + " µs");
        System.out.println("Tiempo en milisegundos: " + duracionMili + " ms");
        return receivedMessage;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(new ServerSocket(PUERTO));
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
