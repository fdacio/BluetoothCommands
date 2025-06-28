package br.com.daciosoftware.bluetoothcommands.ui.commands;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return texto.equals(command.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto);
    }

    public TypeCommand getTipo() {
        return tipo;
    }


}
