package com.natanael.controlarduino;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "AndroidBluetooth")
public class AndroidBluetooth extends Plugin {

    private BluetoothSocket socket;
    private OutputStream outputStream;

    private static final int REQUEST_BLUETOOTH = 1001;

    private static final UUID HC05_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    // =========================================================
    // CONECTAR
    // =========================================================

    @PluginMethod
    public void conectar(PluginCall call) {

        // Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

            boolean connect =
                    ContextCompat.checkSelfPermission(
                            getContext(),
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean scan =
                    ContextCompat.checkSelfPermission(
                            getContext(),
                            Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED;

            if (!connect || !scan) {

                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH
                );

                call.reject("Permiso Bluetooth necesario");
                return;
            }
        }

        try {

            System.out.println("=== BLUETOOTH ===");
            System.out.println("Intentando obtener BluetoothAdapter...");

            BluetoothAdapter adapter =
                    BluetoothAdapter.getDefaultAdapter();

            if (adapter == null) {

                call.reject("Este dispositivo no tiene Bluetooth");
                return;
            }

            System.out.println("Bluetooth encontrado");

            if (!adapter.isEnabled()) {

                call.reject("Bluetooth está apagado");
                return;
            }

            System.out.println("Bluetooth está encendido");

            Set<BluetoothDevice> dispositivos =
                    adapter.getBondedDevices();

            if (dispositivos == null || dispositivos.isEmpty()) {

                call.reject("No hay dispositivos Bluetooth emparejados");
                return;
            }

            BluetoothDevice hc05 = null;

            System.out.println("Dispositivos emparejados:");

            for (BluetoothDevice dispositivo : dispositivos) {

                String nombre = dispositivo.getName();

                System.out.println(
                        "Encontrado: " + nombre
                );

                if (nombre != null &&
                        nombre.toUpperCase().contains("HC-05")) {

                    hc05 = dispositivo;
                    break;
                }
            }

            if (hc05 == null) {

                call.reject(
                        "HC-05 no encontrado. Emparejalo primero desde Android."
                );

                return;
            }

            System.out.println(
                    "HC-05 encontrado: " + hc05.getName()
            );

            System.out.println("Dirección: " + hc05.getAddress());

            // Cerrar conexión anterior
            if (socket != null) {

                try {
                    socket.close();
                } catch (Exception ignored) {
                }

                socket = null;
            }

            System.out.println("Creando socket...");

            socket =
                    hc05.createRfcommSocketToServiceRecord(
                            HC05_UUID
                    );

            System.out.println("Conectando al HC-05...");

            adapter.cancelDiscovery();

            socket.connect();

            System.out.println("¡¡¡HC-05 CONECTADO!!!");

            outputStream =
                    socket.getOutputStream();

            call.resolve();

        } catch (SecurityException e) {

            System.out.println(
                    "ERROR DE PERMISOS: " + e.getMessage()
            );

            call.reject(
                    "Permiso Bluetooth rechazado"
            );

        } catch (IOException e) {

            System.out.println(
                    "ERROR DE CONEXIÓN: " + e.getMessage()
            );

            call.reject(
                    "No se pudo conectar al HC-05: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "ERROR GENERAL: " + e.getMessage()
            );

            call.reject(
                    "Error Bluetooth: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // ENVIAR
    // =========================================================

    @PluginMethod
    public void enviar(PluginCall call) {

        String mensaje =
                call.getString("mensaje");

        if (mensaje == null || mensaje.isEmpty()) {

            call.reject("Mensaje vacío");
            return;
        }

        if (outputStream == null) {

            call.reject(
                    "No hay conexión con el HC-05"
            );

            return;
        }

        try {

            System.out.println(
                    "ENVIANDO AL HC-05: " + mensaje
            );

            outputStream.write(
                    mensaje.getBytes()
            );

            outputStream.flush();

            System.out.println(
                    "ENVIADO CORRECTAMENTE"
            );

            call.resolve();

        } catch (IOException e) {

            System.out.println(
                    "ERROR ENVIANDO: " + e.getMessage()
            );

            call.reject(
                    "Error enviando Bluetooth"
            );
        }
    }


    // =========================================================
    // DESCONECTAR
    // =========================================================

    @PluginMethod
    public void desconectar(PluginCall call) {

        try {

            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }

            if (socket != null) {
                socket.close();
                socket = null;
            }

            System.out.println(
                    "Bluetooth desconectado"
            );

            call.resolve();

        } catch (IOException e) {

            call.reject(
                    "Error desconectando"
            );
        }
    }
}




