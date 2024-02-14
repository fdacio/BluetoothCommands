package br.com.daciosoftware.bluetoothcommands.ui.commands;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.com.daciosoftware.bluetoothcommands.R;

import java.util.List;

public class CommandAdapter extends BaseAdapter {
    private final List<Command> lista;
    private final Context context;

    public CommandAdapter(Context context, List<Command> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Command getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Command comando = lista.get(position);
        LayoutInflater inflater = (LayoutInflater.from(context));
        view = inflater.inflate(R.layout.comandos_adapter, null);
        TextView texto = view.findViewById(R.id.textViewComando);
        texto.setText(comando.getTexto());
        if (comando.getTipo().equals(Command.TypeCommand.ENVIADO)) {
            texto.setTextColor(context.getResources().getColor(R.color.comando_enviado));
            texto.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        if (comando.getTipo().equals(Command.TypeCommand.RECEBIDO)) {
            texto.setTextColor(context.getResources().getColor(R.color.comando_recebido));
            //Se for o ultimo comando alinha a direita por causa do float button de limpar a lista
            if (position == getCount() - 1) {
                texto.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            } else {
                texto.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
        }
        return view;
    }
}
