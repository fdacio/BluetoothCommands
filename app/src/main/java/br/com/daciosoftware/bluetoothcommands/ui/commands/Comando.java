package br.com.daciosoftware.bluetoothcommands.ui.commands;

public class Comando {
    private String texto;
    private TypeCommand tipo;

    public enum TypeCommand {
        ENVIADO, RECEBIDO;
    }

    public Comando(String texto, TypeCommand tipo) {
        this.texto = texto;
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public TypeCommand getTipo() {
        return tipo;
    }
}
