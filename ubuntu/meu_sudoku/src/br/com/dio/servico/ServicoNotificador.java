package br.com.dio.servico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.dio.servico.EventoEnum.LIMPAR_ESPACO;

public class ServicoNotificador {

    private final Map<EventoEnum, List<EscutadorEvento>> ouvintes = new HashMap<>(){{
        put(LIMPAR_ESPACO, new ArrayList<>());
    }};

    public void inscrever(final EventoEnum tipoEvento, EscutadorEvento ouvinte){
        var ouvintesSelecionados = ouvintes.get(tipoEvento);
        ouvintesSelecionados.add(ouvinte);
    }

    public void notificar(final EventoEnum tipoEvento){
        ouvintes.get(tipoEvento).forEach(l -> l.atualizar(tipoEvento));
    }

}
