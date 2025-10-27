/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA

    Descrição: Implementa uma lista encadeada genérica
*/

public class ListaEncadeada<T> {
    protected No<T> inicio;
    protected int tamanho;

    public boolean vazia() { return inicio == null; }
    public int tamanho() { return tamanho; }


    public void inserirInicio(T valor) {
        No<T> novo = new No<>(valor);

        novo.prox = inicio;
        inicio = novo;
        tamanho++;
    }


    public void inserirFim(T valor) {
        No<T> novo = new No<>(valor);

        if (inicio == null) {
            inicio = novo;
        } else {
            No<T> p = inicio;
            while (p.prox != null) p = p.prox;
            p.prox = novo;
        }

        tamanho++;
    }


    public T removerInicio() {
        if (inicio == null) return null;

        No<T> removido = inicio;
        inicio = inicio.prox;
        removido.prox = null;
        tamanho--;

        return removido.info;
    }


    public interface Visitante<T> { void visitar(T valor); }


    public void percorrer(Visitante<T> v) {
        No<T> p = inicio;

        while (p != null) {
            v.visitar(p.info);
            p = p.prox;
        }
    }
}