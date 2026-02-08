/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.jave.modulo1.secondsolution;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author developer
 */
public class UDPCliente {

    private static final int PORT = 5000;
    private static final String IP_ADDRESS = "localhost";
    public static final String EXIT = "exit";
    public static final double TO_MILlISECONDS = 1_000_000.0;

    public static void main(String[] args) throws IOException {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress IPAddress = InetAddress.getByName(IP_ADDRESS);
            Scanner console = new Scanner(System.in);
            byte[] receiveData = new byte[1024];

            System.out.println("Cliente UDP iniciado. Escribe un mensaje y presiona Enter:");

            String readConsole;
            byte[] sendData;

            long initTime;
            long endingTime;
            long diff;
            double ms;

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            DatagramPacket sendPacket = new DatagramPacket(new byte[1], 1, IPAddress, PORT);

            while (true) {
                System.out.print("> ");
                readConsole = console.nextLine();
                if (readConsole.equalsIgnoreCase(EXIT)) {
                    break;
                }

                sendData = readConsole.getBytes();
                sendPacket.setData(sendData, 0, sendData.length);

                initTime = System.nanoTime();

                //envio de mensaje al servidor
                clientSocket.send(sendPacket);
                //recive respuesta
                clientSocket.receive(receivePacket);

                endingTime = System.nanoTime();

                diff = endingTime - initTime;
                ms = diff / TO_MILlISECONDS;

                // Extraer el mensaje recibido del servidor solo para comprobar que si hay interaccion con el
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                System.out.printf("Respuesta del servidor: \"%s\" recibida en: %.4f ms (%d ns)%n", receivedMessage, ms, diff);
            }
        }
    }
}
