/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.jave.modulo1.secondsolution;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author developer
 */
public class UDPServer {

    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] receiveData = new byte[1024];

            System.out.println("Servidor UDP listo. Esperando estímulo...");

            //Pre-calcular la respuesta para evitar trabajo innecesario en el bucle.
            final byte[] sendData = "EN_ESCUCHA".getBytes();

            DatagramPacket sendPacket;
            DatagramPacket receivePacket;
            InetAddress clientAddress;

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket); //espera el mensaje del cliente

                clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);

                socket.send(sendPacket);
            }
        }
    }
}
