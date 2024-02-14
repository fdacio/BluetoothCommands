package br.com.daciosoftware.bluetoothcommands.ui.commands;

public class Command {
    private final String texto;
    private final TypeCommand tipo;

    public enum TypeCommand {
        ENVIADO, RECEBIDO
    }
    public Command(String texto, TypeCommand tipo) {
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
