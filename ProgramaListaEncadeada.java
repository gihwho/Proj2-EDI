/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: especializacao de ListaEncadeada<Linha> para armazenar o programa
    ordenado por numero da linha
*/

class ProgramaListaEncadeada extends ListaEncadeada<Linha> {

    //busca a linha com numeroLinha == n
    public Linha buscar(int n) {
        No<Linha> p = inicio;
        while (p != null && p.info.numeroLinha < n) {
            p = p.prox;
        }
        if (p != null && p.info.numeroLinha == n) {
            return p.info;
        }
        return null;
    }

    //insere mantendo a lista ordenada por numeroLinha.
    public char inserirOuAtualizar(int n, String instr) {
        if (n < 0) {
            throw new IllegalArgumentException("Erro: linha " + n + " inválida.");
        }
        Linha nova = new Linha(n, instr);

        //lista vazia
        if (inicio == null) {
            inserirInicio(nova);
            return 'I';
        }

        //inserir antes do primeiro
        if (n < inicio.info.numeroLinha) {
            inserirInicio(nova);
            return 'I';
        }

        No<Linha> ant = null;
        No<Linha> p = inicio;

        //avanca ate achar posicao (ou linha igual)
        while (p != null && p.info.numeroLinha < n) {
            ant = p;
            p = p.prox;
        }

        //achou linha com mesmo numero - atualiza instrucao
        if (p != null && p.info.numeroLinha == n) {
            p.info.instrucao = instr;
            return 'U';
        }

        //insere no meio ou no fim
        No<Linha> novo = new No<>(nova);
        ant.prox = novo;
        novo.prox = p;
        tamanho++;
        return 'I';
    }

    //remove uma unica linha - retorna a Linha removida ou null se nao existir.
    public Linha removerLinha(int n) {
        if (inicio == null) return null;

        //remove no inicio
        if (inicio.info.numeroLinha == n) {
            Linha r = inicio.info;
            inicio = inicio.prox;
            tamanho--;
            return r;
        }

        No<Linha> ant = inicio;
        No<Linha> p = inicio.prox;

        while (p != null && p.info.numeroLinha != n) {
            ant = p;
            p = p.prox;
        }

        if (p == null) return null; //nao achou

        ant.prox = p.prox;
        tamanho--;
        return p.info;
    }

    //remove intervalo [li, lf] e retorna uma nova lista contendo os removidos
    public ProgramaListaEncadeada removerIntervalo(int li, int lf) {
        if (li > lf) {
            throw new IllegalArgumentException("Erro: intervalo inválido de linhas.");
        }

        ProgramaListaEncadeada removidos = new ProgramaListaEncadeada();

        //remover do inicio enquanto estiver no intervalo
        while (inicio != null &&
               inicio.info.numeroLinha >= li &&
               inicio.info.numeroLinha <= lf) {

            removidos.inserirFim(removerInicio());
        }

        if (inicio == null) {
            return removidos;
        }

        No<Linha> ant = inicio;
        No<Linha> p = inicio.prox;

        while (p != null) {
            if (p.info.numeroLinha >= li && p.info.numeroLinha <= lf) {
                ant.prox = p.prox;
                removidos.inserirFim(p.info);
                tamanho--;
                p = ant.prox;
            } else {
                ant = p;
                p = p.prox;
            }
        }

        return removidos;
    }

    //visitante especifico para (numeroLinha, instrucao)
    public interface LinhaVisitante {
        void visitar(int numeroLinha, String instrucao);
    }

    public void listarComCallback(LinhaVisitante v) {
        percorrer(l -> v.visitar(l.numeroLinha, l.instrucao));
    }
}